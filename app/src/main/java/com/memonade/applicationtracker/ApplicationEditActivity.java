package com.memonade.applicationtracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.memonade.applicationtracker.database.DataSource;
import com.memonade.applicationtracker.database.ApplicationTable;
import com.memonade.applicationtracker.model.Application;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This class represents the activity to edit an Application
 * @author HaarisMemon
 */
public class ApplicationEditActivity extends AppCompatActivity implements TextWatcher {

    /**
     * static string constant used as a key when passing the isEditMode boolean for an application in the intent
     */
    public static final String APPLICATION_EDIT_MODE = "APPLICATION_EDIT_MODE";

    private Application application;
    private DataSource mDataSource;
    private boolean isEditMode;
    private boolean isChangeMade;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.companyNameEditText) TextInputEditText companyNameEditText;
    @BindView(R.id.roleEditText) AutoCompleteTextView roleEditText;
    @BindView(R.id.lengthEditText) AutoCompleteTextView lengthEditText;
    @BindView(R.id.locationEditText) AutoCompleteTextView locationEditText;
    @BindView(R.id.urlEditText) TextInputEditText urlEditText;
    @BindView(R.id.salaryEditText) TextInputEditText salaryEditText;
    @BindView(R.id.notesEditText) TextInputEditText notesEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_edit);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        //adds a back button to the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        mDataSource = new DataSource(this);
        //checks intent to see if it is edit or creation mode
        isEditMode = intent.getBooleanExtra(APPLICATION_EDIT_MODE, false);

        //application that has the same id that was sent in the intent
        application = mDataSource.getApplication(intent.getLongExtra(ApplicationTable.COLUMN_ID, -1L));

        //if editing application then display all existing application information
        if(isEditMode) {
            setTitle("Edit Application");

            companyNameEditText.setText(application.getCompanyName());
            roleEditText.setText(application.getRole());
            lengthEditText.setText(application.getLength());
            locationEditText.setText(application.getLocation());
            urlEditText.setText(application.getUrl());
            if(application.getSalary() != 0) {
                salaryEditText.setText(String.valueOf(application.getSalary()));
            }
            notesEditText.setText(application.getNotes());

        } else {
            setTitle("New Application");
        }

        ArrayAdapter<String> roleNameAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, mDataSource.getAllRoles());
        roleEditText.setAdapter(roleNameAdapter);
        roleEditText.setThreshold(0);

        ArrayAdapter<String> lengthAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, mDataSource.getAllLengths());
        lengthEditText.setAdapter(lengthAdapter);
        lengthEditText.setThreshold(0);

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, mDataSource.getAllLocations());
        locationEditText.setAdapter(locationAdapter);
        locationEditText.setThreshold(0);

        companyNameEditText.addTextChangedListener(this);
        roleEditText.addTextChangedListener(this);
        lengthEditText.addTextChangedListener(this);
        locationEditText.addTextChangedListener(this);
        urlEditText.addTextChangedListener(this);
        salaryEditText.addTextChangedListener(this);
        notesEditText.addTextChangedListener(this);

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
        getMenuInflater().inflate(R.menu.application_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //when save button pressed
            case R.id.action_save_application:
                saveApplication();
                return true;
            //when back button pressed in action bar
            case android.R.id.home:
                onBackPressed();
                return true;

            //when the delete button is pressed in the action bar
            case R.id.action_delete_application_edit:
                //show alert dialog to confirm deletion
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.areYouSureDialogTitle))
                        .setMessage(getResources().getString(R.string.deleteDialogMessage))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDataSource.deleteApplication(application.getApplicationID());

                                //go back to the application list activity
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
     * On click method to save an Application
     * @param view save button that was clicked
     */
    public void saveButton(View view) {
        saveApplication();
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
     * Saves the current application, and returns true if the validation and save was successful
     * @return true if the validation and save was successful
     */
    private boolean saveApplication() {
        //if the form is validated then save the application, otherwise do not
        if(validate()) {
            Application newApplication = null;

            //if editing application then use existing application with existing ID, otherwise create new one
            if (isEditMode) {
                newApplication = application;
            } else {
                newApplication = new Application();
            }

            newApplication.setCompanyName(companyNameEditText.getText().toString().toString().replaceFirst("^ *", ""));
            newApplication.setRole(roleEditText.getText().toString().replaceFirst("^ *", ""));
            newApplication.setLength(lengthEditText.getText().toString().replaceFirst("^ *", ""));
            newApplication.setLocation(locationEditText.getText().toString().replaceFirst("^ *", ""));
            newApplication.setUrl(urlEditText.getText().toString().replaceFirst("^ *", ""));

            try {
                String salary = salaryEditText.getText().toString().replaceFirst("^ *", "");
                if(!salary.equals(""))
                    newApplication.setSalary(Integer.parseInt(salary));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            newApplication.setNotes(notesEditText.getText().toString().replaceFirst("^ *", ""));

            //if editing application then update it, else create a new one in database
            if (isEditMode) {
                mDataSource.updateApplication(newApplication);
            } else {
                mDataSource.createApplication(newApplication);
            }

            //go to the Application Information of the existing or new Application made
            Intent intent = new Intent(getApplicationContext(), ApplicationInformationActivity.class);
            //send the application ID in the intent
            intent.putExtra(ApplicationTable.COLUMN_ID, newApplication.getApplicationID());
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
        if(isChangeMade) {
            //show alert dialog to discard changes
            AlertDialog.Builder discardDialog = new AlertDialog.Builder(this)
                    .setMessage(getResources().getString(R.string.discardDialogMessage))
                    .setPositiveButton("Keep Editing", null)
                    .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ApplicationEditActivity.super.onBackPressed();
                        }
                    });
            discardDialog.show();
        } else {
            super.onBackPressed();
        }
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
