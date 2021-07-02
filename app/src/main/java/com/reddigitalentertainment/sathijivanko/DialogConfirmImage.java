package com.reddigitalentertainment.sathijivanko;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;

public class DialogConfirmImage extends AppCompatDialogFragment {

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    String chatUserId;
    Bitmap bitmap;

    public DialogConfirmImage(String chatUserId, Bitmap bitmap) {
        this.chatUserId = chatUserId;
        this.bitmap = bitmap;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_confirm_image,null);
        builder.setView(view);

        setStyle(DialogFragment.STYLE_NO_TITLE,R.style.AppTheme);

        PhotoView mainImage = view.findViewById(R.id.clickedPhoto_cI);

        ImageView closeButton = view.findViewById(R.id.closeButton_cI);

        closeButton.setOnClickListener(v -> dismiss());

        if(bitmap!=null){
            Glide.with(getContext()).load(bitmap).into(mainImage);
        }
        else {
            Toast.makeText(getContext(),"No Image.",Toast.LENGTH_LONG).show();
        }

        Button confirmButton =view.findViewById(R.id.confirmButton_cI);

        Button anotherButton = view.findViewById(R.id.anotherButton_cI);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImage(bitmap);
            }
        });

        anotherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return builder.create();

    }


    private void sendImage(Bitmap bitmap){
        String fileName = "ChatImages/"+"post_"+new Date().getTime();
        dismiss();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,75,baos);
        byte[] byteData = baos.toByteArray();
        final StorageReference finalFileName = FirebaseStorage.getInstance().getReference().child(fileName);
        finalFileName.putBytes(byteData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                finalFileName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        DatabaseReference finalDatabase = FirebaseDatabase.getInstance().getReference().child("Chats");
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("sender",SathiUserHolder.getSathiUser().getUserId());
                        hashMap.put("receiver",chatUserId);
                        hashMap.put("message",url);
                        hashMap.put("messageTime",new Date().getTime());
                        hashMap.put("type","image");
                        hashMap.put("seen","false");
                        finalDatabase.push().setValue(hashMap);
                    }
                });
            }
        });
    }
}
