package com.haarismemon.applicationorganiser.adapter;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.haarismemon.applicationorganiser.MainActivity;
import com.haarismemon.applicationorganiser.InternshipInformationActivity;
import com.haarismemon.applicationorganiser.R;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.model.Internship;
import com.haarismemon.applicationorganiser.view_holder.InternshipRowViewHolder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * This class represents the Recycler Adapter for the Application List.
 * It constructs the list and puts the internship's information onto each card view in the list.
 */
public class ApplicationListRecyclerAdapter extends RecyclerView.Adapter<InternshipRowViewHolder> {

    public List<Internship> internshipsList;
    private MainActivity context;

    public ApplicationListRecyclerAdapter(MainActivity context, List<Internship> internshipsList) {
        this.internshipsList = internshipsList;
        this.context = context;
    }

    @Override
    public InternshipRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflates the card view layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.internship_row_layout, parent, false);

        return new InternshipRowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final InternshipRowViewHolder holder, final int position) {
        final Internship internship = internshipsList.get(position);
        //adds the company name, role and last updated date to the cardView holder
        holder.companyName.setText(internship.getCompanyName());
        holder.role.setText(internship.getRole());
        holder.updatedDate.setText(internship.getModifiedShortDate());

        if(internship.isPriority()) {
            holder.priorityImage.setVisibility(View.VISIBLE);
        } else {
            holder.priorityImage.setVisibility(View.INVISIBLE);
        }

        //update the status icon for the cardView holder
        ApplicationStage stage = internship.getCurrentStage();

        if(stage != null) {

            ApplicationStage.Status currentStatus = stage.getCurrentStatus();

            if(currentStatus.equals(ApplicationStage.Status.SUCCESSFUL)) {
                DrawableCompat.setTint(holder.internshipStatusIcon.getDrawable(), ContextCompat.getColor(context, R.color.statusSuccessful));
            } else if(currentStatus.equals(ApplicationStage.Status.WAITING)) {
                DrawableCompat.setTint(holder.internshipStatusIcon.getDrawable(), ContextCompat.getColor(context, R.color.statusInProgress));
            } else if(currentStatus.equals(ApplicationStage.Status.UNSUCCESSFUL)) {
                DrawableCompat.setTint(holder.internshipStatusIcon.getDrawable(), ContextCompat.getColor(context, R.color.statusUnsuccessful));
            } else {
                DrawableCompat.setTint(holder.internshipStatusIcon.getDrawable(), ContextCompat.getColor(context, R.color.statusIncomplete));
            }

        } else {
            DrawableCompat.setTint(holder.internshipStatusIcon.getDrawable(), ContextCompat.getColor(context, R.color.statusIncomplete));
        }

        //go to Internship Information when item in Applications List is clicked
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //store view as card view so to access card view methods
                CardView cardView = (CardView) view;

                //if when single clicked the selection mode is already on
                if(context.isSelectionMode) {
                    //if internship was already selected
                    if(internship.isSelected()) {
                        //deselect the internship
                        context.prepareSelection(cardView, internship, false);
                    } else {
                        //if wasn't already selected, then select the internship
                        context.prepareSelection(cardView, internship, true);
                    }

                } else {
                    //else a single click will take you to the internship information page
                    Intent intent = new Intent(context, InternshipInformationActivity.class);
                    //send the ID of the Internship you want to see information of
                    intent.putExtra(InternshipTable.COLUMN_ID, internship.getInternshipID());
                    intent.putExtra(MainActivity.SOURCE, true);
                    context.startActivity(intent);

                }
            }
        });

        //set long click on card view to enable selection mode
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                CardView cardView = (CardView) holder.itemView;

                //if the selection mode is off
                if(!context.isSelectionMode) {
                    //switch the selection mode on
                    context.switchActionMode(true);
                    //select the internship that was long clicked
                    context.prepareSelection(cardView, internship, true);
                } else {
                    //else deselect internship if the selection mode was on
                    context.prepareSelection(cardView, internship, false);
                }

                return true;
            }
        });

        CardView cardView = (CardView) holder.itemView;

        //on adapter refresh update the card backgrounds to back to default colors
        if(internship.isSelected()) {
            context.updateCardBackground(cardView, true);
        } else {
            context.updateCardBackground(cardView, false);
        }

    }

    @Override
    public int getItemCount() {
        return internshipsList.size();
    }

    /**
     * Takes the filtered list of internships and updates the current one, and updates the recycler adapter
     * @param filteredInternships new list of internships filtered according to the search query
     */
    public void searchFilter(List<Internship> filteredInternships) {
        internshipsList = filteredInternships;
        notifyDataSetChanged();
    }

    /**
     * Sorts the Internships List according to the field to sort it by. If item is already checked,
     * the Internships List is just reversed.
     * @param sortByField the Internships field to sort the list by
     * @param selectedItem is the menu item that is selected in the sub menu
     */
    public void sortInternships(String sortByField, MenuItem selectedItem) {
        if(selectedItem.isChecked()) {
            reverseOrder();
        } else {
            final String sortByString = sortByField;
            List<Internship> sortedInternships = new ArrayList<>(internshipsList);

            Collections.sort(sortedInternships, new Comparator<Internship>() {
                @Override
                public int compare(Internship internship1, Internship internship2) {

                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    switch (sortByString) {
                        case InternshipTable.COLUMN_CREATED_ON:
                            try {
                                Date d1 = df.parse(internship1.getCreatedDate());
                                Date d2 = df.parse(internship2.getCreatedDate());

                                return d2.compareTo(d1);
                            } catch (ParseException e) {
                                return 0;
                            }

                        case InternshipTable.COLUMN_MODIFIED_ON:
                            try {
                                Date d1 = df.parse(internship1.getModifiedDate());
                                Date d2 = df.parse(internship2.getModifiedDate());

                                return d2.compareTo(d1);
                            } catch (ParseException e) {
                                return 0;
                            }

                        case InternshipTable.COLUMN_COMPANY_NAME:
                            return internship1.getCompanyName().compareTo(internship2.getCompanyName());

                        case InternshipTable.COLUMN_ROLE:
                            return internship1.getRole().compareTo(internship2.getRole());

                        case InternshipTable.COLUMN_SALARY:
                            Integer salary1 = internship1.getSalary();
                            Integer salary2 = internship2.getSalary();
                            return salary2.compareTo(salary1);
                    }
                    return 0;
                }
            });

            internshipsList = sortedInternships;
            notifyDataSetChanged();
        }
    }

    /**
     * Reverse the order of the internship list
     */
    public void reverseOrder() {
        Collections.reverse(internshipsList);
        notifyDataSetChanged();
    }
}
