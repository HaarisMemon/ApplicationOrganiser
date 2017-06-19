package com.haarismemon.applicationorganiser;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import static com.haarismemon.applicationorganiser.ApplicationListActivity.recyclerAdapter;

/**
 * This class represents the main activity which first opens when the app is launched
 * @author HaarisMemon
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button allApplicationsButton = (Button) findViewById(R.id.allApplicationsButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            case R.id.action_search_main:
                Intent intent = new Intent(getApplicationContext(), ApplicationListActivity.class);
                intent.putExtra(ApplicationListActivity.SEARCH_FROM_MAIN, true);
                startActivity(intent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * On click method to view the Applications List
     * @param view button that was clicked
     */
    public void goToAllApplications(View view) {
        Intent intent = new Intent(getApplicationContext(), ApplicationListActivity.class);
        startActivity(intent);
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
}
