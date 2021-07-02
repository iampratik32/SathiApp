package com.reddigitalentertainment.sathijivanko;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class UserHobbiesHolder extends RecyclerView.ViewHolder{

    public TextView hobbyName;
    public CardView cardView;
    public ImageView addButton;

    public UserHobbiesHolder(@NonNull final View itemView) {
        super(itemView);

        hobbyName = itemView.findViewById(R.id.hobbyName_extrasList);
        cardView = itemView.findViewById(R.id.cardView_hobbiesList);
        addButton = itemView.findViewById(R.id.addButton_hobbiesList);
    }

}
