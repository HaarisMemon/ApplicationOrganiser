package com.haarismemon.applicationorganiser;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haarismemon.applicationorganiser.database.ApplicationStageTable;
import com.haarismemon.applicationorganiser.database.DataSource;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.model.Internship;

import java.util.List;

//import static com.haarismemon.applicationorganiser.R.id.applicationStageListView;

/**
 * This class represents the activity which displays the information of an Internship with list of its stages
 * @author HaarisMemon
 */
public class InternshipInformationActivity extends AppCompatActivity {

    private DataSource mDataSource;
    private Internship internship = null;

    /**
     * ArrayAdapter of Listview for stages in the activity
     */
    public static ArrayAdapter<ApplicationStage> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_information);

        //adds a back button to the action bar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        setTitle("Internship");

        mDataSource = new DataSource(this);
        mDataSource.open();
        Intent intent = getIntent();

        TextView editedText = (TextView) findViewById(R.id.editedDateInternshipText);
        TextView companyNameText = (TextView) findViewById(R.id.companyNameText);
        TextView roleText = (TextView) findViewById(R.id.roleText);
        TextView lengthText = (TextView) findViewById(R.id.lengthText);
        TextView locationText = (TextView) findViewById(R.id.locationText);
        TextView descriptionText = (TextView) findViewById(R.id.descriptionText);

        //internship that has the same id that was sent in the intent
        internship = mDataSource.getInternship(intent.getLongExtra(InternshipTable.COLUMN_ID, -1));

        //display the last time internship or its application stages was modified
        editedText.setText(getApplicationContext().getString(R.string.editedModified) + " " + internship.getModifiedShortDateTime());

        companyNameText.setText(internship.getCompanyName() != null ? internship.getCompanyName() : "None");
        roleText.setText(internship.getRole() != null ? internship.getRole() : "None");
        lengthText.setText(internship.getLength() != null ? internship.getLength() : "None");
        locationText.setText(internship.getLocation() != null ? internship.getLocation() : "None");
        descriptionText.setText(internship.getDescription() != null ? internship.getDescription() : "No Description");
        LinearLayout stagesListLinearLayout = (LinearLayout) findViewById(R.id.stagesListLinearLayout);

        //arraylist of all application stages linked to the internship in the database
        final List<ApplicationStage> stages = mDataSource.getAllApplicationStages(internship.getInternshipID());

        arrayAdapter = new ArrayAdapter<ApplicationStage>(
                getApplicationContext(), android.R.layout.simple_expandable_list_item_1, stages);

        //loop through each stage in the recyclerAdapter
        for (int position = 0; position < arrayAdapter.getCount(); position++) {
            //get the item view of the application stage
            final View itemView = arrayAdapter.getView(position, null, stagesListLinearLayout);
            //add the item view to the stages list linearLayout
            stagesListLinearLayout.addView(itemView);

            final int i = position;

            //set the on click listener for each application stage itemView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemView.setBackgroundColor(Color.parseColor("#DFDFDF"));

                    //go to new activity to see the stage information of application stage clicked
                    Intent intent = new Intent(getApplicationContext(), StageInformationActivity.class);
                    //send the stage id of the stage clicked, in the intent
                    intent.putExtra(ApplicationStageTable.COLUMN_ID, stages.get(i).getStageID());
                    startActivity(intent);
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.internship_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            //when the delete button is pressed in the action bar
            case R.id.action_delete_internship:
                //show alert dialog to confirm deletion
                new AlertDialog.Builder(this)
                        .setTitle("Are you sure?")
                        .setMessage("This will be permanently deleted.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDataSource.deleteInternship(internship.getInternshipID());
                                ApplicationListActivity.recyclerAdapter.notifyDataSetChanged();

                                //go back to the application list activity
                                Intent intent = new Intent(getApplicationContext(), ApplicationListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;

            //when back button pressed in action bar
            case android.R.id.home:
                onBackPressed();
                return true;

            //when the create stage button is pressed
            case R.id.action_create_stage:
                createStage();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Change activity to create new stage
     */
    private void createStage() {
        Intent intent = new Intent(getApplicationContext(), StageEditActivity.class);
        //send a boolean in the intent, of whether the internship is being edited (or created)
        intent.putExtra(InternshipEditActivity.INTERNSHIP_EDIT_MODE, false);
        //send the id of the internship that stage will belong to, in the intent
        intent.putExtra(InternshipTable.COLUMN_ID, internship.getInternshipID());
        startActivity(intent);
    }

    /**
     * On click method to edit an existing Internship
     * @param view edit button that was clicked
     */
    public void editInternship(View view) {
        Intent intent = new Intent(getApplicationContext(), InternshipEditActivity.class);
        //send a boolean that an internship is being edited, in the intent
        intent.putExtra(InternshipEditActivity.INTERNSHIP_EDIT_MODE, true);
        //send the id of the internship to be edited, in the intent
        intent.putExtra(InternshipTable.COLUMN_ID, internship.getInternshipID());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ApplicationListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
