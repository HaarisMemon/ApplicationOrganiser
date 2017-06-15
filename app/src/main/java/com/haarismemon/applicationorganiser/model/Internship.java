package com.haarismemon.applicationorganiser.model;

import android.content.ContentValues;

import com.haarismemon.applicationorganiser.database.InternshipTable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the Internship that a user has applied to.
 */
public class Internship {

    public long internshipId;
    public String company_name;
    public String role;
    public String length;
    public String location;
    public String description;

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
        String internshipString = company_name+","+role+","+length+","+location;
        return internshipString;
    }
}
