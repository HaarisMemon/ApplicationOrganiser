package com.haarismemon.applicationorganiser;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.haarismemon.applicationorganiser.database.ApplicationStageTable;
import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.ApplicationStage;

import java.util.Calendar;

public class StageEditActivity extends AppCompatActivity {

    public static final String STAGE_EDIT_MODE = "STAGE_EDIT_MODE";
    public static final String DATE_SELECTED = "DATE_SELECTED";

    private DataSource mDataSource;
    private boolean isEditMode;
    private ApplicationStage stage;
    private long parentInternshipID;

    private DatePickerDialog.OnDateSetListener mDataSetListener;
    private Button dateButton;
    private TextInputEditText stageNameEditText;
    private TextInputEditText descriptionEditText;
    private RadioButton yesComplete;
    private RadioButton noComplete;
    private RadioButton yesWaiting;
    private RadioButton noWaiting;
    private RadioButton yesSuccessful;
    private RadioButton noSuccessful;
    private Button startDateButton;
    private Button completeDateButton;
    private Button replyDateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_edit);

        Intent intent = getIntent();

        isEditMode = intent.getBooleanExtra(STAGE_EDIT_MODE, false);
        mDataSource = new DataSource(this);
        stage = mDataSource.getApplicationStage(intent.getLongExtra(ApplicationStageTable.COLUMN_ID, -1L));
        parentInternshipID = intent.getLongExtra(InternshipTable.COLUMN_ID, -1L);

        dateButton = (Button) findViewById(R.id.startDateButton);
        stageNameEditText = (TextInputEditText) findViewById(R.id.stageNameEditText);
        descriptionEditText = (TextInputEditText) findViewById(R.id.descriptionStageEditText);

        yesComplete = (RadioButton) findViewById(R.id.yesCompletedRadio);
        noComplete = (RadioButton) findViewById(R.id.noCompletedRadio);
        yesWaiting = (RadioButton) findViewById(R.id.yesWaitingRadio);
        noWaiting = (RadioButton) findViewById(R.id.noWaitingRadio);
        yesSuccessful = (RadioButton) findViewById(R.id.yesSuccessfulRadio);
        noSuccessful = (RadioButton) findViewById(R.id.noSuccessfulRadio);

        startDateButton = (Button) findViewById(R.id.startDateButton);
        completeDateButton = (Button) findViewById(R.id.completionDateButton);
        replyDateButton = (Button) findViewById(R.id.replyDateButton);

        if(stage.getDateOfStart() != null && startDateButton.getText().toString().contains("/")) {
            startDateButton.setTag(DATE_SELECTED);
        }
        if(stage.getDateOfCompletion() != null && completeDateButton.getText().toString().contains("/")) {
            completeDateButton.setTag(DATE_SELECTED);
        }
        if(stage.getDateOfReply() != null && replyDateButton.getText().toString().contains("/")) {
            replyDateButton.setTag(DATE_SELECTED);
        }

        mDataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String monthOfYear = Integer.toString(month);
                monthOfYear = (monthOfYear.length() == 1) ? monthOfYear = "0" + monthOfYear : monthOfYear;

                String dayOfMonth = Integer.toString(day);
                dayOfMonth = (dayOfMonth.length() == 1) ? dayOfMonth = "0" + dayOfMonth : dayOfMonth;

                String date = dayOfMonth + "/" + monthOfYear + "/" + year;
                dateButton.setText(date);
                dateButton.setTag(DATE_SELECTED);
            }
        };

        if(isEditMode) {
            setTitle("Edit Stage");

            stageNameEditText.setText(stage.getStageName());

            if(stage.isCompleted()) {
                yesComplete.setChecked(true);
            }
            else {
                noComplete.setChecked(true);
            }

            if(stage.isWaitingForResponse()) {
                yesWaiting.setChecked(true);
            }
            else {
                noWaiting.setChecked(true);
            }

            if(stage.isSuccessful()) {
                yesSuccessful.setChecked(true);
            }
            else {
                noSuccessful.setChecked(true);
            }

            if(stage.getDateOfStart() != null) {
                startDateButton.setText(stage.getDateOfStart());
            }
            if(stage.getDateOfCompletion() != null) {
                completeDateButton.setText(stage.getDateOfCompletion());
            }
            if(stage.getDateOfReply() != null) {
                replyDateButton.setText(stage.getDateOfReply());
            }

            descriptionEditText.setText(stage.getDescription());

        } else {
            setTitle("New Stage");

            noComplete.setChecked(true);
            noWaiting.setChecked(true);
            noSuccessful.setChecked(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stage_edit_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            case R.id.action_save_stage:
                saveStage();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void saveButton(View view) {
        saveStage();
    }

    private void saveStage() {
        ApplicationStage newStage = null;

        if(isEditMode) {
            newStage = stage;
        } else {
            newStage = new ApplicationStage();
        }

        newStage.setStageName(stageNameEditText.getText().toString());

        if(yesComplete.isChecked()) newStage.setCompleted(true);
        else newStage.setCompleted(false);

        if(yesWaiting.isChecked()) newStage.setWaitingForResponse(true);
        else newStage.setWaitingForResponse(false);

        if(yesSuccessful.isChecked()) newStage.setSuccessful(true);
        else newStage.setSuccessful(false);

        newStage.setDateOfStart(startDateButton.getText().toString());
        newStage.setDateOfCompletion(completeDateButton.getText().toString());
        newStage.setDateOfReply(replyDateButton.getText().toString());

        newStage.setDescription(descriptionEditText.getText().toString());

        Intent intent = new Intent(getApplicationContext(), StageInformationActivity.class);

        if(isEditMode) {
            mDataSource.updateApplicationStage(newStage);
        } else {
            mDataSource.createApplicationStage(newStage, parentInternshipID);
        }

        intent.putExtra(ApplicationStageTable.COLUMN_ID, newStage.getStageID());
        startActivity(intent);

    }

    public void pickDate(View view) {

        if(view.getId() == R.id.startDateButton) {
            dateButton = (Button) findViewById(R.id.startDateButton);
        } else if(view.getId() == R.id.completionDateButton) {
            dateButton = (Button) findViewById(R.id.completionDateButton);
        } else if(view.getId() == R.id.replyDateButton) {
            dateButton = (Button) findViewById(R.id.replyDateButton);
        } else {
            dateButton = null;
        }

        if(dateButton != null) {
            int day;
            int month;
            int year;

            if(dateButton.getTag() != null && ((String) dateButton.getTag()).equals(DATE_SELECTED)) {
                String dates[] = dateButton.getText().toString().split("/");

                day = Integer.parseInt(dates[0]);
                month = Integer.parseInt(dates[1]) - 1;
                year = Integer.parseInt(dates[2]);

                if(month == -1) {
                    month = 11;
                    --year;
                }


            } else {
                Calendar cal  = Calendar.getInstance();

                day = cal.get(Calendar.DAY_OF_MONTH);
                month = cal.get(Calendar.MONTH);
                year = cal.get(Calendar.YEAR);
            }

            DatePickerDialog dialog = new DatePickerDialog(StageEditActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    mDataSetListener, year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } else {
            Toast.makeText(this, "Button not found", Toast.LENGTH_SHORT).show();
        }
    }



}
