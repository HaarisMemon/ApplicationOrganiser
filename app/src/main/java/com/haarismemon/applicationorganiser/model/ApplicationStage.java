package com.haarismemon.applicationorganiser.model;

import android.content.ContentValues;

import com.haarismemon.applicationorganiser.database.ApplicationStageTable;
import com.haarismemon.applicationorganiser.database.InternshipTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class represents a particular ApplicationStage in the application process of an Internship.
 */
public class ApplicationStage {

    private long stageID;
    private String stageName;
    private boolean isCompleted;
    private boolean isWaitingForResponse;
    private boolean isSuccessful;
    private String dateOfStart;
    private String dateOfCompletion;
    private String dateOfReply;
    private String description;
    private long internshipID;
    private String modifiedDate;

    public ApplicationStage() {
    }

    public ApplicationStage(String stageName, boolean isCompleted, boolean isWaitingForResponse, boolean isSuccessful, String dateOfStart, String dateOfCompletion, String dateOfReply, long internshipID) {

        this.stageName = stageName;
        this.isCompleted = isCompleted;
        this.isWaitingForResponse = isWaitingForResponse;
        this.isSuccessful = isSuccessful;
        this.dateOfStart = dateOfStart;
        this.dateOfCompletion = dateOfCompletion;
        this.dateOfReply = dateOfReply;
        this.internshipID = internshipID;
    }

    public long getStageID() {
        return stageID;
    }

    public void setStageID(long stageID) {
        this.stageID = stageID;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isWaitingForResponse() {
        return isWaitingForResponse;
    }

    public void setWaitingForResponse(boolean waitingForResponse) {
        isWaitingForResponse = waitingForResponse;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    public String getDateOfStart() {
        if(dateOfStart != null && dateOfStart.equals("null")) return null;
        return dateOfStart;
    }

    public void setDateOfStart(String dateOfStart) {
        this.dateOfStart = dateOfStart;
    }

    public String getDateOfCompletion() {
        if(dateOfCompletion != null && dateOfCompletion.equals("null")) return null;
        return dateOfCompletion;
    }

    public void setDateOfCompletion(String dateOfCompletion) {
        this.dateOfCompletion = dateOfCompletion;
    }

    public String getDateOfReply() {
        if(dateOfReply != null && dateOfReply.equals("null")) return null;
        return dateOfReply;
    }

    public void setDateOfReply(String dateOfReply) {
        this.dateOfReply = dateOfReply;
    }

    public String getDescription() {
        if(description != null && description.equals("")) return null;
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getInternshipID() {
        return internshipID;
    }

    public void setInternshipID(long internshipID) {
        this.internshipID = internshipID;
    }

    public String getModifiedDate() {
        SimpleDateFormat toDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat toString = new SimpleDateFormat("MMM dd HH:mm");

        try {
            return toString.format(toDate.parse(modifiedDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getCurrentStatus() {
        if(isCompleted) {
            if(isWaitingForResponse) {
                return "Waiting";
            } else {
                if(isSuccessful) {
                    return "Successful!";
                } else {
                    return "Failed";
                }
            }
        } else {
            return "Not Completed";
        }
    }

    @Override
    public String toString() {
        return stageName + " - " + getCurrentStatus();
    }

    @Override
    public boolean equals(Object obj) {
        return stageName.equals(((ApplicationStage) obj).getStageName());
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(ApplicationStageTable.COLUMN_STAGE_NAME, stageName);
        values.put(ApplicationStageTable.COLUMN_IS_COMPLETED, isCompleted);
        values.put(ApplicationStageTable.COLUMN_IS_WAITING, isWaitingForResponse);
        values.put(ApplicationStageTable.COLUMN_IS_SUCCESSFUL, isSuccessful);
        values.put(ApplicationStageTable.COLUMN_START_DATE, dateOfStart);
        values.put(ApplicationStageTable.COLUMN_COMPLETE_DATE, dateOfCompletion);
        values.put(ApplicationStageTable.COLUMN_REPLY_DATE, dateOfReply);
        values.put(ApplicationStageTable.COLUMN_DESCRIPTION, description);
        values.put(ApplicationStageTable.COLUMN_INTERNSHIP_ID, internshipID);

        if(modifiedDate != null) {
            values.put(ApplicationStageTable.COLUMN_MODIFIED_ON, modifiedDate);
        }

        return values;
    }
}
