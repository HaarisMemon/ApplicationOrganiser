package com.haarismemon.applicationorganiser.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.haarismemon.applicationorganiser.model.Application;
import com.haarismemon.applicationorganiser.model.ApplicationStage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is used to easily access the database and execute commands
 * @author HaarisMemon
 */

public class DataSource {

    private Context mContext;
    private SQLiteDatabase mDatabase;
    private DBHelper mDbHelper;

    /**
     * Constructs the DataSource class which includes getting a writable database
     * @param Context
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
     * @return application object with the same Application ID field set
     */
    public void recreateApplication(Application application) {
        //makes a ContentValues object using all the fields in the application object
        ContentValues values = application.toValues();

        values.put(ApplicationTable.COLUMN_ID, application.getApplicationID());

        mDatabase.insert(ApplicationTable.TABLE_APPLICATION, null, values);

        for(ApplicationStage stage : application.getApplicationStages()) {
            recreateApplicationStage(stage);
        }
    }

    /**
     * Inserts a row in the database for a new Application Stage
     * Also updates the MODIFIED_ON column of the parent Application in the Application Table
     * @param applicationStage is passed with all the fields set
     * @param applicationID of Application that the application stage will belong to
     * @return application stage object with the new Stage ID field set
     */
    public ApplicationStage createApplicationStage(ApplicationStage applicationStage, long applicationID) {
        //sets the parent application ID of the application stage object
        applicationStage.setApplicationID(applicationID);

        //makes a ContentValues object using all the fields in the application object
        ContentValues values = applicationStage.toValues();
        long stageID = mDatabase.insert(ApplicationStageTable.TABLE_APPLICATION_STAGE, null, values);
        //sets the Application Stage ID to the id returned (last row) from the database
        applicationStage.setStageID(stageID);

        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        //update the Application's modified_on column as the current date
        ContentValues parentApplicationValues = new ContentValues();
        parentApplicationValues.put(ApplicationTable.COLUMN_MODIFIED_ON, currentDate);

        //when a new stage is created the application modified date should also be updated
        mDatabase.update(ApplicationTable.TABLE_APPLICATION, parentApplicationValues, ApplicationTable.COLUMN_ID + " = ?",
                new String[] {Long.toString(applicationID)});

        return applicationStage;
    }

    private void recreateApplicationStage(ApplicationStage applicationStage) {
        //makes a ContentValues object using all the fields in the application object
        ContentValues values = applicationStage.toValues();
        values.put(ApplicationStageTable.COLUMN_ID, applicationStage.getStageID());

        mDatabase.insert(ApplicationStageTable.TABLE_APPLICATION_STAGE, null, values);
    }

    /**
     * Seeds the database with dummy data if the database is empty
     */
    public void seedDatbase() {
        //gets the number of applications in the database
        long numApplications = DatabaseUtils.queryNumEntries(mDatabase, ApplicationTable.TABLE_APPLICATION);
        //gets the number of stages in the database
        long numApplicationStageCount = DatabaseUtils.queryNumEntries(mDatabase, ApplicationStageTable.TABLE_APPLICATION_STAGE);

        //if there are no applications and stages then populate
        if(numApplications == 0 && numApplicationStageCount == 0) {

            Application application = Application.of("Example Company", "Software Engineering Placement Year",
                    "12 Months", "London", false, "www.examplecompany.com", 15000,
                    "I have signed the contract with Example Company, and will be starting next June.",
                    false);

            ApplicationStage stage1 = ApplicationStage.of("Online Application", true, false, true,
                    "01/12/2016", "16/11/2016", "16/11/2016", "22/11/2016", "Online application required CV and Cover Letter.");

            ApplicationStage stage2 = ApplicationStage.of("Assessment Centre", true, false, true,
                    "12/01/2017", "12/01/2017", "12/01/2017", "02/02/2017", "Assessment Centre involved 2 Interviews, and a Group Task.");

            application.addStage(stage1);
            application.addStage(stage2);

            createApplication(application);
            createApplicationStage(stage1, application.getApplicationID());
            createApplicationStage(stage2, application.getApplicationID());

        }
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
            application.setApplicationStages(getAllApplicationStages(applicationID));

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
            application.setApplicationStages(getAllApplicationStages(applicationID));

            break;
        }

        cursor.close();

