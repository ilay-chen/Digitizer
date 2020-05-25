package com.icstudios.digitizer;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class serverWorker extends Worker {
    private DatabaseReference mDatabase;
    marketingTasks allTasks;
    private NotificationManager mNotificationManager;

    public serverWorker(
            @NonNull Context appContext,
            @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //Toast.makeText(getApplicationContext(), "save data to database", Toast.LENGTH_LONG).show();
        return readFromServer();
    }

    public Result update()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            readData(getApplicationContext());
            mDatabase = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());
            mDatabase.setValue(allTasks);
            return Result.success();
        }
        return Result.failure();
    }

    public void readData(Context c)
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
            //initTasks();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            //errorAlert(c,"לא הצלחנו לאחזר את הקבצים הישנים, ניצור חדשים במקום");
            //initTasks();
        }
    }

    Result result;
    @SuppressLint("StaticFieldLeak")
    public Result readFromServer() {
        new AsyncTask<Void, Void, Result>() {
            private com.google.api.services.calendar.Calendar mService = null;
            private Exception mLastError = null;
            private boolean FLAG = false;

            @Override
            protected Result doInBackground (Void...voids){
                return update();
            }

            @Override
            protected void onPostExecute (Result s){
                super.onPostExecute(s);
                result = s;
                sendNotification(s.toString());
                //getResultsFromApi();
            }
        }.execute();
        return result;
    }

    private void sendNotification(String r) {
        String results = "עדכון שרת בוצע", head = "עדכון שרת!";
        int currentId = 0;
        if(r!=null)
            results = r;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "server");
        Intent ii = new Intent(getApplicationContext(), singIn.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(results);
        bigText.setBigContentTitle(head);
        bigText.setSummaryText(results);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle(head);
        mBuilder.setContentText(results);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "server";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "server of the tasks",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        if(mNotificationManager!=null)
            mNotificationManager.notify(5, mBuilder.build());
    }
}