package com.haarismemon.applicationorganiser;

import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.haarismemon.applicationorganiser.adapter.ApplicationListRecyclerAdapter;
import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.database.ApplicationTable;
import com.haarismemon.applicationorganiser.listener.FilterDialogOnClickListener;
import com.haarismemon.applicationorganiser.listener.MyOnQueryTextListener;
import com.haarismemon.applicationorganiser.model.Application;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.model.FilterType;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This class represents the activity which displays the list of all Applications
 * @author HaarisMemon
 */
public class MainActivity extends AppCompatActivity {

    private DataSource mDataSource;
    public ActionMode actionMode;
    private ActionMode.Callback actionModeCallback;
    public MenuItem prioritiseItem;
    public MenuItem deprioritiseItem;

    private List<Application> applications;
    private List<Application> selectedApplications;
    private List<Application> deletedApplicationsForUndo;
    private List<Application> applicationsBeforeDeletionForUndo;
    private Map<FilterType, List<Integer>> filterSelectedItemsIndexes;
    Set<FilterType> filtersCurrentlyApplied;

    private Boolean isFilterPriority;
    private boolean isFilterChangeMade = false;

    /**
     * RecyclerAdapter of RecyclerView for applications in the activity
     */
    ApplicationListRecyclerAdapter applicationListRecyclerAdapter;

    public static final String SOURCE = "SOURCE";
    public static final String FILTER_LIST = "FILTER_LIST";
    public static final String FILTER_TYPE = "FILTER_TYPE";
    public static final String CHECKED_ITEMS = "CHECKED_ITEMS";
    private String SELECTED_APPLICATIONS = "SELECTED_APPLICATIONS";
    private String SELECTION_MODE = "SELECTION_MODE";

    //used to check if currently in selection mode (whether any applications has been selected)
    public boolean isSelectionMode = false;
    private boolean isAllSelected = false;

