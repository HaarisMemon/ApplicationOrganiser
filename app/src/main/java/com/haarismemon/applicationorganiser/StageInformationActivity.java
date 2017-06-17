package com.haarismemon.applicationorganiser;

import android.content.DialogInterface;
import android.content.Intent;
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

public class StageInformationActivity extends AppCompatActivity {

    DataSource mDataSource;
    ApplicationStage stage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_information);

        setTitle("Application Stage");

        mDataSource = new DataSource(this);

        TextView editedText = (TextView) findViewById(R.id.editedDateStageText);
        TextView stageNameText = (TextView) findViewById(R.id.stageNameText);
        TextView isCompletedText = (TextView) findViewById(R.id.isCompletedText);
        TextView isWaitingForResponseText = (TextView) findViewById(R.id.isWaitingForResponseText);
        TextView isSuccessfulText = (TextView) findViewById(R.id.isSuccessfulText);
        TextView dateOfStartText = (TextView) findViewById(R.id.dateOfStartText);
        TextView dateOfCompletionText = (TextView) findViewById(R.id.dateOfCompletionText);
        TextView dateOfReplyText = (TextView) findViewById(R.id.dateOfReplyText);
        TextView stageDescriptionText = (TextView) findViewById(R.id.stageDescriptionText);


        Intent intent = getIntent();

        stage = mDataSource.getApplicationStage(intent.getLongExtra(ApplicationStageTable.COLUMN_ID, -1));

        editedText.setText(getApplicationContext().getString(R.string.editedModified) + " " + stage.getModifiedDate());

        stageNameText.setText(stage.getStageName());
        isCompletedText.setText(stage.isCompleted() ? "Yes" : "No");
        isWaitingForResponseText.setText(stage.isWaitingForResponse() ? "Yes" : "No");
        isSuccessfulText.setText(stage.isSuccessful() ? "Yes" : "No");
        dateOfStartText.setText(stage.getDateOfStart() != null ? stage.getDateOfStart() : "None");
        dateOfCompletionText.setText(stage.getDateOfCompletion() != null ? stage.getDateOfCompletion() : "None");
        dateOfReplyText.setText(stage.getDateOfReply() != null ? stage.getDateOfReply() : "None");
        stageDescriptionText.setText(stage.getDescription() != null ? stage.getDescription() : "No Description");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.application_stage_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_delete_stage:
                new AlertDialog.Builder(this)
                        .setTitle("Are you sure?")
                        .setMessage("This will be permanently deleted.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDataSource.deleteApplicationStage(stage.getStageID());

                                InternshipInformationActivity.arrayAdapter.notifyDataSetChanged();
                                //go back to the application list activity

                                Intent intent = new Intent(getApplicationContext(), InternshipInformationActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra(InternshipTable.COLUMN_ID, stage.getInternshipID());
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void editStage(View view) {
        Intent intent = new Intent(getApplicationContext(), StageEditActivity.class);
        intent.putExtra(StageEditActivity.STAGE_EDIT_MODE, true);
        intent.putExtra(ApplicationStageTable.COLUMN_ID, stage.getStageID());
        startActivity(intent);
    }
}
