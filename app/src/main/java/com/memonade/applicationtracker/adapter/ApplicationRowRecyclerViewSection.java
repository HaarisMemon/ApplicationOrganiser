package com.memonade.applicationtracker.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.memonade.applicationtracker.ApplicationInformationActivity;
import com.memonade.applicationtracker.R;
import com.memonade.applicationtracker.StageInformationActivity;
import com.memonade.applicationtracker.model.Application;
import com.memonade.applicationtracker.model.ApplicationStage;
import com.memonade.applicationtracker.view_holder.ApplicationHeaderViewHolder;
import com.memonade.applicationtracker.view_holder.ApplicationRowViewHolder;
import com.memonade.applicationtracker.view_holder.TitleSectionViewHolder;

import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * This class represents a the Application Row Section used in the Stage Information Recycler view.
 */

public class ApplicationRowRecyclerViewSection extends StatelessSection {

    private Context context;
    private Application application;
    private String title;

    public ApplicationRowRecyclerViewSection(String title,
                                             StageInformationActivity stageInformationActivity,
                                             Application application) {
        super(R.layout.section_title_layout, R.layout.application_row_layout);

        this.title = title;
        this.context = stageInformationActivity;
        this.application = application;
    }

    @Override
    public int getContentItemsTotal() {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ApplicationRowViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ApplicationRowViewHolder applicationHolder = (ApplicationRowViewHolder) holder;

        //adds the company name, role and last updated date to the cardView holder
        applicationHolder.companyName.setText(application.getCompanyName());
        applicationHolder.role.setText(application.getRole());
        applicationHolder.updatedDate.setText(application.getModifiedShortDate());

        if(application.isPriority()) {
            applicationHolder.priorityImage.setVisibility(View.VISIBLE);
        } else {
            applicationHolder.priorityImage.setVisibility(View.INVISIBLE);
        }

        //update the status icon for the cardView holder
        ApplicationStage stage = application.getCurrentStage();

        if(stage != null) {

            ApplicationStage.Status currentStatus = stage.getStatus();

            applicationHolder.applicationStatusIcon.setImageResource(
                    context.getResources().getIdentifier("ic_status_" + currentStatus.getIconNameText(),
                            "drawable", context.getPackageName()));

        } else
            applicationHolder.applicationStatusIcon.setImageResource(R.drawable.ic_status_incomplete);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new TitleSectionViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        TitleSectionViewHolder titleHolder = (TitleSectionViewHolder) holder;
        titleHolder.headerTitle.setText(title);
    }

}
