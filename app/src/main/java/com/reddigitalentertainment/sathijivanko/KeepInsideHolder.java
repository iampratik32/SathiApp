package com.reddigitalentertainment.sathijivanko;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public  class KeepInsideHolder {

    SathiUser sathiUser;
    LinkedList keepImages;
    private ArrayList<Chat_PotentialChatUser> chatUsers = new ArrayList<>();


    public void insertInHolder(){

        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        Date dateobj = new Date();
        SathiUserHolder.setCurrentDate(df.format(dateobj));

        final String userId = FirebaseAuth.getInstance().getUid();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        keepImages = new LinkedList();
        final DatabaseReference thisUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        readData(thisUserDb, new onGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    sathiUser = new SathiUser(userId,dataSnapshot.child("Name").getValue().toString(),dataSnapshot.child("Gender").getValue().toString()
                            ,Integer.parseInt(dataSnapshot.child("Age")
                            .getValue().toString()),dataSnapshot.child("Bio").getValue().toString(),dataSnapshot.child("Location").getValue()
                            .toString(),dataSnapshot.child("Email").getValue().toString(),dataSnapshot.child("Preference").getValue().toString()
                            ,dataSnapshot.child("ShowAge").getValue().toString(),dataSnapshot.child("ShowMe").getValue().toString(),
                            dataSnapshot.child("LookingIn").getValue().toString(),dataSnapshot.child("Verified").getValue().toString());

                    sathiUser.setDisplayPicture(dataSnapshot.child("DisplayPicture").getValue().toString());
                    sathiUser.setLookingIn(dataSnapshot.child("LookingIn").getValue().toString());
                    SathiUserHolder.setSathiUser(sathiUser);
                    Log.d("QWE",SathiUserHolder.getSathiUser().getLookingIn());
                    DatabaseReference allImages = thisUserDb.child("Images");
                    allImages.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                                    if(postSnapshot.child("Link").exists() && postSnapshot.child("Link")!=null){
                                        String imageName = postSnapshot.child("Link").getValue().toString();
                                        keepImages.add(imageName);
                                    }
                                }
                                SathiUserHolder.getSathiUser().setUserImages(keepImages);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    final DatabaseReference extraInformation = thisUserDb.child("Extra Information");
                    extraInformation.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                DatabaseReference jobRef = extraInformation.child("Job");
                                jobRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            SathiUserHolder.getSathiUser().setUserJob(dataSnapshot.getValue().toString());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                DatabaseReference schoolRef = extraInformation.child("School");
                                schoolRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            SathiUserHolder.getSathiUser().setUserSchool(dataSnapshot.getValue().toString());
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
                                            SathiUserHolder.getSathiUser().setUserCollege(dataSnapshot.getValue().toString());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                DatabaseReference lookingForRef = extraInformation.child("Looking For");
                                lookingForRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            SathiUserHolder.getSathiUser().setLookingFor(dataSnapshot.getValue().toString());
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
                getChatUsers();
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

    public void readData(DatabaseReference databaseReference, final KeepInsideHolder.onGetDataListener listener){
        listener.onStart();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    listener.onSuccess(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }

    public void getChatUsers(){
        LoadChatInfo loadChatInfo = new LoadChatInfo();
        loadChatInfo.execute();
    }

    public class LoadChatInfo extends AsyncTask<Integer,Integer,Integer>{
        @Override
        protected Integer doInBackground(Integer... integers) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId()).child("Matches").child("Matched");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                           DatabaseReference checkUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(postSnapshot.getKey());
                           checkUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                   if(dataSnapshot.exists()){
                                        addToList(dataSnapshot);
                                   }
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError) {

                               }
                           });
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

    private void addToList(DataSnapshot dataSnapshot){
        Chat_PotentialChatUser chatUser = new Chat_PotentialChatUser(dataSnapshot.getKey(),dataSnapshot.child("Name").getValue().toString(),
                dataSnapshot.child("Age").getValue().toString(),dataSnapshot.child("Gender").getValue().toString(),dataSnapshot.child("Verified").getValue().toString());
        chatUser.setProfileImage(dataSnapshot.child("DisplayPicture").getValue().toString());

        chatUsers.add(chatUser);
        SathiUserHolder.getSathiUser().setChatUsers(chatUsers);
    }

}
