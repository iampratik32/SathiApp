package com.reddigitalentertainment.sathijivanko;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;


public class LoginFromPhoneActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_from_phone);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.forUnRegistered));
        final ActivityOptions rightToLeft = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.right_to_left, R.anim.exit_right);

        TextView yourPhone = findViewById(R.id.loginPhonePage_textPhoneNumber);
        yourPhone.setText(Html.fromHtml(String.format(getString(R.string.phone_number))));

        final TextInputEditText phoneNumber = findViewById(R.id.loginFromPhone);
        phoneNumber.requestFocus();
        if(phoneNumber.hasFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        Button continueButton  = findViewById(R.id.phoneLogin_buttonContinue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String takenPhoneNumber = phoneNumber.getText().toString().trim();
                if(takenPhoneNumber.isEmpty() || takenPhoneNumber.length()<10){
                    phoneNumber.setError("Enter Valid Phone Number");
                }
                else{
                    String finalPhoneNumber = "+977"+takenPhoneNumber;
                    Intent verifyPhone = new Intent(LoginFromPhoneActivity.this,VerifyPhoneActivity.class);
                    verifyPhone.putExtra("phoneNumber",finalPhoneNumber);
                    startActivity(verifyPhone,rightToLeft.toBundle());
                }

            }
        });

        ImageView backButton = findViewById(R.id.backButton_loginFromPhone);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button emailButton = findViewById(R.id.phoneLogin_buttonEmailLogin);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(LoginFromPhoneActivity.this,LoginWithEmailActivity.class);
                startActivity(newIntent,rightToLeft.toBundle());
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
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right,R.anim.exit_left);
    }
}
