package com.reddigitalentertainment.sathijivanko;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import static com.reddigitalentertainment.sathijivanko.ProfileFragment.REQUEST_CODE;

public class DialogHandleMedia extends AppCompatDialogFragment {

    ImageView firstImage,secondImage,thirdImage,fourthImage,fifthImage,sixthImage,seventhImage,eighthImage,ninthmage;
    ImageView firstAddButton,secondAddButton,thirdAddButton,fourthAddButton,fifthAddButton,sixthAddButton,seventhAddButton,eighthAddButton,ninthAddButton;
    private LinkedList checkSize = new LinkedList();
    int previousSize=0;
    private View view;
    Button addPhotos;
    CardView firstCardView,secondCardView,thirdCardView,fourthCardView,fifthCardView,sixthCardView,seventhCardView,eighthCardView,ninthCardView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getContext(),R.color.colorForMainGradientBack));
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    dismiss();
                    return true;
                }
                else{
                    return false;
                }
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_handle_media,null);
        builder.setView(view);
        setStyle(DialogFragment.STYLE_NO_TITLE,R.style.AppTheme);

        initializeThings();

        view.findViewById(R.id.closeButton_handleMedia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if(SathiUserHolder.getSathiUser().getUserImages()!=null){
            final int noOfImages = SathiUserHolder.getSathiUser().getUserImages().size();
            firstAddButton.setOnClickListener(v -> {
                if(noOfImages>0){
                    String url = SathiUserHolder.getSathiUser().getUserImages().get(0);
                    SathiUserHolder.getSathiUser().getUserImages().remove(0);
                    DeleteImagesAsyncTask deleteImagesAsyncTask = new DeleteImagesAsyncTask();
                    deleteImagesAsyncTask.execute(url);
                    Glide.with(getContext()).load(R.drawable.add_button).into(firstAddButton);
                    Glide.with(getContext()).load(R.drawable.no_photo).into(firstImage);
                }
                else {
                    chooseMedia();
                }
            });

            secondAddButton.setOnClickListener(v -> {
                if(noOfImages>1){
                    String url = SathiUserHolder.getSathiUser().getUserImages().get(1);
                    SathiUserHolder.getSathiUser().getUserImages().remove(1);
                    DeleteImagesAsyncTask deleteImagesAsyncTask = new DeleteImagesAsyncTask();
                    deleteImagesAsyncTask.execute(url);
                    Glide.with(getContext()).load(R.drawable.add_button).into(secondAddButton);
                    Glide.with(getContext()).load(R.drawable.no_photo).into(secondImage);
                }
                else {
                    chooseMedia();
                }
            });
            thirdAddButton.setOnClickListener(v -> {
                if(noOfImages>2){
                    String url = SathiUserHolder.getSathiUser().getUserImages().get(2);
                    SathiUserHolder.getSathiUser().getUserImages().remove(2);
                    DeleteImagesAsyncTask deleteImagesAsyncTask = new DeleteImagesAsyncTask();
                    deleteImagesAsyncTask.execute(url);
                    Glide.with(getContext()).load(R.drawable.add_button).into(thirdAddButton);
                    Glide.with(getContext()).load(R.drawable.no_photo).into(thirdImage);
                }
                else {
                    chooseMedia();
                }
            });
            fourthAddButton.setOnClickListener(v -> {
                if(noOfImages>3){
                    String url = SathiUserHolder.getSathiUser().getUserImages().get(3);
                    SathiUserHolder.getSathiUser().getUserImages().remove(3);
                    DeleteImagesAsyncTask deleteImagesAsyncTask = new DeleteImagesAsyncTask();
                    deleteImagesAsyncTask.execute(url);
                    Glide.with(getContext()).load(R.drawable.add_button).into(fourthAddButton);
                    Glide.with(getContext()).load(R.drawable.no_photo).into(fourthImage);
                }
                else {
                    chooseMedia();
                }
            });
            fifthAddButton.setOnClickListener(v -> {
                if(noOfImages>4){
                    String url = SathiUserHolder.getSathiUser().getUserImages().get(4);
                    SathiUserHolder.getSathiUser().getUserImages().remove(4);
                    DeleteImagesAsyncTask deleteImagesAsyncTask = new DeleteImagesAsyncTask();
                    deleteImagesAsyncTask.execute(url);
                    Glide.with(getContext()).load(R.drawable.add_button).into(fifthAddButton);
                    Glide.with(getContext()).load(R.drawable.no_photo).into(fifthImage);
                }
                else {
                    chooseMedia();
                }
            });
            sixthAddButton.setOnClickListener(v -> {
                if(noOfImages>5){
                    String url = SathiUserHolder.getSathiUser().getUserImages().get(5);
                    SathiUserHolder.getSathiUser().getUserImages().remove(5);
                    DeleteImagesAsyncTask deleteImagesAsyncTask = new DeleteImagesAsyncTask();
                    deleteImagesAsyncTask.execute(url);
                    Glide.with(getContext()).load(R.drawable.add_button).into(sixthAddButton);
                    Glide.with(getContext()).load(R.drawable.no_photo).into(sixthImage);
                }
                else {
                    chooseMedia();
                }
            });
            seventhAddButton.setOnClickListener(v -> {
                if(noOfImages>6){
                    String url = SathiUserHolder.getSathiUser().getUserImages().get(6);
                    SathiUserHolder.getSathiUser().getUserImages().remove(6);
                    DeleteImagesAsyncTask deleteImagesAsyncTask = new DeleteImagesAsyncTask();
                    deleteImagesAsyncTask.execute(url);
                    Glide.with(getContext()).load(R.drawable.add_button).into(seventhAddButton);
                    Glide.with(getContext()).load(R.drawable.no_photo).into(seventhImage);
                }
                else {
                    chooseMedia();
                }
            });
            eighthAddButton.setOnClickListener(v -> {
                if(noOfImages>7){
                    String url = SathiUserHolder.getSathiUser().getUserImages().get(7);
                    SathiUserHolder.getSathiUser().getUserImages().remove(7);
                    DeleteImagesAsyncTask deleteImagesAsyncTask = new DeleteImagesAsyncTask();
                    deleteImagesAsyncTask.execute(url);
                    Glide.with(getContext()).load(R.drawable.add_button).into(eighthAddButton);
                    Glide.with(getContext()).load(R.drawable.no_photo).into(eighthImage);
                }
                else {
                    chooseMedia();
                }
            });
            ninthAddButton.setOnClickListener(v -> {
                if(noOfImages>8){
                    String url = SathiUserHolder.getSathiUser().getUserImages().get(8);
                    SathiUserHolder.getSathiUser().getUserImages().remove(8);
                    DeleteImagesAsyncTask deleteImagesAsyncTask = new DeleteImagesAsyncTask();
                    deleteImagesAsyncTask.execute(url);
                    Glide.with(getContext()).load(R.drawable.add_button).into(ninthAddButton);
                    Glide.with(getContext()).load(R.drawable.no_photo).into(ninthmage);
                }
                else {
                    chooseMedia();
                }
            });
        }


        addPhotos.setOnClickListener(v -> chooseMedia());
        firstCardView.setOnClickListener(v -> chooseMedia());
        secondCardView.setOnClickListener(v -> chooseMedia());
        thirdCardView.setOnClickListener(v -> chooseMedia());
        fourthCardView.setOnClickListener(v -> chooseMedia());
        fifthCardView.setOnClickListener(v -> chooseMedia());
        sixthCardView.setOnClickListener(v -> chooseMedia());
        seventhCardView.setOnClickListener(v -> chooseMedia());
        eighthCardView.setOnClickListener(v -> chooseMedia());
        ninthCardView.setOnClickListener(v -> chooseMedia());

        return builder.create();

    }

    private void initializeThings() {
        firstImage = view.findViewById(R.id.handleMedia_firstPhoto);
        secondImage = view.findViewById(R.id.handleMedia_secondPhoto);
        thirdImage = view.findViewById(R.id.handleMedia_thirdPhoto);
        fourthImage = view.findViewById(R.id.handleMedia_fourthPhoto);
        fifthImage = view.findViewById(R.id.handleMedia_fifthPhoto);
        sixthImage = view.findViewById(R.id.handleMedia_sixthPhoto);
        seventhImage = view.findViewById(R.id.handleMedia_seventhPhoto);
        eighthImage = view.findViewById(R.id.handleMedia_eighthPhoto);
        ninthmage = view.findViewById(R.id.handleMedia_ninthPhoto);

        firstAddButton = view.findViewById(R.id.first_addButton_dialog);
        secondAddButton = view.findViewById(R.id.second_addButton_dialog);
        thirdAddButton = view.findViewById(R.id.third_addButton_dialog);
        fourthAddButton = view.findViewById(R.id.fourth_addButton_dialog);
        fifthAddButton = view.findViewById(R.id.fifth_addButton_dialog);
        sixthAddButton = view.findViewById(R.id.sixth_addButton_dialog);
        seventhAddButton = view.findViewById(R.id.seventh_addButton_dialog);
        eighthAddButton = view.findViewById(R.id.eighth_addButton_dialog);
        ninthAddButton = view.findViewById(R.id.ninth_addButton_dialog);

        addPhotos = view.findViewById(R.id.addPhoto_handleMedia);
        firstCardView = view.findViewById(R.id.firstCardView_handleMedia);
        secondCardView = view.findViewById(R.id.secondCardView_handleMedia);
        thirdCardView = view.findViewById(R.id.thirdCardView_handleMedia);
        fourthCardView = view.findViewById(R.id.fourthCardView_handleMedia);
        fifthCardView = view.findViewById(R.id.fifthCardView_handleMedia);
        sixthCardView = view.findViewById(R.id.sixthCardView_handleMedia);
        seventhCardView = view.findViewById(R.id.seventhCardView_handleMedia);
        eighthCardView = view.findViewById(R.id.eighthCardView_handleMedia);
        ninthCardView = view.findViewById(R.id.ninthCardView_handleMedia);

        DatabaseReference imagesDb = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId()).child("Images");
        imagesDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int prevCount = SathiUserHolder.getSathiUser().getUserImages().size();
                    SathiUserHolder.getSathiUser().setUserImages(new LinkedList<>());
                    int count=0;
                    for(DataSnapshot post:dataSnapshot.getChildren()){
                        count = (int)dataSnapshot.getChildrenCount();
                        String url = post.child("Link").getValue().toString();
                        SathiUserHolder.getSathiUser().getUserImages().add(url);
                    }
                    if(prevCount!=count){
                        Toast.makeText(getContext(),"Please Wait.",Toast.LENGTH_SHORT).show();
                        CountDownTimer timer = new CountDownTimer(1000,1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                            }

                            @Override
                            public void onFinish() {
                                removeMedia();
                                addMedia();
                            }
                        }.start();
                    }
                    else {
                        addMedia();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //addMedia();
    }

    private void removeMedia() {
        try {
            Glide.with(getContext()).load(R.drawable.no_photo).into(firstImage);
            firstAddButton.setImageResource(R.drawable.add_button);
            Glide.with(getContext()).load(R.drawable.no_photo).into(secondImage);
            secondAddButton.setImageResource(R.drawable.add_button);
            Glide.with(getContext()).load(R.drawable.no_photo).into(thirdImage);
            thirdAddButton.setImageResource(R.drawable.add_button);
            Glide.with(getContext()).load(R.drawable.no_photo).into(fourthImage);
            fourthAddButton.setImageResource(R.drawable.add_button);
            Glide.with(getContext()).load(R.drawable.no_photo).into(fifthImage);
            fifthAddButton.setImageResource(R.drawable.add_button);
            Glide.with(getContext()).load(R.drawable.no_photo).into(sixthImage);
            sixthAddButton.setImageResource(R.drawable.add_button);
            Glide.with(getContext()).load(R.drawable.no_photo).into(seventhImage);
            seventhAddButton.setImageResource(R.drawable.add_button);
            Glide.with(getContext()).load(R.drawable.no_photo).into(eighthImage);
            eighthAddButton.setImageResource(R.drawable.add_button);
            Glide.with(getContext()).load(R.drawable.no_photo).into(ninthmage);
            ninthAddButton.setImageResource(R.drawable.add_button);
        }
        catch (NullPointerException e){
        }
    }


    private void chooseMedia(){
        if(SathiUserHolder.getSathiUser().getUserImages()!=null && SathiUserHolder.getSathiUser().getUserImages().size()!=9){
            Intent pickMedia = new Intent(getContext(),ChooseMediaType.class);
            startActivityForResult(pickMedia,REQUEST_CODE);
            dismiss();
        }
        else {
            if(SathiUserHolder.getSathiUser().getUserImages()!=null){
                    Toast.makeText(getContext(),"Maximum Limit Reached",Toast.LENGTH_SHORT).show();
            }
            else {
                Intent pickMedia = new Intent(getContext(),ChooseMediaType.class);
                startActivityForResult(pickMedia,REQUEST_CODE);
                dismiss();
            }
        }
    }

    private class  DeleteImagesAsyncTask extends AsyncTask<String, Integer, LinkedList>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(LinkedList linkedList) {
            super.onPostExecute(linkedList);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected LinkedList doInBackground(final String... strings) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(strings[0]);
            storageReference.delete().addOnSuccessListener(aVoid -> {
                final DatabaseReference imageDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId()).child("Images");
                imageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                                String img = postSnapshot.child("Link").getValue().toString();
                                if(img.equals(strings[0])){
                                    imageDatabase.child(postSnapshot.getKey()).removeValue();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            });

            return null;
        }
    }

    private void addMedia(){
        try {
            if(SathiUserHolder.getSathiUser().getUserImages()!=null){
                int noOfImages = SathiUserHolder.getSathiUser().getUserImages().size();
                if(noOfImages>0){
                    Glide.with(getContext()).load(Uri.parse(SathiUserHolder.getSathiUser().getUserImages().get(0))).thumbnail(Glide.with(getContext()).load(R.drawable.loading).centerCrop()).into(firstImage);
                    firstAddButton.setImageResource(R.drawable.small_close_button);
                    if(noOfImages>1){
                        Glide.with(getContext()).load(Uri.parse(SathiUserHolder.getSathiUser().getUserImages().get(1))).thumbnail(Glide.with(getContext()).load(R.drawable.loading).centerCrop()).into(secondImage);
                        secondAddButton.setImageResource(R.drawable.small_close_button);
                        if(noOfImages>2){
                            Glide.with(getContext()).load(Uri.parse(SathiUserHolder.getSathiUser().getUserImages().get(2))).thumbnail(Glide.with(getContext()).load(R.drawable.loading).centerCrop()).into(thirdImage);
                            thirdAddButton.setImageResource(R.drawable.small_close_button);
                            if(noOfImages>3){
                                Glide.with(getContext()).load(Uri.parse(SathiUserHolder.getSathiUser().getUserImages().get(3))).thumbnail(Glide.with(getContext()).load(R.drawable.loading).centerCrop()).into(fourthImage);
                                fourthAddButton.setImageResource(R.drawable.small_close_button);
                                if(noOfImages>4){
                                    Glide.with(getContext()).load(Uri.parse(SathiUserHolder.getSathiUser().getUserImages().get(4))).thumbnail(Glide.with(getContext()).load(R.drawable.loading).centerCrop()).into(fifthImage);
                                    fifthAddButton.setImageResource(R.drawable.small_close_button);
                                    if(noOfImages>5){
                                        Glide.with(getContext()).load(Uri.parse(SathiUserHolder.getSathiUser().getUserImages().get(5))).thumbnail(Glide.with(getContext()).load(R.drawable.loading).centerCrop()).into(sixthImage);
                                        sixthAddButton.setImageResource(R.drawable.small_close_button);
                                        if(noOfImages>6){
                                            Glide.with(getContext()).load(Uri.parse(SathiUserHolder.getSathiUser().getUserImages().get(6))).thumbnail(Glide.with(getContext()).load(R.drawable.loading).centerCrop()).into(seventhImage);
                                            seventhAddButton.setImageResource(R.drawable.small_close_button);
                                            if(noOfImages>7){
                                                Glide.with(getContext()).load(Uri.parse(SathiUserHolder.getSathiUser().getUserImages().get(7))).thumbnail(Glide.with(getContext()).load(R.drawable.loading).centerCrop()).into(eighthImage);
                                                eighthAddButton.setImageResource(R.drawable.small_close_button);
                                                if(noOfImages>8){
                                                    Glide.with(getContext()).load(Uri.parse(SathiUserHolder.getSathiUser().getUserImages().get(8))).thumbnail(Glide.with(getContext()).load(R.drawable.loading).centerCrop()).into(ninthmage);
                                                    ninthAddButton.setImageResource(R.drawable.small_close_button);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (NullPointerException e){
        }
    }

    @Override
    public void onDestroy() {
        LinkedList<String> tempList = new LinkedList<>();
        LinkedList<String> prevList = SathiUserHolder.getSathiUser().getUserImages();
        if(prevList!=null){
            for(int i=0;i<prevList.size();i++){
                if(!tempList.contains(prevList.get(i))){
                    tempList.add(prevList.get(i));
                }
            }
            removeMedia();
            SathiUserHolder.getSathiUser().setUserImages(null);
            SathiUserHolder.getSathiUser().setUserImages(tempList);

            MatchesProfileViewPagerAdapter matchesProfileViewPagerAdapter = new MatchesProfileViewPagerAdapter(SathiUserHolder.getProfileContext(),SathiUserHolder.getSathiUser().getUserImages().size(),SathiUserHolder.getSathiUser().getUserId(),SathiUserHolder.getSathiUser().getGender());
//            SathiUserHolder.getProfilePager().setAdapter(matchesProfileViewPagerAdapter);
        }
        super.onDestroy();
    }

}
