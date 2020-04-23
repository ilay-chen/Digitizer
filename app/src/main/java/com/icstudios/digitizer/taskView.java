package com.icstudios.digitizer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

public class taskView {
    public TextView tv;
    public CheckBox task;
    public CheckBox [] cbsList;
    public ArrayList<EditText> et;
    public ArrayList<LinearLayout> addedLayout;
    public LinearLayout inn;
    public RadioGroup radiogroup;
    public int radioSize;
    public ArrayList<String> removedLines;
    LinearLayout noteLayout;
    LinearLayout innerNoteLayout;
    ScrollView scrollNote;
    Typeface face;
    Button add;
    ArrayList<Button> removeButtons;

    public void makeEnabled(Boolean enable)
    {
        enable = !enable;
        if(cbsList!=null)
            for (CheckBox cb : cbsList)
            {
                if(cb!=null)
                    cb.setEnabled(enable);
            }

        if(et!=null&&et.size()>0)
            for(EditText et : et)
            {
                et.setEnabled(enable);
            }

        if(radiogroup!=null&&radioSize>0)
            for(int i = 0; i < radioSize; i++)
            {
                radiogroup.getChildAt(i).setEnabled(enable);
            }

        if(add!=null)
            add.setEnabled(enable);

        if(removeButtons!=null&&removeButtons.size()>0)
            for(Button remove : removeButtons)
            {
                remove.setEnabled(enable);
            }
    }

    public taskView()
    {

    }

    public taskView(TextView tv, Context c, String text)
    {
        essential(tv, c);
        tv.setText(text);
    }

    public taskView(TextView tv, CheckBox task, Context c)
    {
        essential(tv, c);
        //this.tv = tv;
        //this.tv.setMovementMethod(LinkMovementMethod.getInstance());
        this.task = task;

        face = Typeface.createFromAsset(c.getAssets(),
                "GveretLevin.ttf");
        this.task.setTypeface(face);
        this.task.setTextSize(25);
        this.task.setTextColor(c.getResources().getColor(R.color.colorPrimary));

        //this.inn = new LinearLayout(c);
        //inn.setOrientation(LinearLayout.VERTICAL);

        //this.inn.addView(this.tv);
        this.innerNoteLayout.addView(this.task);
    }
    public taskView(TextView tv, CheckBox task, String [] cbsList, Context c)
    {
        essential(tv, c);
        //this.tv = tv;
        //this.tv.setMovementMethod(LinkMovementMethod.getInstance());
        this.task = task;


        face = Typeface.createFromAsset(c.getAssets(),
                "GveretLevin.ttf");
        this.task.setTypeface(face);
        this.task.setTextSize(25);
        this.task.setTextColor(c.getResources().getColor(R.color.colorPrimary));

        //this.inn = new LinearLayout(c);
        //inn.setOrientation(LinearLayout.VERTICAL);

        //this.inn.addView(this.tv);
        this.innerNoteLayout.addView(this.task);
        addCheckboxs(cbsList, c);
    }
    public taskView(TextView tv, CheckBox task, String [] cbsList, Context c, int radioSize)
    {
        essential(tv, c);

        this.task = task;
        this.et = new ArrayList<EditText>();

        face = Typeface.createFromAsset(c.getAssets(),
                "GveretLevin.ttf");
        this.task.setTypeface(face);
        this.task.setTextSize(25);
        this.task.setTextColor(c.getResources().getColor(R.color.colorPrimary));

        //this.inn = new LinearLayout(c);
        //inn.setOrientation(LinearLayout.VERTICAL);

        //this.inn.addView(this.tv);
        this.innerNoteLayout.addView(this.task);
        this.radioSize = radioSize;
        addRadioButton(cbsList, c);
    }
    public taskView(TextView tv, CheckBox task, EditText et, Context c)
    {
        essential(tv, c);

        //this.tv = tv;
        //this.tv.setMovementMethod(LinkMovementMethod.getInstance());
        this.task = task;
        this.et = new ArrayList<EditText>();
        this.et.add(et);

        //ViewPager2 viewPager =  new ViewPager2(c);


        face = Typeface.createFromAsset(c.getAssets(),
                "GveretLevin.ttf");
        //tv.setTypeface(face);
        //this.tv.setTextSize(20);
        //this.tv.setTextColor(c.getResources().getColor(R.color.colorPrimary));
        //this.tv.setTextColor(Color.BLACK);
        //this.tv.setBackgroundResource(R.color.platte4);
        //face = Typeface.createFromAsset(c.getAssets(),
        //        "GveretLevin.ttf");
        this.task.setTypeface(face);
        this.task.setTextSize(25);
        this.task.setTextColor(c.getResources().getColor(R.color.colorPrimary));
        //task.setBackgroundResource(R.color.platte2);
        //task.setTextColor(R.color.platte3);
        this.et.get(0).setTypeface(face);
        //et.setBackgroundResource(R.color.platte2);

        //this.inn = new LinearLayout(c);
        //inn.setOrientation(LinearLayout.VERTICAL);

        /*
        LinearLayout.LayoutParams noteParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        noteParams.setMargins(40, 60, 40, 80);

        LinearLayout.LayoutParams innerNoteParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        innerNoteParams.setMargins(60, 270, 60, 80);

        LinearLayout noteLayout = new LinearLayout(c);
        noteLayout.setOrientation(LinearLayout.VERTICAL);
        noteLayout.setBackgroundResource(R.drawable.pinnote7);

        LinearLayout innerNoteLayout = new LinearLayout(c);
        innerNoteLayout.setOrientation(LinearLayout.VERTICAL);
*/
        //innerNoteLayout.addView(this.tv);
        innerNoteLayout.addView(this.task);
        innerNoteLayout.addView(this.et.get(0));

        //noteLayout.addView(innerNoteLayout, innerNoteParams);
        //this.inn.addView(frame2, layoutParams);
        //this.inn.addView(noteLayout, noteParams);
        //this.inn.setBackgroundResource(R.drawable.boardbackground2);
        //this.inn.setBackgroundResource(R.drawable.pinnote7);
        //this.inn.addView(this.task, layoutParams2);
        //this.inn.addView(this.et.get(0), layoutParams2);
    }
    public taskView(TextView tv, CheckBox task, int size, Context c, String hint) {

        essential(tv, c);
        //this.tv = tv;
        //this.tv.setMovementMethod(LinkMovementMethod.getInstance());
        this.task = task;
        this.et = new ArrayList<EditText>();
        addedLayout = new ArrayList<LinearLayout>();
        removedLines = new ArrayList<String>();
        removeButtons = new ArrayList<Button>();

        //tv.setTypeface(face);
        //tv.setTextSize(20);
        //tv.setTextColor(Color.BLACK);
        face = Typeface.createFromAsset(c.getAssets(),
                "GveretLevin.ttf");
        this.task.setTypeface(face);
        this.task.setTextSize(25);
        this.task.setTextColor(c.getResources().getColor(R.color.colorPrimary));

        //this.inn = new LinearLayout(c);
        //inn.setOrientation(LinearLayout.VERTICAL);

        //this.inn.addView(this.tv);
        this.innerNoteLayout.addView(this.task);

        int button = 0;
        for (int i = 0; i < size; i++) {
            if (i == 2) button++;
            addLine(button, c, hint);
        }

        /*
        for(int i = 0; i < size; i++) {
            this.et.add(new EditText(c));
            this.inn.addView(this.et.get(i));
        }
         */
    }

