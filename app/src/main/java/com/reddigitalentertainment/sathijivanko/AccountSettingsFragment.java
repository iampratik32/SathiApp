package com.reddigitalentertainment.sathijivanko;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;


public class AccountSettingsFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_settings, container, false);

        TextView emailAddress = view.findViewById(R.id.emailAddress_accountSettings);
        emailAddress.setText(SathiUserHolder.getSathiUser().getEmail());
        ImageView backButton = view.findViewById(R.id.backButton_AccountSettingsPage);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getFragmentManager().getBackStackEntryCount() > 0){
                    getFragmentManager().popBackStackImmediate();
                }
            }
        });

        final LinearLayout changeEmailLayout = view.findViewById(R.id.toHide_enterEmailAccountSettings);
        changeEmailLayout.setVisibility(View.GONE);
        final TextInputEditText newEmail = view.findViewById(R.id.enterNewEmail_AccountSettings);
        Button changeEmailButton = view.findViewById(R.id.newEmailDoneButton_AccountSettings);
        CardView changeEmail = view.findViewById(R.id.futsalNameCardView_AccountSettings);
        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changeEmailLayout.getVisibility()==View.GONE){
                    changeEmailLayout.setVisibility(View.VISIBLE);
                    newEmail.requestFocus();
                }
                else {
                    changeEmailLayout.setVisibility(View.GONE);
                }
            }
        });
        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PleaseWaitDialog dialog = new PleaseWaitDialog();
                dialog.setCancelable(false);
                dialog.show(getFragmentManager(),"Wait");
                if(SathiUserHolder.getSathiUser().getAuthCredential()!=null){
                    if(EmailValidator.validateEmail(newEmail.getText().toString()) && newEmail.getText().toString()!=null){
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.reauthenticate(SathiUserHolder.getSathiUser().getAuthCredential()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                user.updateEmail(newEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            changeEmailLayout.setVisibility(View.GONE);
                                            Toast.makeText(getContext(),"Email Changed.",Toast.LENGTH_LONG).show();
                                            SathiUserHolder.getSathiUser().setAuthCredential(null);
                                            dialog.dismiss();
                                        }
                                        else {
                                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                                Toast.makeText(getContext(),"This email is already taken.",Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                Toast.makeText(getContext(),"Cant change Password at this moment.",Toast.LENGTH_LONG).show();
                                            }
                                            dialog.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                    }
                    else {
                        dialog.dismiss();
                    }
                }
                else {
                    dialog.dismiss();
                }
            }
        });


        CardView changePassword = view.findViewById(R.id.changePasswordCardView_AccountSettings);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogChangePassword dialogChangePassword = new DialogChangePassword(getContext());
                dialogChangePassword.show(getFragmentManager(),"ChangePassword");
                dialogChangePassword.setCancelable(true);
            }
        });

        return view;
    }

}
