package com.haarismemon.applicationorganiser;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
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
import android.widget.TextView;

import com.haarismemon.applicationorganiser.adapter.ApplicationListRecyclerAdapter;
import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.model.Internship;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the activity which displays the list of all Internships
 * @author HaarisMemon
 */
public class MainActivity extends AppCompatActivity {

    private DataSource mDataSource;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Intent intent;
    private ActionMode actionMode;
    private ActionMode.Callback actionModeCallback;
    private List<Internship> internships;
    private Map<Internship, CardView> selectedInternshipCardMap;

    /**
     * RecyclerAdapter of RecyclerView for internships in the activity
     */
    public static ApplicationListRecyclerAdapter recyclerAdapter;

    /**
     * a key used when passing boolean to the intent to this activity to check if search needs to be performed
     */
    public static final String SEARCH_FROM_MAIN = "SEARCH_FROM_MAIN";

    //used to check if currently in selection mode (whether any internships has been selected)
    public boolean isSelectionMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Application Organiser");

        mDataSource = new DataSource(this);
        mDataSource.open();
        mDataSource.seedDatbase();
        intent = getIntent();

        //ArrayList of all internships in the database
        internships = mDataSource.getAllInternship();

        //map to track which internships have been selected, and which card they are linked to
        selectedInternshipCardMap = new HashMap<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        //give the recycler adapter the list of all internships
        recyclerAdapter = new ApplicationListRecyclerAdapter(this, internships);
        //set the adapter to the recycler view
        recyclerView.setAdapter(recyclerAdapter);

        //sets what is displayed in actionbar in action mode
        actionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.setTitle("0 internships selected");

                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.action_mode_menu, menu);
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

                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                isSelectionMode = false;
                //unselect all selected internships
                unselectSelectedInternships();
                //update the recycler view
                recyclerAdapter.notifyDataSetChanged();
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
        getMenuInflater().inflate(R.menu.application_list_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search_application_list);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        //set the on query text listener of the search view, and give it the adapter so that it can access the list
        searchView.setOnQueryTextListener(new MyOnQueryTextListener(recyclerAdapter));

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

        //if the search button was pressed in the main activity, then open the search view (searchbar) when this activity opens
        if(intent.getBooleanExtra(SEARCH_FROM_MAIN, false)) {
            searchItem.expandActionView();
        }

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * On click method to create a new Internship
     * @param view create button that was clicked
     */
    public void createInternship(View view) {
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
        recyclerAdapter.internshipsList = internships;
        recyclerAdapter.notifyDataSetChanged();

        //empty the map holding the selected internships
        selectedInternshipCardMap.clear();
    }

}