    public void essential(TextView tv, Context c)
    {
        this.tv = tv;
        this.tv.setMovementMethod(LinkMovementMethod.getInstance());

        face = Typeface.createFromAsset(c.getAssets(),
                "Alef.ttf");
        tv.setTypeface(face);
        this.tv.setTextSize(20);
        this.tv.setTextColor(c.getResources().getColor(R.color.colorPrimary));


        this.inn = new LinearLayout(c);


        LinearLayout.LayoutParams noteParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        noteParams.setMargins(40, 60, 40, 300);

        LinearLayout.LayoutParams innerNoteParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        innerNoteParams.setMargins(60, 270, 60, 80);

        noteLayout = new LinearLayout(c);
        noteLayout.setOrientation(LinearLayout.VERTICAL);
        noteLayout.setBackgroundResource(R.drawable.pinnote7);

        scrollNote = new ScrollView(c);

        innerNoteLayout = new LinearLayout(c);
        innerNoteLayout.setOrientation(LinearLayout.VERTICAL);

        innerNoteLayout.addView(this.tv);

        scrollNote.addView(innerNoteLayout);

        noteLayout.addView(scrollNote, innerNoteParams);
        //this.inn.addView(frame2, layoutParams);
        this.inn.addView(noteLayout, noteParams);
        this.inn.setBackgroundResource(R.drawable.boardbackground2);
    }

    public void addCheckboxs(String []data, Context c)
    {
        cbsList = new CheckBox[data.length];
        for(int j = 0; j < cbsList.length; j++) {
            cbsList[j] = new CheckBox(c);
            cbsList[j].setText(data[j]);
            cbsList[j].setTextSize(20);
            innerNoteLayout.addView(cbsList[j]);
        }
    }

