package com.haarismemon.applicationorganiser;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.Internship;

public class InternshipEditActivity extends AppCompatActivity {

    public static final String INTERNSHIP_EDIT_MODE = "INTERNSHIP_EDIT_MODE";

    private DataSource mDataSource;
    private boolean isEditMode;
    private Internship internship;
    private TextInputEditText companyNameEditText;
    private TextInputEditText roleEditText;
    private TextInputEditText lengthEditText;
    private TextInputEditText locationEditText;
    private TextInputEditText descriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_edit);

        Intent intent = getIntent();
        mDataSource = new DataSource(this);
        isEditMode = intent.getBooleanExtra(INTERNSHIP_EDIT_MODE, false);

        internship = mDataSource.getInternship(intent.getLongExtra(InternshipTable.COLUMN_ID, -1L));

        companyNameEditText = (TextInputEditText) findViewById(R.id.companyNameEditText);
        roleEditText = (TextInputEditText) findViewById(R.id.roleEditText);
        lengthEditText = (TextInputEditText) findViewById(R.id.lengthEditText);
        locationEditText = (TextInputEditText) findViewById(R.id.locationEditText);
        descriptionEditText = (TextInputEditText) findViewById(R.id.descriptionEditText);

//        companyNameEditText.setSingleLine(true);

        if(isEditMode) {
            setTitle("Edit Internship");

            companyNameEditText.setText(internship.getCompanyName());
            roleEditText.setText(internship.getRole());
            lengthEditText.setText(internship.getLength());
            locationEditText.setText(internship.getLocation());
            descriptionEditText.setText(internship.getDescription());

        } else {
            setTitle("New Internship");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.internship_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_save_internship:
                saveInternship();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void saveButton(View view) {
        saveInternship();
    }

    private void saveInternship() {
        Internship newInternship = null;

        if(isEditMode) {
            newInternship = internship;
        } else {
            newInternship = new Internship();
        }

        newInternship.setCompanyName(companyNameEditText.getText().toString().toString());
        newInternship.setRole(roleEditText.getText().toString());
        newInternship.setLength(lengthEditText.getText().toString());
        newInternship.setLocation(locationEditText.getText().toString());
        newInternship.setDescription(descriptionEditText.getText().toString());

        if(isEditMode) {
            mDataSource.updateInternship(newInternship);
        } else {
            mDataSource.createInternship(newInternship);
        }

        Intent intent = new Intent(getApplicationContext(), InternshipInformationActivity.class);
        intent.putExtra(InternshipTable.COLUMN_ID, newInternship.getInternshipID());
        startActivity(intent);

    }

}
