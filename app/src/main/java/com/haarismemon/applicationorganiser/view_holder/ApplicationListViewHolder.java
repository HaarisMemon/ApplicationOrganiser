package com.haarismemon.applicationorganiser.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.haarismemon.applicationorganiser.R;

/**
 * This class represents the View Holder which is used to hold a CardView's data to display
 */
public class ApplicationListViewHolder extends RecyclerView.ViewHolder {

    public TextView companyName;
    public TextView role;
    public TextView updatedDate;
    public ImageView internshipStatusIcon;

    public ApplicationListViewHolder(View itemView) {
        super(itemView);

        companyName = (TextView) itemView.findViewById(R.id.companyNameCardView);
        role = (TextView) itemView.findViewById(R.id.roleCardView);
        updatedDate = (TextView) itemView.findViewById(R.id.updatedDateCardView);
        internshipStatusIcon = (ImageView) itemView.findViewById(R.id.internshipStatusIcon);
    }

}
