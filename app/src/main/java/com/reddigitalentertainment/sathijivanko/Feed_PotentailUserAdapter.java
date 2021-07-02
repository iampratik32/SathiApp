package com.reddigitalentertainment.sathijivanko;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Feed_PotentailUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Feed_Class> userList;
    private Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public Feed_PotentailUserAdapter(List<Feed_Class> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_potentail_feed_holder, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
            return new FeedViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof FeedViewHolder){
            populateFeed((FeedViewHolder) holder,position);
        }
        else if(holder instanceof LoadingViewHolder){
            showLoading((LoadingViewHolder) holder,position);
        }

    }

    @Override
    public int getItemCount() {
        return userList == null ? 0 : userList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return userList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private class FeedViewHolder extends RecyclerView.ViewHolder{

        public TextView userName;
        public TextView userId;
        public ImageView userDisplayPicture, verifiedIcon;
        public TextView userAge;
        public LinearLayout layout;
        public TextView userGender;
        public Button viewButton;
        public TextView what;
        public CircleImageView chatIcon;
        public CardView cardView;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName_feedHolder);
            userId = itemView.findViewById(R.id.userId_feedHolder);
            userAge = itemView.findViewById(R.id.userAge_feedHolder);
            userGender = itemView.findViewById(R.id.userGender_feedHolder);
            userDisplayPicture = itemView.findViewById(R.id.userPhoto_feedHolder);
            chatIcon  = itemView.findViewById(R.id.chatIcon_feedHolder);
            what  = itemView.findViewById(R.id.what_feedHolder);
            viewButton = itemView.findViewById(R.id.viewButton_feed);
            cardView = itemView.findViewById(R.id.cardView_feedHolder);
            layout = itemView.findViewById(R.id.linear_feed);
            verifiedIcon = itemView.findViewById(R.id.verified_fHolder);
            userId.setVisibility(View.GONE);


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(layout.getVisibility()==View.VISIBLE){
                        layout.setVisibility(View.GONE);
                    }
                    else {
                        layout.setVisibility(View.VISIBLE);
                    }
                }
            });
        }


    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder{
        ProgressBar progressBar;


        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar_FeedLoading);
        }
    }

    private void showLoading(LoadingViewHolder viewHolder, int position){

    }

    private void populateFeed(FeedViewHolder holder, int position){
        try {
            holder.userId.setText(userList.get(position).getUserId());
            if(userList.get(position).getShowAge().equals("true")){
                holder.userName.setText(userList.get(position).getUserName()+",");
                holder.userAge.setText(userList.get(position).getAge());
            }
            else {
                holder.userName.setText(userList.get(position).getUserName());
                holder.userAge.setVisibility(View.GONE);
            }
            if(userList.get(position).getVerified().equals("true")){
                holder.verifiedIcon.setVisibility(View.VISIBLE);
            }
            else {
                holder.verifiedIcon.setVisibility(View.GONE);
            }
            holder.userGender.setText(userList.get(position).getGender());
            if(userList.get(position).getDisplayPicture()!=null){
                RequestOptions myOptions = new RequestOptions().centerCrop();
                Glide.with(context).load(userList.get(position).getDisplayPicture()).apply(myOptions).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.userDisplayPicture);

            }
            if(userList.get(position).getMatched().equals("True")){
                holder.chatIcon.setVisibility(View.VISIBLE);
            }
            else {
                holder.chatIcon.setVisibility(View.GONE);
            }

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

            SimpleDateFormat formatter= new SimpleDateFormat("MM/dd 'at' HH:mm");
            Date date = new Date(userList.get(position).getTime());
            String feedTime = formatter.format(date);
            String pronoun = "her";
            if(holder.userGender.getText().equals("Man")){
                pronoun="his";
            }

            if (userList.get(position).getWhat().equals("Matched")) {
                holder.what.setText("You matched on "+feedTime+".");
            }
            else if(userList.get(position).getWhat().equals("ChangePicture")){
                holder.what.setText(holder.userName.getText().toString()+" changed "+pronoun+ " Display Picture on "+ feedTime+".");
            }
            holder.chatIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent chatIntent = new Intent(v.getContext(),ChatWithUserActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("UserId",holder.userId.getText().toString());
                    bundle.putString("UserName",holder.userName.getText().toString());
                    bundle.putString("ImageLink",userList.get(position).getDisplayPicture());
                    chatIntent.putExtras(bundle);
                    v.getContext().startActivity(chatIntent);
                }
            });

        }
        catch (NullPointerException e){
        }
    }

}
