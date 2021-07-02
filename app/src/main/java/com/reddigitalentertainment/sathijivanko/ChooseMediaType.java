package com.reddigitalentertainment.sathijivanko;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;


public class ChooseMediaType extends AppCompatActivity {

    private StorageReference mStorageRef;
    private String currentPhotoPath;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    ArrayList<Uri> fileList = new ArrayList<Uri>();
    private Set<String> set = new HashSet<>();
    Integer REQUEST_CAMERA =1, SELECT_FILE=0;
    private LinkedList<String> listOfImagesList;
    private static final int CAMERA_PIC_REQUEST = 1337;
    int globalCount = 0;
    Button backButton;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_media_type);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.settingsColor));

        progressBar = findViewById(R.id.progressBar_chooseMedia);

        backButton = findViewById(R.id.goBackButton_chooseMedia);
        backButton.setOnClickListener(v -> finish());

        LinearLayout chooseFromCamera = findViewById(R.id.chooseFromCamera_linearLayout);
        LinearLayout chooseFromGallery = findViewById(R.id.chooseFromGallery_linearLayout);

        chooseFromCamera.setOnClickListener(v -> openCamera());
        chooseFromGallery.setOnClickListener(v -> {
            if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(ChooseMediaType.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1000);
            }
            else {
                openGallery();
            }
        });

    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 47);
        }
        else {
            Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
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
                    startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                }
            }
        }
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

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent,"Select File"),SELECT_FILE);
    }

    @Override
    public void onBackPressed() {
        if(progressBar.getVisibility()!=View.VISIBLE){
            super.onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public  void onActivityResult(int requestCode, int resultCode, final Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode== Activity.RESULT_OK) {
            backButton.setEnabled(false);
            mStorageRef = FirebaseStorage.getInstance().getReference(SathiUserHolder.getSathiUser().getUserId());
            if(requestCode== REQUEST_CAMERA){
                File file = new File(currentPhotoPath);
                int file_size = Integer.parseInt(String.valueOf(file.length()/1024));
                if(file_size!=0){
                    progressBar.setVisibility(View.VISIBLE);
                    Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,75,baos);
                    byte[] byteData = baos.toByteArray();
                    String dateAndTime = String.valueOf(Calendar.getInstance().getTime());
                    final StorageReference finalFileName = mStorageRef.child("File"+dateAndTime);
                    finalFileName.putBytes(byteData).addOnSuccessListener(taskSnapshot -> finalFileName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            final DatabaseReference finalDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId()).child("Images");
                            final HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("Link",url);
                            finalDatabaseReference.push().setValue(hashMap).addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    CountDownTimer countDownTimer = new CountDownTimer(1500,1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                        }
                                        @Override
                                        public void onFinish() {
                                            reloadImages();
                                            finish();
                                            MatchesProfileViewPagerAdapter matchesProfileViewPagerAdapter = new MatchesProfileViewPagerAdapter(SathiUserHolder.getProfileContext(),SathiUserHolder.getSathiUser().getUserImages().size(),SathiUserHolder.getSathiUser().getUserId(),SathiUserHolder.getSathiUser().getGender());
                                            SathiUserHolder.getProfilePager().setAdapter(matchesProfileViewPagerAdapter);
                                        }
                                    };
                                    countDownTimer.start();
                                }
                            });
                        }
                    }));
                }
            }
            else if(requestCode==SELECT_FILE){
                progressBar.setVisibility(View.VISIBLE);
                ClipData clipData = data.getClipData();
                if(clipData!=null){
                    int count = clipData.getItemCount();
                    int proposedCount=0;
                    if(SathiUserHolder.getSathiUser().getUserImages()!=null){
                        proposedCount = SathiUserHolder.getSathiUser().getUserImages().size()+count;
                    }
                    if(proposedCount>=9){
                        int r = 9-SathiUserHolder.getSathiUser().getUserImages().size();
                        Toast.makeText(getApplicationContext(),"Can't Upload "+count+" images. You can upload only "+r+" images more.",Toast.LENGTH_LONG).show();
                        return;
                    }
                    int i=0;
                    while (i<count){
                        Uri file = clipData.getItemAt(i).getUri();
                        fileList.add(file);
                        i++;
                    }
                    globalCount=count;
                    for(int j=0;j<fileList.size();j++){
                        Uri image = fileList.get(j);
                        uploadImage(image);
                    }
                }
                else if (data.getData() != null) {
                    Uri image = Uri.parse("content://media"+data.getData().getPath());
                    uploadImage(image);
                    globalCount=1;
                }
                setResult(Activity.RESULT_OK);
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId()).child("Images");
                CountDownTimer countDownTimer = new CountDownTimer(5000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        reloadImages();
                        finish();
                    }
                };
                countDownTimer.start();
            }
        }

    }

    private void uploadImage(Uri image){
        try{
            String scheme = image.getScheme();
            if(scheme.equals(ContentResolver.SCHEME_CONTENT)){

            }
            else if(scheme.equals(ContentResolver.SCHEME_FILE)){

            }
            InputStream is = getContentResolver().openInputStream(image);
            double inMb = is.available()/1000000;
            if(inMb>5.0){
                Toast.makeText(getApplicationContext(),"Images that are greater than 5Mb will not be uploaded.",Toast.LENGTH_LONG).show();
                Log.d("Size",String.valueOf(fileList.size()));
                if(fileList.size()==0){
                    finish();
                }
            }
            else {
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,40,baos);
                byte[] byteData = baos.toByteArray();
                final StorageReference finalFileName = mStorageRef.child("File"+image.getLastPathSegment());
                finalFileName.putBytes(byteData).addOnSuccessListener(taskSnapshot -> {
                    final DatabaseReference finalDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId()).child("Images");
                    final HashMap<String,String> hashMap = new HashMap<>();
                    finalFileName.getDownloadUrl().addOnSuccessListener(uri -> {
                        String url = uri.toString();
                        hashMap.put("Link",url);
                        finalDatabaseReference.push().setValue(hashMap);
                        fileList.clear();
                    });
                });
            }
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reloadImages(){
        final LinkedList <String> keepImages = new LinkedList<>();
        SathiUserHolder.getSathiUser().setUserImages(null);
        DatabaseReference allImages = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId()).child("Images");
        allImages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                        if(postSnapshot.child("Link").exists() && postSnapshot.child("Link")!=null){
                            String imageName = postSnapshot.child("Link").getValue().toString();
                            keepImages.add(imageName);
                        }
                    }
                    SathiUserHolder.getSathiUser().setUserImages(keepImages);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openGallery();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Couldn't Open Gallery.",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
