package com.reddigitalentertainment.sathijivanko;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LikesAdapter extends RecyclerView.Adapter<LikesViewHolder> {

    private List<PotentialMatchesInfo> userList;
    private Context context;
    private String type;

    public LikesAdapter(List<PotentialMatchesInfo> userList, Context context,String type) {
        this.userList = userList;
        this.context = context;
        this.type = type;
    }

    @Override
    public void onBindViewHolder(@NonNull LikesViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @NonNull
    @Override
    public LikesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.likes_holder,null,false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(layoutParams);
        LikesViewHolder holder = new LikesViewHolder(layoutView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LikesViewHolder holder, int position) {
        holder.name.setText(userList.get(position).getUserName());
        if(userList.get(position).getShowAge().equals("true")){
            holder.age.setText(userList.get(position).getAge());
        }
        if(userList.get(position).getVerified().equals("true")){
            holder.verifiedIcon.setVisibility(View.VISIBLE);
        }
        else {
            holder.verifiedIcon.setVisibility(View.GONE);
        }
        if(type.equals("L")){
            holder.what.setText("Liked you on, "+userList.get(position).getMatchedDate());
        }
        else {
            holder.what.setText("You Disliked on, "+userList.get(position).getMatchedDate());
        }
        Glide.with(context).load(userList.get(position).getDisplayPicture()).thumbnail(Glide.with(context).load(R.drawable.loading)).into(holder.displayPic);
        holder.viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userProfile = new Intent(v.getContext(),MatchesProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("UserId",userList.get(position).getUserId());
                bundle.putString("UserName",userList.get(position).getUserName());
                bundle.putString("UserGender",userList.get(position).getGender());
                bundle.putString("Display",userList.get(position).getDisplayPicture());
                bundle.putString("Hide","Chat");
                userProfile.putExtras(bundle);
                v.getContext().startActivity(userProfile);
            }
        });
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PleaseWaitDialog pleaseWaitDialog = new PleaseWaitDialog();
                FragmentManager manager2 = ((AppCompatActivity)context).getSupportFragmentManager();
                if(type.equals("L")){
                    MatchedDialog matchedDialog = new MatchedDialog(getApplicationContext(),userList.get(position).getUserName(),userList.get(position).getDisplayPicture());
                    FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
                    matchedDialog.setAdapter(LikesAdapter.this);
                    matchedDialog.show(manager,"MatchedDialog");

                    ChangeDb changeDb = new ChangeDb();
                    changeDb.execute(position);
                    notifyDataSetChanged();
                    SathiUserHolder.setShownPeople(null);
                }
                else {
                    pleaseWaitDialog.show(manager2,"MatchedDialog");
                    String tempId = userList.get(position).getUserId();
                    String tempName = userList.get(position).getUserName();
                    String tempDp = userList.get(position).getDisplayPicture();
                    String tempAge = userList.get(position).getAge();
                    String tempGender = userList.get(position).getGender();
                    String tempVerified = userList.get(position).getVerified();
                    DatabaseReference likeDb = FirebaseDatabase.getInstance().getReference().child("Users").child(tempId);
                    likeDb.child("Matches").child("Dislikes").child(SathiUserHolder.getSathiUser().getUserId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                likeDb.child("Matches").child("Likes").child(SathiUserHolder.getSathiUser().getUserId()).setValue(SathiUserHolder.getCurrentDate()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.d("1","3");
                                            DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId());
                                            userDb.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.child("Matches").child("Likes").child(tempId).exists()){
                                                        Log.d("1","4");
                                                        pleaseWaitDialog.dismiss();
                                                        MatchedDialog matchedDialog = new MatchedDialog(getApplicationContext(),tempName,tempDp);
                                                        FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
                                                        matchedDialog.setAdapter(LikesAdapter.this);
                                                        matchedDialog.show(manager,"MatchedDialog");
                                                        userDb.child("Matches").child("Matched").child(tempId).setValue(SathiUserHolder.getCurrentDate());
                                                        likeDb.child("Matches").child("Matched").child(SathiUserHolder.getSathiUser().getUserId()).setValue(SathiUserHolder.getCurrentDate());

                                                        Chat_PotentialChatUser chatUser = new Chat_PotentialChatUser(tempId,tempName,tempAge,tempGender,tempVerified);
                                                        if(SathiUserHolder.getSathiUser().getChatUsers()==null){
                                                            SathiUserHolder.getSathiUser().setChatUsers(new ArrayList<Chat_PotentialChatUser>());
                                                        }
                                                        SathiUserHolder.getSathiUser().getChatUsers().add(chatUser);
                                                        Map firstFeedMap = new HashMap();
                                                        firstFeedMap.put("User",tempId);
                                                        firstFeedMap.put("Time",new Date().getTime());
                                                        firstFeedMap.put("What","Matched");
                                                        DatabaseReference feedDb = FirebaseDatabase.getInstance().getReference().child("Feeds").child(SathiUserHolder.getSathiUser().getUserId()).push();
                                                        feedDb.setValue(firstFeedMap);

                                                        Map secondFeedMap = new HashMap();
                                                        secondFeedMap.put("User",SathiUserHolder.getSathiUser().getUserId());
                                                        secondFeedMap.put("Time",new Date().getTime());
                                                        secondFeedMap.put("What","Matched");
                                                        DatabaseReference anotherFeed = FirebaseDatabase.getInstance().getReference().child("Feeds").child(tempId).push();
                                                        anotherFeed.setValue(secondFeedMap);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            userList.remove(0);
                                            notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }


            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.toHide.getVisibility()==View.VISIBLE){
                    holder.toHide.setVisibility(View.GONE);
                }
                else {
                    holder.toHide.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ChangeDb extends AsyncTask<Integer,Integer,Integer>{
        @Override
        protected Integer doInBackground(Integer... integers) {
            Log.d("QW",String.valueOf(userList.get(0).getUserId()));

            DatabaseReference likeDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userList.get(integers[0]).getUserId()).child("Matches");
            likeDb.child("Likes").child(SathiUserHolder.getSathiUser().getUserId()).setValue(SathiUserHolder.getCurrentDate());
            likeDb.child("Matched").child(SathiUserHolder.getSathiUser().getUserId()).setValue(SathiUserHolder.getCurrentDate());
            DatabaseReference myDb = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId()).child("Matches").child("Matched")
                    .child(userList.get(integers[0]).getUserId());
            myDb.setValue(SathiUserHolder.getCurrentDate());

            feedDb(integers[0]);
            userList.remove(integers[0]);

            return null;
        }
    }

    private void feedDb(Integer... integers){
        Chat_PotentialChatUser chatUser = new Chat_PotentialChatUser(userList.get(integers[0]).getUserId(),userList.get(integers[0]).getUserName(),
                userList.get(integers[0]).getAge(),userList.get(integers[0]).getGender(),userList.get(integers[0]).getVerified());
        if(SathiUserHolder.getSathiUser().getChatUsers()==null){
            SathiUserHolder.getSathiUser().setChatUsers(new ArrayList<Chat_PotentialChatUser>());
        }
        SathiUserHolder.getSathiUser().getChatUsers().add(chatUser);
        Map firstFeedMap = new HashMap();
        firstFeedMap.put("User",userList.get(integers[0]).getUserId());
        firstFeedMap.put("Time",new Date().getTime());
        firstFeedMap.put("What","Matched");
        DatabaseReference feedDb = FirebaseDatabase.getInstance().getReference().child("Feeds").child(SathiUserHolder.getSathiUser().getUserId()).push();
        feedDb.setValue(firstFeedMap);

        Map secondFeedMap = new HashMap();
        secondFeedMap.put("User",SathiUserHolder.getSathiUser().getUserId());
        secondFeedMap.put("Time",new Date().getTime());
        secondFeedMap.put("What","Matched");
        DatabaseReference anotherFeed = FirebaseDatabase.getInstance().getReference().child("Feeds").child(userList.get(integers[0]).getUserId()).push();
        anotherFeed.setValue(secondFeedMap);
    }


    public void filter(String constraint){
        try {
            userList.clear();
            if(constraint.length()==0 || constraint.trim().isEmpty()){
                if(type.equals("L")){
                    userList.addAll(SathiUserHolder.getSathiUser().getUserLikes());
                    notifyDataSetChanged();
                }
                else {
                    userList.addAll(SathiUserHolder.getSathiUser().getUserDislikes());
                    notifyDataSetChanged();
                }
            }
            else {
                if(type.equals("L")){
                    for(PotentialMatchesInfo c:SathiUserHolder.getSathiUser().getUserLikes()){
                        if(c.getUserName().toLowerCase().contains(constraint)){
                            userList.add(c);
                        }
                    }
                }
                else {
                    for(PotentialMatchesInfo c:SathiUserHolder.getSathiUser().getUserDislikes()){
                        if(c.getUserName().toLowerCase().contains(constraint)){
                            userList.add(c);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        }
        catch (ConcurrentModificationException exception){
            //filter(constraint);
        }

    }
}
