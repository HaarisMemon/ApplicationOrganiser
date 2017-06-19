package com.haarismemon.applicationorganiser;

import android.support.v7.widget.SearchView;

import com.haarismemon.applicationorganiser.adapter.ApplicationListRecyclerAdapter;
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

        newText.toLowerCase();
        ArrayList<Internship> filteredInternships = new ArrayList<>();

        for(Internship internship : internships) {

            if(internship.getCompanyName().toLowerCase().contains(newText)) {
                filteredInternships.add(internship);
            }

        }

        myAdapter.searchFilter(filteredInternships);

        return true;
    }

}
