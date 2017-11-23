package com.memonade.applicationtracker;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.memonade.applicationtracker.model.ApplicationStage;
import com.memonade.applicationtracker.model.FilterType;
import com.memonade.applicationtracker.model.StatusFilterArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.memonade.applicationtracker.MainActivity.CHECKED_ITEMS;

/**
 * This class represents the custom dialog for the status filter selection.
 */

public class StatusFilterDialogFragment extends DialogFragment {

    private List<Integer> mSelectedItems;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Pick your " + FilterType.STATUS.toString());

        mSelectedItems = new ArrayList<>();
        final List<String> statusStrings = ApplicationStage.Status.getStatusStrings();

        //get all the previously checked items (null at start)
        boolean[] checkedItems = getArguments().getBooleanArray(CHECKED_ITEMS);
        if(checkedItems != null) {
            //add previously selected items to the selected items list
            for (int i = 0; i < checkedItems.length; i++) {
                if (checkedItems[i]) mSelectedItems.add(i);
            }
        }

        View rootView = inflater.inflate(R.layout.status_dialog_layout, container, false);
        ListView statusDialogListView = rootView.findViewById(R.id.statusDialogListView);
        statusDialogListView.setAdapter(new StatusFilterArrayAdapter(getActivity(), statusStrings, mSelectedItems));

        statusDialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckBox checkBox = view.findViewById(R.id.checkbox);

                if(checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    mSelectedItems.remove(Integer.valueOf(i));
                } else {
                    checkBox.setChecked(true);
                    mSelectedItems.add(i);
                }

            }
        });

        Button applyButton = rootView.findViewById(R.id.applyButton);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity callingActivity = (MainActivity) getActivity();
                //call main activity to store selected items list
                callingActivity.onUserSelectValue(FilterType.STATUS, mSelectedItems);

                dismiss();
            }
        });

        Button cancelButton = rootView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Button clearButton = rootView.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity callingActivity = (MainActivity) getActivity();
                mSelectedItems.clear();
                callingActivity.onUserSelectValue(FilterType.STATUS, mSelectedItems);
                dismiss();
            }
        });

        return rootView;
    }

}
