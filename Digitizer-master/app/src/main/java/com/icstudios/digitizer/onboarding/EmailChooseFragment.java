package com.icstudios.digitizer.onboarding;


import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.icstudios.digitizer.R;
import com.icstudios.digitizer.appData;
import com.icstudios.digitizer.mainNav;
import com.icstudios.digitizer.marketingTasks;
import com.icstudios.digitizer.topicTasks;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.icstudios.digitizer.appData.mCredential;
import static com.icstudios.digitizer.mainNav.REQUEST_ACCOUNT_PICKER;
import static com.icstudios.digitizer.mainNav.REQUEST_AUTHORIZATION;
import static com.icstudios.digitizer.mainNav.REQUEST_GOOGLE_PLAY_SERVICES;
import static com.icstudios.digitizer.singIn.RC_SIGN_IN;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailChooseFragment extends Fragment {

    private Button mButtonNext, mButtonPrevious, mButtonChoose;
    private LinearLayout layout;
    TextView email;

    public static com.google.api.services.calendar.Calendar mService = null;
    private static final String PREF_ACCOUNT_NAME = "accountName";

    public EmailChooseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20,20,20,20);

        TextView title = new TextView(getActivity());
        title.setText(R.string.mail_choose_title);
        title.setPadding(0,10,0,30);
        layout.addView(title);

        TextView explanation = new TextView(getContext());
        explanation.setText(R.string.mail_choose_expl);
        explanation.setPadding(0,10,0,30);
        layout.addView(explanation);

        String accountName = (getActivity()).getPreferences(MODE_PRIVATE)
                .getString(PREF_ACCOUNT_NAME, getString(R.string.no_mail));

        email = new TextView(getContext());
        email.setText(accountName);
        email.setPadding(0,10,0,30);
        layout.addView(email);

        LinearLayout nav = new LinearLayout(getContext());
        //nav.setOrientation(LinearLayout.HORIZONTAL);

        mButtonChoose = new Button(getContext());
        mButtonChoose.setText(R.string.choose_mail);
        mButtonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getResultsFromApi();
            }
        });
        layout.addView(mButtonChoose);

        mButtonNext = new Button(getContext());
        mButtonNext.setText("next");
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean allSet = true;
                if (allSet) {
                    //(activity as ViewPager2).setCurrentItem(0,true)
                    ((OnboardingActivity)getActivity()).onProgress(3);
                    OnboardingActivity.Companion.getViewPager().setCurrentItem(2, true);
                }
                else
                {
                    Toast.makeText(getContext(),"you have to choose all the settings!",Toast.LENGTH_LONG).show();
                }
            }
        });

        mButtonPrevious = new Button(getContext());
        mButtonPrevious.setText("Previous");
        mButtonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnboardingActivity.Companion.getViewPager().setCurrentItem(0, true);
            }
        });
        nav.addView(mButtonPrevious);
        nav.addView(mButtonNext);
        layout.addView(nav);

        //rootView.add
        /*
        mButtonNext = rootView.findViewById(R.id.button_next)
        mButtonNext.setOnClickListener(View.OnClickListener { view ->
            (activity as ViewPagerNavigation).onProgress(3)
        })
        R.id.radio_button_smile_interval_long


        mRadioGroupSmileInterval.setOnCheckedChangeListener { group, checkedId ->
            var smileIntervalInMinutes = 0
            when (checkedId)
            {
                R.id.radio_button_smile_interval_short ->
                {
                    smileIntervalInMinutes = resources.getInteger(R.integer.smile_interval_short)
                }
                R.id.radio_button_smile_interval_medium ->
                {
                    smileIntervalInMinutes = resources.getInteger(R.integer.smile_interval_medium)
                }
                R.id.radio_button_smile_interval_long ->
                {
                    smileIntervalInMinutes = resources.getInteger(R.integer.smile_interval_long)
                }
            }
            if (smileIntervalInMinutes == 0)
            {
                throw Exception("Smile interval radio selection falied! with value: " + smileIntervalInMinutes)
            }
            with (PreferenceManager.getDefaultSharedPreferences(context).edit())
            {
                putInt(getString(R.string.settings_key_interval_time_in_minutes),smileIntervalInMinutes)
            }
            (activity as ViewPagerNavigation).onProgress(2)
        }
*/
        return layout;
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(getContext());
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(getContext());
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                getActivity(),
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) ((getActivity()).getBaseContext()).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void chooseAccount() {
        /*
        String accountName = (getActivity()).getPreferences(Context.MODE_PRIVATE)
                .getString(PREF_ACCOUNT_NAME, null);
        if (accountName != null) {
            mCredential.setSelectedAccountName(accountName);
            getResultsFromApi();
        } else {

         */
            // Start a dialog from which the user can choose an account
            startActivityForResult(
                    mCredential.newChooseAccountIntent(),
                    REQUEST_ACCOUNT_PICKER);
        //}
    }

    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (! isDeviceOnline()) {
            //mOutputText.setText("No network connection available.");
        } else {
            chooseAccount();
        }
    }

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

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                Date startDate = new Date();
                EventAttendee []eventAttendeeEmail = {};
                insertEvent("עוזר הנוכחות הדיגיטלית",  appData.makeRandId(), "","", new DateTime(startDate),new DateTime(startDate),eventAttendeeEmail );
            } catch (Exception e) {
                e.printStackTrace();
                mLastError = e;
                cancel(true);
                return null;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            //mOutputText.setText("");
            //mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            //autoSingIn();
            email.setText("מייל נבחר: " + mCredential.getSelectedAccountName());
        }

        @Override
        protected void onCancelled() {
            /*
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
            */
        }
    }

    void insertEvent(String summary, String EventId, String location, String des, DateTime startDate, DateTime endDate, EventAttendee[] eventAttendees) throws IOException {
        Event event = new Event()
                .setSummary(summary)
                .setLocation(location)
                .setDescription(des)
                .setId(EventId);

        EventDateTime start = new EventDateTime()
                .setDateTime(startDate)
                .setTimeZone("Asia/Jerusalem");
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(endDate)
                .setTimeZone("Asia/Jerusalem");
        event.setEnd(end);

        //String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=1"};
        //event.setRecurrence(Arrays.asList(recurrence));


        event.setAttendees(Arrays.asList(eventAttendees));

        String calendarId = "primary";
        try {
            //event.send
            if(mService!=null)
                mService.events().insert(calendarId, event).setSendNotifications(true).execute();
        } catch (IOException e) {
            e.printStackTrace();
            startActivityForResult(
                    ((UserRecoverableAuthIOException) e).getIntent(),
                    mainNav.REQUEST_AUTHORIZATION);
        }
    }

    public void autoSingIn(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null && user.getDisplayName()!=null)
        {
            Intent a = new Intent(getContext(), mainNav.class);
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);
        }
    }

    // [START auth_fui_result]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                readDataFromUser(getContext());
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
        switch(requestCode) {
            case RC_SIGN_IN:
                IdpResponse response = IdpResponse.fromResultIntent(data);
                break;
            case RESULT_OK:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Intent a = new Intent(getContext(),mainNav.class);
                a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(a);
                break;
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {

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
                        SharedPreferences settings = getActivity().getApplicationContext().getSharedPreferences("strings", MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        //getResultsFromApi();
                        new MakeRequestTask(mCredential).execute();
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
    // [END auth_fui_result]

    public void readDataFromUser(final Context c)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                appData.allTasks = new marketingTasks();
                //ArrayList<topicTasks> allt = dataSnapshot.child("allTopics").getValue(ArrayList<topicTasks>);
                //int j = 0;
                for (DataSnapshot task : dataSnapshot.child("allTopics").getChildren())
                {
                    appData.allTasks.addTopic(task.getValue(topicTasks.class));

                }
                //appData.allTasks = dataSnapshot.child("allTopics").getValue(marketingTasks.class);
                //appData.allTasks = new marketingTasks(allt);
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    //appData.allTasks = (marketingTasks) datas.child("allTopics").getValue();
                }

                appData.saveData(c);
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

}