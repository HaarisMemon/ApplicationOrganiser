package com.haarismemon.applicationorganiser;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.haarismemon.applicationorganiser.adapter.ApplicationListRecyclerAdapter;
import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.Internship;

import java.util.List;


/**
 * This class represents the activity which displays the list of all Internships
 * @author HaarisMemon
 */
public class ApplicationListActivity extends AppCompatActivity {

    private DataSource mDataSource;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    /**
     * RecyclerAdapter of RecyclerView for internships in the activity
     */
    public static ApplicationListRecyclerAdapter recyclerAdapter;
    private List<Internship> internships;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_list);

        //adds a back button to the action bar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        setTitle("All Applications");

        mDataSource = new DataSource(this);
        mDataSource.open();
        mDataSource.seedDatbase();

        //ArrayList of all internships in the database
        internships = mDataSource.getAllInternship();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerAdapter = new ApplicationListRecyclerAdapter(getApplicationContext(), internships);
        recyclerView.setAdapter(recyclerAdapter);

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
        getMenuInflater().inflate(R.menu.application_list_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search_application_list);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new MyOnQueryTextListener(recyclerAdapter));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            //when back button in action bar is clicked
            case android.R.id.home:
                onBackPressed();
                return true;

            //when search button pressed
            case R.id.action_search_application_list:
                return true;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
