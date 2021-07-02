package com.reddigitalentertainment.sathijivanko;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SurePageDialog extends AppCompatDialogFragment {

    @Override
    public void onResume() {
        super.onResume();
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    dismiss();
                    return true;
                }
                else{
                    return false;
                }
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.ask_for_close_layout,null);
        builder.setView(view);

        Button yesButton = view.findViewById(R.id.yesButton_dialogBox);
        Button noButton = view.findViewById(R.id.noButton_dialogBox);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAccount(getActivity());
                Intent newIntent = new Intent(getActivity(),LoginActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(newIntent);
            }
        });



        return builder.create();

    }

    public static void removeAccount(final Activity activity){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        try {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),"tempPassword");
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Intent newIntent = new Intent(activity,SplashScreenActivity.class);
                                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    SathiUserHolder.setSathiUser(null);
                                    activity.startActivity(newIntent);
                                    activity.finish();
                                }
                                else{
                                    Toast.makeText(activity,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        auth.signOut();
                    }
                }
            });
        }
        catch (Exception e){
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
        }
    }
}
