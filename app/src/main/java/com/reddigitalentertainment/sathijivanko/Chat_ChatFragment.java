package com.reddigitalentertainment.sathijivanko;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Chat_ChatFragment extends Fragment {
    private RecyclerView chatRecylerView;
    private Chat_PotentialUserAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Chat_PotentialChatUser> chatUsers = new ArrayList<>();
    private LinkedList<String> tempLinkedList = new LinkedList<String>();
    ProgressBar progressBar;
    TextView emptyChat;
    int count = 0;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_chat__chat, container, false);
        chatRecylerView = view.findViewById(R.id.messagesRecyclerView_chatFragment);
        progressBar = view.findViewById(R.id.progressBar_chat);

        if(SathiUserHolder.getSathiUser().getChatUsers()!=null){
            create();
            adapter.notifyDataSetChanged();
        }
        chatRecylerView.setVisibility(View.VISIBLE);


        return view;
    }

    private List<Chat_PotentialChatUser> getMatches(){
        return chatUsers;
    }


    private void create(){
        chatRecylerView.setNestedScrollingEnabled(true);
        emptyChat = view.findViewById(R.id.chatIsEmpty);
        chatRecylerView.setHasFixedSize(true);
        chatRecylerView.setItemViewCacheSize(20);
        chatRecylerView.setDrawingCacheEnabled(true);
        chatRecylerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        layoutManager = new LinearLayoutManager(getContext());
        chatRecylerView.setLayoutManager(layoutManager);
        if(SathiUserHolder.isLoadChat()){
            chatUsers.clear();
            progressBar.setVisibility(View.VISIBLE);
            SathiUserHolder.getSathiUser().setChatUsers(null);
            CountDownTimer timer = new CountDownTimer(2000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    KeepInsideHolder holder = new KeepInsideHolder();
                    holder.getChatUsers();
                }

                @Override
                public void onFinish() {
                    addInAdapter();
                    SathiUserHolder.setLoadChat(false);
                }
            }.start();
        }
        else {
            Handler myHandler = new Handler();
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addInAdapter();
                }
            },100);
        }

        adapter = new Chat_PotentialUserAdapter(getMatches(), getContext());
        adapter.setHasStableIds(true);
        androidx.appcompat.widget.SearchView searchView  = view.findViewById(R.id.searchView_chatUsers);
        searchView.setQueryHint("Search Matched Sathi");
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

        chatRecylerView.setAdapter(adapter);
    }

    private void addInAdapter(){
        try {
            if(SathiUserHolder.getSathiUser().getChatUsers().size()==0){
                emptyChat.setVisibility(View.VISIBLE);
            }
            else {
                for(int i=0;i<SathiUserHolder.getSathiUser().getChatUsers().size();i++){
                    if(!tempLinkedList.contains(SathiUserHolder.getSathiUser().getChatUsers().get(i).getUserId())){
                        chatUsers.add(SathiUserHolder.getSathiUser().getChatUsers().get(i));
                        tempLinkedList.add(SathiUserHolder.getSathiUser().getUserId());
                    }
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                chatRecylerView.setVisibility(View.VISIBLE);
            }
        }
        catch (NullPointerException e){
        }
    }
}
