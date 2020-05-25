package com.icstudios.digitizer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
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

import java.util.concurrent.TimeUnit;

public class notiWorker extends Worker {
    private DatabaseReference mDatabase;
    marketingTasks allTasks;
    Context c;
    int ID = 13;
    public notiWorker(
            @NonNull Context appContext,
            @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        c = appContext;
        Log.w("work", "start");
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.w("work", "doWork");
        if(c!=null) {
            sendNotification();
            //setRemainder(5);
            Log.w("work", "after work");
            return Result.success();
        }
        else return Result.failure();
    }

    public void setRemainder(int hours)
    {
        OneTimeWorkRequest notificationWork =
                new OneTimeWorkRequest.Builder(notiWorker.class)
                        .setInitialDelay(15, TimeUnit.MINUTES)
                        .build();

        //PeriodicWorkRequest remainderWork = notificationWork.build();
        WorkManager.getInstance(c)
                .enqueueUniqueWork("reminder3", ExistingWorkPolicy.REPLACE, notificationWork);
    }


    private NotificationManager mNotificationManager;
    private void sendNotification() {
        String results = "אין מידע", head = "כותרת ריקה";
        int currentId = 0;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(c, "remainder");

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(results);
        bigText.setBigContentTitle(head);
        bigText.setSummaryText(results);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle(head);
        mBuilder.setContentText(results);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

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

}
