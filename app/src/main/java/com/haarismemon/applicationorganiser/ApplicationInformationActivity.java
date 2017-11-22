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

import com.haarismemon.applicationorganiser.adapter.ApplicationInformationAdapter;
import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.database.ApplicationTable;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.model.Application;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This class represents the activity which displays the information of an Application with list of its stages
 * @author HaarisMemon
 */
public class ApplicationInformationActivity extends AppCompatActivity {

    private DataSource mDataSource;
    private Application application = null;
    private boolean isSourceMainActivity;
    private List<ApplicationStage> stages;

    /**
     * StageList adapter of RecylerView for stages in the activity
     */
    ApplicationInformationAdapter adapter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.stageRecyclerView) RecyclerView stageRecyclerView;
    private MenuItem prioritiseItem;
    private MenuItem deprioritiseItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_information);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        //adds a back button to the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getResources().getString(R.string.application));

        mDataSource = new DataSource(this);
        mDataSource.open();
        Intent intent = getIntent();

        isSourceMainActivity = intent.getBooleanExtra(MainActivity.SOURCE, false);

        //application that has the same id that was sent in the intent
        application = mDataSource.getApplication(intent.getLongExtra(ApplicationTable.COLUMN_ID, -1));

        //arraylist of all application stages linked to the application in the database
        stages = new ArrayList<>();
        //add dummy application stage object to be replaced by the application header card view
        stages.add(new ApplicationStage());
        stages.addAll(mDataSource.getAllApplicationStages(application.getApplicationID()));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        stageRecyclerView.setLayoutManager(layoutManager);
        stageRecyclerView.setHasFixedSize(true);

        adapter = new ApplicationInformationAdapter(getApplicationContext(), application, stages, isSourceMainActivity);
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
        getMenuInflater().inflate(R.menu.application_menu, menu);

        prioritiseItem = menu.findItem(R.id.action_mode_prioritise);
        deprioritiseItem = menu.findItem(R.id.action_mode_deprioritise);

        if(application.isPriority()) {
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
            case R.id.action_delete_application:
                //show alert dialog to confirm deletion
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.areYouSureDialogTitle))
                        .setMessage(getResources().getString(R.string.deleteDialogMessage))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDataSource.deleteApplication(application.getApplicationID());

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

            //when the edit application action button is pressed
            case R.id.action_edit_application:
                editApplication(null);
                return true;

            case R.id.action_mode_prioritise:
                prioritiseApplication(true);
                return true;

            case R.id.action_mode_deprioritise:
                prioritiseApplication(false);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void prioritiseApplication(boolean setToPriority) {
        application.setPriority(setToPriority);
        mDataSource.updateApplicationPriority(application);

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
        //send a boolean in the intent, of whether the application is being edited (or created)
        intent.putExtra(ApplicationEditActivity.APPLICATION_EDIT_MODE, false);
        //send the id of the application that stage will belong to, in the intent
        intent.putExtra(ApplicationTable.COLUMN_ID, application.getApplicationID());
        startActivity(intent);
    }

    /**
     * On click method to edit an existing Application
     * @param view edit button that was clicked
     */
    public void editApplication(View view) {
        Intent intent = new Intent(getApplicationContext(), ApplicationEditActivity.class);
        //send a boolean that an application is being edited, in the intent
        intent.putExtra(ApplicationEditActivity.APPLICATION_EDIT_MODE, true);
        //send the id of the application to be edited, in the intent
        intent.putExtra(ApplicationTable.COLUMN_ID, application.getApplicationID());
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
