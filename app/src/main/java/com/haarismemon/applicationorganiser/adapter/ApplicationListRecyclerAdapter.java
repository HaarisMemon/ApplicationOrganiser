package com.haarismemon.applicationorganiser.adapter;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haarismemon.applicationorganiser.MainActivity;
import com.haarismemon.applicationorganiser.InternshipInformationActivity;
import com.haarismemon.applicationorganiser.R;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.model.Internship;
import com.haarismemon.applicationorganiser.view_holder.ApplicationListViewHolder;

import java.util.List;

/**
 * This class represents the Recycler Adapter for the Application List.
 * It constructs the list and puts the internship's information onto each card view in the list.
 */
public class ApplicationListRecyclerAdapter extends RecyclerView.Adapter<ApplicationListViewHolder> {

    public List<Internship> internshipsList;
    private MainActivity context;

    public ApplicationListRecyclerAdapter(MainActivity context, List<Internship> internshipsList) {
        this.internshipsList = internshipsList;
        this.context = context;
    }

    @Override
    public ApplicationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflates the card view layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.application_list_row_layout, parent, false);

        return new ApplicationListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ApplicationListViewHolder holder, final int position) {
        final Internship internship = internshipsList.get(position);
        //adds the company name, role and last updated date to the cardView holder
        holder.companyName.setText(internship.getCompanyName());
        holder.role.setText(internship.getRole());
        holder.updatedDate.setText(internship.getModifiedShortDate());

        //update the status icon for the cardView holder
        ApplicationStage stage = internship.getCurrentStage();

        if(stage != null) {

            ApplicationStage.Status currentStatus = stage.getCurrentStatus();

            if(currentStatus.equals(ApplicationStage.Status.SUCCESSFUL)) {
                holder.internshipStatusIcon.setImageResource(R.drawable.ic_status_success);
            } else if(currentStatus.equals(ApplicationStage.Status.WAITING)) {
                holder.internshipStatusIcon.setImageResource(R.drawable.ic_status_waiting);
            } else if(currentStatus.equals(ApplicationStage.Status.UNSUCCESSFUL)) {
                holder.internshipStatusIcon.setImageResource(R.drawable.ic_status_unsuccess);
            } else {
                holder.internshipStatusIcon.setImageResource(R.drawable.ic_status_uncomplete);
            }

        } else {
            holder.internshipStatusIcon.setImageResource(R.drawable.ic_status_uncomplete);
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
}
