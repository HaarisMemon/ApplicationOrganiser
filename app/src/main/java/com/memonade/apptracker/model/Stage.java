package com.memonade.apptracker.model;

import android.content.ContentValues;

import com.memonade.apptracker.database.ApplicationTable;
import com.memonade.apptracker.database.StageTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a stage in the application application process
 * @author HaarisMemon
 */
public class Stage {

    private String databaseDatePattern = "yyyy-MM-dd HH:mm:ss";
    private String longDatePattern = "MMM dd HH:mm";

    public enum Status {
        SUCCESSFUL("Successful", "successful"), WAITING("In Progress", "in_progress"),
        UNSUCCESSFUL("Unsuccessful", "unsuccessful"), INCOMPLETE("Incomplete", "incomplete");

        private String text;
        private String iconNameText;

        Status(String text, String iconNameText) {
            this.text = text;
            this.iconNameText = iconNameText;
        }

        @Override
        public String toString() {
            return text;
        }

        public static List<String> getStatusStrings() {
            List<String> strings = new ArrayList<>();
            for(Status status : values()) {
                strings.add(status.toString());
            }
            return strings;
        }

        public String getIconNameText() {
            return iconNameText;
        }
    }

    private long stageID;
    private String stageName;
    private boolean isCompleted;
    private boolean isWaitingForResponse;
    private boolean isSuccessful;
    private String dateOfDeadline;
    private String dateOfStart;
    private String dateOfCompletion;
    private String dateOfReply;
    private String notes;
    private long applicationID;
    private String createdDate;
    private String modifiedDate;

    public static final String[] defaultStageNames = {
            "Online Application", "Online Situation Judgement Test", "Online Numerical Test",
            "Online Abstract Test", "Online Verbal Reasoning Test", "Telephone Interview",
            "Online Video Interview", "Interview", "Assessment Centre"
    };

    /**
     * Returns the ID of the stage from the database
     * @return the ID of the stage in the database table
     */
    public long getStageID() {
        return stageID;
    }

    /**
     * Sets the ID of the stage from the database
     * @param stageID of the stage in the database table
     */
    public void setStageID(long stageID) {
        this.stageID = stageID;
    }

    /**
     * Returns the name of the stage in the application process
     * @return name of stage
     */
    public String getStageName() {
        if(stageName != null && stageName.equals("")) stageName = null;
        return stageName;
    }

    /**
     * Sets the name of the stage in the application process
     * e.g. Online Application, Online Interview, or Assessment Centre
     * @param stageName the name of the stage in the application process
     */
    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    /**
     * Returns true if the user has completed this stage
     * @return true if the user has completed this stage, false otherwise.
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Whether the user has completed the stage
     * @param completed where user completed the stage
     */
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    /**
     * Returns true if the user is currently waiting for a response for an stage
     * @return true if the user is currently waiting for response, false otherwise.
     */
    public boolean isWaitingForResponse() {
        return isWaitingForResponse;
    }

    /**
     * Whether the user is currently waiting for a response for an stage
     * @param waitingForResponse whether the user is currently waiting for a response
     */
    public void setWaitingForResponse(boolean waitingForResponse) {
        isWaitingForResponse = waitingForResponse;
    }

    /**
     * Returns true if the user is successful in this stage
     * @return true if the user is successful in this stage, false otherwise.
     */
    public boolean isSuccessful() {
        return isSuccessful;
    }

    /**
     * Whether the user is successful in this stage
     * @param successful whether the user is successful in this stage
     */
    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    /**
     * Returns the deadline date of the stage
     * @return deadline date of the stage
     */
    public String getDateOfDeadline() {
        if(dateOfDeadline != null && dateOfDeadline.equals("null")) return null;
        return dateOfDeadline;
    }

    /**
     * Sets the deadline date of the stage
     * @param dateOfDeadline the stage's deadline date
     */
    public void setDateOfDeadline(String dateOfDeadline) {
        this.dateOfDeadline = dateOfDeadline;
    }

    /**
     * Returns the date at which the stage had started
     * @return date at which the stage had started
     */
    public String getDateOfStart() {
        if(dateOfStart != null && dateOfStart.equals("null")) return null;
        return dateOfStart;
    }

    /**
     * Sets the date at which the stage had started
     * @param dateOfStart when the stage had started
     */
    public void setDateOfStart(String dateOfStart) {
        this.dateOfStart = dateOfStart;
    }

    /**
     * Returns the date at which the stage had been completed
     * @return date at which the stage had been completed
     */
    public String getDateOfCompletion() {
        if(dateOfCompletion != null && dateOfCompletion.equals("null")) return null;
        return dateOfCompletion;
    }

