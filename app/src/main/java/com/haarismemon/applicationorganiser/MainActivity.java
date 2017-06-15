package com.haarismemon.applicationorganiser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button allApplicationsButton = (Button) findViewById(R.id.allApplicationsButton);
    }

    public void goToAllApplications(View view) {

        Intent intent = new Intent(getApplicationContext(), ApplicationListActivity.class);
        startActivity(intent);

    }
}
