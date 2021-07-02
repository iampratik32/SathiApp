package com.reddigitalentertainment.sathijivanko;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatDialogFragment;

public class PleaseWaitDialog extends AppCompatDialogFragment {
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_wait,null);
        builder.setView(view);
        return builder.create();
    }
}
