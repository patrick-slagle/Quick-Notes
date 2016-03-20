package com.patrickslagle.notepad;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by patrickslagle on 2/11/16.                                                                                                                       
 */

/*
Dialog to delete a note from internal memory.
Gives the user a list of options.
 */
public class DeleteDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String[] list = getActivity().fileList();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete_title)
                .setItems(list, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //When the user clicks an item in the list,
                        //delete it from internal memory.
                        getActivity().deleteFile(getActivity().fileList()[which]);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

        return builder.create();
    }
}