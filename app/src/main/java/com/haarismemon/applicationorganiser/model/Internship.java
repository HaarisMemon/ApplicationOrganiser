package com.haarismemon.applicationorganiser.model;

import android.content.ContentValues;

import com.haarismemon.applicationorganiser.database.InternshipTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the Internship that a user has or will apply to
 * @author Haaris Memon
 */
public class Internship {

    private long internshipID;
    private String companyName;
    private String role;
    private String length;
    private String location;
    private String description;
    private String modifiedDate;

    //An internship application will contain a number of stages the user has reached in their application
    private List<ApplicationStage> applicationStages;

    public Internship() {
        applicationStages = new ArrayList<>();
    }

    /**
     * Check if two Internship objects are equal
     * @param obj The second Internship object to compare it to
     * @return true if the two Intnernship objects have same company name and role
     */
    @Override
    public boolean equals(Object obj) {
        boolean isSameCompanyName = companyName.equals(((Internship) obj).companyName);
        boolean isSameRole = role.equals(((Internship) obj).role);
        return isSameCompanyName && isSameRole;
    }

    /**
     * Returns a string representation of the Internship
     * @return String of Internship's "[company name] - [role]"
     */
    @Override
    public String toString() {
        String internshipString = companyName + " - " + role;
        return internshipString;
    }

    /**
     * Generates a ContentValues object with all fields of Internship stored, to use for database
     * @return
     */
    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(InternshipTable.COLUMN_COMPANY_NAME, companyName);
        values.put(InternshipTable.COLUMN_ROLE, role);
        values.put(InternshipTable.COLUMN_LENGTH, length);
        values.put(InternshipTable.COLUMN_LOCATION, location);
        values.put(InternshipTable.COLUMN_DESCRIPTION, description);

        //if no modified date, then Internship not stored in database yet (newly created)
        if(modifiedDate != null) {
            values.put(InternshipTable.COLUMN_MODIFIED_ON, modifiedDate);
        }

        return values;
    }

    /**
     * Returns the Internship ID in database table
     * @return ID of Internship row in database
     */
    public long getInternshipID() {
        return internshipID;
    }

    /**
     * Sets the Internship ID obtained from database table
     * @param internshipID of Internship row in database
     */
    public void setInternshipID(long internshipID) {
        this.internshipID = internshipID;
    }

    /**
     * Returns the company name of Internship
     * @return company name of company offering the Internship
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Sets the company name of Internship
     * @param companyName of  of company offering the Internship
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * Returns the role of the Internship
     * @return internship role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role of the Internship
     * @param role of Internship
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns the length/duration of Internship
     * @return length of employment in the Internship
     */
    public String getLength() {
        return length;
    }

    /**
     * Sets the length/duration of Internship
     * @param length of employment in the Internship
     */
    public void setLength(String length) {
        this.length = length;
    }

    /**
     * Returns the location of company
     * @return location of company where Internship is located
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of company
     * @param location of company where Internship is located
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Returns the description/information the user has provided
     * @return description the user has provided
     */
    public String getDescription() {
        if(description != null && description.equals("")) return null;
        return description;
    }

    /**
     * Sets the description/information the user has provided
     * @param description the user has provided
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the date that the Internship was last updated in the database
     * Date returned in friendly format (MMM dd HH:mm) to be displayed in app
     * @return string date of when Internship last updated
     */
    public String getModifiedDate() {
        SimpleDateFormat toDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat toString = new SimpleDateFormat("dd MMM HH:mm");

        try {
            return toString.format(toDate.parse(modifiedDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Sets the date that the Internship was last updated in the database
     * @param modifiedDate of when Internship last updated
     */
    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    /**
     * Returns all the stages of the Internship application
     * @return stages of the Internship application
     */
    public List<ApplicationStage> getApplicationStages() {
        return applicationStages;
    }

    /**
     * Adds an application stage to the Internship
     * @param stage to be added to Internship
     */
    public void addStage(ApplicationStage stage) {
        applicationStages.add(stage);
    }

}
