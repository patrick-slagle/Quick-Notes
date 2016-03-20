package com.patrickslagle.notepad;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
This application allows a user to
create attractive looking and versatile notes.
It includes the ability to save the note, delete a saved
note, view all saved notes in a list, and seamlessly
choose between bullet-point list and non-bullet point notes,
all from the main UI.

This class lays out the controller for the main UI
and acts as the "MainActivity" of the application.

It also contains the class for the GUI. It draws
an attractive notepad-like design on top of the EditText using Canvas.
It involves a yellow background color and Paint objects
that draw page lines and circles ("hole punches")
*/
public class NewNote extends Activity
        implements SaveDialog.DialogListener {

    protected static EditText pad;
    protected ImageButton saveButton, deleteButton,
                            menuButton, bulletButton,
                            mailButton, clearButton;
    private String[] lines;


    ArrayList<String> texts = new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //The UI
        pad = (EditText) findViewById(R.id.note);

        //Save Not
        saveButton = (ImageButton) findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSave();
            }
        });

        //Icon in the bottom bar for deleting the note
        deleteButton = (ImageButton) findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onDelete();
            }
        });

        //Icon in the bottom bar for viewing all of the notes
        menuButton = (ImageButton) findViewById(R.id.menu);
        menuButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                onMenuSelect();
            }
        });

        //Icon in the bottom bar for switching to bullet-list view
        bulletButton = (ImageButton) findViewById(R.id.bullets);
        bulletButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                onBulletSelected();
            }
        });

        //Icon in the bottom bar for e-mailing a note
        mailButton = (ImageButton) findViewById(R.id.mail);
        mailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });

        //Icon in the bottom bar for clearing the note text
        clearButton = (ImageButton) findViewById(R.id.clear_note);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearNote();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    //open the file list
    private void onMenuSelect() {

        MenuDialog mDialog = new MenuDialog();
        mDialog.show(getFragmentManager(), "Menu Dialog");

    }

    //open the save dialog
    private void onSave() {

        SaveDialog sDialog = new SaveDialog();
        sDialog.show(getFragmentManager(), "MenuItems");
    }

    //On clicking "save" on the SaveDialog
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        Dialog dialogView = dialog.getDialog();

        //The SaveNote Dialog
        EditText editText = (EditText) dialogView.findViewById(R.id.note_name);

        //User input for note name
        String noteName = editText.getText().toString();

        /*
        Save the note by opening a OutputStreamWriter with the note's name
         and writing the pad's text as file content
         */
        try {
            OutputStreamWriter out =
                    new OutputStreamWriter(openFileOutput(noteName, 0));
            out.write(pad.getText().toString());
            out.close();

        } catch (Exception e) {
            Log.e("ERROR", "Error creating output stream");
        }
    }

    //On e-mail button clicked
    private void sendMail() {

        /**
        Sending e-mail:
        use an array (lines) to get the text from the note,
        each line of the note as an index in the array.
        lines[0] is h subject of the e-mail as it would be the
        note header(as per the UI). The body is everying except index 0.
        Use intent to send e-mail.
         */
        String subject = "";
        lines = pad.getText().toString().split("\n");
        String body = pad.getText().toString().replace(lines[0], " ").trim();

        subject = lines[0];

        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode(" ") +
                "?subject=" + Uri.encode(subject) +
                "&body=" + Uri.encode(body);
        Uri uri = Uri.parse(uriText);

        send.setData(uri);
        startActivity(Intent.createChooser(send, "Send mail..."));
    }

    //On delete button clicked
    private void onDelete() {
        DeleteDialog dDialog = new DeleteDialog();
        dDialog.show(getFragmentManager(), "Delete Dialog");
    }

    //Clear text
    private void clearNote() {
        pad.setText("");
    }

    /**
    Make the note text an array element
     */
    private void setPadTextArray() {
        String contents = pad.getText().toString();
        String string = "";
        lines = pad.getText().toString().split("\n");

        //get rid of any bullet points
        if(contents.contains("\u2022")) {
            contents = contents.replace("\u2022", "");
        }

        //Check for special circumstances, such as
        //when the user adds plain text in bullet-point
        //mode. Ensure proper behavior.
        for(int i = 0; i < lines.length; i++) {
            if(i == 0) {
            } else if (!lines[i].contains("\u2022")) {
                string = string + lines[i] + "\n";
            }
            texts.add(contents + string);
        }
    }

    /**
    Get the appropriate text to preserve
    note text when user toggles between bullet-list
    and non-bullet list.
     */
    private String getPadTextArray() {
        if(texts.size() < 2) {
            return texts.get(texts.size() -1);
        }
        return texts.get(texts.size() - 2);
    }

    /**
    This method handles the bullet list functionality.
    It adds everything from the note as an index in an array
    upon button press. It then uses a boolean to determine if the user
    is currently under a bullet point list or not and acts accordingly to
    modify the note contents.
     */
    private void onBulletSelected() {

        setPadTextArray();

        boolean isText;
        String contents = pad.getText().toString();
        lines = pad.getText().toString().split("\n");
        pad.setText(lines[0]);

        //Use boolean to determine if the text
        //contains any bullet points.
        if(!contents.contains("\u2022")) {
            isText = true;
        } else {
            isText = false;
        }

        if (isText) {
            for (int i = 0; i < lines.length; i++) {

                //Is this line empty? Don't add any bullet points.
                if (TextUtils.isEmpty(lines[i].trim())) {
                    pad.append(lines[i]);

                //Add a bullet point
                } else if (i > 0) {
                    pad.append("\n" + "\u2022" + " " + lines[i]);
                }
            }
        } else {
            pad.setText(getPadTextArray());
        }
    }

    public static class LinedEditor extends EditText {

        private Paint paint, cPaint, newPagePaint;

        //Set up the paint objects using the constructors
        public LinedEditor(Context context) {

            super(context);

            paint = new Paint();
            cPaint = new Paint();
            newPagePaint = new Paint();


            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(0x80000000);

            cPaint.setColor(0x80000000);

            newPagePaint.setStyle(Paint.Style.STROKE);
            newPagePaint.setStrokeWidth(8.0F);
            newPagePaint.setColor(0x80000000);

        }

        public LinedEditor(Context context, AttributeSet attrs) {

            super(context, attrs);

            paint = new Paint();
            cPaint = new Paint();
            newPagePaint = new Paint();

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(0x80000000);

            cPaint.setColor(0x80000000);

            newPagePaint.setStyle(Paint.Style.STROKE);
            newPagePaint.setStrokeWidth(8.0F);
            newPagePaint.setColor(0x80000000);
        }

        public LinedEditor(Context context, AttributeSet attrs, int defStyle) {

            super(context, attrs, defStyle);

            paint = new Paint();
            cPaint = new Paint();
            newPagePaint = new Paint();


            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(0x80000000);


            cPaint.setColor(0x80000000);

            newPagePaint.setStyle(Paint.Style.STROKE);
            newPagePaint.setStrokeWidth(8.0F);
            newPagePaint.setColor(0x80000000);
        }


        //I created two methods for making the drawCircles() method
        //Easier to read and type in the onDraw() method
        protected void drawCircles(Canvas canvas, int height) {
            for(int i = 0; i < 5; i++) {
                canvas.drawCircle(45, height, 15, cPaint);
            }
        }

        protected void drawCircles(Canvas canvas, int height, int num) {
            for(int i = 0; i < 5; i++) {
                canvas.drawCircle(45, height + num, 15, cPaint);
            }
        }

        /*
        Steps to the onDraw() method:

        Draw the horizontal lines on the page starting at the baseline of
        the first line of the note body(leaving room for the note heading).

        Draw the vertical line on the left side of the page.

        Draw the parallel lines that create the header baseline

        Draw the hole punches 50px down the page,
        50% down the page, and 85 px above the bottom of the page.

        Then draw the other punches down the page to replicate the "notepad"
        look for 5 pages. Use a while loop and the drawCircles() methods I created
        to accomplish this.
         */
        protected void onDraw(Canvas canvas) {

            int left = getLeft();
            int right = getRight();
            int paddingTop = getPaddingTop();
            int paddingBottom = getPaddingBottom();
            int height = getHeight();
            int width = getWidth();
            int lineHeight = getLineHeight();

            //number of pages of UI available to the user
            int pages = 5;

            //Used to make space for header line
            int start = 38;

            //The amount of lines available on the screen
            int count = (height - paddingTop - paddingBottom) / lineHeight;

            for (int i = 0; i < count * pages; i++) {

                //The baseline of each cursor position. "i" used to calculate
                //what line we are on
                int baseLine = lineHeight * (i + 1) + paddingTop + start;


                canvas.drawLine(left, baseLine, right, baseLine, paint);
            }

            //Vertical line
            canvas.drawLine(80, 0, 80, height * pages, paint);

            //Top parallel lines
            canvas.drawLine(0, 78, width, 78, paint);
            canvas.drawLine(0, 73, width, 73, paint);

            //The "hole punches"
            drawCircles(canvas, 50);
            drawCircles(canvas, height / 2);
            drawCircles(canvas, height - 85);

            //Since we have so many pages in the notepad,
            //a while loop is used in order to avoid a lot
            //of repeated code for hole punches on pages 2-5.
            int counter = 1;
            while(counter < 5) {

                if(counter == 0) { counter++; }

                drawCircles(canvas, 50, height * counter);
                drawCircles(canvas, height / 2, height * counter);
                drawCircles(canvas, height - 85, height * counter);
                counter++;
            }
            super.onDraw(canvas);
        }
    }
}

