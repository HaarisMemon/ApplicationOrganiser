package com.memonade.apptracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.memonade.apptracker.model.FilterType;

import java.util.ArrayList;
import java.util.List;

import static com.memonade.apptracker.MainActivity.CHECKED_ITEMS;
import static com.memonade.apptracker.MainActivity.FILTER_LIST;
import static com.memonade.apptracker.MainActivity.FILTER_TYPE;

/**
 * This class represents the custom dialog for the filter selection.
 */

public class FilterDialogFragment extends DialogFragment {

    private List<Integer> mSelectedItems;
    private String filterType;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSelectedItems = new ArrayList();

        //get the list of all items for the filter type
        String[] allItems = getArguments().getStringArray(FILTER_LIST);

        filterType = getArguments().getString(FILTER_TYPE);
        //get all the previously checked items (null at start)
        boolean[] checkedItems = getArguments().getBooleanArray(CHECKED_ITEMS);

        if(checkedItems == null) checkedItems = new boolean[allItems.length];
        else {
            //add previously selected items to the selected items list
            for (int i = 0; i < checkedItems.length; i++) {
                if (checkedItems[i]) mSelectedItems.add(i);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(String.format("Pick your %s", filterType))
                .setMultiChoiceItems(allItems, checkedItems,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int index,
                                                boolean isChecked) {
                                if (isChecked) {
                                    //add index of item if user checks item
                                    mSelectedItems.add(index);
                                } else if (mSelectedItems.contains(index)) {
                                    //remove item index if unchecked
                                    mSelectedItems.remove(Integer.valueOf(index));
                                }
                            }
                        })
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity callingActivity = (MainActivity) getActivity();
                        //call main activity to store selected items list
                        callingActivity.onUserSelectValue(FilterType.findFilterByName(filterType), mSelectedItems);
                    }
                })
                .setNeutralButton("Clear", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity callingActivity = (MainActivity) getActivity();
                        mSelectedItems.clear();
                        //call main activity to store empty selected items list
                        callingActivity.onUserSelectValue(FilterType.findFilterByName(filterType), mSelectedItems);
                    }
                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }
}
