package com.reddigitalentertainment.sathijivanko;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextInputEditText email;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button registerButton = findViewById(R.id.registerEmailButton);
        email = findViewById(R.id.registerEmail);
        email.requestFocus();
        if(email.hasFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        progressBar = findViewById(R.id.progressBar_register);
        ImageView backButton = findViewById(R.id.closeButton_registerEmail);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.secondaryColor));
        final ActivityOptions rightToLeft = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.right_to_left, R.anim.exit_right);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String takenEmail = email.getText().toString().trim();
                if(takenEmail.isEmpty()){
                    showError("Enter Your Email");
                }
                else if(!EmailValidator.validateEmail(takenEmail)){
                    showError("Enter Valid Email Address");
                }
                else {
                    registerButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    HideKeyboard.hide(RegisterActivity.this);
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.createUserWithEmailAndPassword(takenEmail,"tempPassword").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(RegisterActivity.this,LoggedInActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent,rightToLeft.toBundle());
                            }
                            else{
                                if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                    Toast.makeText(getApplicationContext(),"You already have an account.",Toast.LENGTH_SHORT).show();
                                    Intent newIntent = new Intent(RegisterActivity.this,EnterPasswordActivity.class);
                                    newIntent.putExtra("Email",takenEmail);
                                    registerButton.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    startActivity(newIntent,rightToLeft.toBundle());
                                }
                                else {
                                    registerButton.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                }
            }
        });

    }

    private void showError(String error) {
        email.setError(error);
        email.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(email, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onBackPressed() {
        if(progressBar.getVisibility()!=View.VISIBLE){
            super.onBackPressed();
            overridePendingTransition(R.anim.left_to_right,R.anim.exit_left);
        }
    }
}
