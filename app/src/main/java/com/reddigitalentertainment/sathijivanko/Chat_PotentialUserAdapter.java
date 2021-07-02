package com.reddigitalentertainment.sathijivanko;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ConcurrentModificationException;
import java.util.List;

public class Chat_PotentialUserAdapter extends RecyclerView.Adapter<Chat_PotentialUserHolder>{

    private List<Chat_PotentialChatUser> userList;
    private List<Chat_PotentialChatUser> copyList;
    private Context context;
    BottomSheetDialog confirmSheetDialog;
    LinearLayout confirmLayout;
    TextView confirmAction;
    ImageView confirmImage;

    public Chat_PotentialUserAdapter(List<Chat_PotentialChatUser> userList, Context context) {
        this.userList = userList;
        this.context = context;
        this.copyList=SathiUserHolder.getSathiUser().getChatUsers();
    }

    @NonNull
    @Override
    public Chat_PotentialUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_potentail_user_holder,null,false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(layoutParams);
        Chat_PotentialUserHolder holder = new Chat_PotentialUserHolder(layoutView);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull Chat_PotentialUserHolder holder, int position) {
        holder.userName.setText(userList.get(position).getName());
        holder.userId.setText(userList.get(position).getUserId());
        holder.userGender.setText(userList.get(position).getUserGender());
        holder.userDisplayPicture.setImageDrawable(null);
        if(userList.get(position).getProfileImage()!=null){
            Glide.with(context).load(Uri.parse(userList.get(position).getProfileImage())).thumbnail(Glide.with(context).load(R.drawable.loading).centerCrop()).into(holder.userDisplayPicture);
            holder.imageLink.setText(userList.get(position).getProfileImage());
        }
        else {
            holder.userDisplayPicture.setImageResource(R.drawable.temp_image);
        }

        if(userList.get(position).getVerified().equals("true")){
            holder.verifiedIcon.setVisibility(View.VISIBLE);
        }
        else {
            holder.verifiedIcon.setVisibility(View.GONE);
        }

        Chat_PotentialUserAdapter thisAd = this;

        holder.cardView.setOnClickListener(v -> {
            Intent chatIntent = new Intent(v.getContext(),ChatWithUserActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("UserId",userList.get(position).getUserId());
            bundle.putString("UserName",userList.get(position).getName());
            bundle.putString("ImageLink",userList.get(position).getProfileImage());
            bundle.putString("UserGender",userList.get(position).getUserGender());
            SathiUserHolder.setUnMatchAdapter(thisAd);
            chatIntent.putExtras(bundle);
            Handler myHandler = new Handler();
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    v.getContext().startActivity(chatIntent);
                }
            },100);
        });

        holder.cardView.setOnLongClickListener(v -> {
            PopupMenu menu;
            try{
                menu = new PopupMenu(context,holder.cardView, Gravity.END);
            }
            catch (Exception e){
                menu = new PopupMenu(context,holder.cardView);
            }
            menu.getMenuInflater().inflate(R.menu.chat_list_menu,menu.getMenu());

            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    View view2 = LayoutInflater.from(context).inflate(R.layout.dialog_chat_confirm,null);
                    confirmAction = view2.findViewById(R.id.action_chat_confirm);
                    confirmImage = view2.findViewById(R.id.actionImg_chat_confirm);
                    confirmSheetDialog = new BottomSheetDialog(context);
                    confirmSheetDialog.setContentView(view2);
                    confirmSheetDialog.show();
                    confirmLayout = view2.findViewById(R.id.layout_confirm);
                    PleaseWaitDialog dialog = new PleaseWaitDialog();
                    dialog.setCancelable(false);
                    FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();

                    switch (item.getItemId()) {
                        case R.id.deleteC_listMenu:
                            confirmAction.setText("Delete Conversation?");
                            Glide.with(context).load(R.drawable.trash_icon).into(confirmImage);
                            confirmLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String myId = SathiUserHolder.getSathiUser().getUserId();
                                    String thatId = userList.get(position).getUserId();
                                    CheckUtility.deleteConversations(myId,thatId);
                                    confirmSheetDialog.dismiss();
                                    dialog.show(manager,"Wait");
                                    CountDownTimer timer = new CountDownTimer(2000,1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                        }

                                        @Override
                                        public void onFinish() {
                                            dialog.dismiss();
                                            Toast.makeText(context,"Conversation with " +userList.get(position).getName()+ " Deleted.",Toast.LENGTH_SHORT).show();
                                        }
                                    }.start();
                                }
                            });
                            return true;
                        case R.id.unMatch_listMenu:
                            confirmAction.setText("Unmatch "+userList.get(position).getName()+"?");
                            Glide.with(context).load(R.drawable.heart_broken).into(confirmImage);
                            confirmLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    confirmSheetDialog.dismiss();
                                    CheckUtility.unMatchUsers(userList.get(position).getUserId());
                                    dialog.show(manager,"Wait");
                                    CountDownTimer timer = new CountDownTimer(3000,1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                        }

                                        @Override
                                        public void onFinish() {
                                            userList.remove(position);
                                            notifyDataSetChanged();
                                            dialog.dismiss();
                                            Toast.makeText(context,"UnMatched",Toast.LENGTH_SHORT).show();
                                            CheckUtility.deleteConversations(SathiUserHolder.getSathiUser().getUserId(),userList.get(position).getUserId());
                                        }
                                    }.start();
                                }
                            });
                            return true;
                        case R.id.report_listMenu:
                            confirmAction.setText("Report "+userList.get(position).getName()+"?");
                            Glide.with(context).load(R.drawable.report_icon).into(confirmImage);
                            confirmLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    confirmSheetDialog.dismiss();
                                    DialogReportUser dialogReportUser = new DialogReportUser(context,userList.get(position).getUserId()
                                    ,userList.get(position).getName(),userList.get(position).getUserGender());
                                    dialogReportUser.show(manager,"Report");
                                }
                            });
                            return true;

                        case R.id.userProfile_listMenu:
                            confirmSheetDialog.dismiss();
                            Chat_PotentialChatUser cu = userList.get(position);
                            Intent userProfile = new Intent(v.getContext(),MatchesProfileActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("UserId",cu.getUserId());
                            bundle.putString("UserName",cu.getName());
                            bundle.putString("UserGender",cu.getUserGender());
                            bundle.putString("Display", cu.getProfileImage());
                            bundle.putString("Hide", "Chat");
                            userProfile.putExtras(bundle);
                            v.getContext().startActivity(userProfile);

                            default:
                            return false;
                    }
                }
            });
            menu.show();
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void filter(String constraint){
        try {
            userList.clear();
            if(constraint.length()==0 || constraint.trim().isEmpty()){
                userList.addAll(SathiUserHolder.getSathiUser().getChatUsers());
                notifyDataSetChanged();
            }
            else {
                for(Chat_PotentialChatUser c:SathiUserHolder.getSathiUser().getChatUsers()){
                    if(c.getName().toLowerCase().contains(constraint)){
                        userList.add(c);
                    }
                }
                notifyDataSetChanged();
            }
        }
        catch (ConcurrentModificationException exception){
            //filter(constraint);
        }

    }
}
