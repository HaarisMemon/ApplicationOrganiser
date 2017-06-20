package com.haarismemon.applicationorganiser;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haarismemon.applicationorganiser.adapter.StageListRecyclerAdapter;
import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.model.Internship;

import java.util.List;

/**
 * This class represents the activity which displays the information of an Internship with list of its stages
 * @author HaarisMemon
 */
public class InternshipInformationActivity extends AppCompatActivity {

    private DataSource mDataSource;
    private Internship internship = null;
    private RecyclerView stageRecyclerView;

    /**
     * StageList adapter of RecylerView for stages in the activity
     */
    public static StageListRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_information);

        //adds a back button to the action bar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        setTitle("Internship");

        mDataSource = new DataSource(this);
        mDataSource.open();
        Intent intent = getIntent();

        //internship that has the same id that was sent in the intent
        internship = mDataSource.getInternship(intent.getLongExtra(InternshipTable.COLUMN_ID, -1));

        //arraylist of all application stages linked to the internship in the database
        List<ApplicationStage> stages = mDataSource.getAllApplicationStages(internship.getInternshipID());

//        TextView editedText = (TextView) findViewById(R.id.editedDateInternshipText);
//        TextView companyNameText = (TextView) findViewById(R.id.companyNameText);
//        TextView roleText = (TextView) findViewById(R.id.roleText);
//        TextView lengthText = (TextView) findViewById(R.id.lengthText);
//        TextView locationText = (TextView) findViewById(R.id.locationText);
//        TextView notesText = (TextView) findViewById(R.id.notesText);

        //display the last time internship or its application stages was modified
//        editedText.setText(getApplicationContext().getString(R.string.editedModified) + " " + internship.getModifiedShortDateTime());

//        companyNameText.setText(internship.getCompanyName() != null ? internship.getCompanyName() : "None");
//        roleText.setText(internship.getRole() != null ? internship.getRole() : "None");
//        lengthText.setText(internship.getLength() != null ? internship.getLength() : "None");
//        locationText.setText(internship.getLocation() != null ? internship.getLocation() : "None");
//        notesText.setText(internship.getNotes() != null ? internship.getNotes() : "No Notes");

        stageRecyclerView = (RecyclerView) findViewById(R.id.stageRecyclerView);
        stageRecyclerView.setNestedScrollingEnabled(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        stageRecyclerView.setLayoutManager(layoutManager);
        stageRecyclerView.setHasFixedSize(true);

        adapter = new StageListRecyclerAdapter(getApplicationContext(), internship, stages);
        stageRecyclerView.setAdapter(adapter);

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
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.internship_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            //when the delete button is pressed in the action bar
            case R.id.action_delete_internship:
                //show alert dialog to confirm deletion
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.deleteDialogTitle))
                        .setMessage(getResources().getString(R.string.deleteDialogMessage))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDataSource.deleteInternship(internship.getInternshipID());
                                ApplicationListActivity.recyclerAdapter.notifyDataSetChanged();

                                //go back to the application list activity
                                Intent intent = new Intent(getApplicationContext(), ApplicationListActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;

            //when back action button pressed in action bar
            case android.R.id.home:
                onBackPressed();
                return true;

            //when the create stage action button is pressed
            case R.id.action_create_stage:
                createStage();
                return true;

            //when the edit internship action button is pressed
            case R.id.action_edit_internship:
                editInternship(null);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Change activity to create new stage
     */
    private void createStage() {
        Intent intent = new Intent(getApplicationContext(), StageEditActivity.class);
        //send a boolean in the intent, of whether the internship is being edited (or created)
        intent.putExtra(InternshipEditActivity.INTERNSHIP_EDIT_MODE, false);
        //send the id of the internship that stage will belong to, in the intent
        intent.putExtra(InternshipTable.COLUMN_ID, internship.getInternshipID());
        startActivity(intent);
    }

    /**
     * On click method to edit an existing Internship
     * @param view edit button that was clicked
     */
    public void editInternship(View view) {
        Intent intent = new Intent(getApplicationContext(), InternshipEditActivity.class);
        //send a boolean that an internship is being edited, in the intent
        intent.putExtra(InternshipEditActivity.INTERNSHIP_EDIT_MODE, true);
        //send the id of the internship to be edited, in the intent
        intent.putExtra(InternshipTable.COLUMN_ID, internship.getInternshipID());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ApplicationListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
