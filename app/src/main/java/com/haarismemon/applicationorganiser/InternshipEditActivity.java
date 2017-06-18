package com.haarismemon.applicationorganiser;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.Internship;

/**
 * This class represents the activity to edit an Internship
 * @author HaarisMemon
 */
public class InternshipEditActivity extends AppCompatActivity {

    /**
     * static string constant used as a key when passing the isEditMode boolean for an internship in the intent
     */
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

        //adds a back button to the action bar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        mDataSource = new DataSource(this);
        //checks intent to see if it is edit or creation mode
        isEditMode = intent.getBooleanExtra(INTERNSHIP_EDIT_MODE, false);

        //internship that has the same id that was sent in the intent
        internship = mDataSource.getInternship(intent.getLongExtra(InternshipTable.COLUMN_ID, -1L));

        companyNameEditText = (TextInputEditText) findViewById(R.id.companyNameEditText);
        roleEditText = (TextInputEditText) findViewById(R.id.roleEditText);
        lengthEditText = (TextInputEditText) findViewById(R.id.lengthEditText);
        locationEditText = (TextInputEditText) findViewById(R.id.locationEditText);
        descriptionEditText = (TextInputEditText) findViewById(R.id.descriptionEditText);

        //if editing internship then display all existing internship information
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
            //when save button pressed
            case R.id.action_save_internship:
                saveInternship();
                return true;
            //when back button pressed in action bar
            case android.R.id.home:
                onBackPressed();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * On click method to save an Internship
     * @param view save button that was clicked
     */
    public void saveButton(View view) {
        saveInternship();
    }

    /**
     * Saves the current internship
     */
    private void saveInternship() {
        Internship newInternship = null;

        //if editing internship then use existing internship with existing ID, otherwise create new one
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

        //if editing internship then update it, else create a new one in database
        if(isEditMode) {
            mDataSource.updateInternship(newInternship);
        } else {
            mDataSource.createInternship(newInternship);
        }

        //go to the Internship Information of the exisiting or new Internship made
        Intent intent = new Intent(getApplicationContext(), InternshipInformationActivity.class);
        //send the internship ID in the intent
        intent.putExtra(InternshipTable.COLUMN_ID, newInternship.getInternshipID());
        startActivity(intent);

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
                        //save internship then go back
                        saveInternship();
                        InternshipEditActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //do no save, and go back
                        InternshipEditActivity.super.onBackPressed();
                    }
                });
        saveDialog.show();

    }
}
