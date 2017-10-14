package com.haarismemon.applicationorganiser.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haarismemon.applicationorganiser.MainActivity;
import com.haarismemon.applicationorganiser.R;
import com.haarismemon.applicationorganiser.StageInformationActivity;
import com.haarismemon.applicationorganiser.database.ApplicationStageTable;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.model.Internship;
import com.haarismemon.applicationorganiser.view_holder.InternshipHeaderViewHolder;
import com.haarismemon.applicationorganiser.view_holder.StageListViewHolder;

import java.util.List;

/**
 * This class represents the Recycler Adapter for the Stage List.
 * It constructs the list and puts the stage's information onto each card view in the list.
 */
public class InternshipInformationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Internship internship;
    private List<ApplicationStage> stagesList;
    private boolean isSourceMainActivity;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_STAGE = 1;

    public InternshipInformationAdapter(Context context, Internship internship, List<ApplicationStage> stagesList, boolean isSourceMainActivity) {
        this.context = context;
        this.internship = internship;
        this.stagesList = stagesList;
        this.isSourceMainActivity = isSourceMainActivity;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return TYPE_HEADER;
        return TYPE_STAGE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.internship_header_row_layout, parent, false);
            return new InternshipHeaderViewHolder(view);
        } else if(viewType == TYPE_STAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stage_list_row_layout, parent, false);
            return new StageListViewHolder(view);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof InternshipHeaderViewHolder) {

            InternshipHeaderViewHolder internshipHolder = (InternshipHeaderViewHolder) holder;

            CardView cardView = (CardView) internshipHolder.itemView;

            internshipHolder.editedText.setText(context.getString(R.string.editedModified) + " " + internship.getModifiedShortDateTime());
            internshipHolder.companyNameText.setText(internship.getCompanyName() != null ? internship.getCompanyName() : "None");
            internshipHolder.roleText.setText(internship.getRole() != null ? internship.getRole() : "None");

            if(internship.getLength() != null) internshipHolder.lengthText.setText(internship.getLength() != null ? internship.getLength() : "None");
            else cardView.findViewById(R.id.lengthGroup).setVisibility(View.GONE);

            if(internship.getLocation() != null) internshipHolder.locationText.setText(internship.getLocation() != null ? internship.getLocation() : "None");
            else cardView.findViewById(R.id.locationGroup).setVisibility(View.GONE);

            if(internship.getUrl() != null) internshipHolder.urlText.setText(internship.getUrl() != null ? internship.getUrl() : "None");
            else cardView.findViewById(R.id.urlGroup).setVisibility(View.GONE);

            if(internship.getSalary() != 0) internshipHolder.salaryText.setText(internship.getSalary() != 0 ? "£" + internship.getSalary() : "None");
            else cardView.findViewById(R.id.salaryGroup).setVisibility(View.GONE);

            if(internship.getNotes() != null) internshipHolder.notesText.setText(internship.getNotes() != null ? internship.getNotes() : "No Notes");
            else cardView.findViewById(R.id.notesGroup).setVisibility(View.GONE);

            if(internship.isPriority()) {
                internshipHolder.priorityImage.setVisibility(View.VISIBLE);
            } else {
                internshipHolder.priorityImage.setVisibility(View.INVISIBLE);
            }

        } else if(holder instanceof StageListViewHolder) {

            StageListViewHolder stageHolder = (StageListViewHolder) holder;
            final ApplicationStage stage = stagesList.get(position);

            //adds the company name, role and last updated date to the cardView holder
            stageHolder.stageName.setText(stage.getStageName());
            stageHolder.status.setText(stage.getStatus().toString());
            stageHolder.updatedDate.setText(stage.getModifiedShortDate());

            ApplicationStage.Status currentStatus = stage.getStatus();

            if(currentStatus.equals(ApplicationStage.Status.SUCCESSFUL)) {
                stageHolder.stageStatusIcon.setImageResource(R.drawable.ic_status_successful);
            } else if(currentStatus.equals(ApplicationStage.Status.WAITING)) {
                stageHolder.stageStatusIcon.setImageResource(R.drawable.ic_status_in_progress);
            } else if(currentStatus.equals(ApplicationStage.Status.UNSUCCESSFUL)) {
                stageHolder.stageStatusIcon.setImageResource(R.drawable.ic_status_unsuccessful);
            } else {
                stageHolder.stageStatusIcon.setImageResource(R.drawable.ic_status_incomplete);
            }

            //go to Internship Information when item in Applications List is clicked
            stageHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //go to new activity to see the stage information of application stage clicked
                    Intent intent = new Intent(context, StageInformationActivity.class);
                    //send the stage id of the stage clicked and the parent internship, in the intent
                    intent.putExtra(InternshipTable.INTERNSHIP_ID, internship.getInternshipID());
                    intent.putExtra(ApplicationStageTable.COLUMN_ID, stage.getStageID());

                    if(isSourceMainActivity) {
                        intent.putExtra(MainActivity.SOURCE, true);
                    }

                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return stagesList.size();
    }

}
