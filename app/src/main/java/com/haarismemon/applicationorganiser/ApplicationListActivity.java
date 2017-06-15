package com.haarismemon.applicationorganiser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
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

        ApplicationStage onlineApplication = new ApplicationStage("Online Application", true, false, true,"28/01/2017","28/01/2017","20/02/2017",ibm.internshipId);
        ApplicationStage test = new ApplicationStage("Test",true,false,true,null,null,"20/02/2017", ibm.internshipId);
        ApplicationStage assessmentCentre = new ApplicationStage("Assessment Centre",true,false,true,"07/03/2017","07/03/2017","16/03/2017", ibm.internshipId);

        mDataSource.createApplicationStage(onlineApplication);
        mDataSource.createApplicationStage(test);
        mDataSource.createApplicationStage(assessmentCentre);
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
