package com.haarismemon.applicationorganiser.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.model.Internship;

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
        internship.internshipId = internshipID;
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
            Internship ibm = new Internship("IBM", "Software Engineer Placement", "1 Year", "London");
            Internship cognito = new Internship("Cognito iQ", "Software Developer Placement", "1 Year", "Newbury");

            createInternship(ibm);
            createInternship(cognito);

            if(numApplicationStageCount == 0) {

                ApplicationStage onlineApplication = new ApplicationStage("Online Application", true, false, true, "28/01/2017", "28/01/2017", "20/02/2017", ibm.internshipId);
                ApplicationStage test = new ApplicationStage("Test", true, false, true, null, null, "20/02/2017", ibm.internshipId);
                ApplicationStage assessmentCentre = new ApplicationStage("Assessment Centre", true, false, true, "07/03/2017", "07/03/2017", "16/03/2017", ibm.internshipId);

                createApplicationStage(onlineApplication);
                createApplicationStage(test);
                createApplicationStage(assessmentCentre);

            }
        }

    }

}
