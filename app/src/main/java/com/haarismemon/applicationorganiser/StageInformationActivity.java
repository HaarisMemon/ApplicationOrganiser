package com.haarismemon.applicationorganiser;

import android.app.Activity;
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

import com.haarismemon.applicationorganiser.adapter.StageInformationRecyclerAdapter;
import com.haarismemon.applicationorganiser.database.ApplicationStageTable;
import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.ApplicationStage;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This class represents the activity which displays the information of an Application Stage
 * @author HaarisMemon
 */
public class StageInformationActivity extends AppCompatActivity {

    private DataSource mDataSource;
    private ApplicationStage stage;
    private Intent intent;

    StageInformationRecyclerAdapter stageInformationRecyclerAdapter;
    @BindView(R.id.stageInformationRecyclerView) RecyclerView stageInformationRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_information);

        ButterKnife.bind(this);

        //adds a back button to the action bar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        setTitle("Application Stage");

        mDataSource = new DataSource(this);
        intent = getIntent();
        //application stage that has the same id that was sent in the intent
        stage = mDataSource.getApplicationStage(intent.getLongExtra(ApplicationStageTable.COLUMN_ID, -1));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        stageInformationRecyclerView.setLayoutManager(layoutManager);
        stageInformationRecyclerView.setHasFixedSize(true);
        stageInformationRecyclerAdapter = new StageInformationRecyclerAdapter(getApplicationContext(), stage);
        stageInformationRecyclerView.setAdapter(stageInformationRecyclerAdapter);

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
        getMenuInflater().inflate(R.menu.application_stage_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //when the delete button is pressed in the action bar
        switch(item.getItemId()) {
            case R.id.action_delete_stage:
                //show alert dialog to confirm deletion
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.deleteDialogTitle))
                        .setMessage(getResources().getString(R.string.deleteDialogMessage))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDataSource.deleteApplicationStage(stage.getStageID());

                                //go back to the internship information activity
                                Intent intent = new Intent(getApplicationContext(), InternshipInformationActivity.class);
                                //send the ID of the internship this stage belongs to, in the intent
                                intent.putExtra(InternshipTable.COLUMN_ID, stage.getInternshipID());
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;

            //when back button pressed in action bar
            case android.R.id.home:
                onBackPressed();
                return true;

            //when the edit application stage action button is pressed
            case R.id.action_edit_stage:
                editStage(null);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * On click method to edit an existing Application Stage
     * @param view edit button that was clicked
     */
    public void editStage(View view) {
        Intent intent = new Intent(getApplicationContext(), StageEditActivity.class);
        //send a boolean that an application stage is being edited, in the intent
        intent.putExtra(StageEditActivity.STAGE_EDIT_MODE, true);
        //send the id of the application stage to be edited, in the intent
        intent.putExtra(ApplicationStageTable.COLUMN_ID, stage.getStageID());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        Intent backIntent = new Intent(getApplicationContext(), InternshipInformationActivity.class);
        backIntent.putExtra(InternshipTable.COLUMN_ID, stage.getInternshipID());
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        boolean isSourceMainActivity = getIntent().getBooleanExtra(MainActivity.SOURCE, false);
        if(isSourceMainActivity) {
            backIntent.putExtra(MainActivity.SOURCE, true);
        }

        startActivity(backIntent);
    }
}