    private boolean[] applicationsSelected;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.mainRelativeLayout) RelativeLayout mainRelativeLayout;
    @BindView(R.id.drawerLayout) DrawerLayout mDrawerLayout;
    @BindView(R.id.filterDrawer) RelativeLayout filterDrawer;
    @BindView(R.id.emptyMessageText) TextView emptyMessageText;
    @BindView(R.id.roleSelect) TextView roleSelect;
    @BindView(R.id.lengthSelect) TextView lengthSelect;
    @BindView(R.id.locationSelect) TextView locationSelect;
    @BindView(R.id.minText) TextView minText;
    @BindView(R.id.maxText) TextView maxText;
    @BindView(R.id.salarySelect) CrystalRangeSeekbar salarySelect;
    @BindView(R.id.stageSelect) TextView stageSelect;
    @BindView(R.id.statusSelect) TextView statusSelect;
    @BindView(R.id.prioritySwitch) Switch prioritySwitch;
    @BindView(R.id.filterResultsText) TextView filterResultsText;
    @BindView(R.id.filterApplyButton) Button filterApplyButton;
    MenuItem orderItem;

    private class MinMaxSalary {
        Integer originalMin;
        Integer originalMax;

        private Integer min;
        private Integer max;

        boolean isRangeUpdated;

        MinMaxSalary(Integer min, Integer max) {
            this.min = min;
            this.max = max;

            Double originalMinDouble = Math.floor(min / 1000.0) * 1000;
            Double originalMaxDouble = Math.ceil(max / 1000.0) * 1000;
            this.originalMin = originalMinDouble.intValue();
            this.originalMax = originalMaxDouble.intValue();
        }

        public void setMin(Integer min) {
            this.min = min;
            isRangeUpdated = true;
        }

        public void setMax(Integer max) {
            this.max = max;
            isRangeUpdated = true;
        }
    }

    private MinMaxSalary minMaxSalary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerLayout.setFocusableInTouchMode(false);

        mDataSource = new DataSource(this);
        mDataSource.open();
        mDataSource.seedDatbase();

        //ArrayList of all applications in the database
        applications = mDataSource.getAllApplication();

        filtersCurrentlyApplied = new HashSet<>();

        //list to track which applications have been selected
        selectedApplications = new ArrayList<>();
        //list to store list of applications before deletion
        applicationsBeforeDeletionForUndo = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        //give the recycler adapter the list of all applications
        applicationListRecyclerAdapter = new ApplicationListRecyclerAdapter(this, applications, selectedApplications);
        //set the adapter to the recycler view
        recyclerView.setAdapter(applicationListRecyclerAdapter);

        //sets what is displayed in actionbar in action mode
        actionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.setTitle(selectedApplications.size() + " applications selected");

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
                        //delete all selected applications
                        deleteSelectedApplications();
                        //exit action mode
                        switchActionMode(false);

                        final int oldMinPosition = minMaxSalary.min;
                        final int oldMaxPosition = minMaxSalary.max;

                        setupSalarySeekbarRange(true);

                        Snackbar.make(findViewById(R.id.drawerLayout),
                                R.string.deleted_snackbar_string, Snackbar.LENGTH_LONG)
                                .setAction(R.string.undo_snackbar_string, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        for(Application application : deletedApplicationsForUndo) {
                                            application.setSelected(false);
                                            mDataSource.recreateApplication(application);
                                        }

                                        applications = applicationsBeforeDeletionForUndo;
                                        applicationListRecyclerAdapter.applicationsList = applicationsBeforeDeletionForUndo;

                                        applicationListRecyclerAdapter.notifyDataSetChanged();

                                        displayMessageIfNoApplications(false);

                                        deletedApplicationsForUndo.clear();

                                        minMaxSalary.setMin(oldMinPosition);
                                        minMaxSalary.setMax(oldMaxPosition);
                                        setupSalarySeekbarRange(true);
                                    }
                                })
                                .show();

                        return true;

                    //when the priority action button is pressed in action mode
                    case R.id.action_mode_prioritise:
                        //prioritise all selected applications
                        prioritiseSelectedApplications();
                        //exit action mode
                        switchActionMode(false);
                        return true;

                    //when the priority action button is pressed in action mode
                    case R.id.action_mode_deprioritise:
                        //prioritise all selected applications
                        deprioritiseSelectedApplications();
                        //exit action mode
                        switchActionMode(false);
                        return true;

                    case R.id.action_mode_select_all:
                        selectAllApplications();
                        return true;

                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                isSelectionMode = false;
                isAllSelected = false;
                //unselect all selected applications
                unselectSelectedApplications();
                //update the recycler view
                applicationListRecyclerAdapter.notifyDataSetChanged();
            }
        };

        displayMessageIfNoApplications(false);

        setUpFilterPanel();

        if(savedInstanceState != null) {
            boolean[] applicationsSelected = savedInstanceState.getBooleanArray(SELECTED_APPLICATIONS);

            for (int i = 0; i < applications.size(); i++) {
                Application application = applications.get(i);
                boolean isSelected = applicationsSelected[i];
                application.setSelected(isSelected);

                if(isSelected) {
                    selectedApplications.add(application);
                }
            }

            isSelectionMode = savedInstanceState.getBoolean(SELECTION_MODE);
            switchActionMode(isSelectionMode);

            if(isSelectionMode) {
                applicationListRecyclerAdapter.decideToPrioritiseOrDeprioritiseApplications(selectedApplications);
            }

        }

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
                changeSort(ApplicationTable.COLUMN_MODIFIED_ON, false, item);
                return true;

            case(R.id.action_sort_created_date):
                changeSort(ApplicationTable.COLUMN_CREATED_ON, false, item);
                return true;

            case(R.id.action_sort_company_name):
                changeSort(ApplicationTable.COLUMN_COMPANY_NAME, true, item);
                return true;

            case(R.id.action_sort_role):
                changeSort(ApplicationTable.COLUMN_ROLE, true, item);
                return true;

            case(R.id.action_sort_salary):
                changeSort(ApplicationTable.COLUMN_SALARY, false, item);
                return true;

            case(R.id.action_filter_applications):
                if(mDrawerLayout.isDrawerOpen(filterDrawer)) mDrawerLayout.closeDrawer(filterDrawer);
                else mDrawerLayout.openDrawer(filterDrawer);

                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        applicationsSelected = new boolean[applications.size()];

        for (int i = 0; i < applications.size(); i++) {
            applicationsSelected[i] = applications.get(i).isSelected();
        }

        outState.putBooleanArray(SELECTED_APPLICATIONS, applicationsSelected);

        outState.putBoolean(SELECTION_MODE, isSelectionMode);
    }

    private void setUpFilterPanel() {
        filterSelectedItemsIndexes = new HashMap<>();

        //add all filter types to the map
        for(FilterType filterType : FilterType.values()) {
            filterSelectedItemsIndexes.put(filterType, null);
        }

        roleSelect.setOnClickListener(new FilterDialogOnClickListener(getFragmentManager(),
                mDataSource.getAllRoles(), filterSelectedItemsIndexes, FilterType.ROLE));

        lengthSelect.setOnClickListener(new FilterDialogOnClickListener(getFragmentManager(),
                        mDataSource.getAllLengths(), filterSelectedItemsIndexes, FilterType.LENGTH));

        locationSelect.setOnClickListener(new FilterDialogOnClickListener(getFragmentManager(),
                        mDataSource.getAllLocations(), filterSelectedItemsIndexes, FilterType.LOCATION));

        prioritySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFilterChangeMade = true;
                isFilterPriority = prioritySwitch.isChecked();
                updateFilterPanel();
            }
        });

        stageSelect.setOnClickListener(new FilterDialogOnClickListener(getFragmentManager(),
                        mDataSource.getAllStageNames(), filterSelectedItemsIndexes, FilterType.STAGE));

        prioritySwitch.setChecked(false);

        statusSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                List<Integer> selectedItemsIndexes = filterSelectedItemsIndexes.get(FilterType.STATUS);

                //selectedItemsIndexes is null when nothing initially has been selected
                if(selectedItemsIndexes != null) {
                    boolean[] checkedItems = new boolean[ApplicationStage.Status.values().length];

                    //converts the indexes into boolean array of all items with value true if index in list
                    for (Integer selectedItemIndex : selectedItemsIndexes) {
                        checkedItems[selectedItemIndex] = true;
                    }

                    bundle.putBooleanArray(CHECKED_ITEMS, checkedItems);
                }

                DialogFragment dialogFragment = new StatusFilterDialogFragment();
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getFragmentManager(), "Status Filter Dialog");
            }
        });

        setupSalarySeekbarRange(false);

        updateFilterPanel();
    }

    private void setupSalarySeekbarRange(boolean isApplicationBeingDeleted) {
        salarySelect.setSteps(-1f);
        salarySelect.setGap(0);

        List<Integer> salary = mDataSource.getAllSalary();
        int newMinSalary = 0;
        int newMaxSalary = 0;

        if(!salary.isEmpty()) {
            newMinSalary = salary.get(salary.size() - 1);
            newMaxSalary = salary.get(0);

            if(newMaxSalary == 0) newMaxSalary = 1000;
        }

        if(isApplicationBeingDeleted && minMaxSalary.originalMin != null && minMaxSalary.originalMax != null &&
                newMaxSalary > 0) {
            int currentMinStart;
            int currentMaxEnd;

            int currentMinProgress = minMaxSalary.min;
            int currentMaxProgress = minMaxSalary.max;

            if(currentMinProgress > newMinSalary && currentMinProgress < newMaxSalary) {
                currentMinStart = newMinSalary;
            } else {
                currentMinProgress = newMinSalary;
                currentMinStart = newMinSalary;
            }

            if(currentMaxProgress < newMaxSalary && currentMaxProgress > newMinSalary) {
                currentMaxEnd = newMaxSalary;
            } else {
                currentMaxProgress = newMaxSalary;
                currentMaxEnd = newMaxSalary;
            }

            minMaxSalary = new MinMaxSalary(currentMinStart, currentMaxEnd);
            minMaxSalary.setMin(currentMinProgress);
            minMaxSalary.setMax(currentMaxProgress);
            minMaxSalary.isRangeUpdated = false;

            salarySelect.setMinValue(minMaxSalary.originalMin);
            salarySelect.setMaxValue(minMaxSalary.originalMax);
            salarySelect.setMinStartValue(minMaxSalary.min)
                    .setMaxStartValue(minMaxSalary.max).apply();

            isFilterChangeMade = true;
            updateFilterPanel();
        } else {
            minMaxSalary = new MinMaxSalary(newMinSalary, newMaxSalary);

            salarySelect.setMinValue(minMaxSalary.originalMin);
            salarySelect.setMaxValue(minMaxSalary.originalMax);
            salarySelect.setMinStartValue(minMaxSalary.originalMin)
                    .setMaxStartValue(minMaxSalary.originalMax).apply();
        }

        if(minMaxSalary.originalMax - minMaxSalary.originalMin > 1000) {
            salarySelect.setSteps(1000f);
            salarySelect.setGap(2);
        }

        salarySelect.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                minText.setText(convertToMoneyString(minValue));
                maxText.setText(convertToMoneyString(maxValue));
            }
        });

        // set final value listener
        salarySelect.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                minMaxSalary.setMin(minValue.intValue());
                minMaxSalary.setMax(maxValue.intValue());

                isFilterChangeMade = true;
                updateFilterPanel();
            }
        });
    }

    private String convertToMoneyString(Number value) {
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        return "£" + formatter.format(value);
    }

    /**
     * Stores the selected item's indexes for the particular filter type.
     * @param filterType the filter type that the items selected correspond to
     * @param selectedItemIndexes list of integer indexes of the items selected
     */
    public void onUserSelectValue(FilterType filterType, List<Integer> selectedItemIndexes) {
        if(selectedItemIndexes.isEmpty()) {
            selectedItemIndexes = null;
            filtersCurrentlyApplied.remove(filterType);
        } else {
            filtersCurrentlyApplied.add(filterType);
        }

        filterSelectedItemsIndexes.put(filterType, selectedItemIndexes);

        isFilterChangeMade = true;

        updateFilterPanel();
    }

    private void updateFilterPanel() {
        updateFilterSelectText(FilterType.ROLE, roleSelect);
        updateFilterSelectText(FilterType.LENGTH, lengthSelect);
        updateFilterSelectText(FilterType.LOCATION, locationSelect);
        updateFilterSelectText(FilterType.STAGE, stageSelect);

        if(filterSelectedItemsIndexes.get(FilterType.STATUS) != null &&
                filterSelectedItemsIndexes.get(FilterType.STATUS).size() == 1) {

            ApplicationStage.Status status = getAllStatusFromIndex(
                    filterSelectedItemsIndexes.get(FilterType.STATUS)).get(0);
            statusSelect.setText(status.toString());

            statusSelect.setCompoundDrawablesWithIntrinsicBounds(getResources().getIdentifier("ic_status_" + status.getIconNameText(), "drawable", getPackageName()), 0, 0, 0);
            statusSelect.setCompoundDrawablePadding(16);

        } else {
            updateFilterSelectText(FilterType.STATUS, statusSelect);
            statusSelect.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        filterResultsText.setText("Showing " + applicationListRecyclerAdapter.applicationsList.size() + " Applications");

        if(isFilterChangeMade) filterApplyButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        else filterApplyButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }

    private void updateFilterSelectText(FilterType filterType, TextView selectText) {
        List<Integer> selectedItems = filterSelectedItemsIndexes.get(filterType);
        if(selectedItems != null && selectedItems.size() > 0) {
            selectText.setText(selectedItems.size() + " selected");
        } else if(!filtersCurrentlyApplied.isEmpty() || minMaxSalary.isRangeUpdated ||
                isFilterPriority != null){
            selectText.setText("None selected");
        } else {
            selectText.setText("All " + filterType.getTextPlural());
        }
    }

    public void resetAllFilter(View view) {
        mDrawerLayout.closeDrawer(filterDrawer);

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.areYouSureDialogTitle))
                .setMessage("All selections will be cleared")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //add all filter types to the map
                        for(FilterType filterType : FilterType.values()) {
                            filterSelectedItemsIndexes.put(filterType, null);
                        }

                        prioritySwitch.setChecked(false);
                        isFilterPriority = null;

                        minMaxSalary.isRangeUpdated = false;

                        salarySelect.setMinStartValue(minMaxSalary.originalMin)
                                .setMaxStartValue(minMaxSalary.originalMax)
                                .setGap(10f).apply();

                        filtersCurrentlyApplied.clear();

                        applicationListRecyclerAdapter.applicationsList = applications;
                        applicationListRecyclerAdapter.notifyDataSetChanged();

                        isFilterChangeMade = false;

                        updateFilterPanel();

                        displayMessageIfNoApplications(false);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void applyFilter(View view) {
        if(isFilterChangeMade) {
            List<String> selectedRoles = getAllItemsFromIndex(mDataSource.getAllRoles(),
                    filterSelectedItemsIndexes.get(FilterType.ROLE));

            List<String> selectedLengths = getAllItemsFromIndex(mDataSource.getAllLengths(),
                    filterSelectedItemsIndexes.get(FilterType.LENGTH));

            List<String> selectedLocations = getAllItemsFromIndex(mDataSource.getAllLocations(),
                    filterSelectedItemsIndexes.get(FilterType.LOCATION));

            List<String> selectedStages = getAllItemsFromIndex(mDataSource.getAllStageNames(),
                    filterSelectedItemsIndexes.get(FilterType.STAGE));

            List<ApplicationStage.Status> selectedStatus = getAllStatusFromIndex(
                    filterSelectedItemsIndexes.get(FilterType.STATUS));

            applicationListRecyclerAdapter.applicationsList = filterApplications(selectedRoles,
                    selectedLengths, selectedLocations, selectedStages,
                    selectedStatus);

            applicationListRecyclerAdapter.notifyDataSetChanged();

            isFilterChangeMade = false;

            updateFilterPanel();

            displayMessageIfNoApplications(false);
        }

        mDrawerLayout.closeDrawer(filterDrawer);
    }

    private List<String> getAllItemsFromIndex(List<String> allItems, List<Integer> selectedIndexes) {
        if(selectedIndexes != null) {
            List<String> result = new ArrayList<>();
            for (int i = 0; i < allItems.size(); i++) {
                if (selectedIndexes.contains(i)) {
                    result.add(allItems.get(i));
                }
            }

            return result;
        }
        else return null;
    }

    private List<ApplicationStage.Status> getAllStatusFromIndex(List<Integer> selectedIndexes) {
        ApplicationStage.Status[] statusList = ApplicationStage.Status.values();

        if(selectedIndexes != null) {
            List<ApplicationStage.Status> result = new ArrayList<>();
            for (int i = 0; i < statusList.length; i++) {
                if (selectedIndexes.contains(i)) {
                    result.add(statusList[i]);
                }
            }

            return result;
        }
        else return null;
    }

    private void changeSort(String sortByField, boolean isAscending, MenuItem currentSelectedSortItem) {
        if(currentSelectedSortItem.isChecked()) {
            toggleOrderItemAscendingOrDescending();
            applicationListRecyclerAdapter.reverseOrder();
        } else {
            setOrderItemToAscendingOrDescending(isAscending);
            applicationListRecyclerAdapter.sortApplications(sortByField);
        }

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
        final MenuItem searchItem = menu.findItem(R.id.action_search_applications);
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

    private void setItemsVisibility(final Menu menu, final MenuItem searchMenuItem, final boolean isItemsVisible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            item.setVisible(isItemsVisible);
        }

        if(isItemsVisible) invalidateOptionsMenu();
    }

    /**
     * On click method to create a new Application
     * @param view create button that was clicked
     */
    public void goToCreateApplication(View view) {
        Intent intent = new Intent(getApplicationContext(), ApplicationEditActivity.class);
        intent.putExtra(ApplicationEditActivity.APPLICATION_EDIT_MODE, false);
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
            actionMode = startSupportActionMode(actionModeCallback);
        } else {
            //else if turnOn is false, then exit action mode
            if (actionMode != null) {
                isSelectionMode = false;
                actionMode.finish();
            }
        }
    }

    //this method updates the title in the action bar in action mode, and is called every time application selected
    public void updateActionModeCounter(int counter) {
        if(counter == 1) {
            actionMode.setTitle(counter + " application selected");
        } else {
            actionMode.setTitle(counter + " applications selected");
        }
    }

    //displays message to inform user to add their first application if application list is empty
    public void displayMessageIfNoApplications(boolean isFilteringForSearch) {
        if(applicationListRecyclerAdapter.applicationsList.isEmpty()) {
            emptyMessageText.setVisibility(View.VISIBLE);

            if(isFilteringForSearch) {
                emptyMessageText.setText(getResources().getString(R.string.noMatchFromSearch));
            } else if(!applications.isEmpty()) {
                emptyMessageText.setText(getResources().getString(R.string.noResultsWithFilter));
            } else {
                emptyMessageText.setText(getResources().getString(R.string.addFirstApplication));
            }
        } else {
            emptyMessageText.setVisibility(View.GONE);
        }
    }

    //unselects all the selected applications
    private void unselectSelectedApplications() {
        for(Application application : selectedApplications) {
            application.setSelected(false);
        }

        applicationListRecyclerAdapter.notifyDataSetChanged();

        selectedApplications.clear();
    }

    //deletes all selected applications
    private void deleteSelectedApplications() {
        applicationsBeforeDeletionForUndo = new ArrayList<>(applications);

        //for all selected applications
        for(Application deleteApplication : selectedApplications) {
            //remove application from original unfiltered list
            applications.remove(deleteApplication);

            //remove application from filtered list of applications (allows filter to remain even after deletion)
            applicationListRecyclerAdapter.applicationsList.remove(deleteApplication);

            //delete from database
            mDataSource.deleteApplication(deleteApplication.getApplicationID());
        }
        //update the RecyclerView through the adapter
        applicationListRecyclerAdapter.notifyDataSetChanged();

        deletedApplicationsForUndo = new ArrayList<>(selectedApplications);

        //empty the map holding the selected applications
        selectedApplications.clear();

        displayMessageIfNoApplications(false);
    }

    //prioritises all selected applications
    private void prioritiseSelectedApplications() {
        //for all selected applications
        for(Application application : selectedApplications) {
            application.setSelected(false);
            application.setPriority(true);

            mDataSource.updateApplicationPriority(application);
        }
        //empty the map holding the selected applications
        selectedApplications.clear();
    }

    //deprioritises all selected applications
    private void deprioritiseSelectedApplications() {
        //for all selected applications
        for(Application application : selectedApplications) {
            application.setSelected(false);
            application.setPriority(false);

            mDataSource.updateApplicationPriority(application);
        }
        //empty the map holding the selected applications
        selectedApplications.clear();
    }

    private void selectAllApplications() {
        if(!isAllSelected) {
            applicationListRecyclerAdapter.selectAllApplications();
            isAllSelected = true;
        } else {
            applicationListRecyclerAdapter.deselectAllApplications();
            isAllSelected = false;
        }
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(filterDrawer))
            mDrawerLayout.closeDrawer(filterDrawer);
        else super.onBackPressed();
    }

    private List<Application> filterApplications(List<String> roles,
                                                List<String> lengths,
                                                List<String> locations,
                                                List<String> stages,
                                                List<ApplicationStage.Status> status) {

        List<Application> result = new ArrayList<>();

        for (Application application : applications) {
            if (isFilterPriority != null && isFilterPriority && !application.isPriority()) {
                continue;
            }

            //if no filters have been selected then no filters are applied, so show all applications
            if(!filtersCurrentlyApplied.isEmpty() || isFilterChangeMade ) {
                Application returnedApplication = matchApplication(application, roles, lengths, locations, stages, status);
                if (returnedApplication != null) result.add(returnedApplication);
            } else {
                result.add(application);
            }
        }

        return result;
    }

    private Application matchApplication(Application application,
                                        List<String> roles,
                                        List<String> lengths,
                                        List<String> locations,
                                        List<String> stages,
                                        List<ApplicationStage.Status> statusList) {
        if(statusList != null) {
            boolean statusMatched = false;
            for (ApplicationStage.Status status : statusList) {
                if (application.getCurrentStage().getStatus().equals(status)) {
                    statusMatched = true;
                    break;
                }
            }
            if(!statusMatched) return null;
        }

        if(roles != null) {
            boolean roleMatched = false;
            for (String role : roles) {
                if (application.getRole().equals(role)) {
                    roleMatched = true;
                    break;
                }
            }
            if(!roleMatched) return null;
        }

        if(lengths != null) {
            boolean lengthMatched = false;
            for (String length : lengths) {
                if (application.getLength() != null && application.getLength().equals(length)) {
                    lengthMatched = true;
                    break;
                }
            }
            if(!lengthMatched) return null;
        }

        if(locations != null) {
            boolean locationMatched = false;
            for (String location : locations) {
                if (application.getLocation() != null && application.getLocation().equals(location)) {
                    locationMatched = true;
                    break;
                }
            }
            if(!locationMatched) return null;
        }

        if(stages != null) {
            boolean stageMatched = false;
            for (String stageName : stages) {
                for (ApplicationStage stage : application.getApplicationStages()) {
                    if (stage.getStageName().equals(stageName)) {
                        stageMatched = true;
                        break;
                    }
                }
            }
            if(!stageMatched) return null;
        }

        if (!(minMaxSalary.min <= application.getSalary() && application.getSalary() <= minMaxSalary.max))
            return null;

        return application;
    }

    public void setApplicationList(List<Application> applicationList) {
        applications = applicationList;
    }

}
