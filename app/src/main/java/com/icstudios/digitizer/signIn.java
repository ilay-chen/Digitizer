package com.icstudios.digitizer;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.icstudios.digitizer.onboarding.OnboardingActivity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.icstudios.digitizer.appData.mCredential;
import static com.icstudios.digitizer.mainNav.REQUEST_ACCOUNT_PICKER;
import static com.icstudios.digitizer.mainNav.REQUEST_AUTHORIZATION;
import static com.icstudios.digitizer.mainNav.REQUEST_GOOGLE_PLAY_SERVICES;

public class signIn extends AppCompatActivity {

    public static final int RC_SIGN_IN = 123;
    Button next, login;
    public static com.google.api.services.calendar.Calendar mService = null;
    public static final String PREF_ACCOUNT_NAME = "accountName";
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_firebase_ui);

        context = this;

        SharedPreferences sharedPref = this.getApplicationContext().getSharedPreferences("strings", MODE_PRIVATE);
        boolean firstTime = sharedPref.getBoolean("firstTime", true);

        if(firstTime) {

            Intent a = new Intent(getApplicationContext(), OnboardingActivity.class);
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);

            finishAffinity();

            /*
            findViewById(R.id.layoutIn).post(new Runnable() {
                public void run() {
                    popupWindow.showAtLocation(findViewById(R.id.layoutIn), Gravity.CENTER, 0, 0);
                }
            });
            //popupWindow.showAtLocation(findViewById(R.id.layoutIn), Gravity.CENTER, 0, 0);

            // dismiss the popup window when touched
            popupView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //popupWindow.dismiss();
                    return true;
                }
            });

             */
        }
        else {
            ProcessLifecycleOwner.get().getLifecycle().addObserver(new UserManager(this));

            askPremission();

//            getResultsFromApi();

            //startMarketing();

            next = (Button) findViewById(R.id.singIn);

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createSignInIntent();
                }
            });
        }
/*
        login = (Button) findViewById(R.id.singOut);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        */

    }

//    private GoogleSignInClient buildGoogleSignInClient() {
//        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestScopes(new Scope(CalendarScopes.CALENDAR_READONLY))
//                .requestIdToken("AIzaSyAr9m8GRgbYMkQwsAdK--yDxvczW4PaM3g")
//                .requestServerAuthCode("AIzaSyAr9m8GRgbYMkQwsAdK--yDxvczW4PaM3g")
//                .requestEmail()
//                .build();
//        return GoogleSignIn.getClient(this, signInOptions);
//    }

//    public void autoSingIn(){
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if(user!=null && user.getDisplayName()!=null)
//        {
//            Intent a = new Intent(getApplicationContext(),mainNav.class);
//            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(a);
//
//            finishAffinity();
//        }
//    }


    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                /*new AuthUI.IdpConfig.EmailBuilder().build(),*/
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                readDataFromUser(this);
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
         */

        switch(requestCode) {
            case RC_SIGN_IN:
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if(resultCode == RESULT_OK) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user!=null) {
                        appData.userRootPath = "users/" + user.getUid();
                    }

//                    Intent a = new Intent(getApplicationContext(),mainNav.class);
//                    a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(a);
                }
                break;
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {

                } else {
                    //getResultsFromApi();
                }
                break;
