package com.haarismemon.applicationorganiser.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.haarismemon.applicationorganiser.R;

/**
 * This class represents the View Holder which is used to hold an Application Stage CardView's data to display
 */
public class StageHeaderViewHolder extends RecyclerView.ViewHolder {

    public TextView editedText;
    public TextView stageNameText;
    public TextView currentStatusText;
    public TextView isCompletedText;
    public TextView isWaitingForResponseText;
    public TextView isSuccessfulText;
    public TextView dateOfStartText;
    public TextView dateOfCompletionText;
    public TextView dateOfReplyText;
    public TextView stageDescriptionText;

    public StageHeaderViewHolder(View itemView) {
        super(itemView);

        editedText = (TextView) itemView.findViewById(R.id.editedDateStageText);
        stageNameText = (TextView) itemView.findViewById(R.id.stageNameText);
        currentStatusText = (TextView) itemView.findViewById(R.id.currentStatusText);
        isCompletedText = (TextView) itemView.findViewById(R.id.isCompletedText);
        isWaitingForResponseText = (TextView) itemView.findViewById(R.id.isWaitingForResponseText);
        isSuccessfulText = (TextView) itemView.findViewById(R.id.isSuccessfulText);
        dateOfStartText = (TextView) itemView.findViewById(R.id.dateOfStartText);
        dateOfCompletionText = (TextView) itemView.findViewById(R.id.dateOfCompletionText);
        dateOfReplyText = (TextView) itemView.findViewById(R.id.dateOfReplyText);
        stageDescriptionText = (TextView) itemView.findViewById(R.id.stageNotesText);
    }

}
