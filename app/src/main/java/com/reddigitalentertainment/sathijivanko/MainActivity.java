package com.reddigitalentertainment.sathijivanko;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    String chosenButton = "";
    private Map<String,Fragment> fragments;
    private boolean backPressedOnce = false;
    @Override
    public void onBackPressed() {
        if(bottomNavigationView.getSelectedItemId()==R.id.sathiButton){
            if (backPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.backPressedOnce = true;
            Toast.makeText(this, "Press back again to quit.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    backPressedOnce=false;
                }
            }, 2000);
        }
        else {
            bottomNavigationView.setSelectedItemId(R.id.sathiButton);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle getBundle = getIntent().getExtras();

        if(SathiUserHolder.getSathiUser()==null){
            KeepInsideHolder keepInsideHolder = new KeepInsideHolder();
            keepInsideHolder.insertInHolder();
        }

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorForMainGradientBack));

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful()){
                    String token = task.getResult().getToken();
                    AddToken addToken = new AddToken();
                    addToken.execute(token);
                }
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("ChatNotification");

        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationListener);
        if(getBundle!=null){
            String open = getBundle.getString("Open");
            if(open!=null && open.equals("Chat")){
                bottomNavigationView.setSelectedItemId(R.id.chatButton);
            }
            String load = getBundle.getString("Load");
            if(load!=null && load.equals("Chat")){
                SathiUserHolder.setLoadChat(true);
            }

        }
        else {
            bottomNavigationView.setSelectedItemId(R.id.sathiButton);
        }


        final ImageView sathiLogo = findViewById(R.id.mainSathiLogo_mainActivity);
        final TextView noOne = findViewById(R.id.noOne_mainActivity);
        final Animation shake;
        shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_image);
        sathiLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sathiLogo.startAnimation(shake);
                noOne.startAnimation(shake);
            }
        });

    }
    public BottomNavigationView.OnNavigationItemSelectedListener navigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            int bottomId = bottomNavigationView.getSelectedItemId();
            Menu menu = bottomNavigationView.getMenu();
            Fragment selectedFragment = null;
            Fragment previousFragment = getSupportFragmentManager().findFragmentById(R.id.mainActivityFrameLayout);
            switch (menuItem.getItemId()){
                case R.id.profileButton:
                    if(bottomId!=R.id.profileButton){
                        selectedFragment = new ProfileFragment();
                        chosenButton="Fixed";
                        break;
                    }
                    else {
                        selectedFragment=previousFragment;
                        break;
                    }

                case R.id.sathiButton:
                    if(bottomNavigationView.getSelectedItemId()!=R.id.sathiButton){
                        chosenButton="Broken";
                        selectedFragment = new MainFragment();
                        break;
                    }
                    else {
                        if(chosenButton.equals("Broken")){
                            chosenButton="Fixed";
                            selectedFragment=new MainConnectionsFragment();
                        }
                        else {
                            chosenButton="Broken";
                            selectedFragment=new MainFragment();
                        }
                        previousFragment=selectedFragment;
                        break;
                    }

                case R.id.chatButton:
                    if(bottomNavigationView.getSelectedItemId()!=R.id.chatButton){
                        selectedFragment = new ChatFragment();
                        chosenButton="Fixed";
                        break;
                    }
                    else {
                        selectedFragment=previousFragment;
                        break;
                    }
            }

            menu.getItem(1).setIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.sathi_logo_without_background));
            if(selectedFragment instanceof ChatFragment){
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_to_left,R.anim.exit_right,R.anim.left_to_right,R.anim.exit_left).replace(R.id.mainActivityFrameLayout,selectedFragment).commit();
            }
            else if(selectedFragment instanceof ProfileFragment || selectedFragment instanceof MainConnectionsFragment){
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.left_to_right,R.anim.exit_left,R.anim.right_to_left,R.anim.exit_right).replace(R.id.mainActivityFrameLayout,selectedFragment).commit();
            }
            else {
                if(previousFragment instanceof ChatFragment){
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.left_to_right,R.anim.exit_left,R.anim.right_to_left,R.anim.exit_right).replace(R.id.mainActivityFrameLayout,selectedFragment).commit();
                }
                else {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_to_left,R.anim.exit_right,R.anim.left_to_right,R.anim.exit_left).replace(R.id.mainActivityFrameLayout,selectedFragment).commit();
                }
                if(chosenButton.equals("Fixed")){
                    menu.getItem(1).setIcon(R.drawable.sathi_logo_without_background);
                }
                else {
                    menu.getItem(1).setIcon(R.drawable.broken_heart);
                }
            }

            return true;
        }
    };

    private class AddToken extends AsyncTask<String,Integer,Integer>{
        @Override
        protected Integer doInBackground(String... strings) {
            DatabaseReference tokensDb = FirebaseDatabase.getInstance().getReference().child("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            tokensDb.setValue(strings[0]);
            return null;
        }
    }
}
