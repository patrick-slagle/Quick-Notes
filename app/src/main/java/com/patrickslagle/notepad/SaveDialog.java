package com.patrickslagle.notepad;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

/**
 * Created by patrickslagle on 2/14/16.
 */

/*
Dialog to save the contents of
current note to internal memory.
 */
public class SaveDialog extends DialogFragment {

    //Used in Main Activity (NewNote) to listen for
    //user to request a save
    public interface DialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
    }

    DialogListener listener;

    //Used to cast the DialogListener to the activity,
    //necessary to use it with the main activity
    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        try {
            listener = (DialogListener) activity;
        } catch (ClassCastException e) {
            Log.i("CAST EXCEPTION", "Error casting activity");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.save_note, null))
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Now just call the interface listener.
                        // Data will be saved
                        listener.onDialogPositiveClick(SaveDialog.this);
                    }
                });
        return builder.create();
    }

}
