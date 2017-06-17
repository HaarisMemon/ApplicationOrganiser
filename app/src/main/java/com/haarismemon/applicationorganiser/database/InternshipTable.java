package com.haarismemon.applicationorganiser.database;

import static android.R.attr.id;
import static android.os.Build.ID;

/**
 * Created by Haaris on 15/06/2017.
 */

public class InternshipTable {

    public static final String TABLE_INTERNSHIP = "internship";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_COMPANY_NAME = "company_name";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_LENGTH = "length";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CREATED_ON = "created_on";
    public static final String COLUMN_MODIFIED_ON = "modified_on";

    public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_COMPANY_NAME, COLUMN_ROLE,
            COLUMN_LENGTH, COLUMN_LOCATION, COLUMN_DESCRIPTION,
            COLUMN_CREATED_ON, COLUMN_MODIFIED_ON,
    };

    public static final String SQL_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_INTERNSHIP + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_COMPANY_NAME + " VARCHAR, " +
                COLUMN_ROLE + " VARCHAR, " +
                COLUMN_LENGTH + " VARCHAR, " +
                COLUMN_LOCATION + " VARCHAR," +
                COLUMN_DESCRIPTION + " VARCHAR, " +
                COLUMN_CREATED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_MODIFIED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ")";

//    public static final String SQL_MODIFIED_TRIGGER =
//            "CREATE TRIGGER IF NOT EXISTS internship_modified AFTER INSERT ON " +
//                    InternshipTable.TABLE_INTERNSHIP + " BEGIN UPDATE " +
//                    InternshipTable.TABLE_INTERNSHIP + " SET " +
//                    InternshipTable.COLUMN_MODIFIED_ON + " = CURRENT_TIMESTAMP WHERE " +
//                    InternshipTable.COLUMN_ID + " = NEW." + InternshipTable.COLUMN_ID + "; end";

//    public static final String UPDATE_MODIFIED_ON = "UPDATE " + InternshipTable.TABLE_INTERNSHIP +
//            "SET " + InternshipTable.COLUMN_MODIFIED_ON + " = DateTime('now') " +
//            "WHERE " + InternshipTable.COLUMN_ID + " = ?";

    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_INTERNSHIP;

}
