package com.haarismemon.applicationorganiser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.model.Internship;

import java.util.List;

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

        List<Internship> internships = mDataSource.getAllItems();

        ArrayAdapter<Internship> arrayAdapter = new ArrayAdapter<Internship>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, internships);

        listView.setAdapter(arrayAdapter);

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
