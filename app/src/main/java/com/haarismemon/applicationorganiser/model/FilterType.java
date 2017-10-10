package com.haarismemon.applicationorganiser.model;

/**
 * This class represents the enum for the filter types.
 */

public enum FilterType {

    ROLE("Role", "Roles"), LENGTH("Length", "Lengths"), LOCATION("Location", "Locations"),
    SALARY("Salary", "Salaries"), STAGE("Stage", "Stages"), STATUS("Status", "Status");

    private String text;
    private String textPlural;

    FilterType(String text, String textPlural) {
        this.text = text;
        this.textPlural = textPlural;
    }

    @Override
    public String toString() {
        return text;
    }

    public String getTextPlural() {
        return textPlural;
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