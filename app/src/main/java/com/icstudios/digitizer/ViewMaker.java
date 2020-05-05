package com.icstudios.digitizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class ViewMaker {
    
    appData data;
    marketingTasks allTask;
    topicTasks currentTopic;
    FloatingActionButton fab;
    taskView [] allTv;
    ArrayList<String> pages;
    int pageIndex = 0;
    Context context;
    public ArrayList<LinearLayout> allLayouts = new ArrayList<>();

    int currentPos;
    public static ArrayList<String> removedLines;

    public ViewMaker(Context context, String task) {
        this.context = context;
        data = mainNav.data;
        allTask = appData.getAllTasks();


        currentTopic = allTask.getTopicById(task);
        allTv = new taskView[currentTopic.tasks.size()];
        currentPos = Arrays.asList(appData.ids).indexOf(task);

    }

    public ArrayList<LinearLayout> getViews()
    {
        ArrayList<String> result;

        ScrollView sv = new ScrollView(context);

        final LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundResource(R.color.platte1);

        sv.addView(ll);

        Typeface face = Typeface.createFromAsset(context.getAssets(),
                "GveretLevin.ttf");

        Toolbar toolbar = ((Activity)context).findViewById(R.id.toolbar);
        toolbar.setTitle(currentTopic.title + ", "+ timeToFinish(currentTopic));
        toolbar.setBackgroundResource(R.drawable.upper);

        fab = ((Activity)context).findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appData.setCurrentTopic(context, (Activity)context);
            }
        });

        if(appData.checkTopicPos()<=currentPos)
            fab.setVisibility(View.GONE);

        allLayouts.add(new taskView(new TextView(context), context, currentTopic.subtitle).inn);

        for(int i = 0; i < currentTopic.tasks.size(); i++) {
            final String extra = currentTopic.tasks.get(i).extraDetail;
            if (extra.contains("pages")) {
                allTv[i] = new taskView(new TextView(context), new CheckBox(context), 3, context, context.getString(R.string.fill_here));
                pageIndex = i;
            }
            else if (extra.contains("population"))
                allTv[i] = new taskView(new TextView(context), new CheckBox(context), 0, context, context.getString(R.string.fill_here));
            else if (extra.contains("aim")) {
                String[] market = context.getResources().getStringArray(R.array.market_palaces);
                allTv[i] = new taskView(new TextView(context), new CheckBox(context), market, context);
            }
            else if(extra.contains("radio")) {
                //String[] market = getResources().getStringArray(R.array.content_marketing);
                int id = getStringResourceByName("array", extra.replace("radio",""));
                String[] market = context.getResources().getStringArray(id);
                allTv[i] = new taskView(new TextView(context), new CheckBox(context), market, context, market.length);
            }
            else if (extra.contains("empty"))
            {
                allTv[i] = new taskView(new TextView(context), new CheckBox(context), context);
            }
            else if (extra.contains("text"))
            {
                allTv[i] = new taskView(new TextView(context), context, "");
            }
            else
                allTv[i] = new taskView(new TextView(context), new CheckBox(context), new EditText(context), context);

            //allTv[i].inn.setPadding(50,80,50,80);
            allLayouts.add(allTv[i].inn);
            //ll.addView(allTv[i].inn);
            //lls[i] = new LinearLayout(context);
            //lls[i].setOrientation(LinearLayout.VERTICAL);
            //ll.addView(lls[i]);

            //tvs[i] = new TextView(context);
            //lls[i].addView(tvs[i]);
            //ll.addView(lls[i]);
            //innCbs[i] = new CheckBox(context);
        }

        for(int i = 0; i < currentTopic.tasks.size(); i++) {
            updateText(i);
            updateCheckBox(i);
            updateEditText(i);
        }
        allTv[pageIndex].removedLines = new ArrayList<String>();

        //ll.addView(NavLayout);

        //setContentView(sv);

        nextPage();

        return allLayouts;
    }

    public String timeToFinish(topicTasks topic)
    {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(topic.getTime());
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(System.currentTimeMillis());

        if (topic.undoneTasks()==0)
            return data.getString(R.string.time_done);

        long seconds = (calendar1.getTimeInMillis() - calendar2.getTimeInMillis()) / 1000;
        int hours = (int) (seconds / 3600);
        int days = hours/24;

        if (days == 0)
            return hours + " " + context.getString(R.string.hours);
        else return days + " " + context.getString(R.string.days);
    }

    public Boolean checkEditTextNotNull(ArrayList<EditText> ets)
    {
        if(ets.size()==0) return false;
        for(int i = 0; i < ets.size(); i++)
            if(ets.get(i).getText().toString().equals(""))
                return false;
        return true;
    }

    public Boolean checkRadioNotNull(int i)
    {
        if(((RadioButton)allTv[i].radiogroup.getChildAt(allTv[i].radioSize-1)).isChecked()) {
            return allTv[i].et.size() != 0 && allTv[i].et.get(0).getText() != null && !allTv[i].et.get(0).getText().toString().equals("");
        }
        else {
            for (int j = 0; j < allTv[i].radioSize - 1; j++) {
                if (((RadioButton) allTv[i].radiogroup.getChildAt(j)).isChecked())
                    return true;
            }
            return false;
        }
    }

    public Boolean checkCheckBoxNotNull(CheckBox [] cbs)
    {
        for(int i = 0; i < cbs.length; i++)
            if(cbs[i].isChecked())
                return true;
        return false;
    }

    public void updateEditText(int i)
    {
        final int g = i;
        final EditText et = new EditText(context);
        final String extra = currentTopic.tasks.get(i).extraDetail;

        ArrayList<String> result = currentTopic.tasks.get(i).getResult();

        if (extra.contains("pages"))
        {
            if(result!=null)
                for (int j = 0; j < result.size(); j++)
                {
                    if(j < allTv[i].et.size())
                        allTv[i].et.get(j).setText(result.get(j));
                    else {
                        allTv[i].addLine(2, context, "");
                        allTv[i].et.get(j).setText(result.get(j));
                    }
                }
            pages = result;
        }
        else if (extra.contains("population"))
        {
            //ArrayList<String> popul = currentTopic.tasks.get(1).getResult();

            if(pages!=null) {
                allTv[i].removeMoreLines(allTv[pageIndex].removedLines, currentTopic.tasks.get(i));
                //int biggerNum = pages.size();
                //if(allTv[i].et.size() > pages.size()) biggerNum = allTv[i].et.size();
                for (int j = 0; j < pages.size(); j++) {
                    if (allTv[i].et.size() < pages.size())
                        allTv[i].addLine(0, context, pages.get(j), pages.get(j) + ": ", j);
                    else if (!pages.get(j).equals(allTv[i].et.get(j).getHint().toString())) {
                        //allTv[i].addedLayout.get(j).removeAllViews();
                        allTv[i].addLine(0, context, pages.get(j), pages.get(j) + ": ", j);
                    }
                }
            }
            if(result!=null)
                for (int j = 0; j < result.size(); j++)
                {
                    if(allTv[i].et.size()>j)
                        allTv[i].et.get(j).setText(result.get(j));
                }
        }
        else if(extra.contains("aim")){
            if(result!=null)
                for (int j = 0; j < result.size(); j++)
                {
                    allTv[i].cbsList[j].setChecked(Boolean.valueOf(result.get(j)));
                }
        }
        else if(extra.contains("radio")){
            Boolean something = false;
            if(result!=null&&result.size()!=0) {
                String []results = result.get(0).split("_");
                if(results.length==2) {
                    int pos = Integer.parseInt(results[0]);
                    ((RadioButton) allTv[i].radiogroup.getChildAt(pos)).setChecked(true);

                    if (pos == allTv[i].radioSize - 1) {
                        allTv[i].addEditText(context);
                        allTv[i].et.get(0).setText(results[1]);
                    }
                }
            }
        }
        else if(extra.contains("empty")){

        }
        else
        {
            allTv[i].et.get(0).setHint(context.getString(R.string.enter_answer));
            //allTv[i].inn.addView(et);
            //allText.add(et);
            if(result!=null)
                allTv[i].et.get(0).setText(result.get(0));
        }

        //allTv[i].makeEnabled(currentTopic.tasks.get(i).isDone);
        allTv[i].inn.invalidate();
    }

    public void updateCheckBox(final int i)
    {
        final String extra = currentTopic.tasks.get(i).extraDetail;
        final CheckBox [] cbs = allTv[i].cbsList;
        final ArrayList<EditText> ets = allTv[i].et;

        allTv[i].task.setText(currentTopic.tasks.get(i).taskName);

        String type = extra;
        if(extra.contains("pages")) type = "pages";
        else if (extra.contains("aim")) type = "aim";
        else if(extra.contains("population")) type = "population";
        else if(extra.contains("radio")) type = "radio";

        if(currentTopic.tasks.get(i).isDone) allTv[i].task.setChecked(true);

        allTv[i].makeEnabled(currentTopic.tasks.get(i).isDone);

        allTv[i].task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ArrayList<String> result = new ArrayList<>();

                if (extra.contains("pages")) {
                    if(checkEditTextNotNull(ets) && isChecked) {
                        pages = new ArrayList<String>();
                        for (int i = 0; i < ets.size(); i++) {
                            result.add(ets.get(i).getText().toString());
                            pages.add(ets.get(i).getText().toString());
                        }
                    }
                    else
                    {
                        result = null;
                        allTv[i].task.setChecked(false);
                    }
                    allTask.setDone(currentPos, i, allTv[i].task.isChecked(), result);
                } else if (extra.contains("aim")) {
                    if (checkCheckBoxNotNull(cbs) && isChecked) {
                        for (int i = 0; i < cbs.length; i++) {
                            result.add(String.valueOf(cbs[i].isChecked()));
                        }
                        setTopicsToDo(cbs);
                    }
                    else {
                        result = null;
                        allTv[i].task.setChecked(false);
                    }
                    allTask.setDone(currentPos, i, allTv[i].task.isChecked(), result);
                } else if(extra.contains("population"))
                {
                    if(checkEditTextNotNull(ets) && isChecked) {
                        for (int i = 0; i < ets.size(); i++) {
                            result.add(ets.get(i).getText().toString());
                        }
                    }
                    else {
                        result = null;
                        allTv[i].task.setChecked(false);
                    }
                    allTask.setDone(currentPos, i, allTv[i].task.isChecked(), result);
                    allTv[pageIndex].removedLines = new ArrayList<String>();
                } else if(extra.contains("radio"))
                {
                    if(checkRadioNotNull(i) && isChecked) {
                        for (int j = 0; j < allTv[i].radioSize; j++)
                        {
                            if(((RadioButton)allTv[i].radiogroup.getChildAt(j)).isChecked())
                                result.add(j + "_" + ((RadioButton)allTv[i].radiogroup.getChildAt(j)).getText().toString());
                        }

                        if(result.get(0).contains(context.getString(R.string.somtihng_else))) {
                            result.remove(0);
                            result.add(allTv[i].radioSize-1 + "_" + allTv[i].et.get(0).getText().toString());
                        }
                    }
                    else {
                        result = null;
                        allTv[i].task.setChecked(false);
                    }
                    allTask.setDone(currentPos, i, allTv[i].task.isChecked(), result);
                } else if(extra.contains("empty")){
                    result.add("OK");
                    allTask.setDone(currentPos, i, isChecked, result);
                }else {
                    if(!allTv[i].et.get(0).getText().toString().equals("") && isChecked) {
                        result.add(allTv[i].et.get(0).getText().toString());
                    }
                    else {
                        result = null;
                        allTv[i].task.setChecked(false);
                    }
                    allTask.setDone(currentPos, i, allTv[i].task.isChecked(), result);
                }

                appData.saveData(context);
                buttonView.getTag();
                nextPage();

                for(int i = 0; i < currentTopic.tasks.size(); i++) {
                    updateText(i);
                    updateEditText(i);
                }

                if (isChecked) {

                } else {

                }

                allTv[i].makeEnabled(allTv[i].task.isChecked());
            }
        });

        allTv[i].inn.invalidate();
    }

    public void setCheckOrNot(Boolean isChecked, String type, int j)
    {
        allTv[j].makeEnabled(isChecked);

        /*
        final String extra = currentTopic.tasks.get(j).extraDetail;
        final CheckBox [] cbs = allTv[j].cbsList;
        final ArrayList<EditText> ets = allTv[j].et;

        if (type.contains("pages")) {
            if(isChecked) {
                pages = new ArrayList<String>();
                for (int i = 0; i < ets.size(); i++) {
                    ets.get(i).setEnabled(false);
                }
            }
            else
            {
                for (int i = 0; i < ets.size(); i++) {
                    ets.get(i).setEnabled(true);
                }
            }
        } else if (extra.contains("aim")) {
            if (isChecked) {
                for (int i = 0; i < cbs.length; i++) {
                    cbs[i].setEnabled(false);
                }
            }
            else {
                for (int i = 0; i < cbs.length; i++)
                    cbs[i].setEnabled(true);
            }
        } else if(extra.contains("population"))
        {
            if(isChecked) {
                for (int i = 0; i < ets.size(); i++) {
                    ets.get(i).setEnabled(false);
                }
            }
            else {
                for (int i = 0; i < ets.size(); i++) {
                    ets.get(i).setEnabled(true);
                }
            }
        } else if(extra.contains("radio"))
        {
            if(isChecked) {
                for(int i = 0; i < allTv[j].radioSize; i ++)
                {
                    allTv[j].radiogroup.getChildAt(i).setEnabled(false);
                }
                if(allTv[j].et.size()!=0)
                allTv[j].et.get(0).setEnabled(false);
            }
            else {
                for(int i = 0; i < allTv[j].radioSize; i ++)
                {
                    allTv[j].radiogroup.getChildAt(i).setEnabled(true);
                }
                if(allTv[j].et.size()!=0)
                allTv[j].et.get(0).setEnabled(true);
            }
        }else {
            if(isChecked) {
                if(allTv[j].et!=null)
                    allTv[j].et.get(0).setEnabled(false);
            }
            else {
                if(allTv[j].et!=null)
                    allTv[j].et.get(0).setEnabled(true);
            }
        }

         */
    }

    public void setTopicsToDo(CheckBox [] cbs)
    {
        int j = 0;
        for(int i = 4; i < appData.allTasks.getAllTopics().size(); i++) {
            appData.allTasks.getAllTopics().get(i).setToDo(cbs[j].isChecked());
            j++;
        }
        //appData.saveData(context);
        updateMenu();
    }

    public void updateMenu()
    {
        NavigationView navigationView = ((Activity)context).findViewById(R.id.nav_view);
        final Menu menuNav=navigationView.getMenu();
        for(int i = 0; i < /*menuNav.size() data.checkTopicPos()*/appData.allTasks.getAllTopics().size() ; i++) {
            if((!appData.allTasks.getAllTopics().get(i).getToDo() || appData.allTasks.getAllTopics().get(i).undoneTasks() > 0) &&  i != appData.checkTopicPos())
                menuNav.getItem(i).setEnabled(false);
        }
    }

    public void updateText(int i)
    {
        ArrayList<String> result;

        final String extra = currentTopic.tasks.get(i).extraDetail;
        //tvs[i] = new TextView(context);

        String text = currentTopic.tasks.get(i).task;
        if (extra.contains("link")) {
            //text = text.replace("link","<a href=\"https://instantdomainsearch.com/\">ניתן לבדוק האם הדומיין פנוי באתר זה. הכניסו את השם ואת הסיומת.</a>");
            text = text + "<a href=\"https://instantdomainsearch.com/\">"+context.getString(R.string.domain_check)+"</a>";
        } else if (extra.contains("aim")) {
            result = allTask.getTopicById("strategy").tasks.get(0).getResult();
            if (result != null) {
                text = context.getString(R.string.remaind_to_goals) + "<br>";
                for (int j = 0; j < result.size(); j++) {
                    text += j + 1 + ". " + (result.get(j)) + "<br>";
                }
                result = allTask.getTopicById("strategy").tasks.get(1).getResult();
                if(result!=null)
                    text += context.getString(R.string.place_of_the_bussines) + "<br>" + result.get(0);
                else text += context.getString(R.string.last_assaingments);
            }
        } else if (extra.contains("ads")) {
            result = allTask.getTopicById("content_marketing").tasks.get(1).getResult();
            ArrayList<String> result2 = allTask.getTopicById("content_marketing").tasks.get(7).getResult();
            if (result != null && result2 != null) {
                text += "<br>";
                for(int j = 0; j < result.size(); j++)
                    text += "<br>" + context.getString(R.string.audience) + result.get(j) + "<br>" + context.getString(R.string.audience_exist) + result2.get(j);
            }

            text += context.getString(R.string.where_to_publish);
            /*
            text += "<br><br>" + "אז איפה אפשר לפרסם? יש המון אפשרויות! נביא כאן כמה דוגמאות משמעותיות:" + "<br>";
            text += "<br>" + "<a href=\"https://ads.google.com/\">פלטפורמת החיפוש של גוגל</a>: " + "אחת הפלטפורמות הגדולות לפירסום היא כמובן של גוגל. באתרים רבים ובאפליקציות רבות ישנם שטחי פרסום של גוגל. (בנוסף החיפוש תקף גם לSEO - מודעות בדף החיפוש בגוגל וכן בyouTube).";
            text += "<br>" + "<a href=\"https://www.facebook.com/business/ads\">פלטפורמת הפירסום של פייסבוק</a>: " + "פלטפורמה גדולה נוספת היא פייסבוק. כאן ניתן להראות את הפירסומות לפי סיווג המשתמשים, גילאים, תחומי עיניין וכו'.";
            text += "<br>" + "<a href=\"https://business.instagram.com/advertising/\">פלטפורמת הפירסום של אינסטגרם</a>: " + "רשת חברתית משמעותית נוספת היא אינסטגרם, המציעה מבסלולי פירסום משלה.";
            text += "<br>" + "<a href=\"https://www.waze.com/business/\">פלטפורמת הפירסום של וייז</a>: " + "דרך יצירתית נוספת לפירסום היא דרך וויז, המציעה שירותי פירסום מקומיים כאשר הנהגים בקרבת מקום.";

             */
        } else if (extra.contains("believe")) {
            result = allTask.getTopicById("strategy").tasks.get(0).getResult();
            if (result != null
                    && allTask.getTopicById("usp").tasks.get(3) != null
                    && allTask.getTopicById("usp").tasks.get(3).getResult() != null
                    &&  allTask.getTopicById("usp").tasks.get(0).getResult().get(0) != null) {
                //text += "<br>" + "כאשר אתם מנסחים את ה\"אני מאמין\" של העסק, שימו לב שאתם מתיחסים לדברים הבאים:" + "<br>" + "היעדים:" + "<br>";
                text += context.getString(R.string.my_belive_phrasing);
                for (int j = 0; j < result.size(); j++) {
                    text += j + 1 + ". " + (result.get(j)) + "<br>";
                }
                text += context.getString(R.string.usp_string) + "<br>" + allTask.getTopicById("usp").tasks.get(3).getResult().get(0) + "<br>";
                text += context.getString(R.string.audience_string) + "<br>" + allTask.getTopicById("usp").tasks.get(0).getResult().get(0) + "<br>";
                text += context.getString(R.string.how_are_you);

                text += context.getString(R.string.summery_string);
                String be = "";

                if(allTask.getTopicById("my_believe").tasks.get(4).getResult() != null)
                {
                    be  = allTask.getTopicById("my_believe").tasks.get(4).getResult().get(0);
                }
                else if (allTask.getTopicById("my_believe").tasks.get(0) != null
                                && allTask.getTopicById("my_believe").tasks.get(1).getResult() != null
                                &&  allTask.getTopicById("my_believe").tasks.get(2).getResult() != null
                                && allTask.getTopicById("my_believe").tasks.get(3).getResult() != null) {
                    be = context.getString(R.string.as) + allTask.getTopicById("my_believe").tasks.get(0).getResult().get(0) + " "
                            + context.getString(R.string.provide) + " " + allTask.getTopicById("my_believe").tasks.get(1).getResult().get(0) + " "
                            + context.getString(R.string.with) + " " + allTask.getTopicById("my_believe").tasks.get(2).getResult().get(0) + " "
                            + context.getString(R.string.in_goal) + " " + allTask.getTopicById("my_believe").tasks.get(3).getResult().get(0);
                }
                allTv[i].et.get(0).setText(be);
            }
        }
        if(!text.equals("empty"))
            allTv[i].tv.setText(Html.fromHtml(text));
        allTv[i].inn.invalidate();
    }

    public void nextPage()
    {
        if(currentTopic.undoneTasks()==0)
        {
            if (currentPos+1 < appData.ids.length){
                //TODO finish all!!
            }
            appData.deleteCalendarEvent(appData.ids[currentPos], context, (Activity)context);
            //appData.checkCalendarEvent(appData.ids[currentPos+1], context, (Activity)context);
            if(!currentTopic.getDone()) {
                appData.startNewTopic(context);
                currentTopic.setDone(true);
            }

            NavigationView navigationView = ((Activity)context).findViewById(R.id.nav_view);
            navigationView.getMenu().getItem(appData.checkTopicPos()).setEnabled(true);

/*
            int i = currentPos+1;
            while (!appData.allTasks.getAllTopics().get(i).getToDo())
            {
                i++;
            }

 */
            //appData.checkProgress(context, currentPos, (Activity)context);

            fab.setVisibility(View.VISIBLE);
        }
        else
        {
            fab.setVisibility(View.GONE);
        }
    }

    public void finishAll()
    {
        new AlertDialog.Builder(context)
                .setTitle("סיימת את הקורס!")
                .setMessage("כל הכבוד! מאתחל")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        appData.initTasks(context);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private int getStringResourceByName(String type, String aString) {
        String packageName = context.getPackageName();
        //return getResources().getIdentifier( packageName+":values/strings/" +aString , null, null);

        return context.getResources().getIdentifier(aString, type,
                context.getApplicationContext().getPackageName());
    }
}
