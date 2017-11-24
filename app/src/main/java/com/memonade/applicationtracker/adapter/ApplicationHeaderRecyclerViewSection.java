package com.memonade.applicationtracker.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.memonade.applicationtracker.ApplicationInformationActivity;
import com.memonade.applicationtracker.R;
import com.memonade.applicationtracker.model.Application;
import com.memonade.applicationtracker.view_holder.ApplicationHeaderViewHolder;
import com.memonade.applicationtracker.view_holder.TitleSectionViewHolder;

import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * This class represents a the Application Header Section used in the Application Information Recycler view.
 */

public class ApplicationHeaderRecyclerViewSection extends StatelessSection {

    private Context context;
    private Application application;

    private String title;

    public ApplicationHeaderRecyclerViewSection(String title,
                                                ApplicationInformationActivity applicationInformationActivity,
                                                Application application) {
        super(R.layout.section_title_layout, R.layout.application_header_row_layout);

        this.title = title;
        this.context = applicationInformationActivity;
        this.application = application;
    }

    @Override
    public int getContentItemsTotal() {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ApplicationHeaderViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
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
