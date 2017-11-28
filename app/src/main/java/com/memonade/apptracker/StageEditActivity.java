package com.memonade.apptracker;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.memonade.apptracker.database.ApplicationTable;
import com.memonade.apptracker.database.DataSource;
import com.memonade.apptracker.database.StageTable;
import com.memonade.apptracker.model.Stage;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.memonade.apptracker.model.Stage.defaultStageNames;

/**
 * This class represents the activity to edit an Stage
 * @author HaarisMemon
 */
public class StageEditActivity extends AppCompatActivity implements TextWatcher {

    /**
     * string constant used as a key when passing the isEditMode for an stage boolean in the intent
     */
    public static final String STAGE_EDIT_MODE = "STAGE_EDIT_MODE";
    /**
     * String constant used as a key for the date button, to check whether the date has already been picked.
     * If picked then when button clicked again, the date shown in the date picker dialog is the date they earlier picked.
     * Otherwise, the date in the date picker dialog is today's date.
     */
    private static final String DATE_PICKED = "DATE_PICKED";

    private DataSource mDataSource;
    private Stage stage;
    private boolean isEditMode;
    private long parentApplicationID;
    private boolean isChangeMade;

    private DatePickerDialog.OnDateSetListener mDataSetListener;
    private EditText clickedDateEditText = null;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.stageNameEditText) AutoCompleteTextView stageNameEditText;
    @BindView(R.id.notesStageEditText) TextInputEditText notesStageEditText;
    @BindView(R.id.yesCompletedRadio) RadioButton yesComplete;
    @BindView(R.id.noCompletedRadio) RadioButton noComplete;
    @BindView(R.id.yesWaitingRadio) RadioButton yesWaiting;
    @BindView(R.id.noWaitingRadio) RadioButton noWaiting;
    @BindView(R.id.yesSuccessfulRadio) RadioButton yesSuccessful;
    @BindView(R.id.noSuccessfulRadio) RadioButton noSuccessful;
    @BindView(R.id.waitingRadioGroup) RadioGroup waitingRadioGroup;
    @BindView(R.id.successfulRadioGroup) RadioGroup successfulRadioGroup;
    @BindView(R.id.waitingText) TextView waitingText;
    @BindView(R.id.successfulText) TextView successfulText;
    @BindView(R.id.deadlineDateText) TextView deadlineDateText;
    @BindView(R.id.startDateText) TextView startDateText;
    @BindView(R.id.completionDateText) TextView completionDateText;
    @BindView(R.id.replyDateText) TextView replyDateText;
    @BindView(R.id.deadlineDateEditText) EditText deadlineDateEditText;
    @BindView(R.id.startDateEditText) EditText startDateEditText;
    @BindView(R.id.completionDateEditText) EditText completionDateEditText;
    @BindView(R.id.replyDateEditText) EditText replyDateEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_edit);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        //adds a back button to the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        mDataSource = new DataSource(this);
        //checks intent to see if it is edit or creation mode
        isEditMode = intent.getBooleanExtra(STAGE_EDIT_MODE, false);

        //stage that has the same id that was sent in the intent
        stage = mDataSource.getStage(intent.getLongExtra(StageTable.COLUMN_ID, -1L));
        //store the application id that the stage belongs to
        parentApplicationID = intent.getLongExtra(ApplicationTable.APPLICATION_ID, -1L);

        ArrayAdapter<String> stageNameAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, mDataSource.getAllStageNames());

        stageNameEditText.setAdapter(stageNameAdapter);
        stageNameEditText.setThreshold(0);
        stageNameEditText.clearFocus();

        //if the date is not null and the date is picked, then set the DATE_PICKED tag
        if(stage.getDateOfDeadline() != null) {
            deadlineDateEditText.setTag(DATE_PICKED);
        }
        if(stage.getDateOfStart() != null) {
            startDateEditText.setTag(DATE_PICKED);
        }
        if(stage.getDateOfCompletion() != null) {
            completionDateEditText.setTag(DATE_PICKED);
        }
        if(stage.getDateOfReply() != null) {
            replyDateEditText.setTag(DATE_PICKED);
        }

        //used by date picker dialog to store the date picked in right format
        mDataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //add 1 to the month, as month start with 0
                month = month + 1;

                //if month is one digit, add 0 to the front
                String monthOfYear = Integer.toString(month);
                monthOfYear = (monthOfYear.length() == 1) ? "0" + monthOfYear : monthOfYear;

                //if day is one digit, add 0 to the front
                String dayOfMonth = Integer.toString(day);
                dayOfMonth = (dayOfMonth.length() == 1) ? "0" + dayOfMonth : dayOfMonth;

                String date = dayOfMonth + "/" + monthOfYear + "/" + year;
                clickedDateEditText.setText(date);
                //set the date button to being picked
                clickedDateEditText.setTag(DATE_PICKED);
            }
        };

        editModeSetup();

        radioButtonsLogic();

    }

    private void radioButtonsLogic() {
        yesComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChangeMade = true;
                yesCompleteRadioClick();
            }
        });

        noComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChangeMade = true;
                noCompleteRadioClick();
            }
        });

        yesWaiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChangeMade = true;
                yesWaitingRadioClick();
            }
        });

        noWaiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChangeMade = true;
                noWaitingRadioClick();
            }
        });

        yesSuccessful.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChangeMade = true;
            }
        });

        noSuccessful.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChangeMade = true;
            }
        });

    }

    private void yesCompleteRadioClick() {
        waitingText.setVisibility(View.VISIBLE);
        waitingRadioGroup.setVisibility(View.VISIBLE);

        completionDateText.setVisibility(View.VISIBLE);
        completionDateEditText.setVisibility(View.VISIBLE);
    }

    private void noCompleteRadioClick() {
        waitingText.setVisibility(View.GONE);
        waitingRadioGroup.setVisibility(View.GONE);
        successfulText.setVisibility(View.GONE);
        successfulRadioGroup.setVisibility(View.GONE);

        completionDateText.setVisibility(View.GONE);
        completionDateEditText.setVisibility(View.GONE);
        replyDateText.setVisibility(View.GONE);
        replyDateEditText.setVisibility(View.GONE);

        yesWaiting.setChecked(true);
        noSuccessful.setChecked(true);

        completionDateEditText.getText().clear();
        replyDateEditText.getText().clear();
    }

    private void yesWaitingRadioClick() {
        successfulText.setVisibility(View.GONE);
        successfulRadioGroup.setVisibility(View.GONE);

        replyDateText.setVisibility(View.GONE);
        replyDateEditText.setVisibility(View.GONE);

        noSuccessful.setChecked(true);
        replyDateEditText.getText().clear();
    }

    private void noWaitingRadioClick() {
        successfulText.setVisibility(View.VISIBLE);
        successfulRadioGroup.setVisibility(View.VISIBLE);

        replyDateText.setVisibility(View.VISIBLE);
        replyDateEditText.setVisibility(View.VISIBLE);
    }

    private void editModeSetup() {
        //if editing stage then display all existing stage information
        if(isEditMode) {
            setTitle(getString(R.string.edit_stage_activity_title));

            stageNameEditText.setText(stage.getStageName());

            if(stage.isCompleted()) {
                yesComplete.setChecked(true);
                yesCompleteRadioClick();
            }
            else {
                noComplete.setChecked(true);
                noCompleteRadioClick();
            }

            if(stage.isWaitingForResponse()) {
                yesWaiting.setChecked(true);
                yesWaitingRadioClick();
            }
            else {
                noWaiting.setChecked(true);
                noWaitingRadioClick();
            }

            if(stage.isSuccessful()) {
                yesSuccessful.setChecked(true);
            }
            else {
                noSuccessful.setChecked(true);
            }

            if(stage.getDateOfDeadline() != null) {
                deadlineDateEditText.setText(stage.getDateOfDeadline());
            }
            if(stage.getDateOfStart() != null) {
                startDateEditText.setText(stage.getDateOfStart());
            }
            if(stage.getDateOfCompletion() != null) {
                completionDateEditText.setText(stage.getDateOfCompletion());
            }
            if(stage.getDateOfReply() != null) {
                replyDateEditText.setText(stage.getDateOfReply());
            }

            notesStageEditText.setText(stage.getNotes());

        } else {
            setTitle(getString(R.string.new_stage_activity_title));

            noComplete.setChecked(true);
            yesWaiting.setChecked(true);
            noSuccessful.setChecked(true);
        }

        stageNameEditText.addTextChangedListener(this);
        notesStageEditText.addTextChangedListener(this);
        deadlineDateText.addTextChangedListener(this);
        startDateEditText.addTextChangedListener(this);
        completionDateEditText.addTextChangedListener(this);
        replyDateEditText.addTextChangedListener(this);
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

            //when the delete button is pressed in the action bar
            case R.id.action_delete_stage_edit:
                //show alert dialog to confirm deletion
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.areYouSureDialogTitle))
                        .setMessage(getResources().getString(R.string.deleteDialogMessage))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDataSource.deleteStage(stage.getStageID());

                                //go back to the application information activity
                                Intent intent = new Intent(getApplicationContext(), ApplicationInformationActivity.class);
                                //send the ID of the application this stage belongs to, in the intent
                                intent.putExtra(ApplicationTable.COLUMN_ID, stage.getApplicationID());
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Validates whether the stage name is not left empty
     * @return true if it is valid, where the stage name is not empty
     */
    private boolean validate() {
        boolean isValid = true;

        String stageNameText = stageNameEditText.getText().toString().replaceFirst("^ *", "");
        if(stageNameText.length() < 1) {
            stageNameEditText.setError(getString(R.string.stage_name_validation));
            isValid = false;
        }

        return isValid;
    }

    /**
     * On click method to save an Stage
     * @param view save button that was clicked
     */
    public void saveButton(View view) {
        saveStage();
    }

    /**
     * Saves the current stage, and returns true if the validation and save was successful
     * @return true if the validation and save was successful
     */
    private boolean saveStage() {
        //if the form is validated then save the stage, otherwise do not
        if(validate()) {
            Stage newStage;

            //if editing stage then use existing stage with existing ID, otherwise create new one
            if (isEditMode) {
                newStage = stage;
            } else {
                newStage = new Stage();
            }

            newStage.setStageName(stageNameEditText.getText().toString().replaceFirst("^ *", ""));

            if (yesComplete.isChecked()) newStage.setCompleted(true);
            else newStage.setCompleted(false);

            if (yesWaiting.isChecked()) newStage.setWaitingForResponse(true);
            else newStage.setWaitingForResponse(false);

            if (yesSuccessful.isChecked()) newStage.setSuccessful(true);
            else newStage.setSuccessful(false);

            if (deadlineDateEditText.getText().toString().contains("/"))
                newStage.setDateOfDeadline(deadlineDateEditText.getText().toString());
            else newStage.setDateOfDeadline(null);

            if (startDateEditText.getText().toString().contains("/"))
                newStage.setDateOfStart(startDateEditText.getText().toString());
            else newStage.setDateOfStart(null);

            if (completionDateEditText.getText().toString().contains("/"))
                newStage.setDateOfCompletion(completionDateEditText.getText().toString());
            else newStage.setDateOfCompletion(null);

            if (replyDateEditText.getText().toString().contains("/"))
                newStage.setDateOfReply(replyDateEditText.getText().toString());
            else newStage.setDateOfReply(null);

            newStage.setNotes(notesStageEditText.getText().toString().replaceFirst("^ *", ""));

            //if editing application then update it, else create a new one in database
            if (isEditMode) {
                mDataSource.updateStage(newStage);
            } else {
                mDataSource.createStage(newStage, parentApplicationID);
            }

            Intent intent = new Intent(getApplicationContext(), StageInformationActivity.class);
            //send the stage ID, in the intent
            intent.putExtra(StageTable.COLUMN_ID, newStage.getStageID());
            intent.putExtra(ApplicationTable.APPLICATION_ID, parentApplicationID);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            return true;
        } else {
            Toast.makeText(this, getResources().getString(R.string.pleaseFillForm), Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    /**
     * On click method to pick a date by calling the date picker dialog
     * @param view date button that was clicked
     */
    public void pickDate(View view) {
        //set the date button to which button that was clicked
        if(view.getId() == R.id.deadlineDateEditText) {
            clickedDateEditText = findViewById(R.id.deadlineDateEditText);
        } else if(view.getId() == R.id.startDateEditText) {
            clickedDateEditText = findViewById(R.id.startDateEditText);
        } else if(view.getId() == R.id.completionDateEditText) {
            clickedDateEditText = findViewById(R.id.completionDateEditText);
        } else if(view.getId() == R.id.replyDateEditText) {
            clickedDateEditText = findViewById(R.id.replyDateEditText);
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
            if(!clickedDateEditText.getText().toString().equals("")
                    && clickedDateEditText.getTag() != null
                    && (clickedDateEditText.getTag()).equals(DATE_PICKED)) {
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

            dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear Date", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    clickedDateEditText.getText().clear();
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } else {
            Log.i("StageEditError", "Button not found");
        }
    }

    @Override
    public void onBackPressed() {
        if(isChangeMade) {
            //show alert dialog to discard changes
            AlertDialog.Builder discardDialog = new AlertDialog.Builder(this)
                    .setMessage(getResources().getString(R.string.discardDialogMessage))
                    .setPositiveButton("Keep Editing", null)
                    .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            StageEditActivity.super.onBackPressed();
                        }
                    });
            discardDialog.show();
        } else {
            super.onBackPressed();
        }
    }

    public void suggestStageName(View view) {
        final String[] stageNames = defaultStageNames;

        new AlertDialog.Builder(StageEditActivity.this)
                .setTitle(R.string.stage_name_suggestions)
                .setItems(stageNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String stageName = stageNames[i];
                        stageNameEditText.setText(stageName);
                        stageNameEditText.dismissDropDown();
                        stageNameEditText.setSelection(stageName.length());
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void afterTextChanged(Editable editable) {
        isChangeMade = true;
    }
}
