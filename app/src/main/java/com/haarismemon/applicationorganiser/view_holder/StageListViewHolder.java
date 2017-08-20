package com.haarismemon.applicationorganiser.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.haarismemon.applicationorganiser.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This class represents the View Holder which is used to hold a CardView's data to display
 */
public class StageListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.stageNameCardView) public TextView stageName;
    @BindView(R.id.statusCardView) public TextView status;
    @BindView(R.id.updatedDateCardView) public TextView updatedDate;
    @BindView(R.id.stageStatusIcon) public ImageView stageStatusIcon;

    public StageListViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

}
