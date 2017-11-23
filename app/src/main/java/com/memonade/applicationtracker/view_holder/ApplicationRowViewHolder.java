package com.memonade.applicationtracker.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.memonade.applicationtracker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This class represents the View Holder which is used to hold a CardView's data to display
 */
public class ApplicationRowViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.companyNameCardView) public TextView companyName;
    @BindView(R.id.roleCardView) public TextView role;
    @BindView(R.id.updatedDateCardView) public TextView updatedDate;
    @BindView(R.id.applicationStatusIcon) public ImageView applicationStatusIcon;
    @BindView(R.id.priorityImage) public ImageView priorityImage;

    public ApplicationRowViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

}
