package com.icstudios.digitizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class validationComplete extends DialogFragment {

    long expire = 0;
    public validationComplete()
    {

    }

    public static validationComplete newInstance(long expire) {
        // Required empty public constructor
        validationComplete frag = new validationComplete();
        Bundle args = new Bundle();
        args.putLong("expire", expire);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            setShowsDialog(true);
            expire = args.getLong("expire", 0);
        }
        else setShowsDialog(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Date date=new Date(expire);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
        String dateText = df2.format(date);

        String title = getContext().getString(R.string.validation_complete_title) + dateText + ".";
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setMessage(title)
                .setPositiveButton(R.string.validation_complete_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        UserManager.Companion.userValidation();
                    }
                })
                .create();
    }
}
