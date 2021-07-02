package com.reddigitalentertainment.sathijivanko;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MatchesProfileActivity extends AppCompatActivity {

    int noOfImages = 0;
    private LinkedList lf;
    TextView imageCount;
    private String hide;
    private LinkedList info;
    private String userPicUrl;
    RecyclerView hobbiesRecycler;
    private RecyclerView.LayoutManager layoutManager;
    private UserHobbiesAdapter adapter;
    private ImageView verifiedIcon;
    private ArrayList<String> hobbies = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches_profile);


        Bundle getBundle = getIntent().getExtras();
        String userName = "";
        String userId = "";
        String userGender = "";
        if(getBundle!=null){
            userName = getBundle.getString("UserName");
            userId = getBundle.getString("UserId");
            userGender = getBundle.getString("UserGender");
            userPicUrl = getBundle.getString("Display");
            hide = getBundle.getString("Hide");
        }

        final String finalUserName = userName;
        final String finalUserId = userId;
        final String finalUserGender = userGender;

        ImageView backButton = findViewById(R.id.closeButton_matchesProfile);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        final TextView userNameTextView = findViewById(R.id.userName_matchesProfile);
        final TextView userAgeTextView = findViewById(R.id.userAge_matchesProfile);
        final TextView userSchoolTextView = findViewById(R.id.userSchool_matchesProfile);
        final TextView userCollegeTextView = findViewById(R.id.userCollege_matchesProfile);
        final TextView userLocation = findViewById(R.id.userLocation_matchesProfile);
        final TextView userJobTextView = findViewById(R.id.userWork_matchesProfile);
        final TextView userBioTextView = findViewById(R.id.userBio_matchesProfile);
        final TextView userStatusTextView = findViewById(R.id.userStatus_matchesProfile);
        final TextView userLookingForTextView = findViewById(R.id.userLookingFor_matchesProfile);
        final LinearLayout schoolLayout = findViewById(R.id.schoolLayout_matchesProfile);
        final TextView userPreference = findViewById(R.id.userPreference_matchesProfile);
        final LinearLayout collegeLayout = findViewById(R.id.collegeLayout_matchesProfile);
        final LinearLayout workLayout = findViewById(R.id.workLayout_matchesProfile);
        final LinearLayout statusLayout = findViewById(R.id.statusLayout_matchesProfile);
        final LinearLayout lookingForLayout = findViewById(R.id.lookingForLayout_matchesProfile);
        CircleImageView displayPicture = findViewById(R.id.displayPicture_matchesProfile);
        ImageView chatIcon = findViewById(R.id.chatIcon_matchesProfile);
        verifiedIcon = findViewById(R.id.verified_matches);
        if(hide.equals("Chat")){
            chatIcon.setVisibility(View.GONE);
        }
        else {
            chatIcon.setVisibility(View.VISIBLE);
        }
        Glide.with(getApplicationContext()).load(userPicUrl).thumbnail(Glide.with(getApplicationContext()).load(R.drawable.loading)).into(displayPicture);

        displayPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogViewImage dialogViewImage = new DialogViewImage(userPicUrl);
                dialogViewImage.show(getSupportFragmentManager(),"ViewImage");
            }
        });

        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(v.getContext(),ChatWithUserActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("UserId",finalUserId);
                bundle.putString("UserName",finalUserName);
                bundle.putString("ImageLink",userPicUrl);
                bundle.putString("UserGender",finalUserGender);
                chatIntent.putExtras(bundle);
                v.getContext().startActivity(chatIntent);
            }
        });


        final ViewPager viewPager = findViewById(R.id.viewPager_matchesProfile);

        readData(databaseReference.child("Images"), new onGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

                MatchesProfileViewPagerAdapter matchesProfileViewPagerAdapter = new MatchesProfileViewPagerAdapter(getApplicationContext(),(int) dataSnapshot.getChildrenCount(),finalUserId,finalUserGender);
                matchesProfileViewPagerAdapter.setMatchesProfileActivity(MatchesProfileActivity.this);
                viewPager.setAdapter(matchesProfileViewPagerAdapter);
                userNameTextView.setText(finalUserName);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            userBioTextView.setText(dataSnapshot.child("Bio").getValue().toString());
                            if(dataSnapshot.child("ShowAge").getValue().toString().equals("true")){
                                userAgeTextView.setText(dataSnapshot.child("Age").getValue().toString());
                            }
                            else {
                                userAgeTextView.setVisibility(View.GONE);
                            }
                            if(dataSnapshot.child("Verified").getValue().toString().equals("true")){
                                verifiedIcon.setVisibility(View.VISIBLE);
                            }
                            else {
                                verifiedIcon.setVisibility(View.GONE);
                            }
                            userPreference.setText(dataSnapshot.child("Preference").getValue().toString());
                            userLocation.setText(dataSnapshot.child("Location").getValue().toString());
                            final DatabaseReference extraInformation = databaseReference.child("Extra Information");
                            extraInformation.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        extraInformation.child("Job").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    userJobTextView.setText(dataSnapshot.getValue().toString());
                                                }
                                                else {
                                                    workLayout.setVisibility(View.GONE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        extraInformation.child("School").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    userSchoolTextView.setText(dataSnapshot.getValue().toString());
                                                }
                                                else {
                                                    schoolLayout.setVisibility(View.GONE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        extraInformation.child("College").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    userCollegeTextView.setText(dataSnapshot.getValue().toString());
                                                }
                                                else {
                                                    collegeLayout.setVisibility(View.GONE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        extraInformation.child("Status").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    userStatusTextView.setText("I'm "+dataSnapshot.getValue().toString());
                                                }
                                                else {
                                                    statusLayout.setVisibility(View.GONE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                        extraInformation.child("Looking For").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    userLookingForTextView.setText(dataSnapshot.getValue().toString());
                                                }
                                                else {
                                                    lookingForLayout.setVisibility(View.GONE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        LinearLayout hobbiesLayout = findViewById(R.id.userHobbies_layoutMatches);
                                        hobbiesRecycler = findViewById(R.id.recyclerView_matchesActivity);
                                        layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
                                        hobbiesRecycler.setLayoutManager(layoutManager);
                                        adapter = new UserHobbiesAdapter(hobbies,getApplicationContext());
                                        adapter.setHide("Add");
                                        extraInformation.child("Hobbies").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    hobbiesLayout.setVisibility(View.VISIBLE);
                                                    for(DataSnapshot post:dataSnapshot.getChildren()){
                                                        hobbies.add(post.getKey());
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                }
                                                else {
                                                    hobbiesLayout.setVisibility(View.GONE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        hobbiesRecycler.setAdapter(adapter);

                                    }
                                    else {
                                        schoolLayout.setVisibility(View.GONE);
                                        collegeLayout.setVisibility(View.GONE);
                                        workLayout.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onFailure() {
            }
        });


    }

    public interface onGetDataListener{
        void onSuccess(DataSnapshot dataSnapshot);
        void onStart();
        void onFailure();
    }

    public void readData(DatabaseReference databaseReference, final onGetDataListener listener){
        listener.onStart();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }




}
