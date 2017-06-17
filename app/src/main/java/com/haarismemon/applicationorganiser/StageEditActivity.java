package com.haarismemon.applicationorganiser;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.haarismemon.applicationorganiser.database.DataSource;

import java.util.Calendar;

import static com.haarismemon.applicationorganiser.R.id.descriptionEditText;
import static com.haarismemon.applicationorganiser.R.id.lengthEditText;
import static com.haarismemon.applicationorganiser.R.id.locationEditText;

public class StageEditActivity extends AppCompatActivity {

    public static final String STAGE_EDIT_MODE = "STAGE_EDIT_MODE";

    private DataSource mDataSource;
    private boolean isEditMode;

    DatePickerDialog.OnDateSetListener mDataSetListener;

    Button dateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_edit);

        mDataSource = new DataSource(this);
        dateButton = (Button) findViewById(R.id.startDateButton);

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
            }
        };

        if(isEditMode) {
            setTitle("Edit Stage");

        } else {
            setTitle("New Stage");
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

    }

    public void pickDate(View view) {
        Log.i("buttonID", ""+view.getId());
        Log.i("startDateButton", ""+R.id.startDateButton);
        Log.i("completionDateButton", ""+R.id.completionDateButton);
        Log.i("replyDateButton", ""+R.id.replyDateButton);

        if(view.getId() == R.id.startDateButton) {
            dateButton = (Button) findViewById(R.id.startDateButton);
        } else if(view.getId() == R.id.completionDateButton) {
            dateButton = (Button) findViewById(R.id.completionDateButton);
        } else if(view.getId() == R.id.replyDateButton) {
            dateButton = (Button) findViewById(R.id.replyDateButton);
        } else {
            dateButton = null;
        }

        switch (view.getId()) {
            case R.id.startDateButton:
                dateButton = (Button) findViewById(R.id.startDateButton);
            case R.id.completionDateButton:
                dateButton = (Button) findViewById(R.id.completionDateButton);
            case R.id.replyDateButton:
                dateButton = (Button) findViewById(R.id.replyDateButton);
            default:
                dateButton = null;
        }

        if(dateButton != null) {
            Calendar cal = Calendar.getInstance();

            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);

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
