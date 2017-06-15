package com.haarismemon.applicationorganiser.model;

import android.content.ContentValues;

import com.haarismemon.applicationorganiser.database.InternshipTable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the Internship that a user has applied to.
 */
public class Internship {

    private long internshipId;
    private String company_name;
    private String role;
    private String length;
    private String location;
    private String description;

    public Internship() {
    }

    public Internship(String company_name, String role, String length, String location) {
        this.company_name = company_name;
        this.role = role;
        this.length = length;
        this.location = location;
    }

    @Override
    public boolean equals(Object obj) {
        boolean isSameCompanyName = company_name.equals(((Internship) obj).company_name);
        boolean isSameRole = role.equals(((Internship) obj).role);
        return isSameCompanyName && isSameRole;
    }

    @Override
    public String toString() {
//        String internshipString = company_name+","+role+","+length+","+location;
        String internshipString = company_name + " - " + role;
        return internshipString;
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(InternshipTable.COLUMN_COMPANY_NAME, company_name);
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

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
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
