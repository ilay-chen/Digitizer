package com.icstudios.digitizer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.navigation.NavigationView;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.icstudios.digitizer.mainNav.REQUEST_GOOGLE_PLAY_SERVICES;
import static com.icstudios.digitizer.mainNav.data;
import static com.icstudios.digitizer.signIn.mService;

public class appData extends Application {

    public static marketingTasks allTasks;
    static String[] topics, subtitle, ids;
    private static DatabaseReference mDatabase;
    public static GoogleAccountCredential mCredential;
    public static String userDataPath = "/data/";
    public static String userTaskPath = "/progress/";
    public static String userRootPath;
    public static userData mUserData;
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY, CalendarScopes.CALENDAR };

    @Override
    public void onCreate() {
        super.onCreate();

        //topics = getResources().getStringArray(R.array.topics);
        //subtitle = getResources().getStringArray(R.array.topics_text);
        ids = getResources().getStringArray(R.array.topics_id);
        topics = new String[ids.length];
        subtitle = new String[ids.length];

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        //Intent serviceIntent = new Intent(this, scheduledService.class);
        //this.startService(serviceIntent);

        PeriodicWorkRequest.Builder myWorkBuilder =
                new PeriodicWorkRequest.Builder(serverWorker.class, 24, TimeUnit.HOURS);

        PeriodicWorkRequest myWork = myWorkBuilder.build();
        WorkManager.getInstance(getApplicationContext())
                .enqueueUniquePeriodicWork("server", ExistingPeriodicWorkPolicy.KEEP, myWork);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            userRootPath = "users/" + user.getUid();
            userDataPath = userRootPath + "/data/";
            userTaskPath = userRootPath + "/progress/";
        }
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("strings", MODE_PRIVATE);
        Boolean firstTime = sharedPref.getBoolean("firstTime", true);

//        UserManager.Companion.initListener();
        //if(user!=null && user.getDisplayName()!=null && !firstTime)
        //   checkProgress(this, -1);

