package com.haarismemon.applicationorganiser.database;

import android.content.ContentValues;
import android.content.Context;
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

}
