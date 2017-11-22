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
import com.haarismemon.applicationorganiser.database.ApplicationTable;
import com.haarismemon.applicationorganiser.model.Application;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.view_holder.ApplicationHeaderViewHolder;
import com.haarismemon.applicationorganiser.view_holder.StageListViewHolder;

import java.util.List;

/**
 * This class represents the Recycler Adapter for the Stage List.
 * It constructs the list and puts the stage's information onto each card view in the list.
 */
public class ApplicationInformationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Application application;
    private List<ApplicationStage> stagesList;
    private boolean isSourceMainActivity;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_STAGE = 1;

    public ApplicationInformationAdapter(Context context, Application application, List<ApplicationStage> stagesList, boolean isSourceMainActivity) {
        this.context = context;
        this.application = application;
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.application_header_row_layout, parent, false);
            return new ApplicationHeaderViewHolder(view);
        } else if(viewType == TYPE_STAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stage_list_row_layout, parent, false);
            return new StageListViewHolder(view);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ApplicationHeaderViewHolder) {

            ApplicationHeaderViewHolder applicationHolder = (ApplicationHeaderViewHolder) holder;

            CardView cardView = (CardView) applicationHolder.itemView;

            applicationHolder.editedText.setText(context.getString(R.string.editedModified) + " " + application.getModifiedShortDateTime());
            applicationHolder.companyNameText.setText(application.getCompanyName() != null ? application.getCompanyName() : "None");
            applicationHolder.roleText.setText(application.getRole() != null ? application.getRole() : "None");

            if(application.getLength() != null) applicationHolder.lengthText.setText(application.getLength() != null ? application.getLength() : "None");
            else cardView.findViewById(R.id.lengthGroup).setVisibility(View.GONE);

            if(application.getLocation() != null) applicationHolder.locationText.setText(application.getLocation() != null ? application.getLocation() : "None");
            else cardView.findViewById(R.id.locationGroup).setVisibility(View.GONE);

            if(application.getUrl() != null) applicationHolder.urlText.setText(application.getUrl() != null ? application.getUrl() : "None");
            else cardView.findViewById(R.id.urlGroup).setVisibility(View.GONE);

            if(application.getSalary() != 0) applicationHolder.salaryText.setText(application.getSalary() != 0 ? "£" + application.getSalary() : "None");
            else cardView.findViewById(R.id.salaryGroup).setVisibility(View.GONE);

            if(application.getNotes() != null) applicationHolder.notesText.setText(application.getNotes() != null ? application.getNotes() : "No Notes");
            else cardView.findViewById(R.id.notesGroup).setVisibility(View.GONE);

            if(application.isPriority()) {
                applicationHolder.priorityImage.setVisibility(View.VISIBLE);
            } else {
                applicationHolder.priorityImage.setVisibility(View.INVISIBLE);
            }

        } else if(holder instanceof StageListViewHolder) {

            StageListViewHolder stageHolder = (StageListViewHolder) holder;
            final ApplicationStage stage = stagesList.get(position);

            //adds the company name, role and last updated date to the cardView holder
            stageHolder.stageName.setText(stage.getStageName());
            stageHolder.status.setText(stage.getStatus().toString());
            stageHolder.updatedDate.setText(stage.getModifiedShortDate());

            ApplicationStage.Status currentStatus = stage.getStatus();

            stageHolder.stageStatusIcon.setImageResource(
                    context.getResources().getIdentifier("ic_status_" + currentStatus.getIconNameText(),
                            "drawable", context.getPackageName()));

            //go to Application Information when item in Applications List is clicked
            stageHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //go to new activity to see the stage information of application stage clicked
                    Intent intent = new Intent(context, StageInformationActivity.class);
                    //send the stage id of the stage clicked and the parent application, in the intent
                    intent.putExtra(ApplicationTable.APPLICATION_ID, application.getApplicationID());
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
