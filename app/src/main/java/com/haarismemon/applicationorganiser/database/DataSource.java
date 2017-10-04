package com.haarismemon.applicationorganiser.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.model.Internship;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
     * Inserts a row in the database for a new Internship
     * @param internship is passed with all the fields set
     * @return internship object with the new Internship ID field set
     */
    public Internship createInternship(Internship internship) {
        //makes a ContentValues object using all the fields in the internship object
        ContentValues values = internship.toValues();

        long internshipID = mDatabase.insert(InternshipTable.TABLE_INTERNSHIP, null, values);
        //sets the Internship ID to the id returned (last row) from the database
        internship.setInternshipID(internshipID);

        return internship;
    }

    /**
     * Inserts a row in the database for a new Application Stage
     * Also updates the MODIFIED_ON column of the parent Internship in the Internship Table
     * @param applicationStage is passed with all the fields set
     * @param internshipID of Internship that the application stage will belong to
     * @return application stage object with the new Stage ID field set
     */
    public ApplicationStage createApplicationStage(ApplicationStage applicationStage, long internshipID) {
        //sets the parent internship ID of the application stage object
        applicationStage.setInternshipID(internshipID);

        //makes a ContentValues object using all the fields in the internship object
        ContentValues values = applicationStage.toValues();
        long stageID = mDatabase.insert(ApplicationStageTable.TABLE_APPLICATION_STAGE, null, values);
        //sets the Application Stage ID to the id returned (last row) from the database
        applicationStage.setStageID(stageID);

        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        //update the Internship's modified_on column as the current date
        ContentValues parentInternshipValues = new ContentValues();
        parentInternshipValues.put(InternshipTable.COLUMN_MODIFIED_ON, currentDate);

        //when a new stage is created the internship modified date should also be updated
        mDatabase.update(InternshipTable.TABLE_INTERNSHIP, parentInternshipValues, InternshipTable.COLUMN_ID + " = ?",
                new String[] {Long.toString(internshipID)});

        return applicationStage;
    }

    /**
     * Seeds the database with dummy data if the database is empty
     */
    public void seedDatbase() {
        //gets the number of internships in the database
        long numInternships = DatabaseUtils.queryNumEntries(mDatabase, InternshipTable.TABLE_INTERNSHIP);
        //gets the number of stages in the database
        long numApplicationStageCount = DatabaseUtils.queryNumEntries(mDatabase, ApplicationStageTable.TABLE_APPLICATION_STAGE);

        //if there are no internships and stages then populate
        if(numInternships == 0 && numApplicationStageCount == 0) {

            List<Internship> internships = SeedApplications.parse();

            //if there are no stages then populate
            for (Internship internship : internships) {

                createInternship(internship);

                for (ApplicationStage stage : internship.getApplicationStages()) {

                    createApplicationStage(stage, internship.getInternshipID());

                }

            }
        }
    }

    /**
     * Returns all the internships in the database in descending order of when they were modified/updated
     * @return list of internship objects from the database
     */
    public List<Internship> getAllInternship() {
        List<Internship> internships = new ArrayList<>();

        //query the whole Internship Table for all rows in descending order of modified date
        Cursor cursor = mDatabase.query(InternshipTable.TABLE_INTERNSHIP, InternshipTable.ALL_COLUMNS,
                null, null, null, null, InternshipTable.COLUMN_MODIFIED_ON + " DESC");

        //while there is a next row
        while (cursor.moveToNext()) {
            Internship internship = new Internship();

            long internshipID = cursor.getLong(cursor.getColumnIndex(InternshipTable.COLUMN_ID));

            internship.setInternshipID(internshipID);
            internship.setCompanyName(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_COMPANY_NAME)));
            internship.setRole(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_ROLE)));
            internship.setLength(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_LENGTH)));
            internship.setLocation(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_LOCATION)));
            internship.setUrl(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_URL)));
            internship.setSalary(cursor.getInt(cursor.getColumnIndex(InternshipTable.COLUMN_SALARY)));
            internship.setPriority(cursor.getInt(cursor.getColumnIndex(InternshipTable.COLUMN_PRIORITY)) == 1);
            internship.setNotes(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_NOTES)));
            internship.setCreatedDate(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_CREATED_ON)));
            internship.setModifiedDate(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_MODIFIED_ON)));

            //add all the internship's stages to the list stored in the internship object
            internship.setApplicationStages(getAllApplicationStages(internshipID));

            internships.add(internship);
        }

        cursor.close();

        return internships;
    }

    /**
     * Returns an internship from the database that has the same id passed in
     * @param internshipID id of internship looking for
     * @return internship object that has the same id as internshipID
     */
    public Internship getInternship(long internshipID) {
        //query the Internship Table for a row with matching id
        Cursor cursor = mDatabase.query(InternshipTable.TABLE_INTERNSHIP, InternshipTable.ALL_COLUMNS,
                InternshipTable.COLUMN_ID + "=?", new String[] {Long.toString(internshipID)}, null, null, null);

        Internship internship = new Internship();
        //the first row found should be the internship looking for, so break
        while(cursor.moveToNext()) {

            internship.setInternshipID(cursor.getLong(cursor.getColumnIndex(InternshipTable.COLUMN_ID)));
            internship.setCompanyName(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_COMPANY_NAME)));
            internship.setRole(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_ROLE)));
            internship.setLength(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_LENGTH)));
            internship.setLocation(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_LOCATION)));
            internship.setUrl(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_URL)));
            internship.setSalary(cursor.getInt(cursor.getColumnIndex(InternshipTable.COLUMN_SALARY)));
            internship.setPriority(cursor.getInt(cursor.getColumnIndex(InternshipTable.COLUMN_PRIORITY)) == 1);
            internship.setNotes(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_NOTES)));
            internship.setCreatedDate(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_CREATED_ON)));
            internship.setModifiedDate(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_MODIFIED_ON)));

            //add all the internship's stages to the list stored in the internship object
            internship.setApplicationStages(getAllApplicationStages(internshipID));

            break;
        }

        cursor.close();

        return internship;
    }

    /**
     * Returns all the application stages in the database that belong to a particular Internship
     * @return list of application stages objects from the database that belong to a particular Internship
     */
    public List<ApplicationStage> getAllApplicationStages(long internshipID) {
        List<ApplicationStage> applicationStages = new ArrayList<>();

        //query the whole Application Stage Table for all rows in descending order of creation date with matching internship id
        Cursor cursor = mDatabase.query(ApplicationStageTable.TABLE_APPLICATION_STAGE, ApplicationStageTable.ALL_COLUMNS,
                ApplicationStageTable.COLUMN_INTERNSHIP_ID + "=?", new String[] {Long.toString(internshipID)}, null, null, InternshipTable.COLUMN_CREATED_ON);

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
            stage.setInternshipID(cursor.getLong(cursor.getColumnIndex(ApplicationStageTable.COLUMN_INTERNSHIP_ID)));
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
            stage.setDateOfStart(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_START_DATE)));
            stage.setDateOfCompletion(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_COMPLETE_DATE)));
            stage.setDateOfReply(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_REPLY_DATE)));
            stage.setNotes(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_NOTES)));
            stage.setInternshipID(cursor.getLong(cursor.getColumnIndex(ApplicationStageTable.COLUMN_INTERNSHIP_ID)));
            stage.setModifiedDate(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_MODIFIED_ON)));

            break;
        }

        cursor.close();

        return stage;
    }

    /**
     * Delete Internsip with matching id
     * @param internshipID id of internship to delete
     */
    public void deleteInternship(long internshipID) {
        //delete the internship row which has an id of internshipID
        mDatabase.delete(InternshipTable.TABLE_INTERNSHIP,
                InternshipTable.COLUMN_ID + " = ?",
                new String[]{Long.toString(internshipID)});

        //delete all application stages that belong to the internship to delete
        mDatabase.delete(ApplicationStageTable.TABLE_APPLICATION_STAGE,
                ApplicationStageTable.COLUMN_INTERNSHIP_ID + " = ?",
                new String[] {Long.toString(internshipID)});
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
     * Update internship row which includes the modified on date
     * @param internship object with all fields filled in
     */
    public void updateInternship(Internship internship) {
        //current date formatted for the modified date column
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        ContentValues values = internship.toValues();
        //update the modified on date by the current date
        values.put(InternshipTable.COLUMN_MODIFIED_ON, currentDate);

        //update internship that has matching id
        mDatabase.update(InternshipTable.TABLE_INTERNSHIP, values,
                InternshipTable.COLUMN_ID + " = ?",
                new String[] {Long.toString(internship.getInternshipID())});
    }

    /**
     * Update internship priority without updating the modified date
     * @param internship object for which to update the priority of
     */
    public void updateInternshipPriority(Internship internship) {

        ContentValues values = new ContentValues();
        //update the modified on date by the current date
        values.put(InternshipTable.COLUMN_PRIORITY, internship.isPriority());

        //update internship that has matching id
        mDatabase.update(InternshipTable.TABLE_INTERNSHIP, values,
                InternshipTable.COLUMN_ID + " = ?",
                new String[] {Long.toString(internship.getInternshipID())});
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

        //update the Internship's modified_on column as the current date too
        ContentValues parentInternshipValues = new ContentValues();
        parentInternshipValues.put(InternshipTable.COLUMN_MODIFIED_ON, currentDate);

        //updated the internship's modified date, that the application stage belongs to
        mDatabase.update(InternshipTable.TABLE_INTERNSHIP, parentInternshipValues, InternshipTable.COLUMN_ID + " = ?",
                new String[] {Long.toString(applicationStage.getInternshipID())});
    }

    public Set<String> getAllRoles() {
        Set<String> roles = new LinkedHashSet<>();

        //query the whole Internship Table for all rows in descending order of modified date
        Cursor cursor = mDatabase.query(InternshipTable.TABLE_INTERNSHIP, InternshipTable.ALL_COLUMNS,
                null, null, null, null, InternshipTable.COLUMN_ROLE);

        //while there is a next row
        while (cursor.moveToNext()) {
            roles.add(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_ROLE)));
        }

        cursor.close();

        return roles;
    }

    public Set<String> getAllLengths() {
        Set<String> lengths = new LinkedHashSet<>();

        //query the whole Internship Table for all rows in descending order of modified date
        Cursor cursor = mDatabase.query(InternshipTable.TABLE_INTERNSHIP, InternshipTable.ALL_COLUMNS,
                null, null, null, null, InternshipTable.COLUMN_LENGTH);

        //while there is a next row
        while (cursor.moveToNext()) {
            lengths.add(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_LENGTH)));
        }

        cursor.close();

        return lengths;
    }

    public Set<String> getAllLocations() {
        Set<String> locations = new LinkedHashSet<>();

        //query the whole Internship Table for all rows in descending order of modified date
        Cursor cursor = mDatabase.query(InternshipTable.TABLE_INTERNSHIP, InternshipTable.ALL_COLUMNS,
                null, null, null, null, InternshipTable.COLUMN_LOCATION);

        //while there is a next row
        while (cursor.moveToNext()) {
            locations.add(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_LOCATION)));
        }

        cursor.close();

        return locations;
    }

    public Set<Integer> getAllSalary() {
        Set<Integer> salaries = new LinkedHashSet<>();

        //query the whole Internship Table for all rows in descending order of modified date
        Cursor cursor = mDatabase.query(InternshipTable.TABLE_INTERNSHIP, InternshipTable.ALL_COLUMNS,
                null, null, null, null, InternshipTable.COLUMN_SALARY + " DESC");

        //while there is a next row
        while (cursor.moveToNext()) {
            salaries.add(cursor.getInt(cursor.getColumnIndex(InternshipTable.COLUMN_SALARY)));
        }

        cursor.close();

        return salaries;
    }

    public Set<String> getAllStageNames() {
        Set<String> stages = new LinkedHashSet<>();

        //query the whole Internship Table for all rows in descending order of modified date
        Cursor cursor = mDatabase.query(InternshipTable.TABLE_INTERNSHIP, InternshipTable.ALL_COLUMNS,
                null, null, null, null, ApplicationStageTable.COLUMN_STAGE_NAME);

        //while there is a next row
        while (cursor.moveToNext()) {
            stages.add(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_STAGE_NAME)));
        }

        cursor.close();

        return stages;
    }

}
