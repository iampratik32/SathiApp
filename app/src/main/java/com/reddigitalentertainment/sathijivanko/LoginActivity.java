package com.reddigitalentertainment.sathijivanko;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoginStatusCallback;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private FirebaseAuth auth;
    LoginButton loginFromFacebook;
    Button loginFromPhone;
    boolean bool = false;
    int q =0;
    DatabaseReference database;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final ActivityOptions rightToLeft = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.right_to_left, R.anim.exit_right);
        final ActivityOptions leftToRight = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.left_to_right, R.anim.exit_left);


        if(!CheckUtility.checkLocation(getApplicationContext())){
            Intent openLocation = new Intent(getApplicationContext(),OpenLocationActivity.class);
            startActivity(openLocation);
            finish();
        }
        else {
            
            auth = FirebaseAuth.getInstance();
            loginFromPhone = findViewById(R.id.loginPage_buttonPhoneNumberLogin);
            loginFromFacebook = findViewById(R.id.loginPage_buttonFacebookLogin);
            TextView cantLogin = findViewById(R.id.loginPage_textCantLogin);
            database = FirebaseDatabase.getInstance().getReference().child("Users");

            cantLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent recovery = new Intent(getApplicationContext(),RecoverAccountActivity.class);
                    startActivity(recovery,leftToRight.toBundle());
                }
            });

            Button registerButton = findViewById(R.id.register_loginPage);
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                    startActivity(intent,rightToLeft.toBundle());
                    CheckUtility.checkLocation(getApplicationContext());
                }
            });

            callbackManager = CallbackManager.Factory.create();
            loginFromFacebook.setPermissions(Arrays.asList("email","public_profile"));
            loginFromFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {

                    GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object,GraphResponse response) {

                            JSONObject json = response.getJSONObject();
                            try {
                                if(json != null){
                                    String text = json.getString("email");

                                }
                                else {
                                    Log.d("email","NULL");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", ",email");
                    request.setParameters(parameters);
                    request.executeAsync();

                    handleFacebookAccessToken(loginResult.getAccessToken());
                    loginFromFacebook.setEnabled(false);
                    loginFromPhone.setEnabled(false);
                    Toast.makeText(getApplicationContext(),"Please Wait..",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(getApplicationContext(),"Cancelled.",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                    Log.d("ERROR",error.toString());
                }
            });


            LoginManager.getInstance().retrieveLoginStatus(getApplicationContext(), new LoginStatusCallback() {
                @Override
                public void onCompleted(AccessToken accessToken) {
                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure() {

                }

                @Override
                public void onError(Exception exception) {

                }
            });


            loginFromPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent phonePage = new Intent(LoginActivity.this,LoginFromPhoneActivity.class);
                    startActivity(phonePage,rightToLeft.toBundle());
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        auth.signOut();
        LoginManager.getInstance().logOut();
    }



    private void updateUI(final FirebaseUser user){
        if(user!=null){
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(user.getUid()).exists()){
                        openMainActivity();
                    }
                    else {
                        Intent intent = new Intent(getApplicationContext(),LoggedInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        if(user.getPhoneNumber()!=null){
                            intent.putExtra("PhoneNumber",user.getPhoneNumber());
                        }
                        if(user.getEmail()!=null){
                            intent.putExtra("Email",user.getEmail());
                        }
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            updateUI(null);
                        }
                    }
        });
    }

    private void openMainActivity(){
        Intent newIntent = new Intent(getApplicationContext(),MainActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        newIntent.putExtra("Email",auth.getCurrentUser().getEmail());
        startActivity(newIntent);
        finish();
    }


}