/*
        OneTimeWorkRequest notificationWork =
                new OneTimeWorkRequest.Builder(remainderWorker.class)
                .setInitialDelay(15, TimeUnit.MINUTES)
                .build();

        //PeriodicWorkRequest remainderWork = notificationWork.build();
        WorkManager.getInstance(getApplicationContext())
                .enqueueUniqueWork("reminder", ExistingWorkPolicy.KEEP, notificationWork);


        OneTimeWorkRequest notificationWork2 =
                new OneTimeWorkRequest.Builder(notiWorker.class)
                        .setInitialDelay(15, TimeUnit.MINUTES)
                        .build();
*/
        //PeriodicWorkRequest remainderWork = notificationWork.build();
        //WorkManager.getInstance(getApplicationContext())
        //        .enqueueUniqueWork("reminder3", ExistingWorkPolicy.KEEP, notificationWork2);

        //WorkManager.getInstance(getApplicationContext()).enqueueUniqueWork("noti", ExistingWorkPolicy.KEEP, notificationWork);
        //sendNotification();
    }

    public static int[] timeToFinish(topicTasks topic)
    {
        int[] values = new int[2];
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(topic.getTime());
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(System.currentTimeMillis());

        long seconds = (calendar1.getTimeInMillis() - calendar2.getTimeInMillis()) / 1000;
        int hours = (int) (seconds / 3600);
        int days = hours/24;
        values[0] = hours;
        values[1] = days;

        return values;
    }

    public static void setRemainder(int hours, Context context)
    {
        OneTimeWorkRequest notificationWork =
                new OneTimeWorkRequest.Builder(remainderWorker.class)
                        .setInitialDelay(hours, TimeUnit.HOURS)
                        .build();

        //PeriodicWorkRequest remainderWork = notificationWork.build();
        WorkManager.getInstance(context)
                .enqueueUniqueWork("reminder", ExistingWorkPolicy.REPLACE, notificationWork);
    }

    public static void startNewTopic(Context context)
    {
        int currentId = checkTopicPos();
        topicTasks topic = allTasks.allTopics.get(currentId);
        int[] values = timeToFinish(topic);

        int days = values[1];

        weekDelay(currentId, -days + 7, topic.getScheduledDate());
        //delayTasks(-days);
        saveData(context);
    }

    public static void checkProgress(Context context, int customPos, Activity activity)
    {
        readData(context);
        //int currentId = customPos;
        //if(currentId==-1)
        int currentId = checkTopicPos();
        if(currentId==-1) return;
        topicTasks topic = allTasks.allTopics.get(currentId);

        if(topic.undoneTasks()==0) return;

        int[] values = timeToFinish(topic);

        int hours = values[0];
        int days = values[1];

        if(hours > 24) {
            if (topic.undoneTasks() > 0) {
                sendNotification(days, context, context.getString(R.string.days));
                setRemainder(hours/2, context);
            } else {
                weekDelay(currentId, -days, topic.getScheduledDate());
                //delayTasks(-days);
                saveData(context);
                updateCalendarEvent(appData.ids[currentId], context, activity);
                //setRemainder(hours);
                //move to next task + set time to next date
            }
        }
        else if(hours > 0) {
            if (topic.undoneTasks() > 0) {
                sendNotification(hours, context,context.getString(R.string.hours));
                setRemainder(1, context);
            } else {
                //delayTasks(-days+7);
                //saveData(context);
                //setRemainder(hours);
                //move to next task + set time to next date
            }
        }
        else {
            if (topic.undoneTasks() > 0) {
                sendNotification(days, context,context.getString(R.string.days));
                setRemainder(1, context);
                weekDelay(currentId, 2, topic.getScheduledDate());
                //delayTasks(2);
                saveData(context);
                updateCalendarEvent(appData.ids[currentId], context, activity);
                //alert task to do + delay tasks time
            } else {
                weekDelay(currentId, (days*-1)+7, topic.getScheduledDate());
                //delayTasks((days*-1)+7);
                saveData(context);
                updateCalendarEvent(appData.ids[currentId], context, activity);
                //setRemainder(0-hours);
                //move to next task
            }
        }
    }

    private static NotificationManager mNotificationManager;
    public static void sendNotification(int days, Context context, String units) {
        readData(context);
        String results = "אין מידע", head = context.getString(R.string.noti_head), title = context.getString(R.string.noti_title);
        int currentId = 0;

        if(allTasks != null)
        {
            currentId = checkTopicPos();
            topicTasks tt = allTasks.allTopics.get(currentId);
            results =  context.getString(R.string.noti_body1) + " " + tt.undoneTasks() + " " + context.getString(R.string.noti_body2) + " " + tt.tasks.size() + ". " + days + " "+ units + " " + context.getString(R.string.noti_body3);
            head = allTasks.allTopics.get(currentId).title;
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, "remainder");
        Intent ii = new Intent(context, signIn.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(results);
        bigText.setBigContentTitle(title);
        bigText.setSummaryText(head);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(results);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "remainder";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "remaind of the tasks",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        if(mNotificationManager!=null)
            mNotificationManager.notify(0, mBuilder.build());
    }

    public static void updateStrings(Context c)
    {
        String [] extra = null;
        String [] tasks = null, text = null;
        topicTasks topic = new topicTasks();

        for(int i=0; i < ids.length; i++) {
            String name = "topic_" + ids[i] + "_";
            topics[i] = c.getResources().getString(getStringResourceByName(ids[i], c));
            subtitle[i] = c.getResources().getString(getStringResourceByName(ids[i]+"_subtitle", c));

            int id = getStringArrayResourceByName(name+"task", c);
            tasks = c.getResources().getStringArray(id);
            id = getStringArrayResourceByName(name+"text", c);
            text = c.getResources().getStringArray(id);
            id = getStringArrayResourceByName(name+"extra", c);
            extra = c.getResources().getStringArray(id);

            if((allTasks.allTopics.size() > i)) {
                allTasks.updateStrings(ids[i], topics[i], subtitle[i], i, text, tasks, extra, true);
            }
            else if(i >= allTasks.allTopics.size()) {
                allTasks.addTopic(new topicTasks(tasks, text, extra, i+1, topics[i], subtitle[i], ids[i], true));
            }
        }
        saveData(c);
    }

    public static void initTasks(Context c)
    {
        String [] extra = null;
        allTasks = new marketingTasks();
        String [] tasks = null, text = null;
        topicTasks topic = new topicTasks();

        /*
        int holderint = getResources().getIdentifier("topic_00_task", "array",
                getApplicationContext().getPackageName());


        tasks = getResources().getStringArray(holderint);
        holderint = getResources().getIdentifier("topic_00_text", "array",
                getApplicationContext().getPackageName());
        text = getResources().getStringArray(holderint);
        holderint = getResources().getIdentifier("topic_00_extra", "array",
                getApplicationContext().getPackageName());
        extra = getResources().getStringArray(holderint);
        allTasks.addTopic(new topicTasks(tasks,text,extra,0,topics[0], subtitle[0],0));
*/
        for(int i=0; i < ids.length; i++)
        {
            String name = "topic_"+ids[i]+"_";
            topics[i] = c.getResources().getString(getStringResourceByName(ids[i], c));
            subtitle[i] = c.getResources().getString(getStringResourceByName(ids[i]+"_subtitle", c));

            int id = getStringArrayResourceByName(name+"task", c);
            tasks = c.getResources().getStringArray(id);
            id = getStringArrayResourceByName(name+"text", c);
            text = c.getResources().getStringArray(id);
            id = getStringArrayResourceByName(name+"extra", c);
            extra = c.getResources().getStringArray(id);
            /*
            switch (i)
            {
                case 0:
                    tasks = getResources().getStringArray(R.array.topic_01_task);
                    text = getResources().getStringArray(R.array.topic_01_text);
                    extra = getResources().getStringArray(R.array.topic_01_extra);
                    break;
                case 1: tasks = getResources().getStringArray(R.array.topic_02_task);
                    text = getResources().getStringArray(R.array.topic_02_text);
                    extra = getResources().getStringArray(R.array.topic_02_extra);
                    break;
                case 2: tasks = getResources().getStringArray(R.array.topic_03_task);
                    text = getResources().getStringArray(R.array.topic_03_text);
                    extra = getResources().getStringArray(R.array.topic_03_extra);
                    break;
                case 3: tasks = getResources().getStringArray(R.array.topic_04_task);
                    text = getResources().getStringArray(R.array.topic_04_text);
                    extra = getResources().getStringArray(R.array.topic_04_extra);
                    break;
                case 4: tasks = getResources().getStringArray(R.array.topic_05_task);
                    text = getResources().getStringArray(R.array.topic_05_text);
                    extra = getResources().getStringArray(R.array.topic_05_extra);
                    break;
                case 5: tasks = getResources().getStringArray(R.array.topic_06_task);
                    text = getResources().getStringArray(R.array.topic_06_text);
                    extra = getResources().getStringArray(R.array.topic_06_extra);
                    break;
            }

             */

            allTasks.addTopic(new topicTasks(tasks,text,extra,i+1,topics[i], subtitle[i],ids[i], true));
            //allTasks.addTopic(new topicTasks(tasks,text,extra,i,topics[i], subtitle[i],i));
        }
        saveData(c);
        //update();
    }

    static public void update()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());
            mDatabase.setValue(allTasks);

            mDatabase.setValue(allTasks, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        System.out.println("Data could not be saved. " + databaseError.getMessage());
                    } else {
                        System.out.println("Data saved successfully.");
                    }
                }
            });
        }
    }

    public static void weekDelay(int pos, int days, Calendar oldDate)
    {
        Calendar scheduledDate = oldDate;
        scheduledDate.add(Calendar.DATE, days);
        allTasks.delayDate(pos, scheduledDate);
    }

    public static void delayTasks(int days)
    {

        for(int i = 0; i < allTasks.allTopics.size(); i++)
        {
            if(allTasks.allTopics.get(i).undoneTasks()!=0)
                allTasks.allTopics.get(i).delayDate(days);
        }
    }

    private static int getStringArrayResourceByName(String aString, Context c) {
        String packageName = c.getPackageName();
        //return getResources().getIdentifier( packageName+":values/strings/" +aString , null, null);

            return c.getResources().getIdentifier(aString, "array",
                    c.getApplicationContext().getPackageName());
    }

    private static int getStringResourceByName(String aString, Context c) {
        String packageName = c.getPackageName();
        //return getResources().getIdentifier( packageName+":values/strings/" +aString , null, null);

        return c.getResources().getIdentifier(aString, "string",
                c.getApplicationContext().getPackageName());
    }

    public void setAllTasks(marketingTasks mt)
    {
        allTasks = mt;
    }

    public static marketingTasks getAllTasks() {
        return allTasks;
    }

    public static int checkTopicPos()
    {
        for(int i = 0; i < allTasks.allTopics.size(); i++)
        {
            if(allTasks.allTopics.get(i).undoneTasks() > 0 && allTasks.allTopics.get(i).getToDo())
                return i;
        }
        return -1;
    }

    public static void makeCalendarEvent(String id, Context c, Activity activity)
    {
        SharedPreferences sharedPref = c.getSharedPreferences("strings", MODE_PRIVATE);
        Boolean thereIsEvent = sharedPref.getBoolean(id + "Event", false);
        if(!thereIsEvent)
            makeEvent(id, 1, c, activity);
    }

    public static void deleteCalendarEvent(String id, Context c, Activity activity)
    {
        SharedPreferences sharedPref = c.getSharedPreferences("strings", MODE_PRIVATE);
        Boolean thereIsEvent = sharedPref.getBoolean(id + "Event", false);
        if(thereIsEvent)
            makeEvent(id, 2, c, activity);
    }

    public static void updateCalendarEvent(String id, Context c, Activity activity)
    {
        SharedPreferences sharedPref = c.getSharedPreferences("strings", MODE_PRIVATE);
        Boolean thereIsEvent = sharedPref.getBoolean(id + "Event", false);
        if(thereIsEvent)
            makeEvent(id, 3, c, activity);
    }

    public static String getId(String name)
    {
        String id = "abcdefghijklmnopqrstuv" + allTasks.getTopicNumById(name);
        return id;
    }

    public static String makeRandId() {
        String chars = "0123456789abcdefghijklmnopqrstuv";
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(1024);
        while (randomLength<5)
            randomLength = generator.nextInt(1024);

        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = chars.charAt(generator.nextInt(chars.length()));
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    public static void readDataFromUser(final Context c)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    allTasks = (marketingTasks) datas.child("allTopics").getValue();
                }
                saveData(c);
                Intent a = new Intent(c,mainNav.class);
                a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                c.startActivity(a);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //readData(c);
    }

    public static void setCurrentTopic(Context c, Activity activity)
    {
        NavigationView navigationView = ((Activity)c).findViewById(R.id.nav_view);
        Menu menuNav=navigationView.getMenu();
        int i = checkTopicPos();
        if(i == -1) i = 0;
        menuNav.performIdentifierAction(navigationView.getMenu().getItem(i).getItemId(), 0);

        appData.makeCalendarEvent(appData.ids[i], c, activity);
    }

    public static void makeEvent(String id, int opp, Context c, Activity activity)
    {
        topicTasks currentTopic = appData.allTasks.getTopicById(id);

        EventAttendee []eventAttendeeEmail = new EventAttendee[3];
        String[] email = {};
        int i = 0;
        for (String s : email) {
            EventAttendee eventAttendee = new EventAttendee();
            eventAttendee.setEmail(s);
            eventAttendeeEmail[i] = eventAttendee;
        }
        Calendar temp = Calendar.getInstance();
        Date startDate = currentTopic.getScheduledDate().getTime();
        temp.setTime(startDate);
        temp.set(Calendar.HOUR, 0);
        temp.set(Calendar.MINUTE, 0);
        startDate = temp.getTime();

        Date endDate;
        temp.setTime(startDate);
        temp.add(Calendar.DAY_OF_YEAR, 1);
        endDate = temp.getTime();

        Date currentTime = Calendar.getInstance().getTime();

        //if(eventId==null)
            //eventId = makeRandId();

        String eventId = getId(id);

        if(startDate.after(currentTime))
        createEventAsync(opp, eventId, id, currentTopic.getTitle(), "", "",
                new DateTime(startDate), new DateTime(endDate), eventAttendeeEmail, c, activity);
    }

    public static void readData(Context c)
    {
        ObjectInputStream is = null;
        try {
            FileInputStream fis = c.openFileInput("allData");
            is = new ObjectInputStream(fis);
            marketingTasks allData = (marketingTasks) is.readObject();
            is.close();
            fis.close();

            if(allData!=null) {
                allTasks = allData;
            }

        } catch (IOException e) {
            e.printStackTrace();
            //errorAlert(c,"לא הצלחנו לאחזר את הקבצים הישנים, ניצור חדשים במקום");
            initTasks(c);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            //errorAlert(c,"לא הצלחנו לאחזר את הקבצים הישנים, ניצור חדשים במקום");
            initTasks(c);
        }
    }

    public static void errorAlert(Context context, String text)
    {
        new AlertDialog.Builder(context)
                .setTitle(R.string.error_save_files_title)
                .setMessage(text)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void saveData(Context c)
    {
        FileOutputStream fos = null;
        try {
            fos = c.openFileOutput("allData", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(allTasks);
            os.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            errorAlert(c, c.getString(R.string.error_save_files));

        } catch (IOException e) {
            e.printStackTrace();
            errorAlert(c, c.getString(R.string.error_save_files));

        }
    }

    @SuppressLint("StaticFieldLeak")
    public static void createEventAsync(final int operation, final String eventId, final String taskId, final String summary, final String location,
                                        final String des, final DateTime startDate, final DateTime endDate, final EventAttendee[]
            eventAttendees, final Context c, final Activity activity) {

        new AsyncTask<Void, Void, String>() {
            private com.google.api.services.calendar.Calendar mService = null;
            private Exception mLastError = null;
            private boolean FLAG = false;

            @Override
            protected String doInBackground (Void...voids){
                try {
                    switch (operation) {
                        case 1:
                            insertEvent(summary, eventId, location, des, startDate, endDate, eventAttendees, activity);
                            break;
                        case 2:
                            deleteEvent(eventId, activity);
                            break;
                        case 3:
                            updateEvent(eventId, startDate, endDate, activity);
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onCancelled() {

                //mProgress.hide();
                if (mLastError != null) {
                    if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {

                    } else if (mLastError instanceof UserRecoverableAuthIOException) {
                        ((signIn)c).startActivityForResult(
                                ((UserRecoverableAuthIOException) mLastError).getIntent(),
                                mainNav.REQUEST_AUTHORIZATION);
                    } else {
                        //mOutputText.setText("The following error occurred:\n"
                        //      + mLastError.getMessage());
                    }
                } else {
                    //mOutputText.setText("Request cancelled.");
                }

            }

            @Override
            protected void onPostExecute (String s){
                super.onPostExecute(s);
                SharedPreferences sharedPref = c.getSharedPreferences("strings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(taskId + "Event", true);
                editor.apply();
                //getResultsFromApi();
            }
        }.execute();
    }

    static void deleteEvent(String id, Activity activity)
    {
        try {
            if(mService!=null)
                mService.events().delete("primary", id).execute();
        } catch (IOException e) {
            e.printStackTrace();
            if(activity!=null && e.getClass().equals(((UserRecoverableAuthIOException.class))))
                activity.startActivityForResult(
                        ((UserRecoverableAuthIOException) e).getIntent(),
                        mainNav.REQUEST_AUTHORIZATION);
        }
    }

    static void updateEvent(String id, DateTime startDate, DateTime endDate, Activity activity){
        // Retrieve the event from the API
        Event event = null;
        try {
            event = mService.events().get("primary", id).execute();

            // Make a change
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDate)
                    .setTimeZone(TimeZone.getDefault().getID());
            event.setStart(start);

            EventDateTime end = new EventDateTime()
                    .setDateTime(endDate)
                    .setTimeZone(TimeZone.getDefault().getID());
            event.setEnd(end);

            // Update the event
            Event updatedEvent = mService.events().update("primary", event.getId(), event).execute();
        } catch (IOException e) {
            e.printStackTrace();
            if(activity!=null)
                activity.startActivityForResult(
                        ((UserRecoverableAuthIOException) e).getIntent(),
                        mainNav.REQUEST_AUTHORIZATION);
        }
    }

    static void insertEvent(String summary, String EventId, String location, String des, DateTime startDate, DateTime endDate, EventAttendee[] eventAttendees, Activity activity) throws IOException {
        Event event = new Event()
                .setSummary(summary)
                .setLocation(location)
                .setDescription(des)
                .setId(EventId);

        EventDateTime start = new EventDateTime()
                .setDateTime(startDate)
                .setTimeZone(TimeZone.getDefault().getID());
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(endDate)
                .setTimeZone(TimeZone.getDefault().getID());
        event.setEnd(end);

        //String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=1"};
        //event.setRecurrence(Arrays.asList(recurrence));


        event.setAttendees(Arrays.asList(eventAttendees));

        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";
        try {
            //event.send
            if(mService!=null)
                mService.events().insert(calendarId, event).setSendNotifications(true).execute();
        } catch (IOException e) {
            e.printStackTrace();
            if(activity!=null && e.getClass().equals(((UserRecoverableAuthIOException.class))))
                activity.startActivityForResult(
                        ((UserRecoverableAuthIOException) e).getIntent(),
                        mainNav.REQUEST_AUTHORIZATION);
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(activity, connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(Activity activity,
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                activity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

}
