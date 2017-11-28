package com.memonade.apptracker.model;

import android.content.ContentValues;

import com.memonade.apptracker.database.ApplicationTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the Application that a user has or will apply to
 * @author HaarisMemon
 */
public class Application {

    private long applicationID;
    private String companyName;
    private String role;
    private String length;
    private String location;
    private boolean priority;
    private String url;
    private int salary;
    private String notes;
    private String createdDate;
    private String modifiedDate;
    private boolean isSelected;

    private final String databaseDatePattern = "yyyy-MM-dd HH:mm:ss";

    //An application application will contain a number of stages the user has reached in their application
    private List<Stage> stages;

    public Application() {
        stages = new ArrayList<>();
    }

    /**
     * Returns the most recent and current stage of an application
     * @return The most recent stage
     */
    public Stage getCurrentStage() {
        if(!stages.isEmpty()) return stages.get(stages.size() - 1);
        return null;
    }

    /**
     * Check if two Application objects are equal
     * @param obj The second Application object to compare it to
     * @return true if the two Intnernship objects have same company name and role
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof  Application) {
            Application application = (Application) obj;
            boolean isSameCompanyName = companyName.equals(application.companyName);
            boolean isSameRole = role.equals(application.role);
            return isSameCompanyName && isSameRole;
        } else
            return false;
    }

    /**
     * Returns a string representation of the Application
     * @return String of Application's "[company name] - [role]"
     */
    @Override
    public String toString() {
        return companyName + " - " + role;
    }

    /**
     * Generates a ContentValues object with all fields of Application stored, to use for database
     * @return ContentValue object with all values of the application
     */
    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(ApplicationTable.COLUMN_COMPANY_NAME, companyName);
        values.put(ApplicationTable.COLUMN_ROLE, role);
        values.put(ApplicationTable.COLUMN_LENGTH, length);
        values.put(ApplicationTable.COLUMN_LOCATION, location);
        values.put(ApplicationTable.COLUMN_PRIORITY, priority);
        values.put(ApplicationTable.COLUMN_URL, url);
        values.put(ApplicationTable.COLUMN_SALARY, salary);
        values.put(ApplicationTable.COLUMN_NOTES, notes);

        //if no created date, then Application not stored in database yet (newly created)
        if(createdDate != null) {
            values.put(ApplicationTable.COLUMN_CREATED_ON, createdDate);
        }

        //if no modified date, then Application not stored in database yet (newly created)
        if(modifiedDate != null) {
            values.put(ApplicationTable.COLUMN_MODIFIED_ON, modifiedDate);
        }

        return values;
    }

    /**
     * Returns the Application ID in database table
     * @return ID of Application row in database
     */
    public long getApplicationID() {
        return applicationID;
    }

    /**
     * Sets the Application ID obtained from database table
     * @param applicationID of Application row in database
     */
    public void setApplicationID(long applicationID) {
        this.applicationID = applicationID;
    }

    /**
     * Returns the company name of Application
     * @return company name of company offering the Application
     */
    public String getCompanyName() {
        if(companyName != null && companyName.equals("")) companyName = null;
        return companyName;
    }

    /**
     * Sets the company name of Application
     * @param companyName of  of company offering the Application
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * Returns the role of the Application
     * @return application role
     */
    public String getRole() {
        if(role != null && role.equals("")) role = null;
        return role;
    }

    /**
     * Sets the role of the Application
     * @param role of Application
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns the length/duration of Application
     * @return length of employment in the Application
     */
    public String getLength() {
        if(length != null && length.equals("")) length = null;
        return length;
    }

    /**
     * Sets the length/duration of Application
     * @param length of employment in the Application
     */
    public void setLength(String length) {
        this.length = length;
    }

    /**
     * Returns the location of company
     * @return location of company where Application is located
     */
    public String getLocation() {
        if(location != null && location.equals("")) location = null;
        return location;
    }

    /**
     * Sets the location of company
     * @param location of company where Application is located
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Returns if the application is high priority
     * @return true if the application is high priority
     */
    public boolean isPriority() {
        return priority;
    }

    /**
     * Sets the priority of the application
     * @param priority set to true if application is high priority
     */
    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    /**
     * Returns the url of the website for application
     * @return url website string for application
     */
    public String getUrl() {
        if(url != null && url.equals("")) url = null;
        return url;
    }

    /**
     * Sets the url website of the application
     * @param url website of the application
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Returns the salary of the application
     * @return salary of the application
     */
    public int getSalary() {
        return salary;
    }

    /**
     * Sets the salary of the application
     * @param salary salary of the application
     */
    public void setSalary(int salary) {
        this.salary = salary;
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
     * Returns the date that the Application was last updated in the database
     * Date returned in friendly format (MMM dd HH:mm) to be displayed in app
     * @return string date of when Application last updated in format (MMM dd HH:mm)
     */
    public String getModifiedShortDateTime() {
        SimpleDateFormat toDate = new SimpleDateFormat(databaseDatePattern);
        String loneDatePattern = "dd MMM HH:mm";
        SimpleDateFormat toString = new SimpleDateFormat(loneDatePattern);

        try {
            return toString.format(toDate.parse(modifiedDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns the date that the Application was last updated in the database
     * Date returned in friendly format (MMM dd) to be displayed in app
     * @return string date of when Application last updated in format (MMM dd)
     */
    public String getModifiedShortDate() {
        SimpleDateFormat toDate = new SimpleDateFormat(databaseDatePattern);
        String shortDatePattern = "dd MMM";
        SimpleDateFormat toString = new SimpleDateFormat(shortDatePattern);

        try {
            return toString.format(toDate.parse(modifiedDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    /**
     * Sets the date that the Application was last updated in the database
     * @param modifiedDate of when Application last updated
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
     * Returns all the stages of the Application application
     * @return stages of the Application application
     */
    public List<Stage> getStages() {
        return stages;
    }

    /**
     * Sets a list of all the stages of the Application application
     * @param stages all stages of the Application application
     */
    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    /**
     * Adds an stage to the Application
     * @param stage to be added to Application
     */
    public void addStage(Stage stage) {
        stages.add(stage);
    }

    /**
     * Returns whether the application has been selected in the RecyclerView
     * @return true if it has been selected in the RecyclerView
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Set whether the application has been selected in the RecyclerView
     * @param selected - true if it has been selected in the RecyclerView
     */
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    
    public static Application of(String companyName,
                                 String role,
                                 String length,
                                 String location,
                                 boolean priority,
                                 String url,
                                 int salary,
                                 String notes,
                                 boolean isSelected) {
        Application application = new Application();

        application.setCompanyName(companyName);
        application.setRole(role);
        application.setLength(length);
        application.setLocation(location);
        application.setPriority(priority);
        application.setUrl(url);
        application.setSalary(salary);
        application.setNotes(notes);
        application.setSelected(isSelected);

        return application;
    }
}
