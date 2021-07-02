package com.reddigitalentertainment.sathijivanko;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class CheckUtility {
    public static boolean checkLocation(Context context){
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;
        try{
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch (Exception e){}
        try{
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch (Exception e){}

        if(gpsEnabled && networkEnabled){
            return true;
        }
        else {
            return false;
        }
    }

    public static void getLocation(Context context) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(context)
                .requestLocationUpdates(locationRequest,new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(this);
                        if(locationResult!=null && locationResult.getLocations().size()>0){
                            int latestIndex = locationResult.getLocations().size()-1;
                            double latitude = locationResult.getLocations().get(latestIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestIndex).getLongitude();
                            SathiUserHolder.setUserAddress(latitude+","+longitude);
                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(context, Locale.ENGLISH);
                            try {
                                addresses = geocoder.getFromLocation(latitude,longitude,1);
                                String country = addresses.get(0).getCountryName();
                                SathiUserHolder.setUserCountry(country);
                                if(country.equals("Nepal")){
                                    String region = addresses.get(0).getSubAdminArea();
                                    SathiUserHolder.setUserZone(region);
                                }
                                else {
                                    String city = addresses.get(0).getSubAdminArea();
                                    SathiUserHolder.setUserCity(city);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }, Looper.getMainLooper());
    }

    public static void delTempUser(Context context){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(context,"You have to register again to continue",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });
    }

    public static void deleteConversations(String myId, String thatId){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    try{
                        Chat chat = postSnapshot.getValue(Chat.class);
                        if(chat!=null){
                            try {
                                if(chat.getReceiver().equals(myId) && chat.getSender().equals(thatId) || chat.getReceiver().equals(thatId) && chat.getSender().equals(myId)){
                                    databaseReference.child(postSnapshot.getKey()).removeValue();
                                    if(chat.getType().equals("image")){
                                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(chat.getMessage());
                                        storageReference.delete();
                                    }
                                }
                            }
                            catch (NullPointerException e){

                            }
                        }
                    }
                    catch (DatabaseException e){

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void unMatchUsers(String tempId){
        DatabaseReference thisUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId())
                .child("Matches");
        thisUserDb.child("Matched").child(tempId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                thisUserDb.child("Likes").child(tempId).removeValue();
                DatabaseReference otherDb = FirebaseDatabase.getInstance().getReference().child("Users").child(tempId).child("Matches").child("Matched")
                        .child(SathiUserHolder.getSathiUser().getUserId());
                otherDb.removeValue();
                List<Chat_PotentialChatUser> chatUsers = SathiUserHolder.getSathiUser().getChatUsers();
                for(int i=0;i<chatUsers.size();i++){
                    if(chatUsers.get(i).getUserId().equals(tempId)){
                        chatUsers.remove(i);
                        break;
                    }
                }
            }
        });
    }
}
