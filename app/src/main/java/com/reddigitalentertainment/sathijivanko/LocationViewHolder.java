package com.reddigitalentertainment.sathijivanko;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LocationViewHolder extends RecyclerView.ViewHolder {

    public TextView location;
    public LinearLayout layout;

    public LocationViewHolder(@NonNull View itemView) {
        super(itemView);

        location = itemView.findViewById(R.id.location_holder);
        layout = itemView.findViewById(R.id.locationLayout_locationHolder);
    }
}
