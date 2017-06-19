package com.haarismemon.applicationorganiser.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.haarismemon.applicationorganiser.R;

/**
 * Created by Haaris on 19/06/2017.
 */

public class ApplicationListViewHolder extends RecyclerView.ViewHolder {

    public TextView companyName;
    public TextView role;

    public ApplicationListViewHolder(View itemView) {
        super(itemView);

        companyName = (TextView) itemView.findViewById(R.id.companyNameCardView);
        role = (TextView) itemView.findViewById(R.id.roleCardView);
    }

}
