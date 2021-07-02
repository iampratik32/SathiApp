package com.reddigitalentertainment.sathijivanko;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedList;


public class FirstEnterDetail_Fragment extends Fragment {

    FirebaseAuth firebaseAuth;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_first_enter_detail_, container, false);


        final LinkedList registerDetail = new LinkedList();

        final TextInputEditText nameOfUser = view.findViewById(R.id.newUserName_text);
        final TextInputEditText emailOfUser = view.findViewById(R.id.newUserEmail_text);

        final LinearLayout emailLayout = view.findViewById(R.id.emailLayout_enterDetail);
        emailLayout.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();



        final Button nextButton = view.findViewById(R.id.newUserFirstNextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nextButton.getText().equals("Next")){
                    String takenName = nameOfUser.getText().toString().trim();
                    if(takenName.isEmpty()){
                        nameOfUser.requestFocus();
                        nameOfUser.setError("Enter Your Name");
                    }
                    else if(takenName.matches(".*\\d.*")){
                        nameOfUser.requestFocus();
                        nameOfUser.setError("Enter a valid Name");
                    }
                    else {
                        nextButton.setText("Continue");
                        registerDetail.add(takenName);
                        emailLayout.setVisibility(View.VISIBLE);
                        if(firebaseAuth.getCurrentUser().getEmail()!=null){
                            emailOfUser.setText(firebaseAuth.getCurrentUser().getEmail());
                            emailOfUser.setEnabled(false);
                        }
                    }
                }
                else if(nextButton.getText().equals("Continue")){
                    String takenEmail = emailOfUser.getText().toString().trim();

                    if(takenEmail.isEmpty()){
                        emailOfUser.requestFocus();
                        emailOfUser.setError("Enter Your Email");
                    }
                    else if(!EmailValidator.validateEmail(takenEmail)){
                        emailOfUser.requestFocus();
                        emailOfUser.setError("Enter a valid Email");
                    }
                    else {
                        registerDetail.add(takenEmail);
                        RegisterDetailHolder.setAllDetails(registerDetail);
                        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getContext(),"Email Verification Sent.",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(getContext(),task.getException().toString(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        if(FirebaseAuth.getInstance().getCurrentUser()!=null && FirebaseAuth.getInstance().getCurrentUser().getEmail()==null){
                            FirebaseUser thisUser = FirebaseAuth.getInstance().getCurrentUser();
                            thisUser.updateEmail(takenEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()){
                                        if(task.getException().getMessage().equals("The email address is already in use by another account.")){
                                            //TODO SAME EMAIL
                                        }
                                    }
                                }
                            });
                        }

                        HideKeyboard.hideFragment(getActivity(),view);
                        Fragment secondDetail = new SecondEnterDetail_Fragment();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().setCustomAnimations(R.anim.right_to_left,R.anim.exit_right,R.anim.left_to_right,R.anim.exit_left)
                                .replace(R.id.frameLayout_newUserSecondFragment,secondDetail).addToBackStack(null)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                        if(SathiUserHolder.getUserAddress()==null || SathiUserHolder.getUserAddress().equals("")){
                            CheckUtility.getLocation(getContext());
                        }
                    }
                }
            }
        });



        return view;
    }


}
