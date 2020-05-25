package com.icstudios.digitizer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DatabaseReference;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class remainderWorker extends Worker {
    private DatabaseReference mDatabase;
    marketingTasks allTasks;
    int ID = 11;
    public remainderWorker(
            @NonNull Context appContext,
            @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        Log.w("work", "start");
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.w("work", "doWork");
        //sendNotification();
        Log.w("work", "after work");
        appData.checkProgress(getApplicationContext(), -1, null);
        return Result.success();
    }

    public void setRemainder(int hours)
    {
        OneTimeWorkRequest notificationWork =
                new OneTimeWorkRequest.Builder(remainderWorker.class)
                        .setInitialDelay(hours, TimeUnit.HOURS)
                        .build();

        //PeriodicWorkRequest remainderWork = notificationWork.build();
        WorkManager.getInstance(getApplicationContext())
                .enqueueUniqueWork("reminder", ExistingWorkPolicy.REPLACE, notificationWork);
    }

    public void checkProgress()
    {
        marketingTasks mt = readData(getApplicationContext());
        int currentId = checkTopicPos(mt);
        topicTasks topic = mt.allTopics.get(currentId);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(topic.getTime());
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(System.currentTimeMillis());

        long seconds = (calendar1.getTimeInMillis() - calendar2.getTimeInMillis()) / 1000;
        int hours = (int) (seconds / 3600);
        int days = hours/24;

        if(hours > 24) {
            if (topic.undoneTasks() > 0) {
                sendNotification(days);
                setRemainder(hours/2);
            } else {
                //setRemainder(hours);
                //move to next task + set time to next date
            }
        }
        else if(hours > 0) {
            if (topic.undoneTasks() > 0) {
                sendNotification(days);
                setRemainder(1);
            } else {
                //setRemainder(hours);
                //move to next task + set time to next date
            }
        }
        else {
            if (topic.undoneTasks() > 0) {
                sendNotification(days);
                setRemainder(1);
                //appData.delayTasks();
                //alert task to do + delay tasks time
            } else {
                //setRemainder(0-hours);
                //move to next task
            }
        }
    }

    private NotificationManager mNotificationManager;
    private void sendNotification(int days) {
        marketingTasks mt = readData(getApplicationContext());
        String results = "אין מידע", head = "כותרת ריקה", title = "עוזר הנוכחות הדגיטלית";
        int currentId = 0;

        if(mt != null)
        {
            currentId = checkTopicPos(mt);
            topicTasks tt = mt.allTopics.get(currentId);
            results =  "רק עוד קצת! נשארו לך רק " + tt.undoneTasks() + " משימות מתוך " + tt.tasks.size() + ". " + days + " ימים לסיום הנושא הזה!";
            head = mt.allTopics.get(currentId).title;
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "remainder");
        Intent ii = new Intent(getApplicationContext(), singIn.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, ii, 0);

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
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

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
            mNotificationManager.notify(ID, mBuilder.build());
    }

    public marketingTasks readData(Context c)
    {
        ObjectInputStream is = null;
        try {
            FileInputStream fis = getApplicationContext().openFileInput("allData");
            is = new ObjectInputStream(fis);
            marketingTasks allData = (marketingTasks) is.readObject();
            is.close();
            fis.close();

            if(allData!=null) {
                allTasks = allData;
                return allData;
            }
            else return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int checkTopicPos(marketingTasks mt)
    {
        for(int i = 0; i < mt.allTopics.size(); i++)
        {
            if(mt.allTopics.get(i).undoneTasks()>0)
                return i;
        }
        return 0;
    }
}
