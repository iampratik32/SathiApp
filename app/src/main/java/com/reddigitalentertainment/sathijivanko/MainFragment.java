package com.reddigitalentertainment.sathijivanko;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;


public class MainFragment extends Fragment {

    private View view;
    private DatabaseReference locationsDb;
    private DatabaseReference usersDb;
    PleaseWaitDialog dialog;
    private View bottomSheetView;
    int totalLimit=10;
    int userLimit=3;
    CountDownTimer countDownTimer;
    LinkedList<String> takenUserIds = new LinkedList<>();
    List<PotentialMatchesInfo> takenUsers = new ArrayList<>();
    private PotentialMatchesArrayAdapter arrayAdapter;
    private SwipeFlingAdapterView swipeView;
    private TextView userNameExtra, userAgeExtra, userBioExtra, userJobExtra, userLookingForExtra, userSchoolExtra,userCollegeExtra,userStatusExtra,hobbiesExtra;
    private BottomSheetDialog extraInformationDialog;
    private LinearLayout userJobLayout, userLookingForLayout, userHobbiesLayout, viewPagerLayout,userSchoolLayout,userCollegeLayout,userStatusLayout;
    private ImageView hideButton;
    private ViewPager viewPager;
    LinkedList<PotentialMatchesInfo> previousUsers = new LinkedList<>();
    String thisGender ;
    String thisPrefrence;
    private LinearLayout bottomLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main, container, false);
        showDialog();
        if(SathiUserHolder.getUserCountry()==null){
            CheckUtility.getLocation(getApplicationContext());
        }

        if(SathiUserHolder.getDealtWithList()==null){
            SathiUserHolder.setDealtWithList(new ArrayList<PotentialMatchesInfo>());
        }

        ImageView extraInformationButton = view.findViewById(R.id.extraInformationButton_mainFragment);
        extraInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(takenUsers.size()>0){
                    loadExtraInfo(takenUsers.get(0));
                }
            }
        });

        ImageButton likeButton = view.findViewById(R.id.likeButton_mainFragment);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(takenUsers.size()!=0){
                    swipeView.getTopCardListener().setRotationDegrees(28);
                    swipeView.getTopCardListener().selectRight();
                }
            }
        });

        ImageButton disLikeButton = view.findViewById(R.id.dislikeButton_mainFragment);
        disLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(takenUsers.size()!=0){
                    swipeView.getTopCardListener().setRotationDegrees(28);
                    swipeView.getTopCardListener().selectLeft();
                }
            }
        });

        ImageButton rewindButton = view.findViewById(R.id.rewindButton_mainFragment);
        rewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SathiUserHolder.getDealtWithList().size()!=0){
                    int prevDisLikes = SathiUserHolder.getTotalDislikes();
                    SathiUserHolder.setTotalDislikes(prevDisLikes++);
                    if(takenUsers.size()!=0){
                        PotentialMatchesInfo temp = takenUsers.get(0);
                        takenUsers.set(0,SathiUserHolder.getDealtWithList().get(SathiUserHolder.getDealtWithList().size()-1));
                        takenUsers.add(1,temp);
                        SathiUserHolder.setShownPeople(takenUsers);
                    }
                    else{
                        takenUsers.add(SathiUserHolder.getDealtWithList().get(SathiUserHolder.getDealtWithList().size()-1));
                    }
                    arrayAdapter.notifyDataSetChanged();
                    swipeView.removeAllViewsInLayout();
                    swipeView.forceLayout();
                    PotentialMatchesInfo pu = SathiUserHolder.getDealtWithList().get(SathiUserHolder.getDealtWithList().size()-1);
                    DatabaseReference matchesDb = usersDb.child(pu.getUserId()).child("Matches");
                    matchesDb.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("Dislikes").child(SathiUserHolder.getSathiUser().getUserId()).exists()){
                                matchesDb.child("Dislikes").child(SathiUserHolder.getSathiUser().getUserId()).removeValue();
                                SathiUserHolder.getDealtWithList().remove(SathiUserHolder.getDealtWithList().size()-1);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        bottomLayout = view.findViewById(R.id.bottomLinearLayout_mainFragment);

        return view;

    }

    private void loadExtraInfo(PotentialMatchesInfo thatUser) {
        bottomSheetView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_extra_information_main_fragment,null);
        LoadExtraInfoAsync loadExtraInfoAsync = new LoadExtraInfoAsync();
        loadExtraInfoAsync.execute(thatUser);
        extraInformationDialog = new BottomSheetDialog(getActivity());
        extraInformationDialog.setContentView(bottomSheetView);
        extraInformationDialog.show();
        userNameExtra = bottomSheetView.findViewById(R.id.extraInformation_userName);
        userAgeExtra = bottomSheetView.findViewById(R.id.extraInformation_userAge);
        userBioExtra = bottomSheetView.findViewById(R.id.extraInformation_userBio);
        viewPagerLayout = bottomSheetView.findViewById(R.id.viewPagerLayout_extraInformation);
        userJobExtra = bottomSheetView.findViewById(R.id.userJob_extraInformation);
        userJobLayout = bottomSheetView.findViewById(R.id.jobLayout_extraInformation);
        userJobLayout.setVisibility(View.GONE);
        userSchoolExtra = bottomSheetView.findViewById(R.id.school_extraInformation);
        userSchoolLayout = bottomSheetView.findViewById(R.id.schoolLayout_extraInformation);
        userSchoolLayout.setVisibility(View.GONE);
        userCollegeExtra = bottomSheetView.findViewById(R.id.college_extraInformation);
        userCollegeLayout = bottomSheetView.findViewById(R.id.collegeLayout_extraInformation);
        userCollegeLayout.setVisibility(View.GONE);
        userStatusExtra = bottomSheetView.findViewById(R.id.status_extraInformation);
        userStatusLayout = bottomSheetView.findViewById(R.id.statusLayout_extraInformation);
        userStatusLayout.setVisibility(View.GONE);
        userLookingForExtra = bottomSheetView.findViewById(R.id.lookingFor_extraInformation);
        userLookingForLayout = bottomSheetView.findViewById(R.id.lookingForLayout_extraInformation);
        userLookingForLayout.setVisibility(View.GONE);
        hobbiesExtra = bottomSheetView.findViewById(R.id.hobbies_extraInformation);
        userHobbiesLayout = bottomSheetView.findViewById(R.id.hobbiesLayout_extraInformation);
        userHobbiesLayout.setVisibility(View.GONE);
        hideButton = bottomSheetView.findViewById(R.id.extraInformationDownButton_mainFragment);
        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extraInformationDialog.dismiss();
            }
        });
        String tempName = thatUser.getUserName();
        if(thatUser.getShowAge().equals("true")){
            userAgeExtra.setText(thatUser.getAge());
            tempName=tempName+",";
        }
        else {
            userAgeExtra.setVisibility(View.GONE);
        }
        userNameExtra.setText(tempName);
        userBioExtra.setText(thatUser.getBio());
        viewPager = bottomSheetView.findViewById(R.id.viewPager_extraInformation);
        if(takenUsers.get(0).getListOfImages()>0){
            MatchesProfileViewPagerAdapter matchesProfileViewPagerAdapter = new MatchesProfileViewPagerAdapter(getApplicationContext(),(int) takenUsers.get(0).getListOfImages(),
                    takenUserIds.get(0),takenUsers.get(0).getGender());
            viewPager.setAdapter(matchesProfileViewPagerAdapter);
            viewPagerLayout.setVisibility(View.VISIBLE);
        }
        else {
            viewPagerLayout.setVisibility(View.GONE);
        }


    }


    private void getDatabaseUsers(){
        locationsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot post:dataSnapshot.getChildren()){
                        String thatUserId = post.getKey();
                        if(!thatUserId.equals(SathiUserHolder.getSathiUser().getUserId())){
                            if(!takenUserIds.contains(thatUserId)){
                                String genderAndPref = post.getValue().toString();
                                String[] split = genderAndPref.split(",");
                                String gender = split[0];
                                String preference = split[1];
                                if(thisPrefrence.equals("Both")){
                                    if(preference.equals(thisGender) || preference.equals("Both")){
                                        getUserData(thatUserId);
                                    }
                                }
                                else if(preference.equals("Both")){
                                    if(thisPrefrence.equals(gender)){
                                        getUserData(thatUserId);
                                    }
                                }
                                else {
                                    if(thisGender.equals(preference) && thisPrefrence.equals(gender)){
                                        getUserData(thatUserId);
                                    }
                                }
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeAdapter() {
        thisGender = SathiUserHolder.getSathiUser().getGender();
        thisPrefrence = SathiUserHolder.getSathiUser().getPreference();
        arrayAdapter = new PotentialMatchesArrayAdapter(getContext(),R.layout.swipe_items,takenUsers){
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                v.setTranslationY(position*10);
                v.setTranslationX(position*25);
                return v;
            }
        };
        swipeView = view.findViewById(R.id.swipeFrame);
        swipeView.setAdapter(arrayAdapter);
        swipeView.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int i, Object o) {
                PotentialMatchesInfo currentUser = (PotentialMatchesInfo)o;
                loadExtraInfo(currentUser);
            }
        });
        swipeView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                takenUsers.remove(0);
                takenUserIds.remove(0);
                SathiUserHolder.setShownPeople(takenUsers);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object o) {
                PotentialMatchesInfo currentUser = (PotentialMatchesInfo)o;
                DislikesDatabase dislikesDatabase = new DislikesDatabase();
                dislikesDatabase.execute(currentUser.getUserId());
                SathiUserHolder.getDealtWithList().add((PotentialMatchesInfo) o);
            }

            @Override
            public void onRightCardExit(Object o) {
                PotentialMatchesInfo currentUser = (PotentialMatchesInfo)o;
                LikesDatabase likesDatabase = new LikesDatabase();
                likesDatabase.execute(currentUser);
                int prevLikes = SathiUserHolder.getTotalLikes();
                SathiUserHolder.setTotalLikes(prevLikes++);
            }

            @Override
            public void onAdapterAboutToEmpty(int i) {
                if(i==1){
                    getDatabaseUsers();
                }
            }

            @Override
            public void onScroll(float v) {

            }
        });
        locationsDb = FirebaseDatabase.getInstance().getReference().child("Locations").child(SathiUserHolder.getSathiUser().getLookingIn());
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        if(SathiUserHolder.getShownPeople()!=null){
            if(SathiUserHolder.getShownPeople().size()==0){
                locationsDb = FirebaseDatabase.getInstance().getReference().child("Locations").child(SathiUserHolder.getSathiUser().getLookingIn());
                getDatabaseUsers();
            }
            else {
                for(int i=0;i<SathiUserHolder.getShownPeople().size();i++){
                    takenUsers.add(SathiUserHolder.getShownPeople().get(i));
                    takenUserIds.add(SathiUserHolder.getShownPeople().get(i).getUserId());
                }
                arrayAdapter.notifyDataSetChanged();
            }
        }
        else {
            locationsDb = FirebaseDatabase.getInstance().getReference().child("Locations").child(SathiUserHolder.getSathiUser().getLookingIn());
            getDatabaseUsers();
        }
    }

    private class DislikesDatabase extends AsyncTask<String,Integer,Integer>{
        @Override
        protected Integer doInBackground(String... strings) {
            usersDb.child(strings[0]).child("Matches").child("Dislikes").child(SathiUserHolder.getSathiUser().getUserId()).setValue(SathiUserHolder.getCurrentDate());
            return null;
        }
    }

    private class LikesDatabase extends AsyncTask<PotentialMatchesInfo,Integer,Integer>{
        @Override
        protected Integer doInBackground(PotentialMatchesInfo... thatUsers) {
            usersDb.child(thatUsers[0].getUserId()).child("Matches").child("Likes").child(SathiUserHolder.getSathiUser().getUserId()).setValue(SathiUserHolder.getCurrentDate());
            DatabaseReference checkMatch = usersDb.child(SathiUserHolder.getSathiUser().getUserId()).child("Matches");
            checkMatch.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("Likes").child(thatUsers[0].getUserId()).exists()){
                        MatchedDialog matchedDialog = new MatchedDialog(getApplicationContext(),thatUsers[0].getUserName(),thatUsers[0].getDisplayPicture());
                        matchedDialog.show(getFragmentManager(),"MatchedDialog");
                        checkMatch.child("Matched").child(thatUsers[0].getUserId()).setValue(SathiUserHolder.getCurrentDate());
                        usersDb.child(thatUsers[0].getUserId()).child("Matches").child("Matched").child(SathiUserHolder.getSathiUser().getUserId()).setValue(SathiUserHolder.getCurrentDate());

                        Chat_PotentialChatUser chatUser = new Chat_PotentialChatUser(thatUsers[0].getUserId(),thatUsers[0].getUserName(),thatUsers[0].getAge(),
                                thatUsers[0].getGender(),thatUsers[0].getVerified());
                        if(SathiUserHolder.getSathiUser().getChatUsers()==null){
                            SathiUserHolder.getSathiUser().setChatUsers(new ArrayList<Chat_PotentialChatUser>());
                        }
                        SathiUserHolder.getSathiUser().getChatUsers().add(chatUser);
                        Map firstFeedMap = new HashMap();
                        firstFeedMap.put("User",thatUsers[0].getUserId());
                        firstFeedMap.put("Time",new Date().getTime());
                        firstFeedMap.put("What","Matched");
                        DatabaseReference feedDb = FirebaseDatabase.getInstance().getReference().child("Feeds").child(SathiUserHolder.getSathiUser().getUserId()).push();
                        feedDb.setValue(firstFeedMap);

                        Map secondFeedMap = new HashMap();
                        secondFeedMap.put("User",SathiUserHolder.getSathiUser().getUserId());
                        secondFeedMap.put("Time",new Date().getTime());
                        secondFeedMap.put("What","Matched");
                        DatabaseReference anotherFeed = FirebaseDatabase.getInstance().getReference().child("Feeds").child(thatUsers[0].getUserId()).push();
                        anotherFeed.setValue(secondFeedMap);
                        SathiUserHolder.setLoadChat(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return null;
        }
    }

    private void getUserData(String thatId){
        Log.d("ID",thatId);
        if(totalLimit> takenUserIds.size()){
            if(takenUserIds.size()<userLimit){
                LoadUserInfoAsync loadInfo = new LoadUserInfoAsync();
                loadInfo.execute(thatId);
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"LimitReached",Toast.LENGTH_SHORT).show();
        }
    }

    private class LoadUserInfoAsync extends AsyncTask<String,Integer,Integer>{
        @Override
        protected Integer doInBackground(String... strings) {
            usersDb.child(strings[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        if(dataSnapshot.child("ShowMe").getValue().toString().equals("true")){
                            if(!dataSnapshot.child("Matches").child("Likes").hasChild(SathiUserHolder.getSathiUser().getUserId())
                                    && !dataSnapshot.child("Matches").child("Dislikes").hasChild(SathiUserHolder.getSathiUser().getUserId())){
                                if(!takenUserIds.contains(strings[0])){
                                    PotentialMatchesInfo pUser = new PotentialMatchesInfo(strings[0],dataSnapshot.child("Name").getValue().toString()
                                            ,dataSnapshot.child("Bio").getValue().toString(),dataSnapshot.child("Age").getValue().toString(),
                                            dataSnapshot.child("ShowAge").getValue().toString(),dataSnapshot.child("Gender").getValue().toString(),
                                            dataSnapshot.child("Verified").getValue().toString());
                                    pUser.setDisplayPicture(dataSnapshot.child("DisplayPicture").getValue().toString());
                                    usersDb.child(strings[0]).child("Images").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                pUser.setListOfImages((int)dataSnapshot.getChildrenCount());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                    takenUserIds.add(strings[0]);
                                    takenUsers.add(pUser);
                                    SathiUserHolder.setShownPeople(takenUsers);
                                    arrayAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return null;
        }
    }

    private void showDialog(){
        dialog = new PleaseWaitDialog();
        if(SathiUserHolder.getSathiUser()==null){
            dialog.show(getFragmentManager(),"PleaseWait");
            dialog.setCancelable(false);
            countDownTimer = new CountDownTimer(4500,1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    if(SathiUserHolder.getSathiUser()==null){
                        dialog.dismiss();
                        showDialog();
                    }
                    else {
                        dialog.dismiss();
                        initializeAdapter();
                    }
                }
            }.start();
        }
        else {
            initializeAdapter();
        }
    }

    private class LoadExtraInfoAsync extends AsyncTask<PotentialMatchesInfo,Integer,Integer>{
        @Override
        protected Integer doInBackground(PotentialMatchesInfo... thatUsers) {
            try{
                DatabaseReference extraDb = usersDb.child(thatUsers[0].getUserId()).child("Extra Information");
                extraDb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            extraDb.child("Job").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        userJobExtra.setText(dataSnapshot.getValue().toString());
                                        userJobLayout.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            extraDb.child("College").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        userCollegeExtra.setText(dataSnapshot.getValue().toString());
                                        userCollegeLayout.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            extraDb.child("School").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        userSchoolExtra.setText(dataSnapshot.getValue().toString());
                                        userSchoolLayout.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            extraDb.child("Looking For").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        userLookingForExtra.setText(dataSnapshot.getValue().toString());
                                        userLookingForLayout.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            extraDb.child("Status").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        userStatusExtra.setText(dataSnapshot.getValue().toString());
                                        userStatusLayout.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            extraDb.child("Hobbies").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        userHobbiesLayout.setVisibility(View.VISIBLE);
                                        for(DataSnapshot post:dataSnapshot.getChildren()){
                                            hobbiesExtra.setText(hobbiesExtra.getText()+"\n"+post.getKey());
                                        }
                                    }
                                    else {
                                        userHobbiesLayout.setVisibility(View.GONE);
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
            catch (RuntimeException e){

            }
            return null;
        }
    }


}
