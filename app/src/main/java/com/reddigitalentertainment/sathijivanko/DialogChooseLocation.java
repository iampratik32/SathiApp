package com.reddigitalentertainment.sathijivanko;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DialogChooseLocation extends AppCompatDialogFragment {
    private Context context;
    RecyclerView recyclerView;
    private LocationChangeAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> allLocations = new ArrayList<>();
    private TextView lookingCity;

    public TextView getLookingCity() {
        return lookingCity;
    }

    public void setLookingCity(TextView lookingCity) {
        this.lookingCity = lookingCity;
    }

    public DialogChooseLocation(Context context) {
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_choose_location,null);
        builder.setView(view);

        adapter = new LocationChangeAdapter(allLocations, getContext());
        FindAllLocations allLocations = new FindAllLocations();
        allLocations.execute();

        ImageView closeButton = view.findViewById(R.id.closeButton_chooseLocation);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView fromT = view.findViewById(R.id.from_chooseLocation);
        ProgressBar progressBar = view.findViewById(R.id.progressBar_chooseLocation);

        CountDownTimer loadingTimer = new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                recyclerView = view.findViewById(R.id.recyclerView_chooseLocation);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                fromT.setVisibility(View.VISIBLE);
                recyclerView.setNestedScrollingEnabled(true);
                recyclerView.setHasFixedSize(true);
                recyclerView.setItemViewCacheSize(20);
                recyclerView.setDrawingCacheEnabled(true);
                recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                adapter.setThisDialog(DialogChooseLocation.this);
                adapter.setLookingCity(lookingCity);
                recyclerView.setAdapter(adapter);

            }
        }.start();

        return builder.create();
    }

    public class FindAllLocations extends AsyncTask<Integer,Integer,Integer> {
        @Override
        protected Integer doInBackground(Integer... integers) {
            DatabaseReference getAllLocations = FirebaseDatabase.getInstance().getReference().child("Locations");
            getAllLocations.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot post:dataSnapshot.getChildren()){
                            allLocations.add(post.getKey());
                            adapter.notifyDataSetChanged();
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
}
