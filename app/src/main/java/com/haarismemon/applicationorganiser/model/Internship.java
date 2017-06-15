package com.haarismemon.applicationorganiser.model;

import android.content.ContentValues;

import com.haarismemon.applicationorganiser.database.InternshipTable;

/**
 * This class represents the Internship that a user has applied to.
 */
public class Internship {

    private long internshipId;
    private String companyName;
    private String role;
    private String length;
    private String location;
    private String description;

    public Internship() {
    }

    public Internship(String companyName, String role, String length, String location) {
        this.companyName = companyName;
        this.role = role;
        this.length = length;
        this.location = location;
    }

    @Override
    public boolean equals(Object obj) {
        boolean isSameCompanyName = companyName.equals(((Internship) obj).companyName);
        boolean isSameRole = role.equals(((Internship) obj).role);
        return isSameCompanyName && isSameRole;
    }

    @Override
    public String toString() {
//        String internshipString = companyName+","+role+","+length+","+location;
        String internshipString = companyName + " - " + role;
        return internshipString;
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(InternshipTable.COLUMN_COMPANY_NAME, companyName);
        values.put(InternshipTable.COLUMN_ROLE, role);
        values.put(InternshipTable.COLUMN_LENGTH, length);
        values.put(InternshipTable.COLUMN_LOCATION, location);
        values.put(InternshipTable.COLUMN_DESCRIPTION, description);

        return values;
    }

    public long getInternshipId() {
        return internshipId;
    }

    public void setInternshipId(long internshipId) {
        this.internshipId = internshipId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
