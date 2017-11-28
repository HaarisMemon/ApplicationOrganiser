package com.memonade.apptracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.memonade.apptracker.model.Application;
import com.memonade.apptracker.model.Stage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * This class is used to easily access the database and execute commands
 * @author HaarisMemon
 */

public class DataSource {

    private Context mContext;
    private SQLiteDatabase mDatabase;
    private DBHelper mDbHelper;

    private static final String preference = "my_preference_file";
    private static final String is_first_run = "is_first_run";

    /**
     * Constructs the DataSource class which includes getting a writable database
     * @param Context context of the activity using the data source
     */
    public DataSource(Context Context) {
        this.mContext = Context;
        mDbHelper = new DBHelper(mContext);
        mDatabase = mDbHelper.getWritableDatabase();
    }

    /**
     * Opens the database by getting a writable database
     */
    public void open() {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    /**
     * Closes the database when not in use
     */
    public void close() {
        mDbHelper.close();
    }

    /**
     * Seeds the database with dummy data if the database is empty
     */
    public void seedDatabase() {
        //gets the number of applications in the database
        long numApplications = DatabaseUtils.queryNumEntries(mDatabase, ApplicationTable.TABLE_APPLICATION);
        //gets the number of stages in the database
        long numStageCount = DatabaseUtils.queryNumEntries(mDatabase, StageTable.TABLE_APPLICATION_STAGE);

        //if there are no applications and stages then populate
        if(mContext.getSharedPreferences(preference, MODE_PRIVATE).getBoolean(is_first_run, true) &&
                numApplications == 0 && numStageCount == 0) {

            Application application1 = Application.of("Example Company 1", "Software Engineering Internship",
                    "12 Months", "London", false, null, 0, null, false);
            Stage stage1 = Stage.of("Online Application", true, false, true,
                    "01/12/2016", "16/11/2016", "16/11/2016", "22/11/2016", null);
            Stage stage2 = Stage.of("Assessment Centre", true, false, true,
                    "12/01/2017", "12/01/2017", "12/01/2017", "02/02/2017", null);
            application1.addStage(stage1);
            application1.addStage(stage2);

            Application application2 = Application.of("Example Company 2", "Technology Summer Internship",
                    "9 weeks", "London", false, null, 5000, null, false);
            Stage stage3 = Stage.of("Online Application", true, true, true,
                    "01/12/2016", "16/11/2016", "16/11/2016", "22/11/2016", null);
            Stage stage4 = Stage.of("Assessment Centre", true, true, false,
                    "12/01/2017", "12/01/2017", "12/01/2017", null, null);
            application2.addStage(stage3);
            application2.addStage(stage4);

            Application application3 = Application.of("Example Company 3", "Software Developer",
                    "Full Time", "London", false, null, 30000, null, false);
            Stage stage5 = Stage.of("Online Application", true, false, true,
                    "01/12/2016", "16/11/2016", "16/11/2016", "22/11/2016", null);
            Stage stage6 = Stage.of("Interview", false, false, false,
                    "18/01/2017", null, null, null, null);
            application3.addStage(stage5);
            application3.addStage(stage6);

            Application application4 = Application.of("Example Company 4", "Software Engineer",
                    "Full Time", "London", false, null, 40000, null, false);
            Stage stage7 = Stage.of("Online Application", true, false, true,
                    "01/12/2016", "16/11/2016", "16/11/2016", "22/11/2016", null);
            Stage stage8 = Stage.of("Interview", true, false, false,
                    "12/01/2017", "12/01/2017", "12/01/2017", "02/02/2017", null);
            application4.addStage(stage7);
            application4.addStage(stage8);

            createApplication(application1);
            createStage(stage1, application1.getApplicationID());
            createStage(stage2, application1.getApplicationID());
            createApplication(application2);
            createStage(stage3, application2.getApplicationID());
            createStage(stage4, application2.getApplicationID());
            createApplication(application3);
            createStage(stage5, application3.getApplicationID());
            createStage(stage6, application3.getApplicationID());
            createApplication(application4);
            createStage(stage7, application4.getApplicationID());
            createStage(stage8, application4.getApplicationID());

            // record the fact that the app has been started at least once
            mContext.getSharedPreferences(preference, MODE_PRIVATE).edit().putBoolean(is_first_run, false).apply();

        }
    }

    /**
     * Inserts a row in the database for a new Application
     * @param application is passed with all the fields set
     * @return application object with the new Application ID field set
     */
    public Application createApplication(Application application) {
        //makes a ContentValues object using all the fields in the application object
        ContentValues values = application.toValues();

        long applicationID = mDatabase.insert(ApplicationTable.TABLE_APPLICATION, null, values);
        //sets the Application ID to the id returned (last row) from the database
        application.setApplicationID(applicationID);

        return application;
    }

    /**
     * Reinsert an application row in the database for an exisitng Application that was deleted
     * @param application is passed with all the fields set
     */
    public void recreateApplication(Application application) {
        //makes a ContentValues object using all the fields in the application object
        ContentValues values = application.toValues();

        values.put(ApplicationTable.COLUMN_ID, application.getApplicationID());

        mDatabase.insert(ApplicationTable.TABLE_APPLICATION, null, values);

        for(Stage stage : application.getStages()) {
            recreateStage(stage);
        }
    }

    /**
     * Inserts a row in the database for a new Stage
     * Also updates the MODIFIED_ON column of the parent Application in the Application Table
     * @param stage is passed with all the fields set
     * @param applicationID of Application that the stage will belong to
     * @return stage object with the new Stage ID field set
     */
    public Stage createStage(Stage stage, long applicationID) {
        //sets the parent application ID of the stage object
        stage.setApplicationID(applicationID);

        //makes a ContentValues object using all the fields in the application object
        ContentValues values = stage.toValues();
        long stageID = mDatabase.insert(StageTable.TABLE_APPLICATION_STAGE, null, values);
        //sets the Stage ID to the id returned (last row) from the database
        stage.setStageID(stageID);

        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        //update the Application's modified_on column as the current date
        ContentValues parentApplicationValues = new ContentValues();
        parentApplicationValues.put(ApplicationTable.COLUMN_MODIFIED_ON, currentDate);

        //when a new stage is created the application modified date should also be updated
        mDatabase.update(ApplicationTable.TABLE_APPLICATION, parentApplicationValues, ApplicationTable.COLUMN_ID + " = ?",
                new String[] {Long.toString(applicationID)});

        return stage;
    }

    private void recreateStage(Stage stage) {
        //makes a ContentValues object using all the fields in the application object
        ContentValues values = stage.toValues();
        values.put(StageTable.COLUMN_ID, stage.getStageID());

        mDatabase.insert(StageTable.TABLE_APPLICATION_STAGE, null, values);
    }

    /**
     * Returns all the applications in the database in descending order of when they were modified/updated
     * @return list of application objects from the database
     */
    public List<Application> getAllApplication() {
        List<Application> applications = new ArrayList<>();

        //query the whole Application Table for all rows in descending order of modified date
        Cursor cursor = mDatabase.query(ApplicationTable.TABLE_APPLICATION, ApplicationTable.ALL_COLUMNS,
                null, null, null, null, ApplicationTable.COLUMN_MODIFIED_ON + " DESC");

        //while there is a next row
        while (cursor.moveToNext()) {
            Application application = new Application();

            long applicationID = cursor.getLong(cursor.getColumnIndex(ApplicationTable.COLUMN_ID));

            application.setApplicationID(applicationID);
            application.setCompanyName(cursor.getString(cursor.getColumnIndex(ApplicationTable.COLUMN_COMPANY_NAME)));
            application.setRole(cursor.getString(cursor.getColumnIndex(ApplicationTable.COLUMN_ROLE)));
            application.setLength(cursor.getString(cursor.getColumnIndex(ApplicationTable.COLUMN_LENGTH)));
            application.setLocation(cursor.getString(cursor.getColumnIndex(ApplicationTable.COLUMN_LOCATION)));
            application.setUrl(cursor.getString(cursor.getColumnIndex(ApplicationTable.COLUMN_URL)));
            application.setSalary(cursor.getInt(cursor.getColumnIndex(ApplicationTable.COLUMN_SALARY)));
            application.setPriority(cursor.getInt(cursor.getColumnIndex(ApplicationTable.COLUMN_PRIORITY)) == 1);
            application.setNotes(cursor.getString(cursor.getColumnIndex(ApplicationTable.COLUMN_NOTES)));
            application.setCreatedDate(cursor.getString(cursor.getColumnIndex(ApplicationTable.COLUMN_CREATED_ON)));
            application.setModifiedDate(cursor.getString(cursor.getColumnIndex(ApplicationTable.COLUMN_MODIFIED_ON)));

            //add all the application's stages to the list stored in the application object
            application.setStages(getAllStages(applicationID));

            applications.add(application);
        }

        cursor.close();

        return applications;
    }

    /**
     * Returns an application from the database that has the same id passed in
     * @param applicationID id of application looking for
     * @return application object that has the same id as applicationID
     */
    public Application getApplication(long applicationID) {
        //query the Application Table for a row with matching id
        Cursor cursor = mDatabase.query(ApplicationTable.TABLE_APPLICATION, ApplicationTable.ALL_COLUMNS,
                ApplicationTable.COLUMN_ID + "=?", new String[] {Long.toString(applicationID)}, null, null, null);

        Application application = new Application();
        //the first row found should be the application looking for, so break
        while(cursor.moveToNext()) {

            application.setApplicationID(cursor.getLong(cursor.getColumnIndex(ApplicationTable.COLUMN_ID)));
            application.setCompanyName(cursor.getString(cursor.getColumnIndex(ApplicationTable.COLUMN_COMPANY_NAME)));
            application.setRole(cursor.getString(cursor.getColumnIndex(ApplicationTable.COLUMN_ROLE)));
            application.setLength(cursor.getString(cursor.getColumnIndex(ApplicationTable.COLUMN_LENGTH)));
            application.setLocation(cursor.getString(cursor.getColumnIndex(ApplicationTable.COLUMN_LOCATION)));
            application.setUrl(cursor.getString(cursor.getColumnIndex(ApplicationTable.COLUMN_URL)));
            application.setSalary(cursor.getInt(cursor.getColumnIndex(ApplicationTable.COLUMN_SALARY)));
            application.setPriority(cursor.getInt(cursor.getColumnIndex(ApplicationTable.COLUMN_PRIORITY)) == 1);
            application.setNotes(cursor.getString(cursor.getColumnIndex(ApplicationTable.COLUMN_NOTES)));
            application.setCreatedDate(cursor.getString(cursor.getColumnIndex(ApplicationTable.COLUMN_CREATED_ON)));
            application.setModifiedDate(cursor.getString(cursor.getColumnIndex(ApplicationTable.COLUMN_MODIFIED_ON)));

            //add all the application's stages to the list stored in the application object
            application.setStages(getAllStages(applicationID));

            break;
        }

        cursor.close();

        return application;
    }

    /**
     * Returns all the stages in the database that belong to a particular Application
     * @return list of stages objects from the database that belong to a particular Application
     */
    public List<Stage> getAllStages(long applicationID) {
        List<Stage> stages = new ArrayList<>();

        //query the whole Stage Table for all rows in descending order of creation date with matching application id
        Cursor cursor = mDatabase.query(StageTable.TABLE_APPLICATION_STAGE, StageTable.ALL_COLUMNS,
                StageTable.COLUMN_APPLICATION_ID + "=?", new String[] {Long.toString(applicationID)}, null, null, ApplicationTable.COLUMN_CREATED_ON);

        while(cursor.moveToNext()) {
            Stage stage = new Stage();

            stage.setStageID(cursor.getLong(cursor.getColumnIndex(StageTable.COLUMN_ID)));
            stage.setStageName(cursor.getString(cursor.getColumnIndex(StageTable.COLUMN_STAGE_NAME)));
            stage.setCompleted(cursor.getInt(cursor.getColumnIndex(StageTable.COLUMN_IS_COMPLETED)) == 1);
            stage.setWaitingForResponse(cursor.getInt(cursor.getColumnIndex(StageTable.COLUMN_IS_WAITING)) == 1);
            stage.setSuccessful(cursor.getInt(cursor.getColumnIndex(StageTable.COLUMN_IS_SUCCESSFUL)) == 1);
            stage.setDateOfStart(cursor.getString(cursor.getColumnIndex(StageTable.COLUMN_START_DATE)));
            stage.setDateOfCompletion(cursor.getString(cursor.getColumnIndex(StageTable.COLUMN_COMPLETE_DATE)));
            stage.setDateOfReply(cursor.getString(cursor.getColumnIndex(StageTable.COLUMN_REPLY_DATE)));
            stage.setNotes(cursor.getString(cursor.getColumnIndex(StageTable.COLUMN_NOTES)));
            stage.setApplicationID(cursor.getLong(cursor.getColumnIndex(StageTable.COLUMN_APPLICATION_ID)));
            stage.setModifiedDate(cursor.getString(cursor.getColumnIndex(StageTable.COLUMN_MODIFIED_ON)));

            stages.add(stage);
        }

        cursor.close();

        return stages;
    }

    /**
     * Returns an stage from the database that has the same id passed in
     * @param stageID id of stage looking for
     * @return stage object that has the same id as stageID
     */
    public Stage getStage(long stageID) {
        //query the Stage Table for a row with matching id
        Cursor cursor = mDatabase.query(StageTable.TABLE_APPLICATION_STAGE, StageTable.ALL_COLUMNS,
                StageTable.COLUMN_ID + "=?", new String[] {Long.toString(stageID)}, null, null, null);

        Stage stage = new Stage();
        //the first row found should be the stage looking for, so break
        while(cursor.moveToNext()) {

            stage.setStageID(cursor.getLong(cursor.getColumnIndex(StageTable.COLUMN_ID)));
            stage.setStageName(cursor.getString(cursor.getColumnIndex(StageTable.COLUMN_STAGE_NAME)));
            stage.setCompleted(cursor.getInt(cursor.getColumnIndex(StageTable.COLUMN_IS_COMPLETED)) == 1);
            stage.setWaitingForResponse(cursor.getInt(cursor.getColumnIndex(StageTable.COLUMN_IS_WAITING)) == 1);
            stage.setSuccessful(cursor.getInt(cursor.getColumnIndex(StageTable.COLUMN_IS_SUCCESSFUL)) == 1);
            stage.setDateOfDeadline(cursor.getString(cursor.getColumnIndex(StageTable.COLUMN_DEADLINE_DATE)));
            stage.setDateOfStart(cursor.getString(cursor.getColumnIndex(StageTable.COLUMN_START_DATE)));
            stage.setDateOfCompletion(cursor.getString(cursor.getColumnIndex(StageTable.COLUMN_COMPLETE_DATE)));
            stage.setDateOfReply(cursor.getString(cursor.getColumnIndex(StageTable.COLUMN_REPLY_DATE)));
            stage.setNotes(cursor.getString(cursor.getColumnIndex(StageTable.COLUMN_NOTES)));
            stage.setApplicationID(cursor.getLong(cursor.getColumnIndex(StageTable.COLUMN_APPLICATION_ID)));
            stage.setModifiedDate(cursor.getString(cursor.getColumnIndex(StageTable.COLUMN_MODIFIED_ON)));

            break;
        }

        cursor.close();

        return stage;
    }

    /**
     * Delete Application with matching id
     * @param applicationID id of application to delete
     */
    public void deleteApplication(long applicationID) {
        //delete the application row which has an id of applicationID
        mDatabase.delete(ApplicationTable.TABLE_APPLICATION,
                ApplicationTable.COLUMN_ID + " = ?",
                new String[]{Long.toString(applicationID)});

        //delete all stages that belong to the application to delete
        mDatabase.delete(StageTable.TABLE_APPLICATION_STAGE,
                StageTable.COLUMN_APPLICATION_ID + " = ?",
                new String[] {Long.toString(applicationID)});
    }

    /**
     * Delete Stage with matching id
     * @param stageID id of stage to delete
     */
    public void deleteStage(long stageID) {
        //delete the stage row which has an id of stageID
        mDatabase.delete(StageTable.TABLE_APPLICATION_STAGE,
                StageTable.COLUMN_ID + " = ?",
                new String[] {Long.toString(stageID)});
    }

    /**
     * Update application row which includes the modified on date
     * @param application object with all fields filled in
     */
    public void updateApplication(Application application) {
        //current date formatted for the modified date column
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        ContentValues values = application.toValues();
        //update the modified on date by the current date
        values.put(ApplicationTable.COLUMN_MODIFIED_ON, currentDate);

        //update application that has matching id
        mDatabase.update(ApplicationTable.TABLE_APPLICATION, values,
                ApplicationTable.COLUMN_ID + " = ?",
                new String[] {Long.toString(application.getApplicationID())});
    }

    /**
     * Update application priority without updating the modified date
     * @param application object for which to update the priority of
     */
    public void updateApplicationPriority(Application application) {

        ContentValues values = new ContentValues();
        //update the modified on date by the current date
        values.put(ApplicationTable.COLUMN_PRIORITY, application.isPriority());

        //update application that has matching id
        mDatabase.update(ApplicationTable.TABLE_APPLICATION, values,
                ApplicationTable.COLUMN_ID + " = ?",
                new String[] {Long.toString(application.getApplicationID())});
    }

    /**
     * Update stage row which includes the modified on date
     * @param stage object with all fields filled in
     */
    public void updateStage(Stage stage) {
        //current date formatted for the modified date column
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        ContentValues values = stage.toValues();
        //update the modified on date by the current date
        values.put(StageTable.COLUMN_MODIFIED_ON, currentDate);

        //update stage that has matching id
        mDatabase.update(StageTable.TABLE_APPLICATION_STAGE, values,
                StageTable.COLUMN_ID + " = ?",
                new String[] {Long.toString(stage.getStageID())});

        //update the Application's modified_on column as the current date too
        ContentValues parentApplicationValues = new ContentValues();
        parentApplicationValues.put(ApplicationTable.COLUMN_MODIFIED_ON, currentDate);

        //updated the application's modified date, that the stage belongs to
        mDatabase.update(ApplicationTable.TABLE_APPLICATION, parentApplicationValues, ApplicationTable.COLUMN_ID + " = ?",
                new String[] {Long.toString(stage.getApplicationID())});
    }

    public List<String> getAllRoles() {
        List<String> roles = new ArrayList<>();

        //query the whole Application Table for all rows in descending order of modified date
        Cursor cursor = mDatabase.query(ApplicationTable.TABLE_APPLICATION, ApplicationTable.ALL_COLUMNS,
                null, null, ApplicationTable.COLUMN_ROLE, null, ApplicationTable.COLUMN_ROLE);

        //while there is a next row
        while (cursor.moveToNext()) {
            roles.add(cursor.getString(cursor.getColumnIndex(ApplicationTable.COLUMN_ROLE)));
        }

        cursor.close();

        return roles;
    }

    public List<String> getAllLengths() {
        List<String> lengths = new ArrayList<>();

        //query the whole Application Table for all rows in descending order of modified date
        Cursor cursor = mDatabase.query(ApplicationTable.TABLE_APPLICATION, ApplicationTable.ALL_COLUMNS,
                ApplicationTable.COLUMN_LENGTH + " IS NOT NULL", null, ApplicationTable.COLUMN_LENGTH, null, ApplicationTable.COLUMN_LENGTH);

        //while there is a next row
        while (cursor.moveToNext()) {
            lengths.add(cursor.getString(cursor.getColumnIndex(ApplicationTable.COLUMN_LENGTH)));
        }

        cursor.close();

        return lengths;
    }

    public List<String> getAllLocations() {
        List<String> locations = new ArrayList<>();

        //query the whole Application Table for all rows in descending order of modified date
        Cursor cursor = mDatabase.query(ApplicationTable.TABLE_APPLICATION, ApplicationTable.ALL_COLUMNS,
                null, null, ApplicationTable.COLUMN_LOCATION, null, ApplicationTable.COLUMN_LOCATION);

        //while there is a next row
        while (cursor.moveToNext()) {
            locations.add(cursor.getString(cursor.getColumnIndex(ApplicationTable.COLUMN_LOCATION)));
        }

        cursor.close();

        return locations;
    }

    public List<Integer> getAllSalary() {
        List<Integer> salaries = new ArrayList<>();

        //query the whole Application Table for all rows in descending order of modified date
        Cursor cursor = mDatabase.query(ApplicationTable.TABLE_APPLICATION, ApplicationTable.ALL_COLUMNS,
                null, null, ApplicationTable.COLUMN_SALARY, null, ApplicationTable.COLUMN_SALARY + " DESC");

        //while there is a next row
        while (cursor.moveToNext()) {
            salaries.add(cursor.getInt(cursor.getColumnIndex(ApplicationTable.COLUMN_SALARY)));
        }

        cursor.close();

        return salaries;
    }

    public List<String> getAllStageNames() {
        List<String> stages = new ArrayList<>();

        //query the whole Application Table for all rows in descending order of modified date
        Cursor cursor = mDatabase.query(StageTable.TABLE_APPLICATION_STAGE, StageTable.ALL_COLUMNS,
                null, null, StageTable.COLUMN_STAGE_NAME, null, StageTable.COLUMN_STAGE_NAME);

        //while there is a next row
        while (cursor.moveToNext()) {
            stages.add(cursor.getString(cursor.getColumnIndex(StageTable.COLUMN_STAGE_NAME)));
        }

        cursor.close();

        return stages;
    }

}
