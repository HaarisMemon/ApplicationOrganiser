package com.memonade.apptracker.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.memonade.apptracker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This class represents the View Holder which is used to hold an Stage CardView's data to display
 */
public class StageHeaderViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.editedDateStageText) public TextView editedText;
    @BindView(R.id.stageNameText) public TextView stageNameText;
    @BindView(R.id.currentStatusText) public TextView currentStatusText;
    @BindView(R.id.dateOfDeadlineText) public TextView dateOfDeadlineText;
    @BindView(R.id.dateOfStartText) public TextView dateOfStartText;
    @BindView(R.id.dateOfCompletionText) public TextView dateOfCompletionText;
    @BindView(R.id.dateOfReplyText) public TextView dateOfReplyText;
    @BindView(R.id.stageNotesText) public TextView stageNotesText;
    @BindView(R.id.stageInfoStatusIcon) public ImageView stageInfoStatusIcon;

    public StageHeaderViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

}
