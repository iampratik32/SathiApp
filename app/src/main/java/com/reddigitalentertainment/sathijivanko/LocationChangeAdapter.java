package com.reddigitalentertainment.sathijivanko;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class LocationChangeAdapter extends RecyclerView.Adapter<LocationViewHolder> {

    private ArrayList<String> allLocations;
    private Context context;
    private DialogChooseLocation thisDialog;

    public TextView getLookingCity() {
        return lookingCity;
    }

    public void setLookingCity(TextView lookingCity) {
        this.lookingCity = lookingCity;
    }

    private TextView lookingCity;

    public DialogChooseLocation getThisDialog() {
        return thisDialog;
    }

    public void setThisDialog(DialogChooseLocation thisDialog) {
        this.thisDialog = thisDialog;
    }

    public LocationChangeAdapter(ArrayList<String> allLocations, Context context) {
        this.allLocations = allLocations;
        this.context = context;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.locations_holder,null,false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(layoutParams);
        LocationViewHolder holder = new LocationViewHolder(layoutView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        if(position % 2 == 0){
            holder.layout.setBackgroundResource(R.color.darkishWhite);
        }
        else {
            holder.layout.setBackgroundResource(R.color.colorForGradientBack);
        }

        holder.location.setText(allLocations.get(position));
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisDialog.dismiss();
                PleaseWaitDialog dialog = new PleaseWaitDialog();
                FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
                dialog.show(manager,"Wait");
                DatabaseReference changeDb = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId());
                changeDb.child("LookingIn").setValue(allLocations.get(position)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        SathiUserHolder.getSathiUser().setLookingIn(allLocations.get(position));
                        lookingCity.setText(allLocations.get(position));
                        SathiUserHolder.setShownPeople(new ArrayList<PotentialMatchesInfo>());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return allLocations.size();
    }
}
