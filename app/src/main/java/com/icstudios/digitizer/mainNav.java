package com.icstudios.digitizer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import androidx.lifecycle.ProcessLifecycleOwner;
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
    //private List<ScheduledEvents> scheduledEventsList = new ArrayList<ScheduledEvents>();
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";

    DrawerLayout drawer;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main_nav);

        context = this;

        data = (appData) getApplication();

        //startMarketing();

        askPremission();

        //ProcessLifecycleOwner.get().getLifecycle().addObserver(new UserManager(this));

        //appData.setRemainder(1, this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Digitizer");
        setSupportActionBar(toolbar);

        data = (appData) getApplication();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user==null)
        {
            Intent a = new Intent(getApplicationContext(), signIn.class);
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
                UserManager.Companion.update(context);
                //signOut();
            }
        });

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

        /*
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.google_business, true)
                .build();

        Bundle b = new Bundle();
        b.putString("data", "google_business");

        navController.navigate(R.id.confirmationAction, b, navOptions);

         */

        //navController.navigate(R.id.confirmationAction);
        //int i = data.checkTopicPos();
        //String id = appData.ids[i];
        //createEventAsync(id , this);
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

    public taskFragment getVisibleFragment(){
        FragmentManager fragmentManager = mainNav.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return (taskFragment)fragment;
            }
        }
        return null;
    }

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