//            case REQUEST_ACCOUNT_PICKER:
//                if (resultCode == RESULT_OK && data != null &&
//                        data.getExtras() != null) {
//                    String accountName =
//                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
//                    if (accountName != null) {
//                        SharedPreferences settings = this.getApplicationContext().getSharedPreferences("strings", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = settings.edit();
//                        editor.putString(PREF_ACCOUNT_NAME, accountName);
//                        editor.apply();
//                        mCredential.setSelectedAccountName(accountName);
//                        getResultsFromApi();
//                    }
//                }
//                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    //getResultsFromApi();
                }
                break;
        }
    }
    // [END auth_fui_result]

    public void askPremission()
    {
        ActivityCompat.requestPermissions(signIn.this,
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
//                    getResultsFromApi();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(signIn.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

//    public void signOut() {
//        // [START auth_fui_signout]
//        AuthUI.getInstance()
//                .signOut(this)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // ...
//                    }
//                });
//        // [END auth_fui_signout]
//    }
//
//    public void delete() {
//        // [START auth_fui_delete]
//        AuthUI.getInstance()
//                .delete(this)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // ...
//                    }
//                });
//        // [END auth_fui_delete]
//    }
//
//    public void themeAndLogo() {
//        List<AuthUI.IdpConfig> providers = Collections.emptyList();
//
//        // [START auth_fui_theme_logo]
//        startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setAvailableProviders(providers)
//                        //.setLogo(R.drawable.my_great_logo)      // Set logo drawable
//                        //.setTheme(R.style.MySuperAppTheme)      // Set theme
//                        .build(),
//                RC_SIGN_IN);
//        // [END auth_fui_theme_logo]
//    }
//
//    public void privacyAndTerms() {
//        List<AuthUI.IdpConfig> providers = Collections.emptyList();
//        // [START auth_fui_pp_tos]
//        startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setAvailableProviders(providers)
//                        .setTosAndPrivacyPolicyUrls(
//                                "https://example.com/terms.html",
//                                "https://example.com/privacy.html")
//                        .build(),
//                RC_SIGN_IN);
//        // [END auth_fui_pp_tos]
//    }

//    private boolean isGooglePlayServicesAvailable() {
//        GoogleApiAvailability apiAvailability =
//                GoogleApiAvailability.getInstance();
//        final int connectionStatusCode =
//                apiAvailability.isGooglePlayServicesAvailable(this);
//        return connectionStatusCode == ConnectionResult.SUCCESS;
//    }
//
//    private void acquireGooglePlayServices() {
//        GoogleApiAvailability apiAvailability =
//                GoogleApiAvailability.getInstance();
//        final int connectionStatusCode =
//                apiAvailability.isGooglePlayServicesAvailable(this);
//        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
//            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
//        }
//    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                signIn.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

//    private boolean isDeviceOnline() {
//        ConnectivityManager connMgr =
//                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//        return (networkInfo != null && networkInfo.isConnected());
//    }
//
//    private void chooseAccount() {
//        SharedPreferences sharedPref = this.getApplicationContext().getSharedPreferences("strings", MODE_PRIVATE);
//        String accountName = sharedPref.getString(PREF_ACCOUNT_NAME, null);
//        //String accountName = getPreferences(Context.MODE_PRIVATE)
//               // .getString(PREF_ACCOUNT_NAME, null);
//        if (accountName != null) {
//            mCredential.setSelectedAccountName(accountName);
//            getResultsFromApi();
//        } else {
//            // Start a dialog from which the user can choose an account
//            startActivityForResult(
//                    mCredential.newChooseAccountIntent(),
//                    REQUEST_ACCOUNT_PICKER);
//        }
//    }

//    private void getResultsFromApi() {
//        if (! isGooglePlayServicesAvailable()) {
//            acquireGooglePlayServices();
//        } else if (mCredential.getSelectedAccountName() == null) {
//            chooseAccount();
//        } else if (! isDeviceOnline()) {
//            //mOutputText.setText("No network connection available.");
//        } else {
//            new signIn.MakeRequestTask(mCredential, this).execute();
//        }
//    }

    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {

        private Exception mLastError = null;
        private boolean FLAG = false;
        private Activity activity;

        public MakeRequestTask(GoogleAccountCredential credential, Activity activity) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(context.getString(R.string.app_name))
                    .build();
            this.activity = activity;
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
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
            //appData.checkProgress(context, -1, activity);
        }

        @Override
        protected void onCancelled() {

            //mProgress.hide();
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
                    //mOutputText.setText("The following error occurred:\n"
                      //      + mLastError.getMessage());
                }
            } else {
                //mOutputText.setText("Request cancelled.");
            }

        }
    }
    static public Context getContext()
    {
        return context;
    }
}