package com.haarismemon.applicationorganiser.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haarismemon.applicationorganiser.R;
import com.haarismemon.applicationorganiser.model.Application;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.view_holder.ApplicationRowViewHolder;
import com.haarismemon.applicationorganiser.view_holder.StageHeaderViewHolder;

/**
 * This class represents the Recycler Adapter for the Stage Information.
 * It constructs the list and puts the stage's information onto the card view in the list.
 */
public class StageInformationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Application parentApplication;
    private ApplicationStage stage;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_STAGE = 1;

    public StageInformationAdapter(Context context, Application parentApplication, ApplicationStage stage) {
        this.context = context;
        this.parentApplication = parentApplication;
        this.stage = stage;
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.application_row_layout, parent, false);
            return new ApplicationRowViewHolder(view);
        } else if(viewType == TYPE_STAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stage_header_row_layout, parent, false);
            return new StageHeaderViewHolder(view);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ApplicationRowViewHolder) {

            ApplicationRowViewHolder applicationHolder = (ApplicationRowViewHolder) holder;

            //adds the company name, role and last updated date to the cardView holder
            applicationHolder.companyName.setText(parentApplication.getCompanyName());
            applicationHolder.role.setText(parentApplication.getRole());
            applicationHolder.updatedDate.setText(parentApplication.getModifiedShortDate());

            if(parentApplication.isPriority()) {
                applicationHolder.priorityImage.setVisibility(View.VISIBLE);
            } else {
                applicationHolder.priorityImage.setVisibility(View.INVISIBLE);
            }

            //update the status icon for the cardView holder
            ApplicationStage stage = parentApplication.getCurrentStage();

            if(stage != null) {

                ApplicationStage.Status currentStatus = stage.getStatus();

                applicationHolder.applicationStatusIcon.setImageResource(
                        context.getResources().getIdentifier("ic_status_" + currentStatus.getIconNameText(),
                                "drawable", context.getPackageName()));

            } else
                applicationHolder.applicationStatusIcon.setImageResource(R.drawable.ic_status_incomplete);

        } else if(holder instanceof StageHeaderViewHolder) {

            StageHeaderViewHolder stageHolder = (StageHeaderViewHolder) holder;

            CardView cardView = (CardView) stageHolder.itemView;

            //adds the application stage information to the cardView holder
            stageHolder.editedText.setText(context.getString(R.string.editedModified) + " " + stage.getModifiedShortDateTime());
            stageHolder.stageNameText.setText(stage.getStageName());
            stageHolder.currentStatusText.setText(stage.getStatus().toString());

            ApplicationStage.Status currentStatus = stage.getStatus();

            stageHolder.stageInfoStatusIcon.setImageResource(
                    context.getResources().getIdentifier("ic_status_" + currentStatus.getIconNameText(),
                            "drawable", context.getPackageName()));

            if (stage.getDateOfStart() != null)
                stageHolder.dateOfStartText.setText(stage.getDateOfStart());
            else stageHolder.dateOfStartText.setText("No Start Date");

            if (stage.isCompleted()) {
                if(stage.getDateOfCompletion() != null)
                    stageHolder.dateOfCompletionText.setText(stage.getDateOfCompletion());
                else
                    stageHolder.dateOfCompletionText.setText("No Completion Date");
            }
            else cardView.findViewById(R.id.completedDateGroup).setVisibility(View.GONE);

            if (stage.isCompleted() && !stage.isWaitingForResponse()) {
                if(stage.getDateOfReply() != null)
                    stageHolder.dateOfReplyText.setText(stage.getDateOfReply());
                else
                    stageHolder.dateOfReplyText.setText("No Reply Date");
            }
            else cardView.findViewById(R.id.replyDateGroup).setVisibility(View.GONE);

            if (stage.getNotes() != null) stageHolder.stageNotesText.setText(stage.getNotes());
            else cardView.findViewById(R.id.notesGroup).setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