    /**
     * Sets the date at which the stage had been completed
     * @param dateOfCompletion when the stage had been completed
     */
    public void setDateOfCompletion(String dateOfCompletion) {
        this.dateOfCompletion = dateOfCompletion;
    }

    /**
     * Returns the date at which user received reply about stage
     * @return date at which user received reply about stage
     */
    public String getDateOfReply() {
        if(dateOfReply != null && dateOfReply.equals("null")) return null;
        return dateOfReply;
    }

    /**
     * Sets the date at which user received reply about stage
     * @param dateOfReply when user received reply about stage
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
     * Returns the ID of Application that the stage belongs to, obtained from database
     * @return ID of Application which this stage belongs to
     */
    public long getApplicationID() {
        return applicationID;
    }

    /**
     * Sets the ID of Application that the stage belongs to, obtained from database table
     * @param applicationID that the stage is linked to
     */
    public void setApplicationID(long applicationID) {
        this.applicationID = applicationID;
    }

    /**
     * Returns the date that the Stage was last updated in the database
     * Date returned in friendly format (MMM dd HH:mm) to be displayed in app
     * @return string date of when Stage last updated in format (MMM dd HH:mm)
     */
    public String getModifiedShortDateTime() {
        if(modifiedDate != null) {
            SimpleDateFormat toDate = new SimpleDateFormat(databaseDatePattern);
            SimpleDateFormat toString = new SimpleDateFormat(longDatePattern);

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
     * Returns the date that the Application was last updated in the database
     * Date returned in friendly format (MMM dd) to be displayed in app
     * @return string date of when Application last updated in format (MMM dd)
     */
    public String getModifiedShortDate() {
        if(modifiedDate != null) {
            SimpleDateFormat toDate = new SimpleDateFormat(databaseDatePattern);
            String shortDatePattern = "dd MMM";
            SimpleDateFormat toString = new SimpleDateFormat(shortDatePattern);

            try {
                return toString.format(toDate.parse(modifiedDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Sets the date that the Stage was last updated in the database
     * @param modifiedDate of when Stage last updated
     */
    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Returns the current status of this stage as an enum.
     * Possible values: SUCCESSFUL, WAITING, UNSUCCESSFUL, INCOMPLETE
     * @return status enum of stage
     */
    public Status getStatus() {
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
     * Returns a string representation of the Stage
     * @return String of Stage's "[stage name] - [Current Status]"
     */
    @Override
    public String toString() {
        return stageName + " - " + getStatus();
    }

    /**
     * Check if two Stage objects are equal
     * @param obj The second Stage object to compare it to
     * @return true if the two Stage objects have same stage name
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Stage && stageName.equals(((Stage) obj).getStageName());
    }

    /**
     * Generates a ContentValues object with all fields of Stage stored, to use for database
     * @return ContentValue object with all values of the stage
     */
    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(StageTable.COLUMN_STAGE_NAME, stageName);
        values.put(StageTable.COLUMN_IS_COMPLETED, isCompleted);
        values.put(StageTable.COLUMN_IS_WAITING, isWaitingForResponse);
        values.put(StageTable.COLUMN_IS_SUCCESSFUL, isSuccessful);
        values.put(StageTable.COLUMN_DEADLINE_DATE, dateOfDeadline);
        values.put(StageTable.COLUMN_START_DATE, dateOfStart);
        values.put(StageTable.COLUMN_COMPLETE_DATE, dateOfCompletion);
        values.put(StageTable.COLUMN_REPLY_DATE, dateOfReply);
        values.put(StageTable.COLUMN_NOTES, notes);
        values.put(StageTable.COLUMN_APPLICATION_ID, applicationID);

        //if no created date, then stage not stored in database yet (newly created)
        if(createdDate != null) {
            values.put(ApplicationTable.COLUMN_CREATED_ON, createdDate);
        }

        //if no modified date, then Stage not stored in database yet (newly created)
        if(modifiedDate != null) {
            values.put(StageTable.COLUMN_MODIFIED_ON, modifiedDate);
        }

        return values;
    }
    
    public static Stage of(String stageName,
                           boolean isCompleted,
                           boolean isWaitingForResponse,
                           boolean isSuccessful,
                           String dateOfDeadline,
                           String dateOfStart,
                           String dateOfCompletion,
                           String dateOfReply,
                           String notes) {
        Stage stage = new Stage();
        
        stage.setStageName(stageName);
        stage.setCompleted(isCompleted);
        stage.setWaitingForResponse(isWaitingForResponse);
        stage.setSuccessful(isSuccessful);
        stage.setDateOfDeadline(dateOfDeadline);
        stage.setDateOfStart(dateOfStart);
        stage.setDateOfCompletion(dateOfCompletion);
        stage.setDateOfReply(dateOfReply);
        stage.setNotes(notes);

        return stage;
    }

}
