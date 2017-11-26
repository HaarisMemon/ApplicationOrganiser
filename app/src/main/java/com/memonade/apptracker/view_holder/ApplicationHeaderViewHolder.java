package com.memonade.apptracker.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.memonade.apptracker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This class represents the View Holder which is used to hold an Applications Header CardView's data to display
 */
public class ApplicationHeaderViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.editedDateApplicationText) public TextView editedText;
    @BindView(R.id.companyNameText) public TextView companyNameText;
    @BindView(R.id.roleText) public TextView roleText;
    @BindView(R.id.lengthText) public TextView lengthText;
    @BindView(R.id.locationText) public TextView locationText;
    @BindView(R.id.urlText) public TextView urlText;
    @BindView(R.id.salaryText) public TextView salaryText;
    @BindView(R.id.notesText) public TextView notesText;
    @BindView(R.id.priorityImage) public ImageView priorityImage;

    public ApplicationHeaderViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

}
