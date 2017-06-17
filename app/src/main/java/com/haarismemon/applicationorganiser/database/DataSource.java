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
import java.util.List;

/**
 * Created by Haaris on 15/06/2017.
 */

public class DataSource {

    private Context mContext;
    private SQLiteDatabase mDatabase;
    private DBHelper mDbHelper;

    public DataSource(Context Context) {
        this.mContext = Context;
        mDbHelper = new DBHelper(mContext);
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void open() {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }

    public Internship createInternship(Internship internship) {
        ContentValues values = internship.toValues();
        long internshipID = mDatabase.insert(InternshipTable.TABLE_INTERNSHIP, null, values);
        internship.setInternshipId(internshipID);
        return internship;
    }

    public ApplicationStage createApplicationStage(ApplicationStage applicationStage) {
        ContentValues values = applicationStage.toValues();
        mDatabase.insert(ApplicationStageTable.TABLE_APPLICATION_STAGE, null, values);
        return applicationStage;
    }

    public void seedDatbase() {
        long numInternships = DatabaseUtils.queryNumEntries(mDatabase, InternshipTable.TABLE_INTERNSHIP);
        long numApplicationStageCount = DatabaseUtils.queryNumEntries(mDatabase, ApplicationStageTable.TABLE_APPLICATION_STAGE);

        if(numInternships == 0) {

            List<Internship> internships = SeedApplications.parse();

            if(numApplicationStageCount == 0) {

                for (Internship internship : internships) {

                    createInternship(internship);

                    for (ApplicationStage stage : internship.getApplicationStages()) {

                        stage.setInternshipID(internship.getInternshipID());

                        createApplicationStage(stage);

                    }
                }
            }
        }
    }

    public List<Internship> getAllInternship() {
        List<Internship> internships = new ArrayList<>();

        Cursor cursor = mDatabase.query(InternshipTable.TABLE_INTERNSHIP, InternshipTable.ALL_COLUMNS,
                null, null, null, null, InternshipTable.COLUMN_MODIFIED_ON + " DESC");

        while(cursor.moveToNext()) {
            Internship internship = new Internship();

            internship.setInternshipId(cursor.getLong(cursor.getColumnIndex(InternshipTable.COLUMN_ID)));
            internship.setCompanyName(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_COMPANY_NAME)));
            internship.setRole(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_ROLE)));
            internship.setLength(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_LENGTH)));
            internship.setLocation(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_LOCATION)));
            internship.setDescription(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_DESCRIPTION)));
            internship.setModifiedDate(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_MODIFIED_ON)));

            internships.add(internship);
        }

        cursor.close();

        return internships;
    }

    public Internship getInternship(long internshipID) {
        Cursor cursor = mDatabase.query(InternshipTable.TABLE_INTERNSHIP, InternshipTable.ALL_COLUMNS,
                InternshipTable.COLUMN_ID + "=?", new String[] {Long.toString(internshipID)}, null, null, null);

        Internship internship = new Internship();
        while(cursor.moveToNext()) {

            internship.setInternshipId(cursor.getLong(cursor.getColumnIndex(InternshipTable.COLUMN_ID)));
            internship.setCompanyName(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_COMPANY_NAME)));
            internship.setRole(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_ROLE)));
            internship.setLength(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_LENGTH)));
            internship.setLocation(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_LOCATION)));
            internship.setDescription(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_DESCRIPTION)));
            internship.setModifiedDate(cursor.getString(cursor.getColumnIndex(InternshipTable.COLUMN_MODIFIED_ON)));

            break;
        }

        cursor.close();

        return internship;
    }

    public List<ApplicationStage> getAllApplicationStages(long internshipID) {
        List<ApplicationStage> applicationStages = new ArrayList<>();

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
            stage.setDescription(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_DESCRIPTION)));
            stage.setInternshipID(cursor.getLong(cursor.getColumnIndex(ApplicationStageTable.COLUMN_INTERNSHIP_ID)));
            stage.setModifiedDate(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_MODIFIED_ON)));

            applicationStages.add(stage);
        }

        cursor.close();

        return applicationStages;
    }

    public ApplicationStage getApplicationStage(long applicationStageID) {
        Cursor cursor = mDatabase.query(ApplicationStageTable.TABLE_APPLICATION_STAGE, ApplicationStageTable.ALL_COLUMNS,
                ApplicationStageTable.COLUMN_ID + "=?", new String[] {Long.toString(applicationStageID)}, null, null, null);

        ApplicationStage stage = new ApplicationStage();
        while(cursor.moveToNext()) {

            stage.setStageID(cursor.getLong(cursor.getColumnIndex(ApplicationStageTable.COLUMN_ID)));
            stage.setStageName(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_STAGE_NAME)));
            stage.setCompleted(cursor.getInt(cursor.getColumnIndex(ApplicationStageTable.COLUMN_IS_COMPLETED)) == 1);
            stage.setWaitingForResponse(cursor.getInt(cursor.getColumnIndex(ApplicationStageTable.COLUMN_IS_WAITING)) == 1);
            stage.setSuccessful(cursor.getInt(cursor.getColumnIndex(ApplicationStageTable.COLUMN_IS_SUCCESSFUL)) == 1);
            stage.setDateOfStart(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_START_DATE)));
            stage.setDateOfCompletion(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_COMPLETE_DATE)));
            stage.setDateOfReply(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_REPLY_DATE)));
            stage.setDescription(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_DESCRIPTION)));
            stage.setInternshipID(cursor.getLong(cursor.getColumnIndex(ApplicationStageTable.COLUMN_INTERNSHIP_ID)));
            stage.setModifiedDate(cursor.getString(cursor.getColumnIndex(ApplicationStageTable.COLUMN_MODIFIED_ON)));

        }

        cursor.close();

        return stage;
    }

    public void deleteInternship(long internshipID) {
        //delete the internship row which has an id of internshipID
        mDatabase.delete(InternshipTable.TABLE_INTERNSHIP,
                InternshipTable.COLUMN_ID + " = ?",
                new String[]{Long.toString(internshipID)});

        //delete all application stages
        mDatabase.delete(ApplicationStageTable.TABLE_APPLICATION_STAGE,
                ApplicationStageTable.COLUMN_INTERNSHIP_ID + " = ?",
                new String[] {Long.toString(internshipID)});
    }

    public void deleteApplicationStage(long stageID) {
        //delete the application stage row which has an id of stageID
        mDatabase.delete(ApplicationStageTable.TABLE_APPLICATION_STAGE,
                ApplicationStageTable.COLUMN_ID + " = ?",
                new String[] {Long.toString(stageID)});
    }

    public void updateInternship(Internship internship) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        ContentValues values = internship.toValues();
        values.put(InternshipTable.COLUMN_MODIFIED_ON, currentDate);;
        mDatabase.update(InternshipTable.TABLE_INTERNSHIP, values,
                InternshipTable.COLUMN_ID + " = ?",
                new String[] {Long.toString(internship.getInternshipID())});
    }

    public void updateApplicationStage(ApplicationStage applicationStage) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        ContentValues values = applicationStage.toValues();
        values.put(ApplicationStageTable.COLUMN_MODIFIED_ON, currentDate);

        mDatabase.update(ApplicationStageTable.TABLE_APPLICATION_STAGE, values,
                ApplicationStageTable.COLUMN_ID + " = ?",
                new String[] {Long.toString(applicationStage.getStageID())});

        ContentValues parentInternshipValues = new ContentValues();
        parentInternshipValues.put(InternshipTable.COLUMN_MODIFIED_ON, currentDate);

        mDatabase.update(InternshipTable.TABLE_INTERNSHIP, parentInternshipValues, InternshipTable.COLUMN_ID + " = ?",
                new String[] {Long.toString(applicationStage.getInternshipID())});
    }

}
