package com.reddigitalentertainment.sathijivanko;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class DialogDisplayPicture extends AppCompatDialogFragment {
    private static final int CAMERA_REQUEST = 1335;
    private static final int GALLERY_REQUEST = 1235;
    CircleImageView chosenImage;
    LinearLayout chooseLayout;
    LinearLayout displayLayout;
    Button doneButton;
    Bitmap bitmap;
    private String imageUrl;
    String currentPhotoPath;
    private StorageReference mStorageRef;
    private DatabaseReference databaseReference;
    ProgressBar progressBar;
    View thatView;
    String type;
    String what = "";

    public DialogDisplayPicture(String imageUrl,View thatView,String type) {
        this.imageUrl = imageUrl;
        this.thatView = thatView;
        this.type=type;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_display_picture,null);
        builder.setView(view);

        mStorageRef = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getUid());

        ImageView closeButton = view.findViewById(R.id.closeButton_displayPicture);
        progressBar = view.findViewById(R.id.progressBar_dialogDisplay);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        chosenImage = view.findViewById(R.id.chosenDisplayPicture);
        chooseLayout = view.findViewById(R.id.choosePhotoLayout_displayPicture);
        displayLayout = view.findViewById(R.id.displayPictureLayout_displayPicture);
        CardView openCamera = view.findViewById(R.id.chooseFromCamera_displayPicture);
        CardView openGallery = view.findViewById(R.id.chooseFromGallery_displayPicture);
        doneButton = view.findViewById(R.id.doneButton_displayPicture);
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraIntent();

            }
        });
        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},GALLERY_REQUEST);
                }
                else {
                    openGalleryIntent();
                }
            }
        });

        chosenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(what.equals("Camera")){
                    openCameraIntent();
                }
                else if(what.equals("Gallery")){
                    openGalleryIntent();
                }
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmap!=null){
                    progressBar.setVisibility(View.VISIBLE);
                    doneButton.setEnabled(false);
                    setCancelable(false);
                    closeButton.setVisibility(View.GONE);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,90,baos);
                    byte[] byteData = baos.toByteArray();
                    final StorageReference finalFileName = mStorageRef.child("DisplayPicture");
                    finalFileName.putBytes(byteData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            finalFileName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    Log.d("Type",type);
                                    if(!type.equals("FE")){
                                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId());
                                        DatabaseReference finalDatabase = databaseReference.child("DisplayPicture");
                                        finalDatabase.removeValue();
                                        finalDatabase.setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    UpdateFeed updateFeed = new UpdateFeed();
                                                    updateFeed.execute();
                                                    SathiUserHolder.getSathiUser().setDisplayPicture(uri.toString());
                                                    if(type.equals("P")){
                                                        CircleImageView imageView = thatView.findViewById(R.id.user_profilePhoto);
                                                        Glide.with(getApplicationContext()).load(uri).thumbnail(Glide.with(getApplicationContext()).load(R.drawable.loading)).into(imageView);
                                                    }
                                                    else if(type.equals("E")){
                                                        CircleImageView imageView = thatView.findViewById(R.id.displayImage_editProfileFragment);
                                                        Glide.with(getApplicationContext()).load(uri).thumbnail(Glide.with(getApplicationContext()).load(R.drawable.loading)).into(imageView);
                                                        View theView = SathiUserHolder.getProfileFragmentView();
                                                        CircleImageView anotherImageView = theView.findViewById(R.id.user_profilePhoto);
                                                        Glide.with(getApplicationContext()).load(uri).thumbnail(Glide.with(getApplicationContext()).load(R.drawable.loading)).into(anotherImageView);
                                                    }
                                                    dismiss();
                                                }
                                                else {
                                                    progressBar.setVisibility(View.GONE);
                                                    doneButton.setEnabled(true);
                                                    setCancelable(true);
                                                    closeButton.setVisibility(View.VISIBLE);
                                                    Toast.makeText(getApplicationContext(),"Can't Change the Display Picture Now.",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                    else {
                                        Log.d("Type",type);
                                        CircleImageView imageView = thatView.findViewById(R.id.displayPicture_fourthFragment);
                                        Glide.with(getApplicationContext()).load(url).into(imageView);
                                        RegisterDetailHolder.getAllDetails().add(url);
                                        dismiss();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

        return builder.create();
    }

    private void openGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent,"Select File"),GALLERY_REQUEST);
    }

    private void openCameraIntent() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 47);
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
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        try {
            super.onActivityResult(requestCode,resultCode,data);
            if(requestCode==GALLERY_REQUEST){
                if(data!=null){
                    Uri selectedUri = data.getData();
                    CropImage.activity(selectedUri).setGuidelines(CropImageView.Guidelines.ON).setMultiTouchEnabled(true).setAspectRatio(1,1).start(getContext(),this);
                }
            }
            else if(requestCode==CAMERA_REQUEST){
                File file = new File(currentPhotoPath);
                int file_size = Integer.parseInt(String.valueOf(file.length()/1024));
                if(file_size!=0){
                    chooseLayout.setVisibility(View.GONE);
                    displayLayout.setVisibility(View.VISIBLE);
                    bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                    chosenImage.setImageBitmap(bitmap);
                    what="Camera";
                }
            }
            else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if(resultCode==RESULT_OK){
                    Uri resultUri = result.getUri();
                    try{
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                    }
                    catch (IOException e){
                    }
                    chosenImage.setImageURI(resultUri);
                    chooseLayout.setVisibility(View.GONE);
                    displayLayout.setVisibility(View.VISIBLE);
                    what="Gallery";
                }
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }
        catch (NullPointerException e ){

        }
    }

    private class UpdateFeed extends AsyncTask<Integer,Integer,Integer>{
        @Override
        protected Integer doInBackground(Integer... integers) {
            if(SathiUserHolder.getSathiUser()!=null){
                if(SathiUserHolder.getSathiUser().getChatUsers()!=null){
                    List<Chat_PotentialChatUser> feedUsers = SathiUserHolder.getSathiUser().getChatUsers();
                    for(int i=0;i<feedUsers.size();i++){
                        String userId = feedUsers.get(i).getUserId();
                        DatabaseReference feedDatabase = FirebaseDatabase.getInstance().getReference().child("Feeds").child(userId);
                        Map newMap = new HashMap();
                        newMap.put("Gender",SathiUserHolder.getSathiUser().getGender());
                        newMap.put("Time",new Date().getTime());
                        newMap.put("User",SathiUserHolder.getSathiUser().getUserId());
                        newMap.put("What","ChangePicture");
                        feedDatabase.push().setValue(newMap);
                    }
                }
            }

            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        {
            if(requestCode==GALLERY_REQUEST){
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openGalleryIntent();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Gallery Permission Denied.",Toast.LENGTH_SHORT).show();
                }
            }
            if (requestCode == 47) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCameraIntent();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Camera Permission Denied.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
