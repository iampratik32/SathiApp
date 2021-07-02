package com.reddigitalentertainment.sathijivanko;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotVerifiedActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    Button sendEmail,check;
    ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_verified);

        sendEmail = findViewById(R.id.sendEmail_notVerified);
        progressBar = findViewById(R.id.progressBar_notVerified);
        firebaseAuth = FirebaseAuth.getInstance();
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.secondaryColor));

        check = findViewById(R.id.completeButton_notVerified);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                final FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseUser user = auth.getCurrentUser();
                        if(user.isEmailVerified()){
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("ShowMe");
                            userDb.setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    finish();
                                }
                            });
                        }
                        else {
                            hideProgress();
                            Toast.makeText(getApplicationContext(),"Verification Not Completed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),"Email Verification Sent.",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    hideProgress();
                                    Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }
        });


    }

    private void showProgress(){
        sendEmail.setEnabled(false);
        check.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress(){
        progressBar.setVisibility(View.GONE);
        sendEmail.setEnabled(true);
        check.setEnabled(true);
    }
}
