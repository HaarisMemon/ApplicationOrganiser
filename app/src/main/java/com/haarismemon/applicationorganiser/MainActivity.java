package com.haarismemon.applicationorganiser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * This class represents the main activity which first opens when the app is launched
 * @author HaarisMemon
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button allApplicationsButton = (Button) findViewById(R.id.allApplicationsButton);
    }

    /**
     * On click method to view the Applications List
     * @param view button that was clicked
     */
    public void goToAllApplications(View view) {
        Intent intent = new Intent(getApplicationContext(), ApplicationListActivity.class);
        startActivity(intent);
    }

    /**
     * On click method to create a new Internship
     * @param view create button that was clicked
     */
    public void createInternship(View view) {
        Intent intent = new Intent(getApplicationContext(), InternshipEditActivity.class);
        intent.putExtra(InternshipEditActivity.INTERNSHIP_EDIT_MODE, false);
        startActivity(intent);
    }
}
