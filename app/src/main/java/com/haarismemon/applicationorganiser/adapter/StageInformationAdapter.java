package com.haarismemon.applicationorganiser.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.haarismemon.applicationorganiser.R;
import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.model.Internship;
import com.haarismemon.applicationorganiser.view_holder.InternshipHeaderViewHolder;
import com.haarismemon.applicationorganiser.view_holder.InternshipRowViewHolder;
import com.haarismemon.applicationorganiser.view_holder.StageHeaderViewHolder;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * This class represents the Recycler Adapter for the Stage Information.
 * It constructs the list and puts the stage's information onto the card view in the list.
 */
public class StageInformationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Internship parentInternship;
    private ApplicationStage stage;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_STAGE = 1;

    public StageInformationAdapter(Context context, Internship parentInternship, ApplicationStage stage) {
        this.context = context;
        this.parentInternship = parentInternship;
        this.stage = stage;
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.internship_row_layout, parent, false);
            return new InternshipRowViewHolder(view);
        } else if(viewType == TYPE_STAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stage_header_row_layout, parent, false);
            return new StageHeaderViewHolder(view);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof InternshipRowViewHolder) {

            InternshipRowViewHolder internshipHolder = (InternshipRowViewHolder) holder;

            //adds the company name, role and last updated date to the cardView holder
            internshipHolder.companyName.setText(parentInternship.getCompanyName());
            internshipHolder.role.setText(parentInternship.getRole());
            internshipHolder.updatedDate.setText(parentInternship.getModifiedShortDate());

            if(parentInternship.isPriority()) {
                internshipHolder.priorityImage.setVisibility(View.VISIBLE);
            } else {
                internshipHolder.priorityImage.setVisibility(View.INVISIBLE);
            }

            //update the status icon for the cardView holder
            ApplicationStage stage = parentInternship.getCurrentStage();

            if(stage != null) {

                ApplicationStage.Status currentStatus = stage.getCurrentStatus();

                if(currentStatus.equals(ApplicationStage.Status.SUCCESSFUL)) {
                    internshipHolder.internshipStatusIcon.setImageResource(R.drawable.ic_status_successful);
                } else if(currentStatus.equals(ApplicationStage.Status.WAITING)) {
                    internshipHolder.internshipStatusIcon.setImageResource(R.drawable.ic_status_in_progress);
                } else if(currentStatus.equals(ApplicationStage.Status.UNSUCCESSFUL)) {
                    internshipHolder.internshipStatusIcon.setImageResource(R.drawable.ic_status_unsuccessful);
                } else {
                    internshipHolder.internshipStatusIcon.setImageResource(R.drawable.ic_status_incomplete);
                }

            } else
                DrawableCompat.setTint(internshipHolder.internshipStatusIcon.getDrawable(), ContextCompat.getColor(context, R.color.statusIncomplete));

        } else if(holder instanceof StageHeaderViewHolder) {

            StageHeaderViewHolder stageHolder = (StageHeaderViewHolder) holder;

            CardView cardView = (CardView) stageHolder.itemView;

            //adds the application stage information to the cardView holder
            stageHolder.editedText.setText(context.getString(R.string.editedModified) + " " + stage.getModifiedShortDateTime());
            stageHolder.stageNameText.setText(stage.getStageName());
            stageHolder.currentStatusText.setText(stage.getCurrentStatus().toString());

            ApplicationStage.Status currentStatus = stage.getCurrentStatus();

            if(currentStatus.equals(ApplicationStage.Status.SUCCESSFUL)) {
                stageHolder.stageInfoStatusIcon.setImageResource(R.drawable.ic_status_successful);
            } else if(currentStatus.equals(ApplicationStage.Status.WAITING)) {
                stageHolder.stageInfoStatusIcon.setImageResource(R.drawable.ic_status_in_progress);
            } else if(currentStatus.equals(ApplicationStage.Status.UNSUCCESSFUL)) {
                stageHolder.stageInfoStatusIcon.setImageResource(R.drawable.ic_status_unsuccessful);
            } else {
                stageHolder.stageInfoStatusIcon.setImageResource(R.drawable.ic_status_incomplete);
            }

            if (stage.getDateOfStart() != null)
                stageHolder.dateOfStartText.setText(stage.getDateOfStart());
            else stageHolder.dateOfStartText.setText("No Start Date");

            if (stage.getDateOfCompletion() != null && stage.isCompleted())
                stageHolder.dateOfCompletionText.setText(stage.getDateOfCompletion());
            else cardView.findViewById(R.id.completedDateGroup).setVisibility(View.GONE);

            if (stage.getDateOfReply() != null && (stage.isCompleted() && !stage.isWaitingForResponse()))
                stageHolder.dateOfReplyText.setText(stage.getDateOfReply());
            else cardView.findViewById(R.id.replyDateGroup).setVisibility(View.GONE);

            if (stage.getNotes() != null) stageHolder.stageNotesText.setText(stage.getNotes());
            else cardView.findViewById(R.id.notesGroup).setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
