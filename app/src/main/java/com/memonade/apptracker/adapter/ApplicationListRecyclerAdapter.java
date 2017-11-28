package com.memonade.apptracker.adapter;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.memonade.apptracker.ApplicationInformationActivity;
import com.memonade.apptracker.MainActivity;
import com.memonade.apptracker.R;
import com.memonade.apptracker.database.ApplicationTable;
import com.memonade.apptracker.model.Application;
import com.memonade.apptracker.model.Stage;
import com.memonade.apptracker.view_holder.ApplicationRowViewHolder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.memonade.apptracker.R.string.application;

/**
 * This class represents the Recycler Adapter for the Application List.
 * It constructs the list and puts the application's information onto each card view in the list.
 */
public class ApplicationListRecyclerAdapter extends RecyclerView.Adapter<ApplicationRowViewHolder> {

    private MainActivity context;
    public List<Application> applicationsList;
    private List<Application> selectedApplications;
    private boolean isSortBySalary;

    public ApplicationListRecyclerAdapter(MainActivity context,
                                          List<Application> applicationsList,
                                          List<Application> selectedApplications) {
        this.applicationsList = applicationsList;
        this.context = context;
        this.selectedApplications = selectedApplications;
    }

    @Override
    public ApplicationRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflates the card view layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.application_row_layout, parent, false);

        return new ApplicationRowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ApplicationRowViewHolder holder, final int position) {
        final Application application = applicationsList.get(position);
        //adds the company name, role and last updated date to the cardView holder
        holder.companyName.setText(application.getCompanyName());

        if(isSortBySalary) {
            String poundSign = context.getString(R.string.pound_sign);
            String noSalary = context.getString(R.string.no_salary);
            holder.role.setText(application.getSalary() != 0 ? poundSign + application.getSalary() : noSalary);
        } else {
            holder.role.setText(application.getRole());
        }

        holder.updatedDate.setText(application.getModifiedShortDate());

        if(application.isPriority()) {
            holder.priorityImage.setVisibility(View.VISIBLE);
        } else {
            holder.priorityImage.setVisibility(View.INVISIBLE);
        }

        //update the status icon for the cardView holder
        Stage stage = application.getCurrentStage();

        StatusIconTint.setTint(context, holder.applicationStatusIcon, stage);

        //go to Application Information when item in Applications List is clicked
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if when single clicked the selection mode is already on
                if(context.isSelectionMode) {
                    //if application was already selected
                    //if wasn't already selected, then select the application
                    if(application.isSelected()) {
                        //deselect the application
                        prepareSelection(application, false);
                    } else prepareSelection(application, true);

                } else {
                    //else a single click will take you to the application information page
                    Intent intent = new Intent(context, ApplicationInformationActivity.class);
                    //send the ID of the Application you want to see information of
                    intent.putExtra(ApplicationTable.COLUMN_ID, application.getApplicationID());
                    intent.putExtra(MainActivity.SOURCE, true);
                    context.startActivity(intent);

                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        //set long click on card view to enable selection mode
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //if the selection mode is off
                if(!context.isSelectionMode) {
                    //switch the selection mode on
                    context.switchActionMode(true);
                    //select the application that was long clicked
                    prepareSelection(application, true);
                } else {
                    //else deselect application if the selection mode was on
                    prepareSelection(application, false);
                }

                return true;
            }
        });

        CardView cardView = (CardView) holder.itemView;

        //on adapter refresh update the card backgrounds to back to default colors
        if(application.isSelected()) {
            updateCardBackground(cardView, true);
        } else {
            updateCardBackground(cardView, false);
        }

    }

    @Override
    public int getItemCount() {
        return applicationsList.size();
    }

    /**
     * Takes the filtered list of applications and updates the current one, and updates the recycler adapter
     * @param filteredApplications new list of applications filtered according to the search query
     */
    public void searchFilter(List<Application> filteredApplications) {
        applicationsList = filteredApplications;
        notifyDataSetChanged();

        context.displayMessageIfNoApplications(true);
    }

    /**
     * Sorts the Applications List according to the field to sort it by. If item is already checked,
     * the Applications List is just reversed.
     * @param sortByField the Applications field to sort the list by
     */
    public void sortApplications(String sortByField) {
        final String sortByString = sortByField;
        List<Application> sortedApplications = new ArrayList<>(applicationsList);

        Collections.sort(sortedApplications, new Comparator<Application>() {
            @Override
            public int compare(Application application1, Application application2) {

                String databaseDateFormat = context.getString(R.string.database_date_format);

                DateFormat df = new SimpleDateFormat(databaseDateFormat);

                switch (sortByString) {
                    case ApplicationTable.COLUMN_CREATED_ON:
                        try {
                            Date d1 = df.parse(application1.getCreatedDate());
                            Date d2 = df.parse(application2.getCreatedDate());

                            return d2.compareTo(d1);
                        } catch (ParseException e) {
                            return 0;
                        }

                    case ApplicationTable.COLUMN_MODIFIED_ON:
                        try {
                            Date d1 = df.parse(application1.getModifiedDate());
                            Date d2 = df.parse(application2.getModifiedDate());

                            return d2.compareTo(d1);
                        } catch (ParseException e) {
                            return 0;
                        }

                    case ApplicationTable.COLUMN_COMPANY_NAME:
                        return application1.getCompanyName().compareTo(application2.getCompanyName());

                    case ApplicationTable.COLUMN_ROLE:
                        return application1.getRole().compareTo(application2.getRole());

                    case ApplicationTable.COLUMN_SALARY:
                        Integer salary1 = application1.getSalary();
                        Integer salary2 = application2.getSalary();
                        return salary2.compareTo(salary1);
                }
                return 0;
            }
        });

        applicationsList = sortedApplications;
        notifyDataSetChanged();

        context.setApplicationList(applicationsList);
    }

    /**
     * Reverse the order of the application list
     */
    public void reverseOrder() {
        Collections.reverse(applicationsList);
        notifyDataSetChanged();
    }

    /**
     * Selects or deselects the application depending on value of toBeSelected,
     * and updates the action bar counter and the card background
     * If 0 applications are selected, then action mode turns off
     * @param application that is to be selected or deselected
     * @param toBeSelected true if the application is to be selected
     */
    private void prepareSelection(Application application, boolean toBeSelected) {
        //update the application being selected
        application.setSelected(toBeSelected);

        //if to be selected put application in selected map, and update the cardView background
        if(toBeSelected) {
            selectedApplications.add(application);
        } else {
            selectedApplications.remove(application);
        }

        decideToPrioritiseOrDeprioritiseApplications(selectedApplications);

        //if user selects no applications then exit action mode
        if(selectedApplications.size() == 0) {
            context.switchActionMode(false);
        } else if(context.actionMode != null) {
            context.updateActionModeCounter(selectedApplications.size());
        }

        notifyDataSetChanged();

    }

    /**
     * Update the color of the cardView background depending on whether it is selected or not
     * @param cardView to change background of
     * @param isSelected true if background to set is highlighted, otherwise set to original color
     */
    private void updateCardBackground(CardView cardView, boolean isSelected) {
        if(isSelected) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.applicationCardBackgroundSelected));
        } else {
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.applicationCardBackgroundDefault));
        }
    }

    //method used each time application selected in multi selection to decide to show prioritise or deprioritise action
    public void decideToPrioritiseOrDeprioritiseApplications(List<Application> selectedApplications) {
        boolean isCurrentlyAllPrioritised = false;

        for(Application application : selectedApplications) {
            if(application.isPriority()) {
                isCurrentlyAllPrioritised = true;
            } else {
                isCurrentlyAllPrioritised = false;
                break;
            }
        }

        //only show deprioritise action if all applications are prioritised
        if(isCurrentlyAllPrioritised) {
            context.prioritiseItem.setVisible(false);
            context.deprioritiseItem.setVisible(true);
        } else {
            context.prioritiseItem.setVisible(true);
            context.deprioritiseItem.setVisible(false);
        }

    }

    public void selectAllApplications() {
        selectedApplications.clear();

        for(Application application : applicationsList) {
            prepareSelection(application, true);
        }

        notifyDataSetChanged();
    }

    public void deselectAllApplications() {
        List<Application> selected = new ArrayList<>(selectedApplications);

        for(Application application : selected) {
            prepareSelection(application, false);
        }

        notifyDataSetChanged();
    }

    public void setSortBySalary(boolean sortBySalary) {
        isSortBySalary = sortBySalary;
    }
}
