package com.haarismemon.applicationorganiser;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haarismemon.applicationorganiser.adapter.ApplicationListRecyclerAdapter;
import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.Internship;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This class represents the activity which displays the list of all Internships
 * @author HaarisMemon
 */
public class MainActivity extends AppCompatActivity {

    private DataSource mDataSource;
    private RecyclerView.LayoutManager layoutManager;
    private ActionMode actionMode;
    private ActionMode.Callback actionModeCallback;
    private MenuItem prioritiseItem;
    private MenuItem deprioritiseItem;

    private List<Internship> internships;
    private Map<Internship, CardView> selectedInternshipCardMap;

    /**
     * RecyclerAdapter of RecyclerView for internships in the activity
     */
    ApplicationListRecyclerAdapter applicationListRecyclerAdapter;

    /**
     * a key used when passing boolean to the intent to this activity to check if search needs to be performed
     */
    public static final String SEARCH_FROM_MAIN = "SEARCH_FROM_MAIN";

    public static final String SOURCE = "SOURCE";

    //used to check if currently in selection mode (whether any internships has been selected)
    public boolean isSelectionMode = false;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    MenuItem orderItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setTitle("Application Organiser");

        mDataSource = new DataSource(this);
        mDataSource.open();
        mDataSource.seedDatbase();

        //ArrayList of all internships in the database
        internships = mDataSource.getAllInternship();

        displayMessageIfNoInternships();

        //map to track which internships have been selected, and which card they are linked to
        selectedInternshipCardMap = new HashMap<>();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        //give the recycler adapter the list of all internships
        applicationListRecyclerAdapter = new ApplicationListRecyclerAdapter(this, internships);
        //set the adapter to the recycler view
        recyclerView.setAdapter(applicationListRecyclerAdapter);

