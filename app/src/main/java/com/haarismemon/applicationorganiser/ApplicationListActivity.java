package com.haarismemon.applicationorganiser;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

    /**
     * ArrayAdapter of Listview for internships in the activity
     */
    public static ArrayAdapter<Internship> arrayAdapter;

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

        ListView listView = (ListView) findViewById(R.id.listView);

        //arraylist of all internships in the database
        final List<Internship> internships = mDataSource.getAllInternship();

        arrayAdapter = new ArrayAdapter<Internship>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, internships);

        listView.setAdapter(arrayAdapter);

        //go to Internship Information when item in Applications List is clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), InternshipInformationActivity.class);
                //send the ID of the Internship you want to see information of
                intent.putExtra(InternshipTable.COLUMN_ID, internships.get(i).getInternshipID());
                startActivity(intent);
            }
        });

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            //when back button in action bar is clicked
            case android.R.id.home:
                onBackPressed();
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
