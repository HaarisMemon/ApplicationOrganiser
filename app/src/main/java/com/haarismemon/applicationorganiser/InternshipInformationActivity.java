package com.haarismemon.applicationorganiser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.model.Internship;

import java.util.ArrayList;
import java.util.List;

public class InternshipInformationActivity extends AppCompatActivity {

    DataSource mDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_information);

        mDataSource = new DataSource(this);
        mDataSource.open();

        Intent intent = getIntent();

        Internship internship = mDataSource.getInternship(intent.getLongExtra(InternshipTable.COLUMN_ID, -1));

        TextView companyNameText = (TextView) findViewById(R.id.companyNameText);
        TextView roleText = (TextView) findViewById(R.id.roleText);
        TextView lengthText = (TextView) findViewById(R.id.lengthText);
        TextView locationText = (TextView) findViewById(R.id.locationText);
        TextView descriptionText = (TextView) findViewById(R.id.descriptionText);

        companyNameText.setText(internship.getCompanyName());
        roleText.setText(internship.getRole());
        lengthText.setText(internship.getLength());
        locationText.setText(internship.getLocation());

        if(internship.getDescription() != null) {
            descriptionText.setText(internship.getDescription());
        } else {
            descriptionText.setText("No Description");
        }


        ListView applicationStageListView = (ListView) findViewById(R.id.applicationStageListView);

        List<ApplicationStage> stages = mDataSource.getAllApplicationStages(internship.getInternshipId());

        ArrayAdapter<ApplicationStage> arrayAdapter = new ArrayAdapter<ApplicationStage>(
                getApplicationContext(), android.R.layout.simple_expandable_list_item_1, stages);

        applicationStageListView.setAdapter(arrayAdapter);

    }
}
