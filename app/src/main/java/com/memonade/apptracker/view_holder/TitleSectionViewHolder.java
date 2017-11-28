package com.memonade.apptracker.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.memonade.apptracker.R;

/**
 * This class represents the view holder for the title of the sections used in the recyclerviews.
 */

public class TitleSectionViewHolder extends RecyclerView.ViewHolder{

    public TextView headerTitle;

    public TitleSectionViewHolder(View itemView) {
        super(itemView);
        headerTitle = itemView.findViewById(R.id.stage_title_id);
    }

}
