package com.haarismemon.applicationorganiser.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.haarismemon.applicationorganiser.model.Internship;

import static android.R.attr.version;

/**
 * Created by Haaris on 15/06/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_FILE_NAME = "applications.db";
    public static final int DB_VERSION = 1;


    public DBHelper(Context context) {
        super(context, DB_FILE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(InternshipTable.SQL_CREATE);
        sqLiteDatabase.execSQL(ApplicationStageTable.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(InternshipTable.SQL_DELETE);
        sqLiteDatabase.execSQL(ApplicationStageTable.SQL_DELETE);
        onCreate(sqLiteDatabase);
    }
}
