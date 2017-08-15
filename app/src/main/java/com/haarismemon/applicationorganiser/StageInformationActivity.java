package com.haarismemon.applicationorganiser;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.haarismemon.applicationorganiser.database.ApplicationStageTable;
import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.ApplicationStage;

/**
 * This class represents the activity which displays the information of an Application Stage
 * @author HaarisMemon
 */
public class StageInformationActivity extends AppCompatActivity {

    private DataSource mDataSource;
    private ApplicationStage stage;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_information);

        //adds a back button to the action bar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        setTitle("Application Stage");

        mDataSource = new DataSource(this);
        intent = getIntent();
        //application stage that has the same id that was sent in the intent
        stage = mDataSource.getApplicationStage(intent.getLongExtra(ApplicationStageTable.COLUMN_ID, -1));

        TextView editedText = (TextView) findViewById(R.id.editedDateStageText);
        TextView stageNameText = (TextView) findViewById(R.id.stageNameText);
        TextView currentStatusText = (TextView) findViewById(R.id.currentStatusText);
        TextView isCompletedText = (TextView) findViewById(R.id.isCompletedText);
        TextView isWaitingForResponseText = (TextView) findViewById(R.id.isWaitingForResponseText);
        TextView isSuccessfulText = (TextView) findViewById(R.id.isSuccessfulText);
        TextView dateOfStartText = (TextView) findViewById(R.id.dateOfStartText);
        TextView dateOfCompletionText = (TextView) findViewById(R.id.dateOfCompletionText);
        TextView dateOfReplyText = (TextView) findViewById(R.id.dateOfReplyText);
        TextView stageDescriptionText = (TextView) findViewById(R.id.stageNotesText);

        editedText.setText(getApplicationContext().getString(R.string.editedModified) + " " + stage.getModifiedShortDateTime());

        stageNameText.setText(stage.getStageName() != null ? stage.getStageName() : "No Company Name");
        currentStatusText.setText(stage.getCurrentStatus().toString());
        isCompletedText.setText(stage.isCompleted() ? "Yes" : "No");
        isWaitingForResponseText.setText(stage.isWaitingForResponse() ? "Yes" : "No");
        isSuccessfulText.setText(stage.isSuccessful() ? "Yes" : "No");
        dateOfStartText.setText(stage.getDateOfStart() != null ? stage.getDateOfStart() : "No Start Date");
        dateOfCompletionText.setText(stage.getDateOfCompletion() != null ? stage.getDateOfCompletion() : "No Complete Date");
        dateOfReplyText.setText(stage.getDateOfReply() != null ? stage.getDateOfReply() : "No Reply Date");
        stageDescriptionText.setText(stage.getNotes() != null ? stage.getNotes() : "No Notes");

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
                                InternshipInformationActivity.adapter.notifyDataSetChanged();

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
        startActivity(backIntent);
    }
}
