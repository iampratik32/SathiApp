package com.reddigitalentertainment.sathijivanko;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActivityOptions;
import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;

public class RecoverAccountActivity extends AppCompatActivity {
    ProgressBar progressBar;
    TextInputEditText recoverEmail;

    @Override
    public void onBackPressed() {
        if(progressBar.getVisibility()!=View.VISIBLE){
            super.onBackPressed();
            overridePendingTransition(R.anim.right_to_left,R.anim.exit_right);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_account);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.forUnRegistered));

        Button recoverButton = findViewById(R.id.button_recoverEmail);
        recoverEmail = findViewById(R.id.emailRecovery);
        ImageView backButton = findViewById(R.id.button_goBackRecoverEmail);
        progressBar = findViewById(R.id.progressBar_recoverAccount);

        recoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String takenEmail = recoverEmail.getText().toString().trim();
                if(takenEmail.isEmpty()){
                    showError("Enter your Email");
                }
                else if(!EmailValidator.validateEmail(takenEmail)){
                    showError("Enter valid Email Address");
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    recoverButton.setEnabled(false);
                    backButton.setEnabled(false);
                    FirebaseAuth.getInstance().sendPasswordResetEmail(takenEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Email Sent",Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);
                            recoverButton.setEnabled(true);
                            backButton.setEnabled(true);
                        }
                    });
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showError(String error){
        recoverEmail.requestFocus();
        recoverEmail.setError(error);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(recoverEmail, InputMethodManager.SHOW_IMPLICIT);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.right_to_left,R.anim.exit_right);
    }

}
