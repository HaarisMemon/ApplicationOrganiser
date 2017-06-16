package com.haarismemon.applicationorganiser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.haarismemon.applicationorganiser.database.ApplicationStageTable;
import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.model.ApplicationStage;

public class StageInformationActivity extends AppCompatActivity {

    DataSource mDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_information);

        mDataSource = new DataSource(this);

        TextView stageNameText = (TextView) findViewById(R.id.stageNameText);
        TextView isCompletedText = (TextView) findViewById(R.id.isCompletedText);
        TextView isWaitingForResponseText = (TextView) findViewById(R.id.isWaitingForResponseText);
        TextView isSuccessfulText = (TextView) findViewById(R.id.isSuccessfulText);
        TextView dateOfStartText = (TextView) findViewById(R.id.dateOfStartText);
        TextView dateOfCompletionText = (TextView) findViewById(R.id.dateOfCompletionText);
        TextView dateOfReplyText = (TextView) findViewById(R.id.dateOfReplyText);
        TextView stageDescriptionText = (TextView) findViewById(R.id.stageDescriptionText);


        Intent intent = getIntent();

        ApplicationStage stage = mDataSource.getApplicationStage(intent.getLongExtra(ApplicationStageTable.COLUMN_ID, -1));

        stageNameText.setText(stage.getStageName());
        isCompletedText.setText(stage.isCompleted() ? "Yes" : "No");
        isWaitingForResponseText.setText(stage.isWaitingForResponse() ? "Yes" : "No");
        isSuccessfulText.setText(stage.isSuccessful() ? "Yes" : "No");
        dateOfStartText.setText(stage.getDateOfStart() != null ? stage.getDateOfStart() : "None");
        dateOfCompletionText.setText(stage.getDateOfCompletion() != null ? stage.getDateOfCompletion() : "None");
        dateOfReplyText.setText(stage.getDateOfReply() != null ? stage.getDateOfReply() : "None");
        stageDescriptionText.setText(stage.getDescription() != null ? stage.getDescription() : "No Description");

    }
}
