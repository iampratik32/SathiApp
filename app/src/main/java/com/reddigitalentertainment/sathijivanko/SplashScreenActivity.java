package com.reddigitalentertainment.sathijivanko;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashScreenActivity extends AppCompatActivity {
    int c = 0;
    String load= "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        printHashKey(getApplicationContext());

        if(getIntent().getExtras()!=null){
            load = getIntent().getExtras().getString("Load");
        }

        ImageView sathiLogo = findViewById(R.id.sathiLogo_SplashScreen);
        ProgressBar progressBar = findViewById(R.id.progressBar_splash);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d("ASD",FirebaseAuth.getInstance().getUid());
            final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        c++;
                    }
                    else {
                        CheckUtility.delTempUser(getApplicationContext());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            TextView click = findViewById(R.id.clickOnSathiLogo);
            click.setVisibility(View.VISIBLE);

            KeepInsideHolder keepInsideHolder = new KeepInsideHolder();
            keepInsideHolder.insertInHolder();

            sathiLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (c == 0) {
                        Toast.makeText(getApplicationContext(),"Please Wait. Try again in a few seconds..",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        progressBar.setVisibility(View.VISIBLE);
                        click.setVisibility(View.GONE);
                        sathiLogo.setEnabled(false);

                        if(!CheckUtility.checkLocation(getApplicationContext())){
                            Intent openLocation = new Intent(SplashScreenActivity.this,OpenLocationActivity.class);
                            startActivity(openLocation);
                            progressBar.setVisibility(View.GONE);
                            click.setVisibility(View.VISIBLE);
                            sathiLogo.setEnabled(true);
                        }
                        else {
                            CheckUtility.getLocation(getApplicationContext());
                            final FirebaseAuth auth = FirebaseAuth.getInstance();
                            auth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseUser user = auth.getCurrentUser();
                                    if(user.isEmailVerified()){
                                        Intent mI = new Intent(getApplicationContext(),MainActivity.class);
                                        if(load!=null && load.equals("Chat")){
                                            mI.putExtra("Load","Chat");
                                        }
                                        startActivity(mI);
                                    }
                                    else {
                                        startActivity(new Intent(getApplicationContext(),NotVerifiedActivity.class));
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    finish();
                                }
                            });
                        }






                    }
                }
            });

        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    finish();
                }
            },1200);
        }

    }

    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.d("HASD",hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.d("HASD",e.getMessage());
        } catch (Exception e) {
            Log.d("HASD",e.getMessage());
        }
    }
}
