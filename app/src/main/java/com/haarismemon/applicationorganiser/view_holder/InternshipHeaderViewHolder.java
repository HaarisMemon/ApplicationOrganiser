package com.haarismemon.applicationorganiser.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.haarismemon.applicationorganiser.R;

/**
 * This class represents the View Holder which is used to hold a Internships Header CardView's data to display
 */
public class InternshipHeaderViewHolder extends RecyclerView.ViewHolder {

    public TextView editedText;
    public TextView companyNameText;
    public TextView roleText;
    public TextView lengthText;
    public TextView locationText;
    public TextView urlText;
    public TextView salaryText;
    public TextView notesText;

    public InternshipHeaderViewHolder(View itemView) {
        super(itemView);

        editedText = (TextView) itemView.findViewById(R.id.editedDateInternshipText);
        companyNameText = (TextView) itemView.findViewById(R.id.companyNameText);
        roleText = (TextView) itemView.findViewById(R.id.roleText);
        lengthText = (TextView) itemView.findViewById(R.id.lengthText);
        locationText = (TextView) itemView.findViewById(R.id.locationText);
        urlText = (TextView) itemView.findViewById(R.id.urlText);
        salaryText = (TextView) itemView.findViewById(R.id.salaryText);
        notesText = (TextView) itemView.findViewById(R.id.notesText);
    }

}
