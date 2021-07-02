package com.reddigitalentertainment.sathijivanko;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikesViewHolder extends RecyclerView.ViewHolder {

    public TextView name,age,what;
    public ImageView likeButton;
    public Button viewButton;
    public CircleImageView displayPic;
    public LinearLayout toHide;
    public CardView cardView;
    public ImageView verifiedIcon;

    public LikesViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.userName_likeHolder);
        age = itemView.findViewById(R.id.userAge_likeHolder);
        what = itemView.findViewById(R.id.what_likeHolder);
        likeButton = itemView.findViewById(R.id.like_feedHolder);
        viewButton = itemView.findViewById(R.id.viewButton_likeHolder);
        displayPic = itemView.findViewById(R.id.userPhoto_likeHolder);
        cardView = itemView.findViewById(R.id.cardView_likeHolder);
        toHide = itemView.findViewById(R.id.linear_likeHolder);
        verifiedIcon = itemView.findViewById(R.id.verified_likeHolder);


    }
}
