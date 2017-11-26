package com.memonade.apptracker.model;

/**
 * This class represents an Open Source Library with its Licence.
 */

public class OpenSourceLibrary {

    public enum Type {
        APACHE("Apache 2.0 License"), MIT ("MIT Licence");

        String licenceName;

        Type(String licenceName) {
            this.licenceName = licenceName;
        }
    }

    private String name;
    private Type licenceType;
    private String url;

    public OpenSourceLibrary(String name, Type licenceType, String url) {
        this.name = name;
        this.licenceType = licenceType;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getLicenceType() {
        return licenceType;
    }

    public void setLicenceType(Type licenceType) {
        this.licenceType = licenceType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
