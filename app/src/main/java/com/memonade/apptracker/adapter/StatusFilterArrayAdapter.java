package com.memonade.apptracker.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.memonade.apptracker.R;
import com.memonade.apptracker.model.Stage;

import java.util.List;

/**
 * This class represents the array adapter for the status filter spinner for the filter panel.
 */
public class StatusFilterArrayAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> texts;
    private Stage.Status[] statusValues;
    private List<Integer> mSelectedItems;

    public StatusFilterArrayAdapter(Context context, List<String> statusStrings, Stage.Status[] statusValues,
                                    List<Integer> selectedItemsIndexes) {
        super(context, R.layout.status_dialog_item_layout, statusStrings);
        this.context = context;
        this.statusValues = statusValues;
        mSelectedItems = selectedItemsIndexes;

        texts = statusStrings;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.status_dialog_item_layout, null);
        }

        TextView textView = convertView.findViewById(R.id.spinnerItemText);
        ImageView imageView = convertView.findViewById(R.id.statusIcon);
        CheckBox checkBox = convertView.findViewById(R.id.checkbox);

        textView.setText(texts.get(position));
        StatusIconTint.setTint(context, imageView, statusValues[position]);

        if(mSelectedItems.contains(position)) checkBox.setChecked(true);
        else checkBox.setChecked(false);

        return convertView;
    }

}
