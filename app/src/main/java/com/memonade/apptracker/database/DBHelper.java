package com.memonade.apptracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class is a helper class for the app database to easily create and upgrade all the tables
 * @author HaarisMemon
 */

public class DBHelper extends SQLiteOpenHelper {

    /**
     * The name of the database file
     */
    public static final String DB_FILE_NAME = "applications.db";
    /**
     * The version of the database changes when it gets upgraded
     */
    public static final int DB_VERSION = 1;

    /**
     * Constructs the Database helper
     * @param context of the Application
     */
    public DBHelper(Context context) {
        super(context, DB_FILE_NAME, null, DB_VERSION);
    }

    /**
     * Method called when database is created to make all the tables
     * @param sqLiteDatabase the database object
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ApplicationTable.SQL_CREATE);
        sqLiteDatabase.execSQL(ApplicationStageTable.SQL_CREATE);
    }

    /**
     * Method called when database is upgraded
     * All tables are dropped and the database is created from scratch
     * @param sqLiteDatabase the database object
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(ApplicationTable.SQL_DELETE);
        sqLiteDatabase.execSQL(ApplicationStageTable.SQL_DELETE);
        onCreate(sqLiteDatabase);
    }
}
