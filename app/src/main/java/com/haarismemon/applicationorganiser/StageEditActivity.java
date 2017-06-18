package com.haarismemon.applicationorganiser;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.haarismemon.applicationorganiser.database.ApplicationStageTable;
import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.ApplicationStage;

import java.util.Calendar;

/**
 * This class represents the activity to edit an Application Stage
 * @author HaarisMemon
 */
public class StageEditActivity extends AppCompatActivity {

    /**
     * string constant used as a key when passing the isEditMode for an application stage boolean in the intent
     */
    public static final String STAGE_EDIT_MODE = "STAGE_EDIT_MODE";
    /**
     * String constant used as a key for the date button, to check whether the date has already been picked.
     * If picked then when button clicked again, the date shown in the date picker dialog is the date they earlier picked.
     * Otherwise, the date in the date picker dialog is today's date.
     */
    public static final String DATE_PICKED = "DATE_PICKED";

    private DataSource mDataSource;
    private boolean isEditMode;
    private ApplicationStage stage;
    private long parentInternshipID;

    private DatePickerDialog.OnDateSetListener mDataSetListener;
    private EditText clickedDateEditText = null;
    private AutoCompleteTextView stageNameEditText;
    private TextInputEditText descriptionEditText;
    private RadioButton yesComplete;
    private RadioButton noComplete;
    private RadioButton yesWaiting;
    private RadioButton noWaiting;
    private RadioButton yesSuccessful;
    private RadioButton noSuccessful;
    private EditText startDateButton;
    private EditText completeDateButton;
    private EditText replyDateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_edit);

        //adds a back button to the action bar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        mDataSource = new DataSource(this);
        //checks intent to see if it is edit or creation mode
        isEditMode = intent.getBooleanExtra(STAGE_EDIT_MODE, false);

        //application stage that has the same id that was sent in the intent
        stage = mDataSource.getApplicationStage(intent.getLongExtra(ApplicationStageTable.COLUMN_ID, -1L));
        //store the internship id that the stage belongs to
        parentInternshipID = intent.getLongExtra(InternshipTable.COLUMN_ID, -1L);

        stageNameEditText = (AutoCompleteTextView) findViewById(R.id.stageNameEditText);
        descriptionEditText = (TextInputEditText) findViewById(R.id.descriptionStageEditText);

        yesComplete = (RadioButton) findViewById(R.id.yesCompletedRadio);
        noComplete = (RadioButton) findViewById(R.id.noCompletedRadio);
        yesWaiting = (RadioButton) findViewById(R.id.yesWaitingRadio);
        noWaiting = (RadioButton) findViewById(R.id.noWaitingRadio);
        yesSuccessful = (RadioButton) findViewById(R.id.yesSuccessfulRadio);
        noSuccessful = (RadioButton) findViewById(R.id.noSuccessfulRadio);

        startDateButton = (EditText) findViewById(R.id.startDateEditText);
        completeDateButton = (EditText) findViewById(R.id.completionDateEditText);
        replyDateButton = (EditText) findViewById(R.id.replyDateEditText);

        ArrayAdapter<String> stageNameAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, ApplicationStage.defaultApplicationStageNames);

        stageNameEditText.setAdapter(stageNameAdapter);
        stageNameEditText.setThreshold(1);

        //if the date is not null and the date is picked, then set the DATE_PICKED tag
        if(stage.getDateOfStart() != null && startDateButton.getText().toString().contains("/")) {
            startDateButton.setTag(DATE_PICKED);
        }
        if(stage.getDateOfCompletion() != null && completeDateButton.getText().toString().contains("/")) {
            completeDateButton.setTag(DATE_PICKED);
        }
        if(stage.getDateOfReply() != null && replyDateButton.getText().toString().contains("/")) {
            replyDateButton.setTag(DATE_PICKED);
        }

        //used by date picker dialog to store the date picked in right format
        mDataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //add 1 to the month, as month start with 0
                month = month + 1;

                //if month is one digit, add 0 to the front
                String monthOfYear = Integer.toString(month);
                monthOfYear = (monthOfYear.length() == 1) ? monthOfYear = "0" + monthOfYear : monthOfYear;

                //if day is one digit, add 0 to the front
                String dayOfMonth = Integer.toString(day);
                dayOfMonth = (dayOfMonth.length() == 1) ? dayOfMonth = "0" + dayOfMonth : dayOfMonth;

                String date = dayOfMonth + "/" + monthOfYear + "/" + year;
                clickedDateEditText.setText(date);
                //set the date button to being picked
                clickedDateEditText.setTag(DATE_PICKED);
            }
        };

        //if editing application stage then display all existing application stage information
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
            //when save button pressed
            case R.id.action_save_stage:
                saveStage();
                return true;

            //when back button pressed in action bar
            case android.R.id.home:
                onBackPressed();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * On click method to save an Application Stage
     * @param view save button that was clicked
     */
    public void saveButton(View view) {
        saveStage();
    }

    /**
     * Saves the current application stage
     */
    private void saveStage() {
        ApplicationStage newStage = null;

        //if editing application stage then use existing stage with existing ID, otherwise create new one
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

        if(startDateButton.getText().toString().contains("/")) newStage.setDateOfStart(startDateButton.getText().toString());
        else newStage.setDateOfStart(null);

        if(completeDateButton.getText().toString().contains("/")) newStage.setDateOfCompletion(completeDateButton.getText().toString());
        else newStage.setDateOfCompletion(null);

        if(replyDateButton.getText().toString().contains("/")) newStage.setDateOfReply(replyDateButton.getText().toString());
        else newStage.setDateOfReply(null);

        newStage.setDescription(descriptionEditText.getText().toString());

        //if editing internship then update it, else create a new one in database
        if(isEditMode) {
            mDataSource.updateApplicationStage(newStage);
        } else {
            mDataSource.createApplicationStage(newStage, parentInternshipID);
        }

        Intent intent = new Intent(getApplicationContext(), StageInformationActivity.class);
        //send the stage ID, in the intent
        intent.putExtra(ApplicationStageTable.COLUMN_ID, newStage.getStageID());
        startActivity(intent);

    }

    /**
     * On click method to pick a date by calling the date picker dialog
     * @param view date button that was clicked
     */
    public void pickDate(View view) {
        //set the date button to which button that was clicked
        if(view.getId() == R.id.startDateEditText) {
            clickedDateEditText = (EditText) findViewById(R.id.startDateEditText);
        } else if(view.getId() == R.id.completionDateEditText) {
            clickedDateEditText = (EditText) findViewById(R.id.completionDateEditText);
        } else if(view.getId() == R.id.replyDateEditText) {
            clickedDateEditText = (EditText) findViewById(R.id.replyDateEditText);
        } else {
            clickedDateEditText = null;
        }

        //if date button was found
        if(clickedDateEditText != null) {
            int day;
            int month;
            int year;

            /*  if the date button has tag of DATE_PICKED then set the date on dialog to date picked earlier,
                otherwise display todays date on the dialog  */
            if(clickedDateEditText.getTag() != null && ((String) clickedDateEditText.getTag()).equals(DATE_PICKED)) {
                String dates[] = clickedDateEditText.getText().toString().split("/");

                day = Integer.parseInt(dates[0]);
                //minus 1 to get the month index
                month = Integer.parseInt(dates[1]) - 1;
                year = Integer.parseInt(dates[2]);

            } else {
                //get todays date
                Calendar cal  = Calendar.getInstance();

                day = cal.get(Calendar.DAY_OF_MONTH);
                month = cal.get(Calendar.MONTH);
                year = cal.get(Calendar.YEAR);
            }

            //set the dialog with the date and show it
            DatePickerDialog dialog = new DatePickerDialog(StageEditActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    mDataSetListener, year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } else {
            Toast.makeText(this, "Button not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        //show alert dialog to confirm save
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.saveDialogTitle))
                .setMessage(getResources().getString(R.string.saveDialogMessage))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveStage();
                        StageEditActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StageEditActivity.super.onBackPressed();
                    }
                });
        saveDialog.show();
    }
}
