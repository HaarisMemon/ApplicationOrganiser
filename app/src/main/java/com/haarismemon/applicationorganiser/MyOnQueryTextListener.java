package com.haarismemon.applicationorganiser;

import android.support.v7.widget.SearchView;

import com.haarismemon.applicationorganiser.adapter.ApplicationListRecyclerAdapter;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.model.Internship;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Haaris on 19/06/2017.
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
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase().trim().replaceAll(" +", " ");
        List<Internship> filteredInternships = new ArrayList<>();

        if(newText.equals("")) {
            filteredInternships = internships;
        } else {
            for (Internship internship : internships) {

                boolean isAlreadyAdded = false;

                if (internship.getCompanyName().toLowerCase().contains(newText)) {
                    filteredInternships.add(internship);
                    isAlreadyAdded = true;
                }

                for (ApplicationStage stage : internship.getApplicationStages()) {
                    if (!isAlreadyAdded && stage.getStageName().toLowerCase().contains(newText)) {
                        filteredInternships.add(internship);
                        isAlreadyAdded = true;
                    }
                }

            }
        }

        myAdapter.searchFilter(filteredInternships);

        return true;
    }

}
