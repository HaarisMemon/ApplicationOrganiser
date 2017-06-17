package com.haarismemon.applicationorganiser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.haarismemon.applicationorganiser.database.DataSource;

public class StageEditActivity extends AppCompatActivity {

    public static final String STAGE_EDIT_MODE = "STAGE_EDIT_MODE";

    private DataSource mDataSource;
    private boolean isEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_edit);

        mDataSource = new DataSource(this);

        if(isEditMode) {
            setTitle("Edit Stage");

        } else {
            setTitle("New Stage");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stage_edit_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            case R.id.action_save_stage:
                saveStage();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void saveButton(View view) {
        saveStage();
    }

    private void saveStage() {

    }

}
