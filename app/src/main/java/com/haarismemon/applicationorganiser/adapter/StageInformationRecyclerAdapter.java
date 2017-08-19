package com.haarismemon.applicationorganiser.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haarismemon.applicationorganiser.R;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.view_holder.StageHeaderViewHolder;

/**
 * This class represents the Recycler Adapter for the Stage Information.
 * It constructs the list and puts the stage's information onto the card view in the list.
 */
public class StageInformationRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ApplicationStage stage;

    public StageInformationRecyclerAdapter(Context context, ApplicationStage stage) {
        this.context = context;
        this.stage = stage;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stage_header_row_layout, parent, false);
        return new StageHeaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        StageHeaderViewHolder stageHolder = (StageHeaderViewHolder) holder;

        CardView cardView = (CardView) stageHolder.itemView;

        //adds the application stage information to the cardView holder
        stageHolder.editedText.setText(context.getString(R.string.editedModified) + " " + stage.getModifiedShortDateTime());
        stageHolder.stageNameText.setText(stage.getStageName());
        stageHolder.currentStatusText.setText(stage.getCurrentStatus().toString());

        ApplicationStage.Status currentStatus = stage.getCurrentStatus();

        if(currentStatus.equals(ApplicationStage.Status.SUCCESSFUL)) {
            DrawableCompat.setTint(stageHolder.stageInfoStatusIcon.getDrawable(), ContextCompat.getColor(context, R.color.statusSuccessful));
        } else if(currentStatus.equals(ApplicationStage.Status.WAITING)) {
            DrawableCompat.setTint(stageHolder.stageInfoStatusIcon.getDrawable(), ContextCompat.getColor(context, R.color.statusInProgress));
        } else if(currentStatus.equals(ApplicationStage.Status.UNSUCCESSFUL)) {
            DrawableCompat.setTint(stageHolder.stageInfoStatusIcon.getDrawable(), ContextCompat.getColor(context, R.color.statusUnsuccessful));
        } else {
            DrawableCompat.setTint(stageHolder.stageInfoStatusIcon.getDrawable(), ContextCompat.getColor(context, R.color.statusIncomplete));
        }

        if(stage.getDateOfStart() != null) stageHolder.dateOfStartText.setText(stage.getDateOfStart());
        else stageHolder.dateOfStartText.setText("No Start Date");

        if(stage.getDateOfCompletion() != null && stage.isCompleted()) stageHolder.dateOfCompletionText.setText(stage.getDateOfCompletion());
        else cardView.findViewById(R.id.completedDateGroup).setVisibility(View.GONE);

        if(stage.getDateOfReply() != null && (stage.isCompleted() && !stage.isWaitingForResponse())) stageHolder.dateOfReplyText.setText(stage.getDateOfReply());
        else cardView.findViewById(R.id.replyDateGroup).setVisibility(View.GONE);

        if(stage.getNotes() != null) stageHolder.stageDescriptionText.setText(stage.getNotes());
        else cardView.findViewById(R.id.notesGroup).setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

}