    public void addRadioButton(String []data, final Context c)
    {
        RadioButton[] radiobutton = new RadioButton[data.length];
        radiogroup = new RadioGroup(c);
        cbsList = new CheckBox[data.length];
        for(int j = 0; j < cbsList.length; j++) {
            radiobutton[j] = new RadioButton(c);
            radiobutton[j].setText(data[j]);
            radiobutton[j].setId(j+1);
            radiobutton[j].setTextSize(20);
            radiogroup.addView(radiobutton[j]);
        }

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == cbsList.length) {
                            addEditText(c);
                        }
                        else {
                            removeEditText();
                        }
                    }
                });

        innerNoteLayout.addView(radiogroup);
    }

    public void addPages(Context c)
    {
        for(int i = 0; i < et.size(); i++) {
            //addLine(inn,0,c);
        }
    }

    public void addLine(int button, final Context c, final String hint)
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) c).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        final LinearLayout horiz = new LinearLayout(c);
        horiz.setOrientation(LinearLayout.HORIZONTAL);
        innerNoteLayout.addView(horiz);

        final EditText et = new EditText(c);
        et.setHint(hint);
        et.setTypeface(face);
        et.setSingleLine();
        et.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        int width = displayMetrics.widthPixels;
        if(button!=0) width -= 400;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        et.setLayoutParams(lp);
        horiz.addView(et);
        this.et.add(et);

        if(button==1) {
            add = new Button(c);
            add.setText(c.getString(R.string.add_button));

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addLine( 2, c, hint);
                }
            });

            horiz.addView(add);
        }

        else if (button==2){
            Button remove = new Button(c);
            remove.setText(c.getString(R.string.remove_button));
            removeButtons.add(remove);

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeLine(innerNoteLayout, horiz, et);
                }
            });
            horiz.addView(remove);
        }

        addedLayout.add(horiz);
    }

    public void removeMoreLines(ArrayList<String> removedLines, Task task)
    {
        ArrayList<String> result = task.getResult();

        for(int i = 0; i < removedLines.size(); i++) {
            for(int j = 0; j < addedLayout.size(); j++) {
                if(removedLines.get(i).equals(et.get(j).getHint().toString())) {
                    if(result!=null && result.size()>=j) {
                        result.remove(j);
                        task.setResult(result);
                    }
                    innerNoteLayout.removeView(addedLayout.get(j));
                    this.et.remove(j);
                    addedLayout.remove(j);
                }
            }
        }
    }

    public void removeMoreLines(int from, int end)
    {
        for(int i = from; i < end; i++) {
            inn.removeView(addedLayout.get(i));
            this.et.remove(i);
            addedLayout.remove(i);
        }
    }

    public void addLine(int button, final Context c, final String hint , String tv, int index)
    {
        final LinearLayout horiz = new LinearLayout(c);
        horiz.setOrientation(LinearLayout.HORIZONTAL);

        TextView textView = new TextView(c);
        textView.setText(tv);
        horiz.addView(textView);

        final EditText et = new EditText(c);
        et.setHint(hint);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        et.setLayoutParams(lp);
        horiz.addView(et);

        if(index>=addedLayout.size())
            innerNoteLayout.addView(horiz);
        else {
            innerNoteLayout.removeView(addedLayout.get(index));
            innerNoteLayout.addView(horiz, index+2);
        }

        if(button==1) {
            Button add = new Button(c);
            add.setText("הוסף תיבה");

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addLine( 2, c, hint);
                }
            });

            horiz.addView(add);
        }

        else if (button==2){
            Button remove = new Button(c);
            remove.setText("הסר תיבה");

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeLine(innerNoteLayout, horiz, et);
                }
            });
            horiz.addView(remove);
        }

        if(index>=addedLayout.size()) {
            addedLayout.add(horiz);
            this.et.add(et);
        }
        else {
            addedLayout.set(index, horiz);
            this.et.set(index, et);
        }
    }

    public void removeLine(LinearLayout inn, LinearLayout horiz, EditText et)
    {
        removedLines.add(et.getText().toString());
        inn.removeView(horiz);
        this.addedLayout.remove(horiz);
        this.et.remove(et);
    }

    public void removeEditText()
    {
        if(et.size()!=0 && et.get(0)!=null) {
            innerNoteLayout.removeView(et.get(0));
            this.et.remove(0);
        }
    }

    public void addEditText(Context c){
        if(et.size()==0) {
        EditText newEt = new EditText(c);
        face = Typeface.createFromAsset(c.getAssets(),
                "GveretLevin.ttf");
        newEt.setTypeface(face);
            this.et.add(newEt);
            innerNoteLayout.addView(et.get(0));
        }
        //innerNoteLayout.addView(newEt);
        et.get(0).requestFocus();
        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et.get(0), InputMethodManager.SHOW_IMPLICIT);
    }
}


