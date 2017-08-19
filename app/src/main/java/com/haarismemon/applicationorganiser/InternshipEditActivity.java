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
import android.widget.Toast;

import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.Internship;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    
    @BindView(R.id.companyNameEditText) TextInputEditText companyNameEditText;
    @BindView(R.id.roleEditText) TextInputEditText roleEditText;
    @BindView(R.id.lengthEditText) TextInputEditText lengthEditText;
    @BindView(R.id.locationEditText) TextInputEditText locationEditText;
    @BindView(R.id.urlEditText) TextInputEditText urlEditText;
    @BindView(R.id.salaryEditText) TextInputEditText salaryEditText;
    @BindView(R.id.notesEditText) TextInputEditText notesEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_edit);

        ButterKnife.bind(this);

        //adds a back button to the action bar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        mDataSource = new DataSource(this);
        //checks intent to see if it is edit or creation mode
        isEditMode = intent.getBooleanExtra(INTERNSHIP_EDIT_MODE, false);

        //internship that has the same id that was sent in the intent
        internship = mDataSource.getInternship(intent.getLongExtra(InternshipTable.COLUMN_ID, -1L));

        //if editing internship then display all existing internship information
        if(isEditMode) {
            setTitle("Edit Internship");

            companyNameEditText.setText(internship.getCompanyName());
            roleEditText.setText(internship.getRole());
            lengthEditText.setText(internship.getLength());
            locationEditText.setText(internship.getLocation());
            urlEditText.setText(internship.getUrl());
            if(internship.getSalary() != 0) {
                salaryEditText.setText(String.valueOf(internship.getSalary()));
            }
            notesEditText.setText(internship.getNotes());

        } else {
            setTitle("New Internship");
        }

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

            //when the delete button is pressed in the action bar
            case R.id.action_delete_internship_edit:
                //show alert dialog to confirm deletion
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.deleteDialogTitle))
                        .setMessage(getResources().getString(R.string.deleteDialogMessage))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDataSource.deleteInternship(internship.getInternshipID());
                                MainActivity.recyclerAdapter.notifyDataSetChanged();

                                //go back to the application list activity
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
     * On click method to save an Internship
     * @param view save button that was clicked
     */
    public void saveButton(View view) {
        saveInternship();
    }

    /**
     * Validates whether the company name and role editText are not left empty
     * @return true if it is valid, where the company name and role are not empty
     */
    private boolean validate() {
        boolean isValid = true;

        String companyNameText = companyNameEditText.getText().toString().replaceFirst("^ *", "");
        if(companyNameText.length() < 1) {
            companyNameEditText.setError("Please enter the company name");
            isValid = false;
        }

        String roleText = roleEditText.getText().toString().replaceFirst("^ *", "");
        if(roleText.length() < 1) {
            roleEditText.setError("Please enter the role");
            isValid = false;
        }

        String salaryText = salaryEditText.getText().toString().replaceFirst("^ *", "");
        if(salaryText.length() > 1) {
            try {
                Integer.parseInt(salaryText);
            } catch(NumberFormatException e) {
                salaryEditText.setError("Please enter a valid salary with only number digits");
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * Saves the current internship, and returns true if the validation and save was successful
     * @return true if the validation and save was successful
     */
    private boolean saveInternship() {
        //if the form is validated then save the internship, otherwise do not
        if(validate()) {
            Internship newInternship = null;

            //if editing internship then use existing internship with existing ID, otherwise create new one
            if (isEditMode) {
                newInternship = internship;
            } else {
                newInternship = new Internship();
            }

            newInternship.setCompanyName(companyNameEditText.getText().toString().toString().replaceFirst("^ *", ""));
            newInternship.setRole(roleEditText.getText().toString().replaceFirst("^ *", ""));
            newInternship.setLength(lengthEditText.getText().toString().replaceFirst("^ *", ""));
            newInternship.setLocation(locationEditText.getText().toString().replaceFirst("^ *", ""));
            newInternship.setUrl(urlEditText.getText().toString().replaceFirst("^ *", ""));

            try {
                String salary = salaryEditText.getText().toString().replaceFirst("^ *", "");
                if(!salary.equals(""))
                    newInternship.setSalary(Integer.parseInt(salary));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            newInternship.setNotes(notesEditText.getText().toString().replaceFirst("^ *", ""));

            //if editing internship then update it, else create a new one in database
            if (isEditMode) {
                mDataSource.updateInternship(newInternship);
            } else {
                mDataSource.createInternship(newInternship);
            }

            //go to the Internship Information of the existing or new Internship made
            Intent intent = new Intent(getApplicationContext(), InternshipInformationActivity.class);
            //send the internship ID in the intent
            intent.putExtra(InternshipTable.COLUMN_ID, newInternship.getInternshipID());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            return true;

        } else {
            Toast.makeText(this, getResources().getString(R.string.pleaseFillForm), Toast.LENGTH_LONG).show();
            return false;
        }

    }

    @Override
    public void onBackPressed() {
        //show alert dialog to discard changes
        AlertDialog.Builder discardDialog = new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.discardDialogMessage))
                .setPositiveButton("Keep Editing", null)
                .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InternshipEditActivity.super.onBackPressed();
                    }
                });
        discardDialog.show();
    }
}
