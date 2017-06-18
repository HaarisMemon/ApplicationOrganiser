package com.haarismemon.applicationorganiser.database;

/**
 * This class contains all the static string constants for Internship to help
 * create the Internship Table in the database
 * @author Haaris Memon
 */
public class InternshipTable {

    //static String constants for all the columns in the Internship Table
    public static final String TABLE_INTERNSHIP = "internship";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_COMPANY_NAME = "company_name";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_LENGTH = "length";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CREATED_ON = "created_on";
    public static final String COLUMN_MODIFIED_ON = "modified_on";

    /**
     * List of all columns in the Internship Table as String constants
     */
    public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_COMPANY_NAME, COLUMN_ROLE,
            COLUMN_LENGTH, COLUMN_LOCATION, COLUMN_DESCRIPTION,
            COLUMN_CREATED_ON, COLUMN_MODIFIED_ON,
    };

    /**
     * SQL Create statement to create the Internship table will all the columns, if not exists already
     */
    public static final String SQL_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_INTERNSHIP + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_COMPANY_NAME + " VARCHAR, " +
                COLUMN_ROLE + " VARCHAR, " +
                COLUMN_LENGTH + " VARCHAR, " +
                COLUMN_LOCATION + " VARCHAR," +
                COLUMN_DESCRIPTION + " VARCHAR, " +
                COLUMN_CREATED_ON + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')), " +
                COLUMN_MODIFIED_ON + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
            ")";

    /**
     * SQL Create statement to drop the Internship Table
     */
    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_INTERNSHIP;

}
