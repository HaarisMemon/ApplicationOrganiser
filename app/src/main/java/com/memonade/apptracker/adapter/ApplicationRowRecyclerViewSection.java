package com.memonade.apptracker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.memonade.apptracker.R;
import com.memonade.apptracker.StageInformationActivity;
import com.memonade.apptracker.model.Application;
import com.memonade.apptracker.model.Stage;
import com.memonade.apptracker.view_holder.ApplicationRowViewHolder;
import com.memonade.apptracker.view_holder.TitleSectionViewHolder;

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

        StatusIconTint.setTint(context, applicationHolder.applicationStatusIcon, application.getCurrentStage());
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
