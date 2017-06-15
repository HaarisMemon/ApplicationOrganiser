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

    private String stageName;
    private boolean isCompleted;
    private boolean isWaitingForResponse;
    private boolean isSuccessful;
    private String dateOfStart;
    private String dateOfCompletion;
    private String dateOfReply;
    private String description;
    private long internshipID;

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

    public String getCurrentStatus() {
        if(isCompleted) {
            if(isWaitingForResponse) {
                return "Waiting";
            } else {
                if(isSuccessful() != null) {
                    if(isSuccessful) {
                        return "Successful!";
                    } else {
                        return "Failed";
                    }
                } else return "Don't Know";
            }
        } else {
            return "Not Completed";
        }
    }

    public String getStageName() {
        return stageName;
    }

    public String getDateOfStart() {
        return dateOfStart;
    }

    public String getDateOfCompletion() {
        return dateOfCompletion;
    }

    public String getDateOfReply() {
        return dateOfReply;
    }

    public void setStartDate(String dateOfStart) {
        this.dateOfStart = dateOfStart;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setCompleted(boolean completed, String date) {
        setCompleted(completed);
        this.dateOfCompletion = date;
    }

    public boolean isWaitingForResponse() {
        return isWaitingForResponse;
    }

    public void setWaitingForResponse(boolean waitingForResponse) {
        isWaitingForResponse = waitingForResponse;
    }

    public Boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(Boolean successful) {
        isSuccessful = successful;
        //if the user specifies if stage is successful or not, then they are no longer waiting for a response
        isWaitingForResponse = false;
    }

    public void setSuccessful(Boolean successful, String date) {
        setSuccessful(successful);
        this.dateOfReply = date;
    }

    @Override
    public String toString() {
        String s = stageName;

        if(isCompleted) {
            if(isWaitingForResponse) {
                s += " - Waiting";
            } else {
                if(isSuccessful() != null) {
                    if(isSuccessful) {
                        s+= " - Successful!";
                    } else {
                        s+= " - Failed";
                    }
                } else s += " - Don't Know";
            }
        } else {
            s += " - Not Completed";
        }

        return s;
    }

    @Override
    public boolean equals(Object obj) {
        return stageName.equals(((ApplicationStage) obj).getStageName());
    }
}