        //sets what is displayed in actionbar in action mode
        actionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.setTitle("0 internships selected");

                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.main_action_mode_menu, menu);

                prioritiseItem = menu.findItem(R.id.action_mode_prioritise);
                deprioritiseItem = menu.findItem(R.id.action_mode_deprioritise);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    //when the delete action button is pressed in action mode
                    case R.id.action_mode_delete:
                        //delete all selected internships
                        deleteSelectedInternships();
                        //exit action mode
                        switchActionMode(false);
                        return true;

                    //when the priority action button is pressed in action mode
                    case R.id.action_mode_prioritise:
                        //prioritise all selected internships
                        prioritiseSelectedInternships();
                        //exit action mode
                        switchActionMode(false);
                        return true;

                    //when the priority action button is pressed in action mode
                    case R.id.action_mode_deprioritise:
                        //prioritise all selected internships
                        deprioritiseSelectedInternships();
                        //exit action mode
                        switchActionMode(false);
                        return true;

                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                isSelectionMode = false;
                //unselect all selected internships
                unselectSelectedInternships();
                //update the recycler view
                applicationListRecyclerAdapter.notifyDataSetChanged();
            }
        };

    }

    @Override
    protected void onPause() {
        super.onPause();
        mDataSource.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDataSource.open();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //add search action button to action bar
        getMenuInflater().inflate(R.menu.main_menu, menu);

        searchMenuActionSetup(menu);

        orderItem = menu.findItem(R.id.action_sort_order);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case(R.id.action_sort_order):
                toggleOrderItemAscendingOrDescending();
                applicationListRecyclerAdapter.reverseOrder();
                return true;

            case(R.id.action_sort_modified_date):
                changeSort(InternshipTable.COLUMN_MODIFIED_ON, false, item);
                return true;

            case(R.id.action_sort_created_date):
                changeSort(InternshipTable.COLUMN_CREATED_ON, false, item);
                return true;

            case(R.id.action_sort_company_name):
                changeSort(InternshipTable.COLUMN_COMPANY_NAME, true, item);
                return true;

            case(R.id.action_sort_role):
                changeSort(InternshipTable.COLUMN_ROLE, true, item);
                return true;

            case(R.id.action_sort_salary):
                changeSort(InternshipTable.COLUMN_SALARY, false, item);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void changeSort(String sortByField, boolean isAscending, MenuItem currentSelectedSortItem) {
        if(currentSelectedSortItem.isChecked()) {
            toggleOrderItemAscendingOrDescending();
        } else {
            setOrderItemToAscendingOrDescending(isAscending);
        }

        applicationListRecyclerAdapter.sortInternships(InternshipTable.COLUMN_SALARY);
        currentSelectedSortItem.setChecked(true);
    }

    private void setOrderItemToAscendingOrDescending(boolean isAscending) {
        if(isAscending) {
            orderItem.setTitle("Ascending Order");
            orderItem.setIcon(R.drawable.ic_arrow_upward_black_24dp);
        } else {
            orderItem.setTitle("Descending Order");
            orderItem.setIcon(R.drawable.ic_arrow_downward_black_24dp);
        }
    }

    private void toggleOrderItemAscendingOrDescending() {
        if(orderItem.getTitle().toString().toLowerCase().contains("ascending")) {
            orderItem.setTitle("Descending Order");
            orderItem.setIcon(R.drawable.ic_arrow_downward_black_24dp);
        } else {
            orderItem.setTitle("Ascending Order");
            orderItem.setIcon(R.drawable.ic_arrow_upward_black_24dp);
        }
    }

    private void searchMenuActionSetup(final Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.action_search_internships);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        //hides other menu items in action bar when search bar is expanded
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(final MenuItem item) {
                setItemsVisibility(menu, searchItem, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(final MenuItem item) {
                setItemsVisibility(menu, searchItem, true);
                return true;
            }
        });

        //set the on query text listener of the search view, and give it the adapter so that it can access the list
        searchView.setOnQueryTextListener(new MyOnQueryTextListener(applicationListRecyclerAdapter));

        //get the search manager to set the searchable.xml to the search view
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.onActionViewExpanded();

        //change the color of the caret in the search view from the default accent color to white
        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setItemsVisibility(final Menu menu, final MenuItem searchMenuItem, final boolean isVisible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != searchMenuItem) item.setVisible(isVisible);
        }
        invalidateOptionsMenu();
    }

    /**
     * On click method to create a new Internship
     * @param view create button that was clicked
     */
    public void goToCreateInternship(View view) {
        Intent intent = new Intent(getApplicationContext(), InternshipEditActivity.class);
        intent.putExtra(InternshipEditActivity.INTERNSHIP_EDIT_MODE, false);
        startActivity(intent);
    }

    /**
     * Turns on action mode if true passed, otherwise turns action mode off
     * @param turnOn true if to turn action mode on
     */
    public void switchActionMode(boolean turnOn) {
        //if turnOn is true, then start action mode
        if(turnOn) {
            isSelectionMode = true;
            actionMode = startActionMode(actionModeCallback);
        } else {
            //else if turnOn is false, then exit action mode
            if (actionMode != null) {
                isSelectionMode = false;
                actionMode.finish();
            }
        }
    }

    //this method updates the title in the action bar in action mode, and is called every time internship selected
    private void updateActionModeCounter(int counter) {
        if(counter == 1) {
            actionMode.setTitle(counter + " internship selected");
        } else {
            actionMode.setTitle(counter + " internships selected");
        }
    }

    /**
     * Selects or deselects the internship depending on value of toBeSelected,
     * and updates the action bar counter and the card background
     * If 0 internships are selected, then action mode turns off
     * @param cardView that is linked to the internship in the RecyclerView
     * @param internship that is to be selected or deselected
     * @param toBeSelected true if the internship is to be selected
     */
    public void prepareSelection(CardView cardView, Internship internship, boolean toBeSelected) {
        //update the internship being selected
        internship.setSelected(toBeSelected);

        //if to be selected put internship in selected map, and update the cardView background
        if(toBeSelected) {
            selectedInternshipCardMap.put(internship, cardView);
            updateCardBackground(cardView, true);
        } else {
            selectedInternshipCardMap.remove(internship);
            updateCardBackground(cardView, false);
        }

        decideToPrioritiseOrDeprioritiseInternships(selectedInternshipCardMap.keySet());

        //if user selects no internships then exit action mode
        if(selectedInternshipCardMap.size() == 0) {
            switchActionMode(false);
        } else if(actionMode != null) {
            updateActionModeCounter(selectedInternshipCardMap.size());
        }

    }

    /**
     * Update the color of the cardView background depending on whether it is selected or not
     * @param cardView to change background of
     * @param isSelected true if background to set is highlighted, otherwise set to original color
     */
    public void updateCardBackground(CardView cardView, boolean isSelected) {
        if(isSelected) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.internshipCardBackgroundSelected));
        } else {
            cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.internshipCardBackgroundDefault));
        }
    }

    //unselects all the selected internships
    private void unselectSelectedInternships() {
        for(Internship internship : selectedInternshipCardMap.keySet()) {
            internship.setSelected(false);
            updateCardBackground(selectedInternshipCardMap.get(internship), false);
        }

        selectedInternshipCardMap.clear();
    }

    //deletes all selected internships
    private void deleteSelectedInternships() {
        //for all selected internships
        for(Internship deleteInternship : selectedInternshipCardMap.keySet()) {
            //remove from list
            internships.remove(deleteInternship);
            //delete from database
            mDataSource.deleteInternship(deleteInternship.getInternshipID());
        }
        //update the RecyclerView through the adapter
        applicationListRecyclerAdapter.internshipsList = internships;
        applicationListRecyclerAdapter.notifyDataSetChanged();

        //empty the map holding the selected internships
        selectedInternshipCardMap.clear();

        displayMessageIfNoInternships();
    }

    //prioritises all selected internships
    private void prioritiseSelectedInternships() {
        //for all selected internships
        for(Internship internship : selectedInternshipCardMap.keySet()) {
            internship.setSelected(false);
            internship.setPriority(true);

            mDataSource.updateInternshipPriority(internship);
        }
        //empty the map holding the selected internships
        selectedInternshipCardMap.clear();
    }

    //deprioritises all selected internships
    private void deprioritiseSelectedInternships() {
        //for all selected internships
        for(Internship internship : selectedInternshipCardMap.keySet()) {
            internship.setSelected(false);
            internship.setPriority(false);

            mDataSource.updateInternshipPriority(internship);
        }
        //empty the map holding the selected internships
        selectedInternshipCardMap.clear();
    }

    //displays message to inform user to add their first internship if internship list is empty
    private void displayMessageIfNoInternships() {
        if(internships.isEmpty()) {
            RelativeLayout mainRelativeLayout = (RelativeLayout) findViewById(R.id.mainRelativeLayout);

            TextView messageWhenEmpty = new TextView(this);
            messageWhenEmpty.setText(getResources().getString(R.string.addFirstInternship));

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            messageWhenEmpty.setLayoutParams(layoutParams);

            mainRelativeLayout.addView(messageWhenEmpty);
        }
    }

    //method used each time internship selected in multi selection to decide to show prioritise or deprioritise action
    private void decideToPrioritiseOrDeprioritiseInternships(Set<Internship> selectedInternships) {
        boolean isCurrentlyAllPrioritised = false;

        for(Internship internship : selectedInternships) {
            if(internship.isPriority()) {
                isCurrentlyAllPrioritised = true;
            } else {
                isCurrentlyAllPrioritised = false;
                break;
            }
        }

        //only show deprioritise action if all internships are prioritised
        if(isCurrentlyAllPrioritised) {
            prioritiseItem.setVisible(false);
            deprioritiseItem.setVisible(true);
        } else {
            prioritiseItem.setVisible(true);
            deprioritiseItem.setVisible(false);
        }

    }

}
