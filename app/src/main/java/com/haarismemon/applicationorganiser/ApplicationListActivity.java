package com.haarismemon.applicationorganiser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.Internship;

import java.util.List;

public class ApplicationListActivity extends AppCompatActivity {

    DataSource mDataSource;
    static ArrayAdapter<Internship> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_list);

        setTitle("All Applications");

        ListView listView = (ListView) findViewById(R.id.listView);

        mDataSource = new DataSource(this);
        mDataSource.open();
        mDataSource.seedDatbase();

        final List<Internship> internships = mDataSource.getAllInternship();

        arrayAdapter = new ArrayAdapter<Internship>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, internships);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), InternshipInformationActivity.class);
                intent.putExtra(InternshipTable.COLUMN_ID, internships.get(i).getInternshipId());
                startActivity(intent);
            }
        });

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
