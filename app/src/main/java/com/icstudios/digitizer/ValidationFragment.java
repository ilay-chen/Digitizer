package com.icstudios.digitizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.firebase.ui.auth.data.model.User;

import org.jetbrains.annotations.Nullable;

public class ValidationFragment extends DialogFragment implements UserManager.MyListener {

    private static FragmentActivity myContext;
    private static Boolean init = false;

    public ValidationFragment()
    {

    }

    public static ValidationFragment newInstance(FragmentActivity myContext, Boolean init) {
        // Required empty public constructor
        ValidationFragment frag = new ValidationFragment();
        ValidationFragment.myContext = myContext;
        Bundle args = new Bundle();
        args.putBoolean("init", init);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            init = args.getBoolean("init", false);
        }
        setShowsDialog(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        UserManager.Companion.setCustomEventListener(new UserManager.MyListener() {
            @Override
            public void callback(@Nullable Boolean success, long time) {
                if (success)
                {
                    FragmentManager ft  = myContext.getSupportFragmentManager();

                    DialogFragment newFragment = validationComplete.newInstance(time);
                    newFragment.setCancelable(false);
                    newFragment.show(ft, "completeValidation");
                }
                else
                {
                    Toast.makeText(signIn.context, "Not valid", Toast.LENGTH_LONG).show();
                    //UserManager.Companion.logout(signIn.context);
                    FragmentManager ft  = myContext.getSupportFragmentManager();

                    DialogFragment newFragment = ValidationFragment.newInstance(myContext, false);
                    newFragment.setCancelable(false);
                    newFragment.show(ft, "setValidation");
                }
            }
        });

        if(init)
        {
            UserManager.Companion.setValidation(null,  init);
        }

        // Set an EditText view to get user input
        final EditText input = new EditText(getContext());
        input.setHint(R.string.edit_text_validation);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(input)
                .setMessage(R.string.validation_message)
                .setNegativeButton(R.string.close_validation, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        getActivity().finishAffinity();
                    }
                })

                .setPositiveButton(R.string.enter_validation, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
//                        Thread thread = new Thread() {
//                            @Override
//                            public void run() {
//                                try {
//                                    while(true) {
//                                        sleep(1000);
//                                        handler.post(this);
//                                    }
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        };

//                        thread.start();

                        UserManager.Companion.setValidation(input.getText().toString(),false);
                    }
                })
                .setNeutralButton(R.string.buy_validation, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        getActivity().finishAffinity();
                    }
                }).create();


    }

    @Override
    public void callback(@Nullable Boolean success, long time) {

    }
}
