package com.memonade.apptracker.listener;

import android.support.v7.widget.SearchView;

import com.memonade.apptracker.adapter.ApplicationListRecyclerAdapter;
import com.memonade.apptracker.model.Application;
import com.memonade.apptracker.model.ApplicationStage;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a custom OnQueryTextListener that filters and updates the Application List as the text changes.
 */

public class MyOnQueryTextListener implements SearchView.OnQueryTextListener {

    private ApplicationListRecyclerAdapter myAdapter;
    private List<Application> applications;

    public MyOnQueryTextListener(ApplicationListRecyclerAdapter myAdapter) {
        this.myAdapter = myAdapter;
        applications = myAdapter.applicationsList;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchQuery) {
        //remove all excess whitespaces and make the text lowercase
        searchQuery = searchQuery.toLowerCase().trim().replaceAll(" +", " ");
        List<Application> filteredApplications = new ArrayList<>();

        //loop through all the applications from the recycler view adapter
        for (Application application : applications) {
            //boolean to check if application had already been added to the filtered list
            boolean isAlreadyAdded = false;

            //if the current application's company name contains the search query, then add to filtered list
            if (application.getCompanyName().toLowerCase().contains(searchQuery)) {
                filteredApplications.add(application);
                isAlreadyAdded = true;
            }

            for (ApplicationStage stage : application.getApplicationStages()) {
                /*if the current stage's name contains the search query, and parent application not already added
                /then add the parent application to filtered list */
                if (!isAlreadyAdded && stage.getStageName().toLowerCase().contains(searchQuery)) {
                    filteredApplications.add(application);
                    isAlreadyAdded = true;
                }
            }

        }

        //update the recycler view
        myAdapter.searchFilter(filteredApplications);

        return true;
    }

}
