package com.haarismemon.applicationorganiser;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.haarismemon.applicationorganiser.database.ApplicationStageTable;
import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.model.Internship;

import java.util.List;

import static com.haarismemon.applicationorganiser.R.id.companyNameText;
import static com.haarismemon.applicationorganiser.R.id.descriptionText;
import static com.haarismemon.applicationorganiser.R.id.lengthText;
import static com.haarismemon.applicationorganiser.R.id.locationText;
import static com.haarismemon.applicationorganiser.R.id.roleText;

public class InternshipInformationActivity extends AppCompatActivity {

    DataSource mDataSource;
    Internship internship = null;
    static ArrayAdapter<ApplicationStage> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_information);

        setTitle("Internship");

        TextView editedText = (TextView) findViewById(R.id.editedDateInternshipText);
        TextView companyNameText = (TextView) findViewById(R.id.companyNameText);
        TextView roleText = (TextView) findViewById(R.id.roleText);
        TextView lengthText = (TextView) findViewById(R.id.lengthText);
        TextView locationText = (TextView) findViewById(R.id.locationText);
        TextView descriptionText = (TextView) findViewById(R.id.descriptionText);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mDataSource = new DataSource(this);
        mDataSource.open();

        Intent intent = getIntent();

        internship = mDataSource.getInternship(intent.getLongExtra(InternshipTable.COLUMN_ID, -1));

        editedText.setText(getApplicationContext().getString(R.string.editedModified) + " " + internship.getModifiedDate());

        companyNameText.setText(internship.getCompanyName() + " - ID: " + internship.getInternshipID() + " / " + intent.getLongExtra(InternshipTable.COLUMN_ID, -1));
        roleText.setText(internship.getRole());
        lengthText.setText(internship.getLength() != null ? internship.getLength() : "None");
        locationText.setText(internship.getLocation() != null ? internship.getLocation() : "None");
        descriptionText.setText(internship.getDescription() != null ? internship.getDescription() : "No Description");


        ListView applicationStageListView = (ListView) findViewById(R.id.applicationStageListView);

        final List<ApplicationStage> stages = mDataSource.getAllApplicationStages(internship.getInternshipID());

        arrayAdapter = new ArrayAdapter<ApplicationStage>(
                getApplicationContext(), android.R.layout.simple_expandable_list_item_1, stages);

        applicationStageListView.setAdapter(arrayAdapter);

        applicationStageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), StageInformationActivity.class);
                intent.putExtra(ApplicationStageTable.COLUMN_ID, stages.get(i).getStageID());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.internship_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            case R.id.action_delete_internship:
                new AlertDialog.Builder(this)
                        .setTitle("Are you sure?")
                        .setMessage("This will be permanently deleted.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDataSource.deleteInternship(internship.getInternshipID());

                                ApplicationListActivity.arrayAdapter.notifyDataSetChanged();
                                //go back to the application list activity
                                Intent intent = new Intent(getApplicationContext(), ApplicationListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_create_stage:
                createStage();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void createStage() {
        Intent intent = new Intent(getApplicationContext(), StageEditActivity.class);
        intent.putExtra(InternshipEditActivity.INTERNSHIP_EDIT_MODE, false);
        intent.putExtra(InternshipTable.COLUMN_ID, internship.getInternshipID());
        startActivity(intent);
    }

    public void editInternship(View view) {
        Intent intent = new Intent(getApplicationContext(), InternshipEditActivity.class);
        intent.putExtra(InternshipEditActivity.INTERNSHIP_EDIT_MODE, true);
        intent.putExtra(InternshipTable.COLUMN_ID, internship.getInternshipID());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ApplicationListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
