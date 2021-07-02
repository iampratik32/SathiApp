package com.reddigitalentertainment.sathijivanko;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VerifyUserActivity extends AppCompatActivity {

    String currentPhotoPath;
    Bitmap bitmap;
    ImageView imageView;
    Button openCamera;
    String toOpen = "";
    Button verifyButton;
    ProgressBar progressBar;

    private void openIt(){
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);

        if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImgFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                        "com.reddigitalentertainment.sathijivanko",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 2789);
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_user);

        findViewById(R.id.getSymbol).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Make yourself standout among others",Toast.LENGTH_SHORT).show();
            }
        });

        progressBar = findViewById(R.id.progressBar_verifyUser);

        findViewById(R.id.increaseMatches).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Increase your chances of getting more matches",Toast.LENGTH_SHORT).show();
            }
        });

        openCamera = findViewById(R.id.openCamera_verifyUser);
        imageView = findViewById(R.id.uploadPhoto_verifyUser);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ASD",toOpen);
                DialogViewImage dialogViewImage = new DialogViewImage(toOpen);
                dialogViewImage.show(getSupportFragmentManager(),"View");
            }
        });

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (!hasPermissions(VerifyUserActivity.this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(VerifyUserActivity.this, PERMISSIONS, 255 );
                    }
                    else {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                            ActivityCompat.requestPermissions(VerifyUserActivity.this, new String[] {Manifest.permission.CAMERA}, 47);
                        }
                        else {
                            openIt();
                        }
                    }
                }
                else {
                    openIt();
                }

            }
        });

        verifyButton = findViewById(R.id.requestVerificationButton);
        verifyButton.setVisibility(View.GONE);

        DatabaseReference checkVerify = FirebaseDatabase.getInstance().getReference().child("Verifications").child(SathiUserHolder.getSathiUser().getUserId());
        checkVerify.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    verifyButton.setVisibility(View.VISIBLE);
                    verifyButton.setText("Verification is Sent");
                    verifyButton.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ImageView closeBtn = findViewById(R.id.closeButton_verifyUser);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.GONE);
                openCamera.setEnabled(false);
                verifyButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                closeBtn.setEnabled(false);
                imageView.setEnabled(false);
                DatabaseReference verifyDb = FirebaseDatabase.getInstance().getReference().child("Verifications");
                StorageReference uploadVerification = FirebaseStorage.getInstance().getReference().child("Verifications").child(SathiUserHolder.getSathiUser().getUserId());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,75,baos);
                byte[] byteData = baos.toByteArray();
                uploadVerification.putBytes(byteData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadVerification.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                verifyDb.child(SathiUserHolder.getSathiUser().getUserId()).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.setVisibility(View.VISIBLE);
                                        openCamera.setEnabled(true);
                                        verifyButton.setEnabled(true);
                                        imageView.setEnabled(true);
                                        progressBar.setVisibility(View.GONE);
                                        closeBtn.setEnabled(true);
                                        Toast.makeText(getApplicationContext(),"Verification Request Sent.",Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });


                            }
                        });
                    }
                });
            }
        });


    }

    private File createImgFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        try {
            super.onActivityResult(requestCode,resultCode,data);

            if(requestCode==2789){
                if(resultCode==RESULT_OK){
                    File file = new File(currentPhotoPath);
                    int file_size = Integer.parseInt(String.valueOf(file.length()/1024));
                    if(file_size!=0){
                        bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                        openImage(bitmap);
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        toOpen = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap, "Title", null);
                    }
                }

            }
        }
        catch (NullPointerException e ){

        }
    }

    private void openImage(Bitmap bitmap) {
        Glide.with(getApplicationContext()).load(bitmap).into(imageView);
        imageView.setVisibility(View.VISIBLE);
        openCamera.setText("Change Picture");
        verifyButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 255: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openIt();
                } else {
                    Toast.makeText(VerifyUserActivity.this, "Sathi not allowed to write in your storage.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
