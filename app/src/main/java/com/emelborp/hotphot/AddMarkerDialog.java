package com.emelborp.hotphot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Manuel on 09.10.2014.
 */
public class AddMarkerDialog extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
      * implement this interface in order to receive event callbacks.
      * Each method passes the DialogFragment in case the host needs to query it. */
    public static interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_add_marker, null);
        builder.setView(view);
        builder.setMessage(R.string.add_marker_title)
                .setPositiveButton(R.string.save_marker, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            NoticeDialogListener callback = null;
                            try {
                                callback = (NoticeDialogListener) getTargetFragment();
                            } catch (ClassCastException e) {
                                Log.e(this.getClass().getSimpleName(), "Callback of this class must be implemented by target fragment!", e);
                                throw e;
                            }

                            if (callback != null) {
                                callback.onDialogPositiveClick(AddMarkerDialog.this);
                            }
                        }
                    })
                .setNegativeButton(R.string.dismiss_marker, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    NoticeDialogListener callback = null;
                                    try {
                                        callback = (NoticeDialogListener) getTargetFragment();
                                    } catch (ClassCastException e) {
                                        Log.e(this.getClass().getSimpleName(), "Callback of this class must be implemented by target fragment!", e);
                                        throw e;
                                    }

                                    if (callback != null) {
                                        callback.onDialogNegativeClick(AddMarkerDialog.this);
                                    }
                                }
                            }

                    );
                    // Create the AlertDialog object and return it
                    return builder.create();
                }

    public String getName() {
        EditText theNameText = (EditText) view.findViewById(R.id.marker_name);
        return theNameText.getText().toString();
    }
}
