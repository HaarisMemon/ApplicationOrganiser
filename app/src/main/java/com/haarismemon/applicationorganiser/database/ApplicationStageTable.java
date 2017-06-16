package com.haarismemon.applicationorganiser.database;

/**
 * Created by Haaris on 15/06/2017.
 */

public class ApplicationStageTable {

    public static final String TABLE_APPLICATION_STAGE = "application_stage";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_STAGE_NAME = "stage_name";
    public static final String COLUMN_IS_COMPLETED = "is_completed";
    public static final String COLUMN_IS_WAITING = "is_waiting";
    public static final String COLUMN_IS_SUCCESSFUL = "is_successful";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_COMPLETE_DATE = "complete_date";
    public static final String COLUMN_REPLY_DATE = "reply_date";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CREATED_ON = "created_on";
    public static final String COLUMN_MODIFIED_ON = "modified_on";
    public static final String COLUMN_INTERNSHIP_ID = "internship_id";

    public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_STAGE_NAME, COLUMN_IS_COMPLETED,
            COLUMN_IS_WAITING, COLUMN_IS_SUCCESSFUL, COLUMN_START_DATE, COLUMN_COMPLETE_DATE,
            COLUMN_REPLY_DATE, COLUMN_DESCRIPTION, COLUMN_CREATED_ON, COLUMN_MODIFIED_ON,
            COLUMN_INTERNSHIP_ID
    };

    public static final String SQL_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_APPLICATION_STAGE +
                    " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_STAGE_NAME + " VARCHAR, " +
                    COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0, " +
                    COLUMN_IS_WAITING + " INTEGER DEFAULT 0, " +
                    COLUMN_IS_SUCCESSFUL + " INTEGER DEFAULT 0, " +
                    //TEXT as ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS").
                    COLUMN_START_DATE + " VARCHAR, " +
                    COLUMN_COMPLETE_DATE + " DATETIME, " +
                    COLUMN_REPLY_DATE + " DATETIME, " +
                    COLUMN_DESCRIPTION + " DATETIME, " +
                    COLUMN_CREATED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    COLUMN_MODIFIED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    COLUMN_INTERNSHIP_ID + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_INTERNSHIP_ID + ") REFERENCES " + InternshipTable.TABLE_INTERNSHIP + "(" + InternshipTable.COLUMN_ID + ") " +
                    ")";

    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_APPLICATION_STAGE;

}
