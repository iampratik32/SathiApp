package com.reddigitalentertainment.sathijivanko;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DialogReportUser extends AppCompatDialogFragment {

    private Context context;
    private String id,name,gender;
    private String reasonFor;
    private ProgressBar progressBar;
    private ImageView backButton, closeButton;

    public DialogReportUser(Context context, String id, String name, String gender) {
        this.context = context;
        this.id = id;
        this.name = name;
        this.gender = gender;
    }

    private LinearLayout firstLayout, secondLayout;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_report_user,null);
        builder.setView(view);

        progressBar = view.findViewById(R.id.progressBar_rD);

        firstLayout = view.findViewById(R.id.firstLayout_rD);
        secondLayout = view.findViewById(R.id.secondLayout_rD);

        secondLayout.setVisibility(View.GONE);

        TextView reportUser = view.findViewById(R.id.reportUser_rD);
        TextView wontTell = view.findViewById(R.id.wontTell_rD);

        String pronoun = "her";

        if(gender.equals("Man")){
            pronoun="him";
        }

        reportUser.setText("Report "+name+"?");
        wontTell.setText("We will not tell "+pronoun);

        closeButton = view.findViewById(R.id.closeButton_rD);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        CardView messageProb = view.findViewById(R.id.message_rD);
        CardView photoProb = view.findViewById(R.id.image_rD);
        CardView spamProb = view.findViewById(R.id.spam_rD);

        messageProb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSecond("Inappropriate Message");
            }
        });

        photoProb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSecond("Inappropriate Photo");
            }
        });

        spamProb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSecond("Probably Spam");
            }
        });

        Button reportButton = view.findViewById(R.id.report_rD);

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReport();
            }
        });

        backButton = view.findViewById(R.id.backButton_rd);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissSecond();
            }
        });

        return builder.create();
    }

    private void openSecond(String what){
        firstLayout.setVisibility(View.GONE);
        secondLayout.setVisibility(View.VISIBLE);
        reasonFor = what;
        backButton.setVisibility(View.VISIBLE);
    }

    private void dismissSecond(){
        firstLayout.setVisibility(View.VISIBLE);
        secondLayout.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
    }

    private void sendReport (){
        progressBar.setVisibility(View.VISIBLE);
        setCancelable(false);
        closeButton.setEnabled(false);
        backButton.setEnabled(false);
        DatabaseReference reportDb = FirebaseDatabase.getInstance().getReference().child("Reports");
        Map<String,String> reportMap = new HashMap<>();
        reportMap.put("ReportedBy",SathiUserHolder.getSathiUser().getUserId());
        reportMap.put("Reported",id);
        reportMap.put("Reason",reasonFor);
        reportDb.push().setValue(reportMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    dismiss();
                    Toast.makeText(context,name+" is Reported. Thank You.",Toast.LENGTH_LONG).show();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    setCancelable(true);
                    closeButton.setEnabled(true);
                    backButton.setEnabled(true);
                    Toast.makeText(context,"Couldn't Report this user at this moment.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
