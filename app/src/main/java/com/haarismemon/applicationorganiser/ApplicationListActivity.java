package com.haarismemon.applicationorganiser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.model.Internship;

public class ApplicationListActivity extends AppCompatActivity {

    DataSource mDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_list);

        setTitle("Applications");

        ListView listView = (ListView) findViewById(R.id.listView);

        mDataSource = new DataSource(this);
        mDataSource.open();

        Internship ibm = new Internship("IBM", "Software Engineer Placement", "1 Year", "London");
        Internship cognito = new Internship("Cognito iQ", "Software Developer Placement", "1 Year", "Newbury");

        mDataSource.createInternship(ibm);
        mDataSource.createInternship(cognito);
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
}
