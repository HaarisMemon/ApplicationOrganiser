package com.haarismemon.applicationorganiser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.Internship;

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

    }
}
