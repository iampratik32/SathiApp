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
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginWithEmailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextInputEditText emailTextField;
    private PleaseWaitDialog pleaseWaitDialog;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_email);
        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar_loginWithEmail);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.secondaryColor));
        final ActivityOptions rightToLeft = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.right_to_left, R.anim.exit_right);


        TextView yourEmail = findViewById(R.id.loginWithEmail_loginEmail);
        yourEmail.setText(Html.fromHtml(String.format(getString(R.string.email_address))));

        ImageView closeButton = findViewById(R.id.closeButton_loginEmail);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        emailTextField = findViewById(R.id.loginFromEmail);
        emailTextField.requestFocus();
        if(emailTextField.hasFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        final Button loginButton = findViewById(R.id.loginWithEmail_LoginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String takenEmail = emailTextField.getText().toString().trim();
                if(takenEmail.isEmpty()){
                    showError("Enter Email Address");
                }
                else if(!EmailValidator.validateEmail(takenEmail)){
                    showError("Enter Valid Email Address");
                }
                else{
                    HideKeyboard.hide(LoginWithEmailActivity.this);
                    loginButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(takenEmail,"tempPassword").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(LoginWithEmailActivity.this,LoggedInActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent,rightToLeft.toBundle());
                                Toast.makeText(getApplicationContext(),"You have not been registered.",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                    Intent newIntent = new Intent(LoginWithEmailActivity.this,EnterPasswordActivity.class);
                                    newIntent.putExtra("Email",takenEmail);
                                    loginButton.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    startActivity(newIntent,rightToLeft.toBundle());
                                }
                                else {
                                    loginButton.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.left_to_right,R.anim.exit_left);
    }

    @Override
    public void onBackPressed() {
        if(progressBar.getVisibility()!=View.VISIBLE){
            super.onBackPressed();
            overridePendingTransition(R.anim.left_to_right,R.anim.exit_left);
        }
    }

    private void showError(String error){
        emailTextField.setError(error);
        emailTextField.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(emailTextField, InputMethodManager.SHOW_IMPLICIT);
    }
}
