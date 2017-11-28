package com.memonade.apptracker.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.memonade.apptracker.ApplicationInformationActivity;
import com.memonade.apptracker.MainActivity;
import com.memonade.apptracker.R;
import com.memonade.apptracker.StageInformationActivity;
import com.memonade.apptracker.database.ApplicationTable;
import com.memonade.apptracker.database.StageTable;
import com.memonade.apptracker.model.Application;
import com.memonade.apptracker.model.Stage;
import com.memonade.apptracker.view_holder.StageListViewHolder;
import com.memonade.apptracker.view_holder.TitleSectionViewHolder;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * This class represents a the Stages List Section used in the Application Information Recycler view.
 */

public class StageListRecyclerViewSection extends StatelessSection {

    private ApplicationInformationActivity applicationInformationActivity;
    private Context context;
    private Application application;

    private String title;
    private List<Stage> stagesList;
    private boolean isSourceMainActivity;

    public StageListRecyclerViewSection(String title,
                                        ApplicationInformationActivity applicationInformationActivity,
                                        Application application,
                                        List<Stage> stagesList,
                                        boolean isSourceMainActivity) {
        super(R.layout.section_title_layout, R.layout.stage_list_row_layout);

        this.title = title;
        this.applicationInformationActivity = applicationInformationActivity;
        this.context = applicationInformationActivity;
        this.application = application;
        this.stagesList = stagesList;
        this.isSourceMainActivity = isSourceMainActivity;
    }

    @Override
    public int getContentItemsTotal() {
        return stagesList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new StageListViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        StageListViewHolder stageHolder = (StageListViewHolder) holder;
        final Stage stage = stagesList.get(position);

        //adds the company name, role and last updated date to the cardView holder
        stageHolder.stageName.setText(stage.getStageName());
        stageHolder.status.setText(stage.getStatus().toString());
        stageHolder.updatedDate.setText(stage.getModifiedShortDate());

        StatusIconTint.setTint(context, stageHolder.stageStatusIcon, stage);

        //go to Application Information when item in Applications List is clicked
        stageHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to new activity to see the stage information of stage clicked
                Intent intent = new Intent(context, StageInformationActivity.class);
                //send the stage id of the stage clicked and the parent application, in the intent
                intent.putExtra(ApplicationTable.APPLICATION_ID, application.getApplicationID());
                intent.putExtra(StageTable.COLUMN_ID, stage.getStageID());

                if(isSourceMainActivity) {
                    intent.putExtra(MainActivity.SOURCE, true);
                }

                context.startActivity(intent);

                applicationInformationActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
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
