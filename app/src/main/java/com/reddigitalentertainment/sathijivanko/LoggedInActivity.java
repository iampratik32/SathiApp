package com.reddigitalentertainment.sathijivanko;

import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class LoggedInActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.firstTimeUsers_frameLayout);
        if (count == 0) {
            if(f instanceof NewUserFirstFragment){
                SurePageDialog dialog = new SurePageDialog();
                dialog.show(getSupportFragmentManager(),"Dialog");
            }
            else {
                super.onBackPressed();
            }
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.forUnRegistered));

        CheckUtility.getLocation(getApplicationContext());

        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.firstTimeUsers_frameLayout,new NewUserFirstFragment());
        fragmentTransaction.commit();

        FloatingActionButton close = findViewById(R.id.fab);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SurePageDialog dialog = new SurePageDialog();
                dialog.show(getSupportFragmentManager(),"Dialog");
            }
        });



    }

}
