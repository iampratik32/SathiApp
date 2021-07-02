package com.reddigitalentertainment.sathijivanko;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MatchedDialog extends AppCompatDialogFragment {

    private Context context;
    private String userName;
    private String displayPicture;
    private LikesAdapter adapter;
    int toCount=0;

    public LikesAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(LikesAdapter adapter) {
        this.adapter = adapter;
    }

    public MatchedDialog(Context context, String userName, String displayPicture) {
        this.context = context;
        this.userName = userName;
        this.displayPicture = displayPicture;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_matched,null);
        builder.setView(view);

        final TextView userNameTextView = view.findViewById(R.id.userName_dialogMatched);
        final CircleImageView displayPictureImage = view.findViewById(R.id.matchedUserPhoto_dialogMatched);

        final GifImageView matchedGif = view.findViewById(R.id.matchedGif);
        ((GifDrawable) matchedGif.getDrawable()).addAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationCompleted(int loopNumber) {
                if(toCount==1){
                    ((GifDrawable) matchedGif.getDrawable()).stop();
                    userNameTextView.setText(userName);
                    if(displayPicture!=null){
                        Glide.with(context).load(Uri.parse(displayPicture)).into(displayPictureImage);
                    }
                    userNameTextView.setVisibility(View.VISIBLE);
                    displayPictureImage.setVisibility(View.VISIBLE);
                }
                toCount++;
            }
        });

        Button okButton = view.findViewById(R.id.okButton_matchedDialog);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });



        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

}
