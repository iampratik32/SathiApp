package com.reddigitalentertainment.sathijivanko;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    public static final int REQUEST_CODE = 10001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SathiUserHolder.setProfileFragmentView(view);

        final CardView settingsButton = view.findViewById(R.id.cardView_settingsProfile);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getActivity(),SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
        CircleImageView profilePhoto = view.findViewById(R.id.user_profilePhoto);
        if(SathiUserHolder.getSathiUser()!=null){
            if(SathiUserHolder.getSathiUser().getDisplayPicture()!=null){
                Glide.with(getContext()).load(Uri.parse(SathiUserHolder.getSathiUser().getDisplayPicture()))
                        .thumbnail(Glide.with(getContext()).load(R.drawable.loading).centerCrop()).into(profilePhoto);
            }
        }

        //final ViewPager viewPager = view.findViewById(R.id.viewPager_profileFragment);
        ImageView noPhoto = view.findViewById(R.id.noPhoto_viewPagerPF);
        Glide.with(getContext()).load(SathiUserHolder.getSathiUser().getDisplayPicture()).centerCrop().
                apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))).into(noPhoto);
        noPhoto.setVisibility(View.VISIBLE);

        if(SathiUserHolder.getSathiUser().getUserImages()!=null && SathiUserHolder.getSathiUser().getUserImages().size()!=0){
            try{
                MatchesProfileViewPagerAdapter matchesProfileViewPagerAdapter = new MatchesProfileViewPagerAdapter(getContext(),(int) SathiUserHolder.getSathiUser().getUserImages().size(),SathiUserHolder.getSathiUser().getUserId(),SathiUserHolder.getSathiUser().getGender());
                //viewPager.setAdapter(matchesProfileViewPagerAdapter);
            }
            catch (NullPointerException e){
                //viewPager.setVisibility(View.GONE);
                noPhoto.setVisibility(View.VISIBLE);
            }
        }
        else {
            //viewPager.setVisibility(View.GONE);
            Glide.with(getContext()).load(SathiUserHolder.getSathiUser().getDisplayPicture()).
                    apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))).into(noPhoto);
            noPhoto.setVisibility(View.VISIBLE);
        }


        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogViewImage dialogViewImage = new DialogViewImage(SathiUserHolder.getSathiUser().getDisplayPicture());
                dialogViewImage.show(getFragmentManager(),"ViewImage");
            }
        });

        profilePhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                DialogDisplayPicture dialogDisplayPicture = new DialogDisplayPicture(SathiUserHolder.getSathiUser().getDisplayPicture(),view,"P");
                dialogDisplayPicture.show(getFragmentManager(),"DisplayPictureDialog");
                return true;
            }
        });

        CardView cameraButton = view.findViewById(R.id.cardView_addPhotos);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHandleMedia dialog = new DialogHandleMedia();
                dialog.show(getFragmentManager(),"Dialog");
                SathiUserHolder.setProfileContext(getContext());
                //SathiUserHolder.setProfilePager(viewPager);
            }
        });

        CardView editProfileButton = view.findViewById(R.id.cardView_profileFragment);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

        TextView nameOfUser = view.findViewById(R.id.profileFragment_nameOfUser);
        TextView getGoing = view.findViewById(R.id.getGoing_pF);
        ImageView verifyIcon = view.findViewById(R.id.verified_profileFragment);
        SathiUser currentSathiUser = SathiUserHolder.getSathiUser();
        if(currentSathiUser!=null){
            nameOfUser.setText(currentSathiUser.getName()+", "+currentSathiUser.getAge());
            if(currentSathiUser.getVerified().equals("true")){
                Glide.with(getContext()).load(R.drawable.verified).into(verifyIcon);
                getGoing.setVisibility(View.GONE);
            }
            else {
                Glide.with(getContext()).load(R.drawable.not_verified).into(verifyIcon);
                getGoing.setVisibility(View.VISIBLE);
            }
        }

        getGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent verifyUser = new Intent(getContext(),VerifyUserActivity.class);
                startActivity(verifyUser);
            }
        });

        CardView viewOffers = view.findViewById(R.id.viewOfferCardView_profileFragment);
        viewOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),SplashScreenActivity.class));
                getActivity().finish();
            }
        });



        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId()).child("Verified");
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString().equals("true")){
                    SathiUserHolder.getSathiUser().setVerified("true");
                    Glide.with(getContext()).load(R.drawable.verified).into(verifyIcon);
                    getGoing.setVisibility(View.GONE);
                }
                else {
                    SathiUserHolder.getSathiUser().setVerified("false");
                    Glide.with(getContext()).load(R.drawable.not_verified).into(verifyIcon);
                    getGoing.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

}
