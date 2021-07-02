package com.reddigitalentertainment.sathijivanko;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

public class VerifyPasswordDialog extends AppCompatDialogFragment {
    private Context context;
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

    public VerifyPasswordDialog(Context context) {
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
        View view = inflater.inflate(R.layout.dialog_verify_password,null);
        builder.setView(view);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Button verifyButton = view.findViewById(R.id.verifyButton_VerifyDialog);

        ImageView backButton = view.findViewById(R.id.closeButton_verifyPassword);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final TextInputEditText passwordEditText = view.findViewById(R.id.enterPassword_verifyPasswordDialog);
        final ProgressBar progressBar = view.findViewById(R.id.progressBar_VerifyPassword);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                setCancelable(false);
                backButton.setEnabled(false);
                verifyButton.setEnabled(false);
                final String takenPassword = passwordEditText.getText().toString();
                if(takenPassword.isEmpty() || takenPassword==null){
                    passwordEditText.requestFocus();
                    Toast.makeText(getContext(),"Enter Password.",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                final AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),takenPassword);
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            SathiUserHolder.getSathiUser().setAuthCredential(credential);
                            dismiss();
                            Fragment newFragment = new AccountSettingsFragment();
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.frameLayout_SettingsActivity,newFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                        else{
                            passwordEditText.requestFocus();
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(getContext(),"Wrong Password Entered",Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(getContext(),"Can't continue at this moment.",Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);
                            verifyButton.setEnabled(true);
                            setCancelable(true);
                            backButton.setEnabled(true);
                        }

                    }
                });
            }
        });

        return builder.create();
    }

}
