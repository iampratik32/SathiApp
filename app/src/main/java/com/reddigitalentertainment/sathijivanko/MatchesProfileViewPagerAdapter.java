package com.reddigitalentertainment.sathijivanko;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

public class MatchesProfileViewPagerAdapter extends PagerAdapter {

    private Context context;
    private int noOfImages;
    private LayoutInflater layoutInflater;
    private String userId;
    private String userGender;
    private LinkedList listOfMedia;
    private int check =0;
    private View view;
    private MatchesProfileActivity matchesProfileActivity;

    public MatchesProfileActivity getMatchesProfileActivity() {
        return matchesProfileActivity;
    }

    public void setMatchesProfileActivity(MatchesProfileActivity matchesProfileActivity) {
        this.matchesProfileActivity = matchesProfileActivity;
    }

    private LinkedList checkSize = new LinkedList();

    public MatchesProfileViewPagerAdapter(Context context, int noOfImages, String userId, String userGender) {
        this.context = context;
        this.noOfImages = noOfImages;
        this.userId = userId;
        this.userGender = userGender;
    }

    @Override
    public int getCount() {
        return noOfImages;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.extra_information_slidelayout,null,false);
        ImageView images = view.findViewById(R.id.extraInformation_slideImage);
        listOfMedia = new LinkedList();
        LoadImagesAsyncTask loadImagesAsyncTask = new LoadImagesAsyncTask();
        loadImagesAsyncTask.execute(images);
        container.addView(view);
        return view;
    }
    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    }

    private class LoadImagesAsyncTask extends AsyncTask<ImageView,Integer,Integer>{
        @Override
        protected Integer doInBackground(final ImageView... imageViews) {

            DatabaseReference imagesDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Images");
            imagesDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                            if(postSnapshot.child("Link").exists() && postSnapshot.child("Link")!=null){
                                String imageName = postSnapshot.child("Link").getValue().toString();
                                if(!checkSize.contains(imageName)){
                                    checkSize.add(imageName);
                                    Glide.with(context).load(Uri.parse(imageName)).thumbnail(Glide.with(context).load(R.drawable.loading)).into(imageViews[0]);
                                    imageViews[0].setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            DialogViewImage dialogViewImage = new DialogViewImage(imageName);
                                            try {
                                                dialogViewImage.show(((AppCompatActivity)context).getSupportFragmentManager(),"ViewImage");
                                            }
                                            catch (ClassCastException e){
                                                try {
                                                    dialogViewImage.show(matchesProfileActivity.getSupportFragmentManager(),"ViewImage");
                                                }
                                                catch (NullPointerException e2){
                                                }
                                            }
                                        }
                                    });
                                    break;
                                }
                            }
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
