package com.reddigitalentertainment.sathijivanko;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SettingsFragment extends Fragment{
    private Switch showMe;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        ImageView backButton = view.findViewById(R.id.backButton_SettingsFragment);

        showMe = view.findViewById(R.id.showMeSwitch_settingsFragment);
        if(SathiUserHolder.getSathiUser().getShowme().equals("false")){
            showMe.setChecked(false);
        }
        else {
            showMe.setChecked(true);
        }

        backButton.setOnClickListener(v -> getActivity().finish());

        CardView accountSettings = view.findViewById(R.id.accountSettingsCardView_SettingsFragment);
        CardView locationSettings = view.findViewById(R.id.locationCardView_SettingsFragment);
        CardView shareSathi = view.findViewById(R.id.shareCardView_SettingsFragment);
        CardView contactUs = view.findViewById(R.id.contactUsCardView_SettingsFragment);
        CardView logOut = view.findViewById(R.id.logOutCardView_SettingsFragment);
        CardView deleteAccount = view.findViewById(R.id.deleteAccountCardView_SettingsFragment);

        TextView contactDetail = view.findViewById(R.id.contactUsTextView);

        contactUs.setOnClickListener(v -> {
            if(contactDetail.getVisibility()==View.VISIBLE){
                contactDetail.setVisibility(View.GONE);
            }
            else {
                contactDetail.setVisibility(View.VISIBLE);
            }
        });

        deleteAccount.setOnClickListener(v -> {
            Fragment newFragment = new DeleteAccountFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout_SettingsActivity,newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        logOut.setOnClickListener(v -> {
            LoginManager.getInstance().logOut();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            Intent intent = new Intent(getActivity(),LoginActivity.class);
            SathiUserHolder.setSathiUser(null);
            SathiUserHolder.setTotalLoad(0);
            SathiUserHolder.setPotentialLists(null);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        accountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SathiUserHolder.getSathiUser().getAuthCredential()==null){
                    VerifyPasswordDialog verifyPasswordDialog = new VerifyPasswordDialog(getContext());
                    verifyPasswordDialog.show(getActivity().getSupportFragmentManager(),"VerifyPassword");
                }
                else {
                    Fragment newFragment = new AccountSettingsFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout_SettingsActivity,newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }

            }
        });

        locationSettings.setOnClickListener(v -> {
            Fragment newFragment = new LocationSettingsFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout_SettingsActivity,newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        shareSathi.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Find me on Sathi. Download Sathi from,\nhttps://play.google.com/store/apps/details?id=com.reddigitalentertainment.sathijivanko&hl=en");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        });

        TextView communityGuidelines = view.findViewById(R.id.cg_settingsFragment);
        TextView safety = view.findViewById(R.id.s_settingsFragment);

        communityGuidelines.setOnClickListener(v -> openDocuments("CG"));

        safety.setOnClickListener(v -> openDocuments("S"));



        return view;
    }

    private void openDocuments(String type){
        Fragment newFragment = new DocumentsFragment(type);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout_SettingsActivity,newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            DatabaseReference changeShowing = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId()).child("ShowMe");
            if(showMe.isChecked() && SathiUserHolder.getSathiUser().getShowme().equals("false")){
                SathiUserHolder.getSathiUser().setShowme("true");
                changeShowing.setValue("true");
            }
            else if(!showMe.isChecked() && SathiUserHolder.getSathiUser().getShowme().equals("true")){
                SathiUserHolder.getSathiUser().setShowme("false");
                changeShowing.setValue("false");
            }
        }
        catch (NullPointerException e){

        }
    }
}
