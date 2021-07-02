package com.reddigitalentertainment.sathijivanko;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;


public class LocationSettingsFragment extends Fragment {

    ProgressBar updateLocationPB,lookingLocationPB;
    Button changeCurrentLocation, lookingLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_location_settings, container, false);

        ImageView backButton = view.findViewById(R.id.backButton_locationSettingsPage);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getFragmentManager().getBackStackEntryCount() > 0){
                    getFragmentManager().popBackStackImmediate();
                }
            }
        });



        updateLocationPB = view.findViewById(R.id.progressBar_upLocationSettings);
        lookingLocationPB = view.findViewById(R.id.progressBar_lfLocationSettings);
        lookingLocation = view.findViewById(R.id.updateLookingFor_settingsFragment);
        TextView currentCity = view.findViewById(R.id.currentLocation_locationSettings);
        currentCity.setText(SathiUserHolder.getSathiUser().getLocation());
        TextView lookingCity = view.findViewById(R.id.currentLookingIn_locationSettings);
        lookingCity.setText(SathiUserHolder.getSathiUser().getLookingIn());

        lookingLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogChooseLocation dialogChooseLocation = new DialogChooseLocation(getContext());
                dialogChooseLocation.setLookingCity(lookingCity);
                dialogChooseLocation.show(getFragmentManager(),"Choose");
            }
        });


        CardView lookingCardView = view.findViewById(R.id.lookingInCardView_SettingsFragment);
        lookingCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"You're Looking For the Sathi in "+SathiUserHolder.getSathiUser().getLookingIn(),Toast.LENGTH_SHORT).show();
            }
        });

        CardView cityCardView = view.findViewById(R.id.currentCityCardView);
        cityCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Your Current Location is "+SathiUserHolder.getSathiUser().getLocation(),Toast.LENGTH_SHORT).show();
            }
        });

        changeCurrentLocation = view.findViewById(R.id.updateLocation_settingsFragment);
        changeCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SathiUserHolder.getUserCountry()==null){
                    if(CheckUtility.checkLocation(getContext())){
                        Toast.makeText(getContext(),"Close Your Location and Try Again",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 25);
                    }
                    else {
                        Intent open = new Intent(getContext(),OpenLocationActivity.class);
                        open.putExtra("Temp","Change");
                        startActivityForResult(open,40);
                        Toast.makeText(getContext(),"Open Your Location.",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    if(SathiUserHolder.getUserCity()!=null){
                        if(SathiUserHolder.getUserCountry().equals("Nepal")){
                            DialogChooseDistrict dialogChooseDistrict = new DialogChooseDistrict(getApplicationContext());
                            dialogChooseDistrict.setZone(SathiUserHolder.getUserZone());
                            dialogChooseDistrict.setLocationCity(currentCity);
                            dialogChooseDistrict.show(getFragmentManager(),"District");
                        }
                        else {
                            if(SathiUserHolder.getUserCity().equals(SathiUserHolder.getSathiUser().getLocation())){
                                Toast.makeText(getContext(),"The current location is same as the new one.",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                PleaseWaitDialog dialog = new PleaseWaitDialog();
                                dialog.setCancelable(false);
                                dialog.show(getFragmentManager(),"Wait");
                                String prevLocation = SathiUserHolder.getSathiUser().getLocation();
                                String city = SathiUserHolder.getUserCountry()+","+SathiUserHolder.getUserCity();
                                SathiUserHolder.getSathiUser().setLocation(city);
                                DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId()).child("Location");
                                userDb.setValue(SathiUserHolder.getSathiUser().getLocation()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            DatabaseReference prevDb = FirebaseDatabase.getInstance().getReference().child("Locations").child(prevLocation)
                                                    .child(SathiUserHolder.getSathiUser().getUserId());
                                            prevDb.removeValue();
                                            DatabaseReference newDb = FirebaseDatabase.getInstance().getReference().child("Locations").child(SathiUserHolder.getSathiUser().getLocation())
                                                    .child(SathiUserHolder.getSathiUser().getUserId());
                                            newDb.setValue(SathiUserHolder.getSathiUser().getGender()+","+SathiUserHolder.getSathiUser().getPreference()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        dialog.dismiss();
                                                        currentCity.setText(SathiUserHolder.getSathiUser().getLocation());
                                                        Toast.makeText(getApplicationContext(),"Updated Successfully",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }
                                        else {
                                            Toast.makeText(getContext(),"Couldn't Update Location",Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    }
                                });
                                Toast.makeText(getContext(),SathiUserHolder.getUserCity(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else {
                        String zone = SathiUserHolder.getUserZone();
                        DialogChooseDistrict dialogChooseDistrict = new DialogChooseDistrict(getApplicationContext());
                        if(zone.equals("Bagmati") || zone.equals("बागमती")){
                            dialogChooseDistrict.setZone("Bagmati");
                            SathiUserHolder.setUserZone("Bagmati");
                        }
                        else if(zone.equals("Narayani") || zone.equals("नारायणी")){
                            dialogChooseDistrict.setZone("Narayani");
                            SathiUserHolder.setUserZone("Narayani");
                        }
                        else if(zone.equals("Lumbini") || zone.equals("लुम्बिनी")){
                            dialogChooseDistrict.setZone("Lumbini");
                            SathiUserHolder.setUserZone("Lumbini");
                        }
                        else if(zone.equals("Bheri") || zone.equals("भेरी")){
                            dialogChooseDistrict.setZone("Bheri");
                            SathiUserHolder.setUserZone("Bheri");
                        }
                        else if(zone.equals("Mahakali") || zone.equals("माहाकाली")){
                            dialogChooseDistrict.setZone("Mahakali");
                            SathiUserHolder.setUserZone("Mahakali");
                        }
                        else if(zone.equals("Dhawalagiri") || zone.equals("धौलागिरी")){
                            dialogChooseDistrict.setZone("Dhawalagiri");
                            SathiUserHolder.setUserZone("Dhawalagiri");
                        }
                        else if(zone.equals("Mechi") || zone.equals("मेची")){
                            dialogChooseDistrict.setZone("Mechi");
                            SathiUserHolder.setUserZone("Mechi");
                        }
                        else if(zone.equals("Gandaki") || zone.equals("गण्डकी")){
                            dialogChooseDistrict.setZone("Gandaki");
                            SathiUserHolder.setUserZone("Gandaki");
                        }
                        else if(zone.equals("Janakpur") || zone.equals("जनकपुर")){
                            dialogChooseDistrict.setZone("Janakpur");
                            SathiUserHolder.setUserZone("Janakpur");
                        }
                        else if(zone.equals("Rapti") || zone.equals("राप्ती")){
                            dialogChooseDistrict.setZone("Rapti");
                            SathiUserHolder.setUserZone("Rapti");
                        }
                        else if(zone.equals("Karnali") || zone.equals("कर्णाली")){
                            dialogChooseDistrict.setZone("Karnali");
                            SathiUserHolder.setUserZone("Karnali");
                        }
                        else if(zone.equals("Sagarmatha") || zone.equals("सगरमाथा")){
                            dialogChooseDistrict.setZone("Sagarmatha");
                            SathiUserHolder.setUserZone("Sagarmatha");
                        }
                        else if(zone.equals("Koshi") || zone.equals("कोशी")){
                            dialogChooseDistrict.setZone("Koshi");
                            SathiUserHolder.setUserZone("Koshi");
                        }
                        else {
                            dialogChooseDistrict.setZone("Seti");
                            SathiUserHolder.setUserZone("Seti");
                        }
                        dialogChooseDistrict.setLocationCity(currentCity);
                        dialogChooseDistrict.show(getFragmentManager(),"District");
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==40){
            CountDownTimer timer = new CountDownTimer(5000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    updateLocationPB.setVisibility(View.VISIBLE);
                    changeCurrentLocation.setVisibility(View.GONE);
                }

                @Override
                public void onFinish() {
                    changeCurrentLocation.setVisibility(View.VISIBLE);
                    updateLocationPB.setVisibility(View.GONE);
                    if(SathiUserHolder.getUserCity()==null){
                        Toast.makeText(getActivity(),"Try Again.",Toast.LENGTH_SHORT).show();
                    }
                }
            }.start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
