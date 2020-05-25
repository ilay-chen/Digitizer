package com.icstudios.digitizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.database.DatabaseReference;

import java.util.concurrent.TimeUnit;

public class MyBroadCastReceiver extends BroadcastReceiver {

    private DatabaseReference mDatabase;
    marketingTasks allTasks;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            //Intent serviceIntent = new Intent(context, scheduledService.class);
            //context.startService(serviceIntent);

            PeriodicWorkRequest.Builder myWorkBuilder =
                    new PeriodicWorkRequest.Builder(serverWorker.class, 24, TimeUnit.HOURS);

            PeriodicWorkRequest myWork = myWorkBuilder.build();
            WorkManager.getInstance(context)
                    .enqueueUniquePeriodicWork("server", ExistingPeriodicWorkPolicy.KEEP, myWork);

            appData.checkProgress(context, -1, null);

/*
            OneTimeWorkRequest notificationWork =
                    new OneTimeWorkRequest.Builder(remainderWorker.class)
                            .setInitialDelay(15, TimeUnit.MINUTES)
                            .build();

            WorkManager.getInstance(context)
                    .enqueueUniqueWork("reminder", ExistingWorkPolicy.KEEP, notificationWork);
*/
            //PeriodicWorkRequest remainderWork = notificationWork.build();

            //WorkManager.getInstance()
            //        .enqueueUniquePeriodicWork("server", ExistingPeriodicWorkPolicy.KEEP, myWork);


        } else {
            //Toast.makeText(context.getApplicationContext(), "save data to database", Toast.LENGTH_LONG).show();
            //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            //readData(context);
            //mDatabase = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());
            //mDatabase.setValue(allTasks);
        }

    }
}