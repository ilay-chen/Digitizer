package com.icstudios.digitizer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class mainNav extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;
    public static appData data;

    private TextView mOutputText;
    private ImageButton mCallApiButton;
    private ImageButton scheduleMeeting;
    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;
    //private List<ScheduledEvents> scheduledEventsList = new ArrayList<ScheduledEvents>();
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";

    DrawerLayout drawer;
    ProgressDialog proDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main_nav);

        data = (appData) getApplication();

        //startMarketing();

        askPremission();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Digitizer");
        setSupportActionBar(toolbar);

        data = (appData) getApplication();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user==null || user.getDisplayName()==null)
        {
            Intent a = new Intent(getApplicationContext(),singIn.class);
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);

            finish();
        }

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setBackgroundResource(R.drawable.notebook6);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Toast.makeText(getApplicationContext(),"Hello",Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        final View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.textView);
        String name = user.getDisplayName();
        nav_user.setText(name);

        Button login = (Button) hView.findViewById(R.id.logout);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                update();
                //signOut();
            }
        });

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Syncing with calendar..");

        final Menu menuNav=navigationView.getMenu();

        //MenuItem nav_item2 = menuNav.findItem(R.id.sem);
        //nav_item2.setEnabled(false);

        for (int i=0;i<menuNav.size();i++) {
            MenuItem mi = menuNav.getItem(i);
            //mi.setTitle(appData.topics[i]);
            applyFontToMenuItem(mi);
            //the method we have create in activity
        }

        for(int i = 0; i < /*menuNav.size() data.checkTopicPos()*/appData.allTasks.getAllTopics().size() ; i++) {
            if((!appData.allTasks.getAllTopics().get(i).getToDo() || appData.allTasks.getAllTopics().get(i).undoneTasks() > 0) &&  i != data.checkTopicPos())
                menuNav.getItem(i).setEnabled(false);
        }

        /*
        // get menu from navigationView
        Menu menu = navigationView.getMenu();

        for(int i = 0; i < appData.topics.length; i++)
        {
            MenuItem newMenuItem = menu.add(appData.ids[i]);
            newMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Fragment fragment = new testFragment();

                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, fragment);
                    transaction.commit();

                    Bundle bundle = new Bundle();
                    bundle.putInt("", 2);
                    fragment.setArguments(bundle);

                    //Intent a = new Intent(getApplicationContext(),tasks.class);
                    //a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //a.putExtra("topic",/*data.checkTopicPos()item.getTitle().toString());
                    //startActivity(a);


                    return true;
                }
            });
            //newMenuItem.setTitle("NewTitleForCamera");
        }
*/
        // find MenuItem you want to change
        //MenuItem nav_camara = menu.findItem(R.id.nav_home);

        // set new title to the MenuItem
        //nav_camara.setTitle("NewTitleForCamera");

        // do the same for other MenuItems
        //MenuItem nav_gallery = menu.findItem(R.id.nav_gallery);
        //nav_gallery.setTitle("NewTitleForGallery");

        // add NavigationItemSelectedListener to check the navigation clicks
        //navigationView.setNavigationItemSelectedListener(this);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.strategy,
                R.id.usp, R.id.my_believe, R.id.kpi,
                R.id.website, R.id.app, R.id.sem, R.id.ads, R.id.google_business,
                R.id.content_marketing,R.id.posts,R.id.mail)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        mCredential = appData.mCredential;
        //getResultsFromApi();

        appData.setCurrentTopic(this, this);
        //int i = data.checkTopicPos();
        //String id = appData.ids[i];
        //createEventAsync(id , this);
    }

    public void update()
    {
        proDialog = ProgressDialog.show(this, "יוצא", "מעדכן התקדמות");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            appData.readData(this);
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());
            mDatabase.setValue(data.allTasks);

            mDatabase.setValue(data.allTasks, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        System.out.println("Data could not be saved. " + databaseError.getMessage());
                    } else {
                        signOut();
                        System.out.println("Data saved successfully.");
                    }
                }
            });
        }
    }

    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        proDialog.dismiss();
                        Intent a = new Intent(getApplicationContext(),singIn.class);
                        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(a);
                    }
                });
        // [END auth_fui_signout]
    }
