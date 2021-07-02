package com.reddigitalentertainment.sathijivanko;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

public class SliderAdapter_PotentialMatches extends PagerAdapter {

    private Context context;
    private List<PotentialMatchesInfo> rowItem;
    private LayoutInflater layoutInflater;
    LinkedList listOfMedia;
    private LinkedList checkSize = new LinkedList();

    public SliderAdapter_PotentialMatches(Context context, List<PotentialMatchesInfo> rowItem){
        this.context =context;
        this.rowItem = rowItem;
    }

    @Override
    public int getCount() {
        return rowItem.get(0).getListOfImages();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.extra_information_slidelayout,null,false);
        ImageView images = view.findViewById(R.id.extraInformation_slideImage);
        listOfMedia = new LinkedList();
        LoadMediaAsyncTask loadMediaAsyncTask = new LoadMediaAsyncTask();
        loadMediaAsyncTask.execute(images);
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




    private class LoadMediaAsyncTask extends AsyncTask<ImageView,Integer,LinkedList> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(LinkedList s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected LinkedList doInBackground(final ImageView... imageViews) {
            final DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(rowItem.get(0).getGender()).child(rowItem.get(0).getUserId()).child("Images");
            userDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                            if(postSnapshot.child("Link").exists() && postSnapshot.child("Link")!=null){
                                String imageName = postSnapshot.child("Link").getValue().toString();
                                if(!checkSize.contains(imageName)){
                                    checkSize.add(imageName);
                                    Glide.with(context).load(Uri.parse(imageName)).into(imageViews[0]);
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
            return listOfMedia;
        }
    }

}
