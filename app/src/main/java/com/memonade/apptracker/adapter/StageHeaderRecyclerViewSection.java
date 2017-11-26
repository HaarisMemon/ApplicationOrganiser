package com.memonade.apptracker.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.memonade.apptracker.R;
import com.memonade.apptracker.StageInformationActivity;
import com.memonade.apptracker.model.ApplicationStage;
import com.memonade.apptracker.view_holder.StageHeaderViewHolder;
import com.memonade.apptracker.view_holder.TitleSectionViewHolder;

import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * This class represents a the Stages Header Section used in the Stage Information Recycler view.
 */

public class StageHeaderRecyclerViewSection extends StatelessSection {

    private Context context;
    private String title;
    private ApplicationStage stage;

    public StageHeaderRecyclerViewSection(String title,
                                          StageInformationActivity stageInformationActivity,
                                          ApplicationStage stage) {
        super(R.layout.section_title_layout, R.layout.stage_header_row_layout);

        this.title = title;
        this.context = stageInformationActivity;
        this.stage = stage;
    }

    @Override
    public int getContentItemsTotal() {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new StageHeaderViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        StageHeaderViewHolder stageHolder = (StageHeaderViewHolder) holder;

        CardView cardView = (CardView) stageHolder.itemView;

        //adds the application stage information to the cardView holder
        stageHolder.editedText.setText(context.getString(R.string.editedModified) + " " + stage.getModifiedShortDateTime());
        stageHolder.stageNameText.setText(stage.getStageName());
        stageHolder.currentStatusText.setText(stage.getStatus().toString());

        ApplicationStage.Status currentStatus = stage.getStatus();
        String ic_status = context.getString(R.string.status_icon_file_prefix);

        stageHolder.stageInfoStatusIcon.setImageResource(
                context.getResources().getIdentifier(ic_status + currentStatus.getIconNameText(),
                        "drawable", context.getPackageName()));

        if (stage.getDateOfDeadline() != null)
            stageHolder.dateOfDeadlineText.setText(stage.getDateOfDeadline());
        else cardView.findViewById(R.id.deadlineDateGroup).setVisibility(View.GONE);

        if (stage.getDateOfStart() != null)
            stageHolder.dateOfStartText.setText(stage.getDateOfStart());
        else stageHolder.dateOfStartText.setText(context.getString(R.string.no_date_of_start));

        if (stage.isCompleted()) {
            if(stage.getDateOfCompletion() != null)
                stageHolder.dateOfCompletionText.setText(stage.getDateOfCompletion());
            else
                stageHolder.dateOfCompletionText.setText(context.getString(R.string.no_date_of_completion));
        }
        else cardView.findViewById(R.id.completedDateGroup).setVisibility(View.GONE);

        if (stage.isCompleted() && !stage.isWaitingForResponse()) {
            if(stage.getDateOfReply() != null)
                stageHolder.dateOfReplyText.setText(stage.getDateOfReply());
            else
                stageHolder.dateOfReplyText.setText(context.getString(R.string.no_date_of_reply));
        }
        else cardView.findViewById(R.id.replyDateGroup).setVisibility(View.GONE);

        if (stage.getNotes() != null) stageHolder.stageNotesText.setText(stage.getNotes());
        else cardView.findViewById(R.id.notesGroup).setVisibility(View.GONE);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new TitleSectionViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        TitleSectionViewHolder titleHeader = (TitleSectionViewHolder) holder;
        titleHeader.headerTitle.setText(title);
    }

}
