package com.reddigitalentertainment.sathijivanko;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DialogChangePassword extends AppCompatDialogFragment {

    private Context context;
    @Override
    public void onResume() {
        super.onResume();
    }

    public DialogChangePassword(Context context) {
        if(context==null){
            throw new NullPointerException();
        }
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_password,null);
        builder.setView(view);

        ImageView closeButton = view.findViewById(R.id.closeButton_changePassword);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final Button changeButton = view.findViewById(R.id.changePasswordButton);
        final TextInputEditText passwordEditText = view.findViewById(R.id.enterPassword_changePassword);
        final TextInputEditText verifyPasswordEditText = view.findViewById(R.id.enterVerifyPassword_changePassword);
        final ProgressBar progressBar = view.findViewById(R.id.progressBar_changePassword);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String passwordText = passwordEditText.getText().toString().trim();
                String verifyPasswordText = verifyPasswordEditText.getText().toString().trim();
                if(passwordText.isEmpty() || passwordText.length()<7){
                    passwordEditText.requestFocus();
                    Toast.makeText(getContext(),"Enter valid password.",Toast.LENGTH_LONG).show();
                }
                else if(verifyPasswordText.isEmpty()){
                    verifyPasswordEditText.requestFocus();
                    Toast.makeText(getContext(),"Enter something to verify password.",Toast.LENGTH_LONG).show();
                }
                else if(!passwordText.equals(verifyPasswordText)){
                    verifyPasswordEditText.requestFocus();
                    Toast.makeText(getContext(),"Your passwords do not match.",Toast.LENGTH_LONG).show();
                }
                else {
                    setCancelable(false);
                    progressBar.setVisibility(View.VISIBLE);
                    changeButton.setVisibility(View.GONE);
                    closeButton.setVisibility(View.GONE);
                    AuthCredential credential = null;
                    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    credential=SathiUserHolder.getSathiUser().getAuthCredential();
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                user.updatePassword(passwordText).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            firebaseAuth.signOut();
                                            firebaseAuth.signInWithEmailAndPassword(user.getEmail(),passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if(task.isSuccessful()){
                                                        SathiUserHolder.getSathiUser().setAuthCredential(null);
                                                        Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
                                                        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(settingsIntent);
                                                        dismiss();
                                                        Toast.makeText(getContext(),"Password Changed Successfully.",Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                        else {
                                            Toast.makeText(getContext(),"Could Not Change Password.",Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                            changeButton.setVisibility(View.VISIBLE);
                                            closeButton.setVisibility(View.VISIBLE);
                                            setCancelable(true);
                                        }
                                    }
                                });
                            }
                            else {
                                Toast.makeText(getContext(),"Authentication Failed.",Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                changeButton.setVisibility(View.VISIBLE);
                                closeButton.setVisibility(View.VISIBLE);
                                setCancelable(true);
                            }
                        }
                    });
                }
            }
        });

        return builder.create();
    }
}
