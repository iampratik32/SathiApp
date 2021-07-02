package com.reddigitalentertainment.sathijivanko;

import android.animation.Animator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class DialogViewImage extends AppCompatDialogFragment {

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    String imageUrl;

    public DialogViewImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_view_image,null);
        builder.setView(view);


        setStyle(DialogFragment.STYLE_NO_TITLE,R.style.AppTheme);

        PhotoView mainImage = view.findViewById(R.id.clickedPhoto_viewImageDialog);

        mainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ImageView closeButton = view.findViewById(R.id.closeButton_viewImageDialog);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if(imageUrl!=null){
            Glide.with(getContext()).load(Uri.parse(imageUrl)).into(mainImage);
        }
        else {
            Toast.makeText(getContext(),"No Image.",Toast.LENGTH_LONG).show();
        }


        return builder.create();
    }
}
