package com.reddigitalentertainment.sathijivanko;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class VerifyDeleteDialog extends AppCompatDialogFragment {

    private Context context;

    public VerifyDeleteDialog(Context context) {
        if(context==null){
            throw new NullPointerException();
        }
        this.context = context;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_verify_password,null);
        builder.setView(view);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        ImageView closeButton = view.findViewById(R.id.closeButton_verifyPassword);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button verifyButton = view.findViewById(R.id.verifyButton_VerifyDialog);
        verifyButton.setText("Delete");
        final TextInputEditText passwordEditText = view.findViewById(R.id.enterPassword_verifyPasswordDialog);
        final ProgressBar progressBar = view.findViewById(R.id.progressBar_VerifyPassword);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String takenText = passwordEditText.getText().toString().trim();
                if(takenText.isEmpty()){
                    Toast.makeText(context,"Enter Password to Continue.",Toast.LENGTH_LONG).show();
                    passwordEditText.requestFocus();
                }
                else {
                    setCancelable(true);
                    closeButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    final AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),takenText);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                deleteAccount(user,context);
                            }
                            else {
                                passwordEditText.requestFocus();
                                setCancelable(false);
                                closeButton.setEnabled(true);
                                if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                    Toast.makeText(getContext(),"Wrong Password Entered",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(getContext(),"Can't continue at this moment.",Toast.LENGTH_LONG).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });


        return builder.create();
    }

    String location = "";
    private void deleteAccount(FirebaseUser user, final Context context){
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    String userId = SathiUserHolder.getSathiUser().getUserId();
                    DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                    userDb.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                location = dataSnapshot.child("Location").getValue().toString();
                                DatabaseReference userLocation = FirebaseDatabase.getInstance().getReference().child("Locations").child(location).child(userId);
                                userLocation.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            DatabaseReference feedDb = FirebaseDatabase.getInstance().getReference().child(userId);
                                            feedDb.removeValue();
                                            DatabaseReference matchedDb = userDb.child("Matches").child("Matched");
                                            matchedDb.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot post) {
                                                    if(post.exists()){
                                                        for(DataSnapshot p:post.getChildren()){
                                                            String tId = p.getKey();
                                                            DatabaseReference thatUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(tId)
                                                                    .child("Matches").child("Matched").child(userId);
                                                            thatUserDb.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        userDb.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isSuccessful()){
                                                                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(userId);
                                                                                    storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if(task.isSuccessful()){
                                                                                                dismiss();
                                                                                                FirebaseAuth.getInstance().signOut();
                                                                                                Toast.makeText(context,"User Deleted.",Toast.LENGTH_LONG).show();
                                                                                                SathiUserHolder.setSathiUser(null);
                                                                                                SathiUserHolder.setShownPeople(null);
                                                                                                SathiUserHolder.setShownUsers(null);
                                                                                                SathiUserHolder.setTotalLikes(0);
                                                                                                SathiUserHolder.setTotalDislikes(0);
                                                                                                SathiUserHolder.setDealtWithList(null);
                                                                                                SathiUserHolder.setPotentialLists(null);
                                                                                                SathiUserHolder.setTotalLoad(0);
                                                                                                SathiUserHolder.setLoadChat(false);
                                                                                                Intent intent = new Intent(context, LoginActivity.class);
                                                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                                context.startActivity(intent);
                                                                                            }
                                                                                            else {
                                                                                                dismiss();
                                                                                                FirebaseAuth.getInstance().signOut();
                                                                                                Toast.makeText(context,"User Deleted.",Toast.LENGTH_LONG).show();
                                                                                                SathiUserHolder.setSathiUser(null);
                                                                                                SathiUserHolder.setShownPeople(null);
                                                                                                SathiUserHolder.setShownUsers(null);
                                                                                                SathiUserHolder.setTotalLikes(0);
                                                                                                SathiUserHolder.setTotalDislikes(0);
                                                                                                SathiUserHolder.setDealtWithList(null);
                                                                                                SathiUserHolder.setPotentialLists(null);
                                                                                                SathiUserHolder.setTotalLoad(0);
                                                                                                SathiUserHolder.setLoadChat(false);
                                                                                                Intent intent = new Intent(context, LoginActivity.class);
                                                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                                context.startActivity(intent);
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}
