package com.haarismemon.applicationorganiser;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.haarismemon.applicationorganiser.adapter.InternshipInformationAdapter;
import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.model.Internship;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This class represents the activity which displays the information of an Internship with list of its stages
 * @author HaarisMemon
 */
public class InternshipInformationActivity extends AppCompatActivity {

    private DataSource mDataSource;
    private Internship internship = null;
    private boolean isSourceMainActivity;
    private List<ApplicationStage> stages;

    /**
     * StageList adapter of RecylerView for stages in the activity
     */
    InternshipInformationAdapter adapter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.stageRecyclerView) RecyclerView stageRecyclerView;
    private MenuItem prioritiseItem;
    private MenuItem deprioritiseItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_information);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        //adds a back button to the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Internship");

        mDataSource = new DataSource(this);
        mDataSource.open();
        Intent intent = getIntent();

        isSourceMainActivity = intent.getBooleanExtra(MainActivity.SOURCE, false);

        //internship that has the same id that was sent in the intent
        internship = mDataSource.getInternship(intent.getLongExtra(InternshipTable.COLUMN_ID, -1));

        //arraylist of all application stages linked to the internship in the database
        stages = new ArrayList<>();
        //add dummy application stage object to be replaced by the internship header card view
        stages.add(new ApplicationStage());
        stages.addAll(mDataSource.getAllApplicationStages(internship.getInternshipID()));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        stageRecyclerView.setLayoutManager(layoutManager);
        stageRecyclerView.setHasFixedSize(true);

        adapter = new InternshipInformationAdapter(getApplicationContext(), internship, stages, isSourceMainActivity);
        stageRecyclerView.setAdapter(adapter);

        displayMessageIfNoStages();

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

        prioritiseItem = menu.findItem(R.id.action_mode_prioritise);
        deprioritiseItem = menu.findItem(R.id.action_mode_deprioritise);

        if(internship.isPriority()) {
            prioritiseItem.setVisible(false);
            deprioritiseItem.setVisible(true);
        } else {
            prioritiseItem.setVisible(true);
            deprioritiseItem.setVisible(false);
        }

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

                                //go back to the application list activity
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

            case R.id.action_mode_prioritise:
                prioritiseInternship(true);
                return true;

            case R.id.action_mode_deprioritise:
                prioritiseInternship(false);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void prioritiseInternship(boolean setToPriority) {
        internship.setPriority(setToPriority);
        mDataSource.updateInternshipPriority(internship);

        prioritiseItem.setVisible(!setToPriority);
        deprioritiseItem.setVisible(setToPriority);

        getIntent().putExtra(MainActivity.SOURCE, false);

        recreate();
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
        //if last activity was the main activity then do normal back press to keep the previous activity state
        if(isSourceMainActivity) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    //displays message to inform user to add their first stage if stage list is empty
    private void displayMessageIfNoStages() {
        TextView messageWhenEmpty = (TextView) findViewById(R.id.addStageMessage);
        if(stages.size() <= 1) {
            messageWhenEmpty.setVisibility(View.VISIBLE);
        } else {
            messageWhenEmpty.setVisibility(View.INVISIBLE);
        }
    }

}