/*
    public void refreshMenu()
    {
        NavigationView navigationView = ((mainNav) getActivity()).findViewById(R.id.nav_view);
        Menu menuNav=navigationView.getMenu();
        //MenuItem nav_item2 = menuNav.findItem(R.id.sem);
        //nav_item2.setEnabled(false);

        for(int i = appData.topics.length-1; i > menuNav.size() data.checkTopicPos(); i--) {
            menuNav.getItem(i).setEnabled(false);
        }
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_nav, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void askPremission()
    {
        ActivityCompat.requestPermissions(mainNav.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.GET_ACCOUNTS},
                1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(mainNav.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "GveretLevin.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        int end = mNewTitle.length();
        mNewTitle.setSpan(new RelativeSizeSpan(1.5f), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public testFragment getVisibleFragment(){
        FragmentManager fragmentManager = mainNav.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return (testFragment)fragment;
            }
        }
        return null;
    }
/*
    private com.google.api.services.calendar.Calendar mService = null;
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {

        private Exception mLastError = null;
        private boolean FLAG = false;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                getDataFromApi();
            } catch (Exception e) {
                e.printStackTrace();
                mLastError = e;
                cancel(true);
                return null;
            }
            return null;
        }


        private void getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<String>();
            Events events = mService.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();
            ScheduledEvents scheduledEvents;
            scheduledEventsList.clear();
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                scheduledEvents = new ScheduledEvents();
                scheduledEvents.setEventId(event.getId());
                scheduledEvents.setDescription(event.getDescription());
                scheduledEvents.setEventSummery(event.getSummary());
                scheduledEvents.setLocation(event.getLocation());
                scheduledEvents.setStartDate(start.toString());
                scheduledEvents.setEndDate("");
                StringBuffer stringBuffer = new StringBuffer();
                if(event.getAttendees()!=null) {
                    for (EventAttendee eventAttendee : event.getAttendees()) {
                        if(eventAttendee.getEmail()!=null)
                            stringBuffer.append(eventAttendee.getEmail() + "       ");
                    }
                    scheduledEvents.setAttendees(stringBuffer.toString());
                }
                else{
                    scheduledEvents.setAttendees("");
                }
                scheduledEventsList.add(scheduledEvents);
                System.out.println("-----"+event.getDescription()+", "+event.getId()+", "+event.getLocation());
                System.out.println(event.getAttendees());
                eventStrings.add(
                        String.format("%s (%s)", event.getSummary(), start));
            }
        }

        @Override
        protected void onPreExecute() {
            //mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            //System.out.println("--------------------"+scheduledEventsList.size());
            if (scheduledEventsList.size()<=0) {
                mOutputText.setText("No results returned.");
            } else {
                //eventListAdapter = new EventListAdapter(mainNav.this, scheduledEventsList);
                //eventListView.setAdapter(eventListAdapter);
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            mainNav.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void createEventAsync(final String summary, final String location, final String des, final DateTime startDate, final DateTime endDate, final EventAttendee[]
            eventAttendees) {

        new AsyncTask<Void, Void, String>() {
            private com.google.api.services.calendar.Calendar mService = null;
            private Exception mLastError = null;
            private boolean FLAG = false;


            @Override
            protected String doInBackground (Void...voids){
                try {
                    insertEvent(summary, location, des, startDate, endDate, eventAttendees);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute (String s){
                super.onPostExecute(s);
                //getResultsFromApi();
            }
        }.execute();
    }
    void insertEvent(String summary, String location, String des, DateTime startDate, DateTime endDate, EventAttendee[] eventAttendees) throws IOException {
        Event event = new Event()
                .setSummary(summary)
                .setLocation(location)
                .setDescription(des);

        EventDateTime start = new EventDateTime()
                .setDateTime(startDate)
                .setTimeZone("Asia/Jerusalem");
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(endDate)
                .setTimeZone("Asia/Jerusalem");
        event.setEnd(end);

        String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=1"};
        event.setRecurrence(Arrays.asList(recurrence));


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
        //event.send
        if(mService!=null)
            mService.events().insert(calendarId, event).setSendNotifications(true).execute();
    }

    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                mainNav.this,
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

    private void chooseAccount() {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                //getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
    }


    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    //getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        //getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    //getResultsFromApi();
                }
                break;
        }
    }
    */

@Override
public void onBackPressed() {
    new AlertDialog.Builder(this)
            .setTitle(getString(R.string.exit))
            .setMessage(getString(R.string.exit_exp))
            .setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) {
                    finishAffinity();
                }
            }).create().show();
}
}
