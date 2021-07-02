package com.reddigitalentertainment.sathijivanko;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

class Feed_PotentialUserHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView userName;
    public TextView userId;
    public ImageView userDisplayPicture;
    public TextView userAge;
    public TextView userGender;
    public TextView what;
    public CircleImageView chatIcon;

    public Feed_PotentialUserHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        userName = itemView.findViewById(R.id.userName_feedHolder);
        userId = itemView.findViewById(R.id.userId_feedHolder);
        userAge = itemView.findViewById(R.id.userAge_feedHolder);
        userGender = itemView.findViewById(R.id.userGender_feedHolder);
        userDisplayPicture = itemView.findViewById(R.id.userPhoto_feedHolder);
        chatIcon  = itemView.findViewById(R.id.chatIcon_feedHolder);
        what  = itemView.findViewById(R.id.what_feedHolder);
        userId.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {
        Intent userProfile = new Intent(v.getContext(),MatchesProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("UserId",userId.getText().toString());
        bundle.putString("UserName",userName.getText().toString());
        bundle.putString("UserGender",userGender.getText().toString());
        userProfile.putExtras(bundle);
        v.getContext().startActivity(userProfile);
    }


}
