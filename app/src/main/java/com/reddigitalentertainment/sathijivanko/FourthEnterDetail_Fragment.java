package com.reddigitalentertainment.sathijivanko;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class FourthEnterDetail_Fragment extends Fragment {

    PleaseWaitDialog dialog;
    String chosenUrl;

    FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_fourth_enter_detail_, container, false);

        Button chooseLocation = view.findViewById(R.id.selectCity_fourthFragment);
        if(SathiUserHolder.getUserCountry()==null){
            CheckUtility.getLocation(getContext());
            PleaseWaitDialog dialog = new PleaseWaitDialog();
            dialog.setCancelable(false);
            dialog.show(getFragmentManager(),"Wait");
            CountDownTimer timer = new CountDownTimer(4000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    dialog.dismiss();
                    if(SathiUserHolder.getUserCountry().equals("Nepal")){
                        chooseLocation.setVisibility(View.VISIBLE);
                    }
                    else {
                        chooseLocation.setVisibility(View.GONE);
                    }
                }
            }.start();

        }
        else {
            if(SathiUserHolder.getUserCountry().equals("Nepal")){
                chooseLocation.setVisibility(View.VISIBLE);
            }
            else {
                chooseLocation.setVisibility(View.GONE);
            }
        }

        chooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zone = SathiUserHolder.getUserZone();
                if(zone.equals("Bagmati") || zone.equals("बागमती")){
                    SathiUserHolder.setUserZone("Bagmati");
                }
                else if(zone.equals("Narayani") || zone.equals("नारायणी")){
                    SathiUserHolder.setUserZone("Narayani");
                }
                else if(zone.equals("Lumbini") || zone.equals("लुम्बिनी")){
                    SathiUserHolder.setUserZone("Lumbini");
                }
                else if(zone.equals("Bheri") || zone.equals("भेरी")){
                    SathiUserHolder.setUserZone("Bheri");
                }
                else if(zone.equals("Mahakali") || zone.equals("माहाकाली")){
                    SathiUserHolder.setUserZone("Mahakali");
                }
                else if(zone.equals("Dhawalagiri") || zone.equals("धौलागिरी")){
                    SathiUserHolder.setUserZone("Dhawalagiri");
                }
                else if(zone.equals("Mechi") || zone.equals("मेची")){
                    SathiUserHolder.setUserZone("Mechi");
                }
                else if(zone.equals("Gandaki") || zone.equals("गण्डकी")){
                    SathiUserHolder.setUserZone("Gandaki");
                }
                else if(zone.equals("Janakpur") || zone.equals("जनकपुर")){
                    SathiUserHolder.setUserZone("Janakpur");
                }
                else if(zone.equals("Rapti") || zone.equals("राप्ती")){
                    SathiUserHolder.setUserZone("Rapti");
                }
                else if(zone.equals("Karnali") || zone.equals("कर्णाली")){
                    SathiUserHolder.setUserZone("Karnali");
                }
                else if(zone.equals("Sagarmatha") || zone.equals("सगरमाथा")){
                    SathiUserHolder.setUserZone("Sagarmatha");
                }
                else if(zone.equals("Koshi") || zone.equals("कोशी")){
                    SathiUserHolder.setUserZone("Koshi");
                }
                else {
                    SathiUserHolder.setUserZone("Seti");
                }
                DialogChooseDistrict dialogChooseDistrict = new DialogChooseDistrict(getContext());
                dialogChooseDistrict.setZone(SathiUserHolder.getUserZone());
                dialogChooseDistrict.setType("R");
                dialogChooseDistrict.show(getFragmentManager(),"District");
            }
        });

        final Button nextButton = view.findViewById(R.id.newUserFourthNextButton);
        final TextInputEditText bioOfUser = view.findViewById(R.id.newUserBio_text);

        CircleImageView displayPic = view.findViewById(R.id.displayPicture_fourthFragment);
        displayPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogDisplayPicture dialogDisplayPicture = new DialogDisplayPicture("",view,"FE");
                dialogDisplayPicture.show(getFragmentManager(),"DisplayPictureDialog");
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String takenBio = bioOfUser.getText().toString().trim();
                final LinkedList holder = RegisterDetailHolder.getAllDetails();
                if(takenBio.isEmpty()){
                    bioOfUser.requestFocus();
                    bioOfUser.setError("Bio is Empty.");
                }
                else if(holder.size()!=8){
                    Toast.makeText(getContext(),"Select Display Picture",Toast.LENGTH_SHORT).show();
                }
                else {
                    if(SathiUserHolder.getUserCity()==null){
                        Toast.makeText(getContext(),"Select Your City.",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        dialog = new PleaseWaitDialog();
                        dialog.show(getFragmentManager(),"Dialog");
                        dialog.setCancelable(false);
                        nextButton.setEnabled(false);
                        firebaseAuth = FirebaseAuth.getInstance();
                        final FirebaseUser user = firebaseAuth.getCurrentUser();
                        String userId = user.getUid();
                        String currentCity = SathiUserHolder.getUserCity();
                        DatabaseReference currentUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                        final String c = currentCity;
                        Map newUser = new HashMap();
                        newUser.put("Name",holder.get(0).toString());
                        newUser.put("Email",holder.get(1).toString());
                        newUser.put("Gender",holder.get(3).toString());
                        newUser.put("Preference",holder.get(4).toString());
                        newUser.put("Age",holder.get(5).toString());
                        newUser.put("ShowAge",holder.get(6).toString());
                        newUser.put("Bio",takenBio);
                        newUser.put("DisplayPicture",holder.get(7));
                        newUser.put("status","1");
                        newUser.put("Verified",false);
                        newUser.put("ShowMe","false");
                        newUser.put("LookingIn",c);
                        newUser.put("Location",c);
                        currentUserDatabase.setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    DatabaseReference locationDb = FirebaseDatabase.getInstance().getReference().child("Locations").child(c).child(userId);
                                    locationDb.setValue(holder.get(3).toString()+","+holder.get(4).toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),"tempPassword");
                                                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            user.updatePassword(holder.get(2).toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        firebaseAuth.signOut();
                                                                        firebaseAuth.signInWithEmailAndPassword(holder.get(1).toString(),holder.get(2).toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                                if(task.isSuccessful()){
                                                                                    goToMainActivity();
                                                                                }
                                                                                else {
                                                                                    nextButton.setEnabled(true);
                                                                                    dialog.dismiss();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                    else {
                                                                        nextButton.setEnabled(true);
                                                                        dialog.dismiss();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                        else {
                                                            user.updatePassword(holder.get(2).toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        firebaseAuth.signOut();
                                                                        LoginManager.getInstance().logOut();
                                                                        firebaseAuth.signInWithEmailAndPassword(holder.get(1).toString(),holder.get(2).toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                                if(task.isSuccessful()){
                                                                                    goToMainActivity();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                                else {
                                    nextButton.setEnabled(true);
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                }
            }
        });

        return view;
    }

    private void goToMainActivity(){
        Intent newIntent = new Intent(getActivity(),NotVerifiedActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(newIntent);
        getActivity().finish();
        dialog.dismiss();
    }

}
