package com.reddigitalentertainment.sathijivanko;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;


public class SecondEnterDetail_Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_second_enter_detail_, container, false);

        final TextInputEditText passwordField = view.findViewById(R.id.newUserPassword_text);
        passwordField.requestFocus();
        if(passwordField.hasFocus()){
            HideKeyboard.showSoftKeyboard(view,getActivity());
        }
        final TextInputEditText verifyPasswordField = view.findViewById(R.id.newUserVerifyPassword_text);

        Button nextButton = view.findViewById(R.id.newUserSecondNextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String takenPassword = passwordField.getText().toString();
                String takenVerifyPassword = verifyPasswordField.getText().toString();

                if(takenPassword.isEmpty()){
                    passwordField.requestFocus();
                    Toast.makeText(getContext(),"Enter your Password",Toast.LENGTH_SHORT).show();
                    passwordField.requestFocus();
                }
                else if(takenPassword.length()<=6){
                    Toast.makeText(getContext(),"Password length should me more than 6 letters.",Toast.LENGTH_LONG).show();
                    passwordField.requestFocus();
                }
                else if(takenVerifyPassword.isEmpty()){
                    Toast.makeText(getContext(),"Verify your Password",Toast.LENGTH_SHORT).show();
                    verifyPasswordField.requestFocus();
                }
                else if(!takenVerifyPassword.equals(takenPassword)){
                    Toast.makeText(getContext(),"Your two password do not match",Toast.LENGTH_SHORT).show();
                    verifyPasswordField.requestFocus();
                }
                else {
                    HideKeyboard.hideFragment(getActivity(),view);
                    RegisterDetailHolder.getAllDetails().add(takenPassword);
                    Fragment thirdDetail = new ThirdEnterDetail_Fragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.right_to_left,R.anim.exit_right,R.anim.left_to_right,R.anim.exit_left)
                            .replace(R.id.frameLayout_newUserSecondFragment,thirdDetail).addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                }
            }
        });

        return view;
    }


}
