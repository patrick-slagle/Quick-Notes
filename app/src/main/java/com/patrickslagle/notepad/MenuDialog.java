package com.patrickslagle.notepad;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by patrickslagle on 2/15/16.
 */

/*
Dialog that shows a list
of files from internal memory to choose from.
When the user chooses one, pull up that note's text and populate
the note with it.
 */
public class MenuDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String[] list = getActivity().fileList();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.menu_title)
                .setItems(list, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        /*
                        Cycle through the files in internal memory. If the name
                        of file in location i equals the name of file in list
                        that was selected, open InputStream and BufferedReader
                        to get the text of that note and populate the EditText with it
                         */
                        for (int i = 0; i < list.length; i++) {
                            if (list[i].equals(list[which])) {
                                try {
                                    InputStream in = getActivity().openFileInput(list[which]);

                                    if (in != null) {

                                        InputStreamReader tmp = new InputStreamReader(in);

                                        BufferedReader reader = new BufferedReader(tmp);

                                        StringBuilder buf = new StringBuilder();

                                        String str;

                                        while ((str = reader.readLine()) != null) {

                                            buf.append(str + "\n");
                                        }
                                        in.close();
                                        NewNote.pad.setText(buf.toString());
                                    }
                                } catch (IOException e) {
                                    Log.e("IO", "problem establishing file input");
                                }
                            }
                        }
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
