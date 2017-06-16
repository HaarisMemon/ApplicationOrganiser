package com.haarismemon.applicationorganiser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.internship_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_save_internship:
                saveInternship();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void saveInternship() {
        Toast.makeText(this, "Internship saved!", Toast.LENGTH_SHORT).show();
    }

}
