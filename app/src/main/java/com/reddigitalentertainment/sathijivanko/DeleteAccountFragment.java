package com.reddigitalentertainment.sathijivanko;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

public class DeleteAccountFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_delete_account, container, false);

        ImageView backButton = view.findViewById(R.id.backButton_deleteAccount);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getFragmentManager().getBackStackEntryCount() > 0){
                    getFragmentManager().popBackStackImmediate();
                }
            }
        });

        final CheckBox firstCheckBox = view.findViewById(R.id.firstCheckBox_deleteAccount);
        final CheckBox secondCheckBox = view.findViewById(R.id.secondCheckBox_deleteAccount);
        final CheckBox thirdCheckBox = view.findViewById(R.id.thirdCheckBox_deleteAccount);
        final CheckBox fourthCheckBox = view.findViewById(R.id.fourthCheckBox_deleteAccount);
        final CheckBox fifthCheckBox = view.findViewById(R.id.fifthCheckBox_deleteAccount);

        firstCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    secondCheckBox.setChecked(false);
                    thirdCheckBox.setChecked(false);
                    fourthCheckBox.setChecked(false);
                    fifthCheckBox.setChecked(false);
                }
            }
        });

        secondCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    firstCheckBox.setChecked(false);
                    thirdCheckBox.setChecked(false);
                    fourthCheckBox.setChecked(false);
                    fifthCheckBox.setChecked(false);
                }

            }
        });

        thirdCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    secondCheckBox.setChecked(false);
                    firstCheckBox.setChecked(false);
                    fourthCheckBox.setChecked(false);
                    fifthCheckBox.setChecked(false);
                }
            }
        });

        fourthCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    secondCheckBox.setChecked(false);
                    thirdCheckBox.setChecked(false);
                    firstCheckBox.setChecked(false);
                    fifthCheckBox.setChecked(false);
                }
            }
        });

        fifthCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    secondCheckBox.setChecked(false);
                    thirdCheckBox.setChecked(false);
                    fourthCheckBox.setChecked(false);
                    firstCheckBox.setChecked(false);
                }
            }
        });

        Button deleteAccount = view.findViewById(R.id.deleteAccount_deleteAccount);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstCheckBox.isChecked() || secondCheckBox.isChecked() || thirdCheckBox.isChecked() || fourthCheckBox.isChecked() || fifthCheckBox.isChecked()){
                    VerifyDeleteDialog dialog = new VerifyDeleteDialog(getContext());
                    dialog.setCancelable(false);
                    dialog.show(getFragmentManager(),"DelAccount");
                }
                else {
                    Toast.makeText(getContext(),"Select a reason to delete your account.",Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

}
