package com.haarismemon.applicationorganiser.model;

import android.content.ContentValues;

import com.haarismemon.applicationorganiser.database.ApplicationStageTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * This class represents a stage in the internship application process
 * @author HaarisMemon
 */
public class ApplicationStage {

    public enum Status {
        SUCCESSFUL("Successful"), WAITING("In Progress"),
        UNSUCCESSFUL("Unsuccessful"), INCOMPLETE("Incomplete");

        private String text;

        Status(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private long stageID;
    private String stageName;
    private boolean isCompleted;
    private boolean isWaitingForResponse;
    private boolean isSuccessful;
    private String dateOfStart;
    private String dateOfCompletion;
    private String dateOfReply;
    private String notes;
    private long internshipID;
    private String modifiedDate;

    public static final String[] defaultApplicationStageNames = {
            "Online Application Form", "Online Situation Judgement Test", "Online Numerical Test",
            "Online Verbal Reasoning Test", "Online Abstract Test", "Telephone Interview",
            "Online Video Interview", "Business Interview", "Assessment Centre"
    };

    /**
     * Returns the ID of the application stage from the database
     * @return the ID of the application stage in the database table
     */
    public long getStageID() {
        return stageID;
    }

    /**
     * Sets the ID of the application stage from the database
     * @param stageID of the application stage in the database table
     */
    public void setStageID(long stageID) {
        this.stageID = stageID;
    }

    /**
     * Returns the name of the stage in the application process
     * @return name of application stage
     */
    public String getStageName() {
        if(stageName != null && stageName.equals("")) stageName = null;
        return stageName;
    }

    /**
     * Sets the name of the stage in the application process
     * e.g. Online Application, Online Interview, or Assessment Centre
     * @param stageName
     */
    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    /**
     * Returns true if the user has completed this application stage
     * @return true if the user has completed this application stage, false otherwise.
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Whether the user has completed the application stage
     * @param completed where user completed the application stage
     */
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    /**
     * Returns true if the user is currently waiting for a response for an application stage
     * @return true if the user is currently waiting for response, false otherwise.
     */
    public boolean isWaitingForResponse() {
        return isWaitingForResponse;
    }

    /**
     * Whether the user is currently waiting for a response for an application stage
     * @param waitingForResponse whether the user is currently waiting for a response
     */
    public void setWaitingForResponse(boolean waitingForResponse) {
        isWaitingForResponse = waitingForResponse;
    }

    /**
     * Returns true if the user is successful in this application stage
     * @return true if the user is successful in this application stage, false otherwise.
     */
    public boolean isSuccessful() {
        return isSuccessful;
    }

    /**
     * Whether the user is successful in this application stage
     * @param successful whether the user is successful in this application stage
     */
    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    /**
     * Returns the date at which the application stage had started
     * @return date at which the application stage had started
     */
    public String getDateOfStart() {
        if(dateOfStart != null && dateOfStart.equals("null")) return null;
        return dateOfStart;
    }

    /**
     * Sets the date at which the application stage had started
     * @param dateOfStart when the application stage had started
     */
    public void setDateOfStart(String dateOfStart) {
        this.dateOfStart = dateOfStart;
    }

    /**
     * Returns the date at which the application stage had been completed
     * @return date at which the application stage had been completed
     */
    public String getDateOfCompletion() {
        if(dateOfCompletion != null && dateOfCompletion.equals("null")) return null;
        return dateOfCompletion;
    }

    /**
     * Sets the date at which the application stage had been completed
     * @param dateOfCompletion when the application stage had been completed
     */
    public void setDateOfCompletion(String dateOfCompletion) {
        this.dateOfCompletion = dateOfCompletion;
    }

    /**
     * Returns the date at which user received reply about application stage
     * @return date at which user received reply about application stage
     */
    public String getDateOfReply() {
        if(dateOfReply != null && dateOfReply.equals("null")) return null;
        return dateOfReply;
    }

    /**
     * Sets the date at which user received reply about application stage
     * @param dateOfReply when user received reply about application stage
     */
    public void setDateOfReply(String dateOfReply) {
        this.dateOfReply = dateOfReply;
    }

    /**
     * Returns the notes/information the user has provided
     * @return notes the user has provided
     */
    public String getNotes() {
        if(notes != null && notes.equals("")) notes = null;
        return notes;
    }

    /**
     * Sets the notes/information the user has provided
     * @param notes the user has provided
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Returns the ID of Internship that the application stage belongs to, obtained from database
     * @return ID of Internship which this application stage belongs to
     */
    public long getInternshipID() {
        return internshipID;
    }

    /**
     * Sets the ID of Internship that the application stage belongs to, obtained from database table
     * @param internshipID that the application stage is linked to
     */
    public void setInternshipID(long internshipID) {
        this.internshipID = internshipID;
    }

    /**
     * Returns the date that the Application Stage was last updated in the database
     * Date returned in friendly format (MMM dd HH:mm) to be displayed in app
     * @return string date of when Application Stage last updated in format (MMM dd HH:mm)
     */
    public String getModifiedShortDateTime() {
        if(modifiedDate != null) {
            SimpleDateFormat toDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat toString = new SimpleDateFormat("MMM dd HH:mm");

            try {
                //covert date to friendly format to display in app
                return toString.format(toDate.parse(modifiedDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Returns the date that the Internship was last updated in the database
     * Date returned in friendly format (MMM dd) to be displayed in app
     * @return string date of when Internship last updated in format (MMM dd)
     */
    public String getModifiedShortDate() {
        if(modifiedDate != null) {
            SimpleDateFormat toDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat toString = new SimpleDateFormat("dd MMM");

            try {
                return toString.format(toDate.parse(modifiedDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Sets the date that the Application Stage was last updated in the database
     * @param modifiedDate of when Application Stage last updated
     */
    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    /**
     * Returns the current status of this application stage as an enum.
     * Possible values: SUCCESSFUL, WAITING, UNSUCCESSFUL, INCOMPLETE
     * @return status enum of application stage
     */
    public Status getCurrentStatus() {
        if(isCompleted) {
            if(isWaitingForResponse) {
                return Status.WAITING;
            } else {
                if(isSuccessful) {
                    return Status.SUCCESSFUL;
                } else {
                    return Status.UNSUCCESSFUL;
                }
            }
        } else {
            return Status.INCOMPLETE;
        }
    }

    /**
     * Returns a string representation of the Application Stage
     * @return String of Application Stage's "[stage name] - [Current Status]"
     */
    @Override
    public String toString() {
        return stageName + " - " + getCurrentStatus();
    }

    /**
     * Check if two Application Stage objects are equal
     * @param obj The second Application Stage object to compare it to
     * @return true if the two Application Stage objects have same stage name
     */
    @Override
    public boolean equals(Object obj) {
        return stageName.equals(((ApplicationStage) obj).getStageName());
    }

    /**
     * Generates a ContentValues object with all fields of Application Stage stored, to use for database
     * @return
     */
    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(ApplicationStageTable.COLUMN_STAGE_NAME, stageName);
        values.put(ApplicationStageTable.COLUMN_IS_COMPLETED, isCompleted);
        values.put(ApplicationStageTable.COLUMN_IS_WAITING, isWaitingForResponse);
        values.put(ApplicationStageTable.COLUMN_IS_SUCCESSFUL, isSuccessful);
        values.put(ApplicationStageTable.COLUMN_START_DATE, dateOfStart);
        values.put(ApplicationStageTable.COLUMN_COMPLETE_DATE, dateOfCompletion);
        values.put(ApplicationStageTable.COLUMN_REPLY_DATE, dateOfReply);
        values.put(ApplicationStageTable.COLUMN_NOTES, notes);
        values.put(ApplicationStageTable.COLUMN_INTERNSHIP_ID, internshipID);

        //if no modified date, then Application Stage not stored in database yet (newly created)
        if(modifiedDate != null) {
            values.put(ApplicationStageTable.COLUMN_MODIFIED_ON, modifiedDate);
        }

        return values;
    }

}
