package com.haarismemon.applicationorganiser;

import android.support.v7.widget.SearchView;

import com.haarismemon.applicationorganiser.adapter.ApplicationListRecyclerAdapter;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.model.Internship;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a custom OnQueryTextListener that filters and updates the Application List as the text changes.
 */

public class MyOnQueryTextListener implements SearchView.OnQueryTextListener {

    private ApplicationListRecyclerAdapter myAdapter;
    private List<Internship> internships;

    public MyOnQueryTextListener(ApplicationListRecyclerAdapter myAdapter) {
        this.myAdapter = myAdapter;
        internships = myAdapter.internshipsList;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchQuery) {
        //remove all excess whitespaces and make the text lowercase
        searchQuery = searchQuery.toLowerCase().trim().replaceAll(" +", " ");
        List<Internship> filteredInternships = new ArrayList<>();

        //loop through all the internships from the recycler view adapter
        for (Internship internship : internships) {
            //boolean to check if internship had already been added to the filtered list
            boolean isAlreadyAdded = false;

            //if the current internship's company name contains the search query, then add to filtered list
            if (internship.getCompanyName().toLowerCase().contains(searchQuery)) {
                filteredInternships.add(internship);
                isAlreadyAdded = true;
            }

            for (ApplicationStage stage : internship.getApplicationStages()) {
                /*if the current stage's name contains the search query, and parent internship not already added
                /then add the parent internship to filtered list */
                if (!isAlreadyAdded && stage.getStageName().toLowerCase().contains(searchQuery)) {
                    filteredInternships.add(internship);
                    isAlreadyAdded = true;
                }
            }

        }

        //update the recycler view
        myAdapter.searchFilter(filteredInternships);

        return true;
    }

}
