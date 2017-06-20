package com.haarismemon.applicationorganiser.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.haarismemon.applicationorganiser.R;

/**
 * This class represents the View Holder which is used to hold a CardView's data to display
 */
public class StageListViewHolder extends RecyclerView.ViewHolder {

    public TextView stageName;
    public TextView status;
    public TextView updatedDate;

    public StageListViewHolder(View itemView) {
        super(itemView);

        stageName = (TextView) itemView.findViewById(R.id.stageNameCardView);
        status = (TextView) itemView.findViewById(R.id.statusCardView);
        updatedDate = (TextView) itemView.findViewById(R.id.updatedDateCardView);
    }

}
