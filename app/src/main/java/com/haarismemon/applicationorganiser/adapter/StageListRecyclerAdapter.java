package com.haarismemon.applicationorganiser.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haarismemon.applicationorganiser.R;
import com.haarismemon.applicationorganiser.StageInformationActivity;
import com.haarismemon.applicationorganiser.database.ApplicationStageTable;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.model.Internship;
import com.haarismemon.applicationorganiser.view_holder.InternshipHeaderViewHolder;
import com.haarismemon.applicationorganiser.view_holder.StageListViewHolder;

import java.util.List;

import static com.haarismemon.applicationorganiser.R.id.companyNameText;
import static com.haarismemon.applicationorganiser.R.id.lengthText;
import static com.haarismemon.applicationorganiser.R.id.locationText;
import static com.haarismemon.applicationorganiser.R.id.notesText;
import static com.haarismemon.applicationorganiser.R.id.roleText;

/**
 * This class represents the Recycler Adapter for the Stage List.
 * It constructs the list and puts the stage's information onto each card view in the list.
 */
public class StageListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Internship internship;
    private List<ApplicationStage> stagesList;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_STAGE = 1;

    public StageListRecyclerAdapter(Context context, Internship internship, List<ApplicationStage> stagesList) {
        this.context = context;
        this.internship = internship;
        this.stagesList = stagesList;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return TYPE_HEADER;
        return TYPE_STAGE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.internship_header_row_layout, parent, false);
            return new InternshipHeaderViewHolder(view);
        } else if(viewType == TYPE_STAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stage_list_row_layout, parent, false);
            return new StageListViewHolder(view);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof InternshipHeaderViewHolder) {

            InternshipHeaderViewHolder internshipHolder = (InternshipHeaderViewHolder) holder;

            internshipHolder.editedText.setText(context.getString(R.string.editedModified) + " " + internship.getModifiedShortDateTime());
            internshipHolder.companyNameText.setText(internship.getCompanyName() != null ? internship.getCompanyName() : "None");
            internshipHolder.roleText.setText(internship.getRole() != null ? internship.getRole() : "None");
            internshipHolder.lengthText.setText(internship.getLength() != null ? internship.getLength() : "None");
            internshipHolder.locationText.setText(internship.getLocation() != null ? internship.getLocation() : "None");
            internshipHolder.notesText.setText(internship.getNotes() != null ? internship.getNotes() : "No Notes");

        } else if(holder instanceof StageListViewHolder) {

            StageListViewHolder stageHolder = (StageListViewHolder) holder;
            final ApplicationStage stage = stagesList.get(position);

            //adds the company name, role and last updated date to the cardView holder
            stageHolder.stageName.setText(stage.getStageName());
            stageHolder.status.setText(stage.getCurrentStatus());
            stageHolder.updatedDate.setText(stage.getModifiedShortDate());

            //go to Internship Information when item in Applications List is clicked
            stageHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //go to new activity to see the stage information of application stage clicked
                    Intent intent = new Intent(context, StageInformationActivity.class);
                    //send the stage id of the stage clicked, in the intent
                    intent.putExtra(ApplicationStageTable.COLUMN_ID, stage.getStageID());
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return stagesList.size();
    }

}
