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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String verificationId;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        mAuth = FirebaseAuth.getInstance();

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.secondaryColor));

        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        Button verifyButton = findViewById(R.id.phoneVerify_buttonVerify);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode();
            }
        });
        sendVerification(phoneNumber);
    }

    private void verifyCode(){
        String code = findViewById(R.id.verifyFromPhone).toString().trim();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        signIn(credential);
    }

    private void signIn(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent loggedIN = new Intent(VerifyPhoneActivity.this,LoggedInActivity.class);
                    startActivity(loggedIN);
                }
                else{
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        Toast.makeText(getApplicationContext(),"Invalid Code Entered.",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void sendVerification(String number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(number,60,TimeUnit.SECONDS,TaskExecutors.MAIN_THREAD,mCallBacks);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code!=null){
                TextInputEditText verifyFromPhone = findViewById(R.id.verifyFromPhone);
                verifyFromPhone.setText(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(),"Verification Code Sent.2",Toast.LENGTH_LONG).show();
        }
    };
}
