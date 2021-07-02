package com.reddigitalentertainment.sathijivanko;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;

    private Context context;
    private List<Chat> chatList;
    private String imageUrl;
    private ChatWithUserActivity thatActivity;

    public ChatWithUserActivity getThatActivity() {
        return thatActivity;
    }

    public void setThatActivity(ChatWithUserActivity thatActivity) {
        this.thatActivity = thatActivity;
    }

    BottomSheetDialog messageDialog,confirmDialog;
    LinearLayout copyLayout, deleteLayout, saveLayout,confirmLayout;
    TextView confirmAction;
    ImageView actionImage;
    String type2 ="left";

    public ChatAdapter(Context context, List<Chat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==MSG_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_sender_message,parent,false);
            type2="right";
            return new ChatAdapter.ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_receiver_message,parent,false);
            type2 = "left";
            return new ChatAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatAdapter.ViewHolder holder, int position) {

        final Chat chat = chatList.get(position);
        if(chat!=null){
            holder.showMessage.setText(chat.getMessage());
            String type = chat.getType();
            SimpleDateFormat formatter= new SimpleDateFormat("MM/dd 'at' HH:mm");
            Date date = new Date(chat.getMessageTime());//+60 time try
            holder.chatTime.setText(formatter.format(date));
            holder.chatTime.setVisibility(View.GONE);

            if(imageUrl!=null && !imageUrl.isEmpty()){
                Glide.with(context).load(Uri.parse(imageUrl)).thumbnail(Glide.with(context).load(R.drawable.loading)).apply(RequestOptions.bitmapTransform(new RoundedCorners(14))).into(holder.profilePhoto);
            }

            holder.showMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.chatTime.getVisibility()==View.VISIBLE){
                        holder.chatTime.setVisibility(View.GONE);
                    }
                    else {
                        holder.chatTime.setVisibility(View.VISIBLE);
                    }
                }
            });
            holder.chatPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogViewImage dialogViewImage = new DialogViewImage(chat.getMessage());
                    dialogViewImage.show(((AppCompatActivity) context).getSupportFragmentManager(),"ViewImage");
                }
            });

            holder.chatPhoto.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(holder.chatTime.getVisibility()==View.VISIBLE){
                        holder.chatTime.setVisibility(View.GONE);
                    }
                    else {
                        showMenu("P",position);
                        holder.chatTime.setVisibility(View.VISIBLE);
                    }
                    return true;
                }
            });

            try{
                //Toast.makeText(context,chatList.get(position-1).getMessageTime())
            }
            catch (Exception e){

            }

            holder.showMessage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showMenu("M",position);
                    return true;
                }
            });

            if(type==null){
                holder.showMessage.setVisibility(View.VISIBLE);
                holder.chatPhoto.setVisibility(View.GONE);
            }
            else if(type.equals("image")){
                holder.chatPhoto.setVisibility(View.VISIBLE);
                holder.showMessage.setVisibility(View.GONE);
                Glide.with(context).load(Uri.parse(chat.getMessage())).into(holder.chatPhoto);
            }
            else {
                holder.showMessage.setVisibility(View.VISIBLE);
                holder.chatPhoto.setVisibility(View.GONE);
            }
            try{
                if(position == chatList.size()-1){
                    if(chat.getSeen().equals("true")){
                        holder.seenMessage.setText("Seen");
                    }
                    else {
                        holder.seenMessage.setText("Delivered");
                    }
                }
                else {
                    holder.seenMessage.setVisibility(View.GONE);
                }
            }
            catch (NullPointerException e){

            }
        }


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView showMessage;
        public ImageView profilePhoto;
        public TextView chatTime;
        public TextView userDisplayImage;
        public ImageView chatPhoto;
        public TextView seenMessage;
        public ProgressBar progressBar;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView){
            super(itemView);
            showMessage = itemView.findViewById(R.id.chat_message);
            chatTime = itemView.findViewById(R.id.chatTime_chatLayout);
            chatPhoto = itemView.findViewById(R.id.chat_sendingImage);
            profilePhoto = itemView.findViewById(R.id.chatReceiver_profileImage);
            userDisplayImage = itemView.findViewById(R.id.userImage_chatLayout);
            seenMessage = itemView.findViewById(R.id.textSeenMessage);
            progressBar = itemView.findViewById(R.id.progressBar_forChat);
            progressBar.setVisibility(View.GONE);
            relativeLayout = itemView.findViewById(R.id.relativeLayout_chat);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if(chatList.get(position).getSender().equals(SathiUserHolder.getSathiUser().getUserId())){
            return MSG_RIGHT;
        }
        else {
            return MSG_LEFT;
        }
    }

    private void showMenu(String type, int position){
        View view2 = LayoutInflater.from(context).inflate(R.layout.dialog_message,null);
        messageDialog = new BottomSheetDialog(context);
        messageDialog.setContentView(view2);
        messageDialog.show();
        copyLayout = view2.findViewById(R.id.dialogMessage_copy);
        deleteLayout = view2.findViewById(R.id.dialogMessage_delete);
        saveLayout = view2.findViewById(R.id.dialogMessage_save);
        LinearLayout confirmL = view2.findViewById(R.id.layout_confirm);

        if(type.equals("M")){
            saveLayout.setVisibility(View.GONE);
            copyLayout.setVisibility(View.VISIBLE);
        }
        else if(type.equals("P")){
            saveLayout.setVisibility(View.GONE);
            copyLayout.setVisibility(View.GONE);
            confirmL.setWeightSum(1);
        }


        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageDialog.dismiss();
                View view3 = LayoutInflater.from(context).inflate(R.layout.dialog_chat_confirm,null);
                confirmAction = view3.findViewById(R.id.action_chat_confirm);
                actionImage = view3.findViewById(R.id.actionImg_chat_confirm);
                confirmDialog = new BottomSheetDialog(context);
                confirmDialog.setContentView(view3);
                confirmDialog.show();
                confirmLayout = view3.findViewById(R.id.layout_confirm);

                confirmAction.setText("Delete Message?");
                Glide.with(context).load(R.drawable.trash_icon).into(actionImage);

                confirmLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = chatList.get(position).getMessage();
                        DatabaseReference chat = FirebaseDatabase.getInstance().getReference().child("Chats").child(chatList.get(position).getKey());
                        chat.removeValue();
                        chatList.remove(position);
                        notifyDataSetChanged();
                        confirmDialog.dismiss();
                        if(type.equals("P")){
                            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                            storageReference.delete();
                        }
                    }
                });
            }
        });

        copyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Message",chatList.get(position).getMessage());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(context,"Copied",Toast.LENGTH_SHORT).show();
                messageDialog.dismiss();
            }
        });

    }


}
