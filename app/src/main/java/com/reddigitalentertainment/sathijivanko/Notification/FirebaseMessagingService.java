package com.reddigitalentertainment.sathijivanko.Notification;

import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.RemoteMessage;
import com.reddigitalentertainment.sathijivanko.SathiUserHolder;
import com.reddigitalentertainment.sathijivanko.SplashScreenActivity;

import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        if(remoteMessage.getData()!=null){
            Map<String,String> data = remoteMessage.getData();
            String text = data.get("text");
            String title = data.get("title");


            if(title.equals("It's a Match.")){
                try{
                    SathiUserHolder.setLoadChat(true);
                }
                catch (Exception e){
                }
                DisplayNotification.matchesNotify(getApplicationContext(),title,text);
            }
            else {
                if(!SathiUserHolder.isChatLifeCycle()){
                    String gender = data.get("s_gender");
                    String imageLink = data.get("s_image");
                    String uId = data.get("s_id");
                    DisplayNotification.chatNotify(getApplicationContext(),title,text,uId,gender,imageLink);
                }
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }


}
