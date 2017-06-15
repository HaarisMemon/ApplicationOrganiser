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
        mDataSource.seedDatbase();
        
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
