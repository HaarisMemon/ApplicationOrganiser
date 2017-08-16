package com.haarismemon.applicationorganiser.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haarismemon.applicationorganiser.R;
import com.haarismemon.applicationorganiser.StageInformationActivity;
import com.haarismemon.applicationorganiser.database.ApplicationStageTable;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.model.Internship;
import com.haarismemon.applicationorganiser.view_holder.InternshipHeaderViewHolder;
import com.haarismemon.applicationorganiser.view_holder.StageHeaderViewHolder;
import com.haarismemon.applicationorganiser.view_holder.StageListViewHolder;

import java.util.List;

import static com.haarismemon.applicationorganiser.R.id.currentStatusText;
import static com.haarismemon.applicationorganiser.R.id.dateOfCompletionText;
import static com.haarismemon.applicationorganiser.R.id.dateOfReplyText;
import static com.haarismemon.applicationorganiser.R.id.dateOfStartText;
import static com.haarismemon.applicationorganiser.R.id.isCompletedText;
import static com.haarismemon.applicationorganiser.R.id.isSuccessfulText;
import static com.haarismemon.applicationorganiser.R.id.isWaitingForResponseText;
import static com.haarismemon.applicationorganiser.R.id.stageNameText;

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

        //adds the application stage information to the cardView holder
        stageHolder.editedText.setText(context.getString(R.string.editedModified) + " " + stage.getModifiedShortDateTime());
        stageHolder.stageNameText.setText(stage.getStageName() != null ? stage.getStageName() : "No Company Name");
        stageHolder.currentStatusText.setText(stage.getCurrentStatus().toString());
        stageHolder.isCompletedText.setText(stage.isCompleted() ? "Yes" : "No");
        stageHolder.isWaitingForResponseText.setText(stage.isWaitingForResponse() ? "Yes" : "No");
        stageHolder.isSuccessfulText.setText(stage.isSuccessful() ? "Yes" : "No");
        stageHolder.dateOfStartText.setText(stage.getDateOfStart() != null ? stage.getDateOfStart() : "No Start Date");
        stageHolder.dateOfCompletionText.setText(stage.getDateOfCompletion() != null ? stage.getDateOfCompletion() : "No Complete Date");
        stageHolder.dateOfReplyText.setText(stage.getDateOfReply() != null ? stage.getDateOfReply() : "No Reply Date");
        stageHolder.stageDescriptionText.setText(stage.getNotes() != null ? stage.getNotes() : "No Notes");
    }

    @Override
    public int getItemCount() {
        return 1;
    }

}
