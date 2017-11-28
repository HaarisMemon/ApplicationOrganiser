package com.memonade.apptracker.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.memonade.apptracker.R;
import com.memonade.apptracker.model.Stage;

/**
 * This class contains a method to be used by all adapters to set the tint of the status icon according to the status value.
 */

class StatusIconTint {

    static void setTint(Context context, ImageView statusIcon, Stage stage) {
        String statusPrefixString = context.getString(R.string.status_icon_file_prefix);

        if(stage != null) {
            statusIcon.setColorFilter(context.getResources().getColor(
                    context.getResources().getIdentifier(statusPrefixString + stage.getStatus().getIconNameText(),
                            "color", context.getPackageName())), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            //set to default color if stage is null
            statusIcon.setColorFilter(
                    context.getResources().getColor(R.color.status_incomplete));
        }
    }

    static void setTint(Context context, ImageView statusIcon, Stage.Status stageStatus) {
        String statusPrefixString = context.getString(R.string.status_icon_file_prefix);

        if(stageStatus != null) {
            statusIcon.setColorFilter(context.getResources().getColor(
                    context.getResources().getIdentifier(statusPrefixString + stageStatus.getIconNameText(),
                            "color", context.getPackageName())), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            //set to default color if stage is null
            statusIcon.setColorFilter(
                    context.getResources().getColor(R.color.status_incomplete));
        }
    }

}
