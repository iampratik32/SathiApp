package com.reddigitalentertainment.sathijivanko;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat_PotentialUserHolder extends RecyclerView.ViewHolder {

    public TextView userName;
    public TextView userId;
    public CircleImageView userDisplayPicture;
    public TextView imageLink;
    public TextView userGender;
    public CardView cardView;
    public RelativeLayout relativeLayout;
    public ImageView verifiedIcon;

    public Chat_PotentialUserHolder(@NonNull View itemView) {
        super(itemView);

        userName = itemView.findViewById(R.id.chat_potential_userHolderName);
        userId = itemView.findViewById(R.id.chat_potential_userHolderId);
        userGender=itemView.findViewById(R.id.chatPotential_userGender);
        userDisplayPicture = itemView.findViewById(R.id.chat_potential_userHolderImage);
        userId.setVisibility(View.GONE);
        imageLink = itemView.findViewById(R.id.chat_potential_userHolderLink);
        cardView = itemView.findViewById(R.id.chatCardView);
        relativeLayout = itemView.findViewById(R.id.relativeLayout_chat_holder);
        verifiedIcon = itemView.findViewById(R.id.verified_cpHolder);

    }
}
