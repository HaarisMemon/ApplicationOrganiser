package com.haarismemon.applicationorganiser.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haarismemon.applicationorganiser.InternshipInformationActivity;
import com.haarismemon.applicationorganiser.R;
import com.haarismemon.applicationorganiser.database.InternshipTable;
import com.haarismemon.applicationorganiser.model.Internship;
import com.haarismemon.applicationorganiser.view_holder.ApplicationListViewHolder;

import java.util.List;

/**
 * Created by Haaris on 19/06/2017.
 */

public class ApplicationListRecyclerAdapter extends RecyclerView.Adapter<ApplicationListViewHolder> {

    List<Internship> internshipsList;
    private Context context;

    public ApplicationListRecyclerAdapter(Context context, List<Internship> internshipsList) {
        this.internshipsList = internshipsList;
        this.context = context;
    }

    @Override
    public ApplicationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.application_list_row_layout, parent, false);

        return new ApplicationListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ApplicationListViewHolder holder, final int position) {

        holder.companyName.setText(internshipsList.get(position).getCompanyName());
        holder.role.setText(internshipsList.get(position).getRole());
        holder.updatedDate.setText(internshipsList.get(position).getModifiedShortDate());

        //go to Internship Information when item in Applications List is clicked
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, InternshipInformationActivity.class);
                //send the ID of the Internship you want to see information of
                intent.putExtra(InternshipTable.COLUMN_ID, internshipsList.get(position).getInternshipID());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return internshipsList.size();
    }
}
