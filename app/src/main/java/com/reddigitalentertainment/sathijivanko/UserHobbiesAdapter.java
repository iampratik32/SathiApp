package com.reddigitalentertainment.sathijivanko;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserHobbiesAdapter extends RecyclerView.Adapter<UserHobbiesHolder> {

    private List<String> hobbiesList;
    private Context context;

    public String getHide() {
        return hide;
    }

    public void setHide(String hide) {
        this.hide = hide;
    }

    private String hide;

    public UserHobbiesAdapter(List<String> hobbiesList, Context context) {
        this.hobbiesList=hobbiesList;
        this.context=context;
    }


    @NonNull
    @Override
    public UserHobbiesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_hobbies_list,null,false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutView.setLayoutParams(layoutParams);
        UserHobbiesHolder holder = new UserHobbiesHolder(layoutView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserHobbiesHolder holder, final int position) {

        if(position==0){
            if(!hide.equals("Add")){
                holder.hobbyName.setText("Add New");
                holder.addButton.setImageResource(R.drawable.add_button);
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogAddExtra dialogAddExtra = new DialogAddExtra(context,hobbiesList,"Add",UserHobbiesAdapter.this,position);
                        dialogAddExtra.show(((AppCompatActivity)context).getSupportFragmentManager(),"Add");
                    }
                });
            }
            else {
                holder.cardView.setVisibility(View.GONE);
            }
        }
        else {
            if(hide.equals("Add")){
                holder.addButton.setVisibility(View.GONE);
            }
            else {
                holder.addButton.setVisibility(View.VISIBLE);
            }
            if(!hobbiesList.get(position-1).equals("NullValue")){
                holder.hobbyName.setText(hobbiesList.get(position-1));
                holder.addButton.setImageResource(R.drawable.edit_second);
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!hide.equals("Add")){
                            DialogAddExtra dialogAddExtra = new DialogAddExtra(context,hobbiesList,hobbiesList.get(position-1),UserHobbiesAdapter.this,position);
                            dialogAddExtra.show(((AppCompatActivity)context).getSupportFragmentManager(),"Edit");
                        }
                    }
                });
            }
            else {
                hobbiesList.remove(position-1);
            }
        }
    }

    @Override
    public int getItemCount() {
        int s = hobbiesList.size()+1;
        return s;
    }
}
