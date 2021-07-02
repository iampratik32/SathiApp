package com.reddigitalentertainment.sathijivanko;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EnterPasswordActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);


        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar_enterPassword);
        String takenEmail = "";

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.secondaryColor));
        final ActivityOptions rightToLeft = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.right_to_left, R.anim.exit_right);


        final Bundle email = getIntent().getExtras();
        if(email!=null){
            takenEmail=email.getString("Email");
        }
        final String finalEmail = takenEmail;
        final TextInputEditText password= findViewById(R.id.loginUserPassword);
        password.requestFocus();
        if(password.hasFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        final Button loginButton = findViewById(R.id.loginButton_EnterPassword);
        ImageView backButton = findViewById(R.id.closeButton_enterPassword);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String takenPassword = password.getText().toString();
                if(takenPassword.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter Password.",Toast.LENGTH_SHORT).show();
                }
                else {
                    HideKeyboard.hide(EnterPasswordActivity.this);
                    loginButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(finalEmail,takenPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                final DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                                userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent,rightToLeft.toBundle());
                                            finish();
                                        }
                                        else {
                                            loginButton.setEnabled(true);
                                            progressBar.setVisibility(View.GONE);
                                            CheckUtility.delTempUser(getApplicationContext());
                                            finish();
                                            Toast.makeText(getApplicationContext(),"You have to register again.",Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else {
                                try {
                                    loginButton.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    throw task.getException();
                                }
                                catch (FirebaseAuthInvalidCredentialsException e){
                                    Toast.makeText(getApplicationContext(),"Invalid Password Entered",Toast.LENGTH_LONG).show();
                                }
                                catch (Exception e) {
                                    Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_LONG).show();
                                }

                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(progressBar.getVisibility()!=View.VISIBLE){
            super.onBackPressed();
            overridePendingTransition(R.anim.left_to_right,R.anim.exit_left);
        }
    }

}
