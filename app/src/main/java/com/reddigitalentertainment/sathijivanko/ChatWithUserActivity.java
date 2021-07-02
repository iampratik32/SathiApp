package com.reddigitalentertainment.sathijivanko;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatWithUserActivity extends AppCompatActivity {

    private ImageView sendButton;
    private CircleImageView userImageTop;
    private EditText messageBox;
    ChatAdapter chatAdapter;
    List<Chat> chatList;
    String currentPhotoPath;
    TextView confirmAction;
    ImageView confirmImage;
    DatabaseReference seenDb;
    private String chatUserId;
    RecyclerView chatRecyclerView;
    BottomSheetDialog menuBottomSheetDialog, chooseMediaBottomSheetDialog, confirmSheetDialog;
    LinearLayout unmatchOption, reportOption, cameraOption, galleryOption, cancelPictureOption, confirmLayout;
    public static final int CHAT_CAMERA_REQUEST = 1984;
    public static final int CHAT_GALLERY_REQUEST = 1985;
    Bitmap bitmap;
    private String forChatUserId, forChatUserName;
    private ProgressBar progressBar;
    ValueEventListener seenListener;
    String userGender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_user);
        SathiUserHolder.setChatLifeCycle(true);

        Bundle getBundle = getIntent().getExtras();
        String userName = "";
        String userId = "";
        String userImage = "";
        if(getBundle!=null){
            userName = getBundle.getString("UserName");
            userId = getBundle.getString("UserId");
            chatUserId = userId;
            forChatUserName = userName;
            userGender=getBundle.getString("UserGender");
            userImage = getBundle.getString("ImageLink");
        }
        final String finalUserId = userId;
        chatUserId = finalUserId;
        final String finalUserName = userName;

        FirebaseMessaging.getInstance().subscribeToTopic(SathiUserHolder.getSathiUser().getUserId()+"-"+chatUserId);

        String tempId = userId;
        ImageView chatMenu = findViewById(R.id.chat_menu);
        seenDb = FirebaseDatabase.getInstance().getReference().child("Chats");
        chatMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(ChatWithUserActivity.this).inflate(R.layout.dialog_chat_menu,null);
                unmatchOption = view.findViewById(R.id.unmatchOption_chatMenu);
                reportOption = view.findViewById(R.id.reportOption_chatMenu);
                menuBottomSheetDialog = new BottomSheetDialog(ChatWithUserActivity.this);
                menuBottomSheetDialog.setContentView(view);
                menuBottomSheetDialog.show();
                progressBar = view.findViewById(R.id.progressBar_chatMenu);
                unmatchOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        menuBottomSheetDialog.dismiss();
                        View view2 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_chat_confirm,null);
                        confirmAction = view2.findViewById(R.id.action_chat_confirm);
                        confirmImage = view2.findViewById(R.id.actionImg_chat_confirm);
                        confirmSheetDialog = new BottomSheetDialog(ChatWithUserActivity.this);
                        confirmSheetDialog.setContentView(view2);
                        confirmSheetDialog.show();
                        confirmLayout = view2.findViewById(R.id.layout_confirm);
                        PleaseWaitDialog dialog = new PleaseWaitDialog();
                        dialog.setCancelable(false);

                        confirmAction.setText("Unmatch "+forChatUserName+"?");
                        Glide.with(getApplicationContext()).load(R.drawable.heart_broken).into(confirmImage);
                        confirmLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmSheetDialog.dismiss();
                                CheckUtility.unMatchUsers(chatUserId);
                                dialog.show(getSupportFragmentManager(),"Wait");
                                CountDownTimer timer = new CountDownTimer(3000,1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        for(int i=0;i<SathiUserHolder.getSathiUser().getChatUsers().size();i++){
                                            if(chatUserId.equals(SathiUserHolder.getSathiUser().getChatUsers().get(i))){
                                                SathiUserHolder.getSathiUser().getChatUsers().remove(i);
                                                break;
                                            }
                                        }
                                        CheckUtility.deleteConversations(SathiUserHolder.getSathiUser().getUserId(),chatUserId);
                                    }

                                    @Override
                                    public void onFinish() {
                                        SathiUserHolder.getUnMatchAdapter().notifyDataSetChanged();
                                        Intent intent = new Intent(ChatWithUserActivity.this,MainActivity.class);
                                        intent.putExtra("Open","Chat");
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        SathiUserHolder.setLoadChat(true);
                                        dialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Successfully UnMatched.",Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                        finish();
                                    }
                                }.start();
                            }
                        });
                    }
                });
                reportOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        menuBottomSheetDialog.dismiss();
                        View view2 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_chat_confirm,null);
                        confirmAction = view2.findViewById(R.id.action_chat_confirm);
                        confirmImage = view2.findViewById(R.id.actionImg_chat_confirm);
                        confirmSheetDialog = new BottomSheetDialog(ChatWithUserActivity.this);
                        confirmSheetDialog.setContentView(view2);
                        confirmSheetDialog.show();
                        confirmLayout = view2.findViewById(R.id.layout_confirm);
                        PleaseWaitDialog dialog = new PleaseWaitDialog();
                        dialog.setCancelable(false);
                        confirmAction.setText("Report "+forChatUserName+"?");
                        Glide.with(getApplicationContext()).load(R.drawable.report_icon).into(confirmImage);

                        confirmLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogReportUser dialogReportUser = new DialogReportUser(ChatWithUserActivity.this,chatUserId,forChatUserName,userGender);
                                confirmSheetDialog.dismiss();
                                dialogReportUser.show(getSupportFragmentManager(),"Report");
                            }
                        });
                    }
                });
            }
        });

        ImageView cameraIcon = findViewById(R.id.chooseMedia_chatActivity);
        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(ChatWithUserActivity.this).inflate(R.layout.dialog_chat_choose_media,null);
                cameraOption = view.findViewById(R.id.cameraOption_chooseMenu);
                galleryOption = view.findViewById(R.id.galleryOption_chooseMenu);
                cancelPictureOption = view.findViewById(R.id.cancelOption_chooseMenu);
                cancelPictureOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseMediaBottomSheetDialog.dismiss();
                    }
                });
                cameraOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCamera();
                    }
                });
                galleryOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(ChatWithUserActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1000);
                        }
                        else {
                            openGallery();
                        }
                    }
                });
                chooseMediaBottomSheetDialog = new BottomSheetDialog(ChatWithUserActivity.this);
                chooseMediaBottomSheetDialog.setContentView(view);
                chooseMediaBottomSheetDialog.show();
            }
        });



        userImageTop = findViewById(R.id.chatWithUser_userProfilePhoto);
        if(userImage!=null && !userImage.isEmpty()){
            Glide.with(getApplicationContext()).load(Uri.parse(userImage)).thumbnail(Glide.with(getApplicationContext()).load(R.drawable.loading).centerCrop()).into(userImageTop);
        }
        String finalUserImage = userImage;
        userImageTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userProfile = new Intent(v.getContext(),MatchesProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("UserId",finalUserId);
                bundle.putString("UserName",finalUserName);
                bundle.putString("UserGender",userGender);
                bundle.putString("Display", finalUserImage);
                bundle.putString("Hide", "Chat");
                userProfile.putExtras(bundle);
                v.getContext().startActivity(userProfile);
            }
        });

        TextView userNameTextView = findViewById(R.id.chatWithUser_userName);
        userNameTextView.setText(userName);

        ImageView backButton = findViewById(R.id.chatWithUser_backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(linearLayoutManager);

        sendButton = findViewById(R.id.chatWithUser_sendButton);
        messageBox = findViewById(R.id.chatWithUser_sendMessageEditText);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!messageBox.getText().toString().trim().isEmpty()){
                    Handler myHandler = new Handler();
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendMessage(SathiUserHolder.getSathiUser().getUserId(),finalUserId, messageBox.getText().toString(), new Date().getTime());
                            messageBox.setText("");
                        }
                    },250);
                }
            }
        });

        readMessages(SathiUserHolder.getSathiUser().getUserId(),finalUserId,userImage);
        seenMessage(finalUserId);

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
                    startActivityForResult(takePictureIntent, CHAT_CAMERA_REQUEST);
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
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent,"Select File"),CHAT_GALLERY_REQUEST);
    }

    private void seenMessage(final String userId){
        seenListener = seenDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Chat chat = postSnapshot.getValue(Chat.class);
                    try {
                        if(chat.getReceiver().equals(SathiUserHolder.getSathiUser().getUserId()) && chat.getSender().equals(userId)){
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("seen","true");
                            postSnapshot.getRef().updateChildren(hashMap);
                        }
                    }
                    catch (NullPointerException e){

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message, long messageTime){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Chat chat = new Chat(sender,receiver,message,messageTime,"text","false");
        databaseReference.child("Chats").push().setValue(chat);

    }

    private void readMessages(final String userId, final String senderId, final String imageUrl){
        chatList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    try{
                        Chat chat = postSnapshot.getValue(Chat.class);
                        if(chat!=null){
                            try {
                                if(chat.getReceiver().equals(userId) && chat.getSender().equals(senderId) || chat.getReceiver().equals(senderId) && chat.getSender().equals(userId)){
                                    chat.setKey(postSnapshot.getKey());
                                    chatList.add(chat);
                                }
                            }
                            catch (NullPointerException e){

                            }
                        }
                    }
                    catch (DatabaseException e){

                    }

                }
                chatAdapter = new ChatAdapter(ChatWithUserActivity.this,chatList,imageUrl);
                chatRecyclerView.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        try {
            super.onActivityResult(requestCode,resultCode,data);
            if(requestCode==CHAT_GALLERY_REQUEST){
                if(data!=null){
                    Uri selectedUri = data.getData();
                    try{
                        Bitmap srcBmp = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(selectedUri), null, null);
                        chooseMediaBottomSheetDialog.dismiss();
                        DialogConfirmImage confirmImage = new DialogConfirmImage(chatUserId,srcBmp);
                        confirmImage.show(getSupportFragmentManager(),"Confirm?");

                    }
                    catch (IOException e){

                    }
                }
            }
            else if(requestCode==CHAT_CAMERA_REQUEST){
                if(resultCode==RESULT_OK){
                    File file = new File(currentPhotoPath);
                    int file_size = Integer.parseInt(String.valueOf(file.length()/1024));
                    if(file_size!=0){
                        bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                        DialogConfirmImage confirmImage = new DialogConfirmImage(chatUserId,bitmap);
                        confirmImage.show(getSupportFragmentManager(),"Confirm?");
                        chooseMediaBottomSheetDialog.dismiss();
                    }
                }

            }
        }
        catch (NullPointerException e ){

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        SathiUserHolder.setChatLifeCycle(true);
    }

    @Override
    protected void onPause() {
        SathiUserHolder.setChatLifeCycle(false);
        seenDb.removeEventListener(seenListener);
        super.onPause();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SathiUserHolder.setChatLifeCycle(false);
    }
}
