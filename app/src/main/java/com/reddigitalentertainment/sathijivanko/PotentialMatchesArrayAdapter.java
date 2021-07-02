package com.reddigitalentertainment.sathijivanko;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class PotentialMatchesArrayAdapter extends ArrayAdapter<PotentialMatchesInfo> {

    Context context;

    public PotentialMatchesArrayAdapter(Context context, int resourceId, List<PotentialMatchesInfo> potentialMatchesInfoList){
        super(context,resourceId,potentialMatchesInfoList);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        PotentialMatchesInfo potentialMatchesInfo = getItem(position);

        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.swipe_items,parent, false);
        }

        TextView userName = convertView.findViewById(R.id.swipe_item_userNameText);
        ImageView userImage = convertView.findViewById(R.id.swipe_item_userImage);
        TextView userBio = convertView.findViewById(R.id.swipe_item_userBioText);
        TextView userAge = convertView.findViewById(R.id.swipe_item_userAgeText);
        CardView cardView = convertView.findViewById(R.id.swipe_item_cardView);
        ImageView verified = convertView.findViewById(R.id.verifiedIcon_swipeItem);


        userName.setText(potentialMatchesInfo.getUserName());
        if(potentialMatchesInfo.getDisplayPicture()!=null){
            if(position==0){
                Glide.with(getContext()).load(Uri.parse(potentialMatchesInfo.getDisplayPicture())).centerCrop().into(userImage);
            }
            else {
                Glide.with(getContext()).load(Uri.parse(potentialMatchesInfo.getDisplayPicture()))
                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(25,3))).into(userImage);
            }
        }
        else {
            userImage.setImageResource(R.drawable.main_application_gradient_background);
        }
        userBio.setText(potentialMatchesInfo.getBio());
        if(potentialMatchesInfo.getShowAge().equals("true")){
            userAge.setText(potentialMatchesInfo.getAge());
        }
        if(potentialMatchesInfo.getVerified().equals("true")){
            verified.setVisibility(View.VISIBLE);
        }
        else {
            verified.setVisibility(View.GONE);
        }



        return convertView;
    }




}
