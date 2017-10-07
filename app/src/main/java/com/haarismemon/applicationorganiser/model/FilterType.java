package com.haarismemon.applicationorganiser.model;

/**
 * This class represents the enum for the filter types.
 */

public enum FilterType {

    ROLE("Role"), LENGTH("Length"), LOCATION("Location"),
    SALARY("Salary"), STAGE("Stage");

    private String text;

    FilterType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static FilterType findFilterByName(String filterName) {
        for(FilterType filterType : FilterType.values()) {
            if(filterName.toLowerCase().equals(filterType.toString().toLowerCase())) {
                return filterType;
            }
        }

        return null;
    }

}