        return application;
    }

    /**
     * Returns all the application stages in the database that belong to a particular Application
     * @return list of application stages objects from the database that belong to a particular Application
     */
    public List<ApplicationStage> getAllApplicationStages(long applicationID) {
        List<ApplicationStage> applicationStages = new ArrayList<>();

        //query the whole Application Stage Table for all rows in descending order of creation date with matching application id
        Cursor cursor = mDatabase.query(ApplicationStageTable.TABLE_APPLICATION_STAGE, ApplicationStageTable.ALL_COLUMNS,
                ApplicationStageTable.COLUMN_APPLICATION_ID + "=?", new String[] {Long.toString(applicationID)}, null, null, ApplicationTable.COLUMN_CREATED_ON);

        while(cursor.moveToNext()) {
            ApplicationStage stage = new ApplicationStage();

            stage.setStageID(cursor.getLong(cursor.getColumnIndex(ApplicationStageTable.COLUMN_ID)));
            stage.setStageName(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_STAGE_NAME)));
            stage.setCompleted(cursor.getInt(cursor.getColumnIndex(ApplicationStageTable.COLUMN_IS_COMPLETED)) == 1);
            stage.setWaitingForResponse(cursor.getInt(cursor.getColumnIndex(ApplicationStageTable.COLUMN_IS_WAITING)) == 1);
            stage.setSuccessful(cursor.getInt(cursor.getColumnIndex(ApplicationStageTable.COLUMN_IS_SUCCESSFUL)) == 1);
            stage.setDateOfStart(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_START_DATE)));
            stage.setDateOfCompletion(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_COMPLETE_DATE)));
            stage.setDateOfReply(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_REPLY_DATE)));
            stage.setNotes(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_NOTES)));
            stage.setApplicationID(cursor.getLong(cursor.getColumnIndex(ApplicationStageTable.COLUMN_APPLICATION_ID)));
            stage.setModifiedDate(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_MODIFIED_ON)));

            applicationStages.add(stage);
        }

        cursor.close();

        return applicationStages;
    }

    /**
     * Returns an application stage from the database that has the same id passed in
     * @param applicationStageID id of application stage looking for
     * @return application stage object that has the same id as applicationStageID
     */
    public ApplicationStage getApplicationStage(long applicationStageID) {
        //query the Application Stage Table for a row with matching id
        Cursor cursor = mDatabase.query(ApplicationStageTable.TABLE_APPLICATION_STAGE, ApplicationStageTable.ALL_COLUMNS,
                ApplicationStageTable.COLUMN_ID + "=?", new String[] {Long.toString(applicationStageID)}, null, null, null);

        ApplicationStage stage = new ApplicationStage();
        //the first row found should be the application stage looking for, so break
        while(cursor.moveToNext()) {

            stage.setStageID(cursor.getLong(cursor.getColumnIndex(ApplicationStageTable.COLUMN_ID)));
            stage.setStageName(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_STAGE_NAME)));
            stage.setCompleted(cursor.getInt(cursor.getColumnIndex(ApplicationStageTable.COLUMN_IS_COMPLETED)) == 1);
            stage.setWaitingForResponse(cursor.getInt(cursor.getColumnIndex(ApplicationStageTable.COLUMN_IS_WAITING)) == 1);
            stage.setSuccessful(cursor.getInt(cursor.getColumnIndex(ApplicationStageTable.COLUMN_IS_SUCCESSFUL)) == 1);
            stage.setDateOfDeadline(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_DEADLINE_DATE)));
            stage.setDateOfStart(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_START_DATE)));
            stage.setDateOfCompletion(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_COMPLETE_DATE)));
            stage.setDateOfReply(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_REPLY_DATE)));
            stage.setNotes(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_NOTES)));
            stage.setApplicationID(cursor.getLong(cursor.getColumnIndex(ApplicationStageTable.COLUMN_APPLICATION_ID)));
            stage.setModifiedDate(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_MODIFIED_ON)));

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

        //delete all application stages that belong to the application to delete
        mDatabase.delete(ApplicationStageTable.TABLE_APPLICATION_STAGE,
                ApplicationStageTable.COLUMN_APPLICATION_ID + " = ?",
                new String[] {Long.toString(applicationID)});
    }

    /**
     * Delete Application Stage with matching id
     * @param stageID id of application stage to delete
     */
    public void deleteApplicationStage(long stageID) {
        //delete the application stage row which has an id of stageID
        mDatabase.delete(ApplicationStageTable.TABLE_APPLICATION_STAGE,
                ApplicationStageTable.COLUMN_ID + " = ?",
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
     * Update application stage row which includes the modified on date
     * @param applicationStage object with all fields filled in
     */
    public void updateApplicationStage(ApplicationStage applicationStage) {
        //current date formatted for the modified date column
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        ContentValues values = applicationStage.toValues();
        //update the modified on date by the current date
        values.put(ApplicationStageTable.COLUMN_MODIFIED_ON, currentDate);

        //update application stage that has matching id
        mDatabase.update(ApplicationStageTable.TABLE_APPLICATION_STAGE, values,
                ApplicationStageTable.COLUMN_ID + " = ?",
                new String[] {Long.toString(applicationStage.getStageID())});

        //update the Application's modified_on column as the current date too
        ContentValues parentApplicationValues = new ContentValues();
        parentApplicationValues.put(ApplicationTable.COLUMN_MODIFIED_ON, currentDate);

        //updated the application's modified date, that the application stage belongs to
        mDatabase.update(ApplicationTable.TABLE_APPLICATION, parentApplicationValues, ApplicationTable.COLUMN_ID + " = ?",
                new String[] {Long.toString(applicationStage.getApplicationID())});
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
        Cursor cursor = mDatabase.query(ApplicationStageTable.TABLE_APPLICATION_STAGE, ApplicationStageTable.ALL_COLUMNS,
                null, null, ApplicationStageTable.COLUMN_STAGE_NAME, null, ApplicationStageTable.COLUMN_STAGE_NAME);

        //while there is a next row
        while (cursor.moveToNext()) {
            stages.add(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_STAGE_NAME)));
        }

        cursor.close();

        return stages;
    }

}
