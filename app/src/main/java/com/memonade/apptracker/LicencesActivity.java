package com.memonade.apptracker;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.memonade.apptracker.model.OpenSourceLibrary;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.media.CamcorderProfile.get;
import static com.memonade.apptracker.R.id.libraryName;
import static com.memonade.apptracker.R.id.recyclerView;

public class LicencesActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.licenceListView)
    ListView licenceListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licences);

        setTitle(getString(R.string.licences_title));

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        List<OpenSourceLibrary> libraries = new ArrayList<>();
        libraries.add(new OpenSourceLibrary("crystal-range-seekbar", OpenSourceLibrary.Type.APACHE,
                "https://github.com/syedowaisali/crystal-range-seekbar"));
        libraries.add(new OpenSourceLibrary("butterknife", OpenSourceLibrary.Type.APACHE,
                "https://github.com/JakeWharton/butterknife"));
        libraries.add(new OpenSourceLibrary("sectionedrecyclerviewadapter", OpenSourceLibrary.Type.MIT,
                "https://github.com/luizgrp/SectionedRecyclerViewAdapter"));

        List<String> libraryNames = new ArrayList<>();
        for(OpenSourceLibrary library : libraries) {
            libraryNames.add(library.getName());
        }

        ListAdapter adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, libraryNames);
        licenceListView.setAdapter(adapter);

        final List<OpenSourceLibrary> finalLibraries = new ArrayList<>(libraries);
        licenceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OpenSourceLibrary library = finalLibraries.get(i);
                Uri uri = Uri.parse(library.getUrl()); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

    }
}
