package com.haarismemon.applicationorganiser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class InternshipEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_edit);

        Intent intent = getIntent();

        if(intent.getBooleanExtra("add_internship", true)) {
            setTitle("Add Internship");
        } else {
            setTitle("Edit Internship");
        }

    }
}
