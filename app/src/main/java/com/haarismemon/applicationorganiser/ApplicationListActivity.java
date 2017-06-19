package com.haarismemon.applicationorganiser;

import android.app.SearchManager;
import android.content.Context;
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
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.haarismemon.applicationorganiser.adapter.ApplicationListRecyclerAdapter;
import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.model.Internship;

import java.lang.reflect.Field;
import java.util.List;


/**
 * This class represents the activity which displays the list of all Internships
 * @author HaarisMemon
 */
public class ApplicationListActivity extends AppCompatActivity {

    private DataSource mDataSource;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Internship> internships;
    private Intent intent;

    /**
     * RecyclerAdapter of RecyclerView for internships in the activity
     */
    public static ApplicationListRecyclerAdapter recyclerAdapter;
    /**
     * a key used when passing boolean to the intent to this activity to check if search needs to be performed
     */
    public static final String SEARCH_FROM_MAIN = "SEARCH_FROM_MAIN";


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
        intent = getIntent();

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

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.onActionViewExpanded();

        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(intent.getBooleanExtra(SEARCH_FROM_MAIN, false)) {
            searchItem.expandActionView();
        }

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
