package com.reddigitalentertainment.sathijivanko.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.reddigitalentertainment.sathijivanko.ChatWithUserActivity;
import com.reddigitalentertainment.sathijivanko.MainActivity;
import com.reddigitalentertainment.sathijivanko.R;
import com.reddigitalentertainment.sathijivanko.SathiUserHolder;
import com.reddigitalentertainment.sathijivanko.SplashScreenActivity;

import java.io.IOException;
import java.net.URL;

import retrofit2.http.Url;

public class DisplayNotification {

    public static void chatNotify(Context context, String title, String body,String uId, String gender, String imageLink){
        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ context.getApplicationContext().getPackageName() + "/" + R.raw.tone);


        createChannel(context,NotificationManager.IMPORTANCE_HIGH,"Chat Notifications","Notifications for Messages","sathi_jivanko_chat");

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.sathi_logo_without_background);

        try {
            URL url = new URL(imageLink);
            largeIcon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
        }

        PendingIntent pendingIntent = null;
        try{
            Intent intent = new Intent(context, ChatWithUserActivity.class);
            intent.putExtra("Open","Chat");
            Bundle bundle = new Bundle();
            bundle.putString("UserId",uId);
            bundle.putString("UserName",title);
            bundle.putString("ImageLink",imageLink);
            bundle.putString("UserGender",gender);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context,79,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        }
        catch (Exception e){
            Intent tempIntent = new Intent(context,SplashScreenActivity.class);
            pendingIntent = PendingIntent.getActivity(context,79,tempIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"sathi_jivanko_chat")
                .setSmallIcon(R.drawable.sathi_logo_without_background)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLargeIcon(largeIcon)
                .setVibrate(new long[]{0,500,1000})
                .setSound(soundUri, AudioManager.STREAM_NOTIFICATION)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1,builder.build());
    }

    public static void matchesNotify(Context context, String title, String body){
        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ context.getApplicationContext().getPackageName() + "/" + R.raw.tone);


        createChannel(context,NotificationManager.IMPORTANCE_HIGH,"Matches Notifications","Show Notifications for Matches","sathi_jivanko_matches");

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.sathi_logo_without_background);
        Intent intent = new Intent(context, SplashScreenActivity.class);
        intent.putExtra("Load","Chat");


        PendingIntent pendingIntent = PendingIntent.getActivity(context,79,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"sathi_jivanko_chat")
                .setSmallIcon(R.drawable.sathi_logo_without_background)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLargeIcon(largeIcon)
                .setGroup(title+" group")
                .setVibrate(new long[]{0,500,1000})
                .setSound(soundUri, AudioManager.STREAM_NOTIFICATION)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1,builder.build());
    }

    private static void createChannel(Context context, int priority, String name, String description, String id){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ context.getApplicationContext().getPackageName() + "/" + R.raw.tone);
            NotificationChannel channel = new NotificationChannel(id, name, priority);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            channel.setLightColor(Color.parseColor("#970099"));
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setSound(soundUri,attributes);
            channel.setDescription(description);

            channel.setSound(soundUri, attributes);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

}
