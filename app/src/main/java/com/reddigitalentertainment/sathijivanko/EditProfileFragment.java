package com.reddigitalentertainment.sathijivanko;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reddigitalentertainment.sathijivanko.BasicDisplay.BDInstagramHelper;
import com.reddigitalentertainment.sathijivanko.BasicDisplay.InstagramBDUserData;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditProfileFragment extends Fragment implements BDInstagramHelper.DataListener<InstagramBDUserData> {

    String chosenPreference = "";
    String chosenStatus = "";
    RecyclerView hobbiesRecycler;
    private RecyclerView.LayoutManager layoutManager;
    View view;
    private UserHobbiesAdapter adapter;
    private ArrayList<String> hobbies = new ArrayList<>();
    CheckBox showAgeCheckBox;
    Activity thatActivity;
    private BDInstagramHelper instaHelper = new BDInstagramHelper();

    public Activity getThatActivity() {
        return thatActivity;
    }

    public void setThatActivity(Activity thatActivity) {
        this.thatActivity = thatActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        BDInstagramHelper.Companion.initialiseWithContext(getActivity());
        Button connect = view.findViewById(R.id.connectInstagram_editProfile);

        if(instaHelper.isLoggedIn()){
            connect.setText("Profile");
        }

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(instaHelper.isLoggedIn()){
                    BDInstagramHelper.Companion.fetchProfileData(EditProfileFragment.this,true);
                }
                else {
                    instaHelper.performLogin(getFragmentManager(),EditProfileFragment.this,"en");
                }
            }
        });

        hobbiesRecycler=view.findViewById(R.id.recyclerViewHobbies_editProfile);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId());
        final DatabaseReference extraInformationDatabase = databaseReference.child("Extra Information");

        CircleImageView displayImage = view.findViewById(R.id.displayImage_editProfileFragment);
        if(SathiUserHolder.getSathiUser().getDisplayPicture()!=null){
            Glide.with(getContext()).load(Uri.parse(SathiUserHolder.getSathiUser().getDisplayPicture())).into(displayImage);
        }
        else {
            displayImage.setImageResource(R.drawable.temp_image);
        }
        displayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogDisplayPicture dialogDisplayPicture = new DialogDisplayPicture(SathiUserHolder.getSathiUser().getDisplayPicture(),view,"E");
                dialogDisplayPicture.show(getFragmentManager(),"DisplayPictureDialog");
            }
        });
        TextView userName = view.findViewById(R.id.userName_editProfileFragment);
        userName.setText(SathiUserHolder.getSathiUser().getName());
        TextView userAge = view.findViewById(R.id.userAge_editProfileFragment);
        userAge.setText(String.valueOf(SathiUserHolder.getSathiUser().getAge()));
        ImageView backButton = view.findViewById(R.id.backButton_EditProfileFragment);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        TextView userJob = view.findViewById(R.id.userJob_editProfileFragment);
        TextView userJobEditTextField = view.findViewById(R.id.editJobTextView_EditProfile);
        if(SathiUserHolder.getSathiUser().getUserJob()!=null){
            userJob.setText(SathiUserHolder.getSathiUser().getUserJob());
        }
        else {
            userJobEditTextField.setText("Add");
        }
        TextView userSchool = view.findViewById(R.id.userSchool_editProfileFragment);
        TextView userSchoolEditTextField = view.findViewById(R.id.editUserSchoolTextView_EditProfile);
        if(SathiUserHolder.getSathiUser().getUserSchool()!=null){
            userSchool.setText(SathiUserHolder.getSathiUser().getUserSchool());
        }
        else {
            userSchoolEditTextField.setText("Add");
        }
        TextView userCollege = view.findViewById(R.id.userCollege_editProfileFragment);
        TextView userCollegeEditTextField = view.findViewById(R.id.editUserCollegeTextView_EditProfile);
        if(SathiUserHolder.getSathiUser().getUserCollege()!=null){
            userCollege.setText(SathiUserHolder.getSathiUser().getUserCollege());
        }
        else {
            userCollegeEditTextField.setText("Add");
        }
        TextView userLookingFor = view.findViewById(R.id.userLookingFor_editProfileFragment);
        TextView userLookingForEditTextField = view.findViewById(R.id.editUserLookingForTextView_EditProfile);
        if(SathiUserHolder.getSathiUser().getLookingFor()!=null){
            userLookingFor.setText(SathiUserHolder.getSathiUser().getLookingFor());
        }
        else {
            userLookingForEditTextField.setText("Add");
        }
        if(SathiUserHolder.getSathiUser().getLookingFor()!=null){
            userLookingFor.setText(SathiUserHolder.getSathiUser().getLookingFor());
        }
        else {
            userLookingForEditTextField.setText("Add");
        }
        TextView userBio = view.findViewById(R.id.userBio_editProfileFragment);
        if(SathiUserHolder.getSathiUser().getBio().length()>25){
            String limitedBio = SathiUserHolder.getSathiUser().getBio().substring(0,25)+"...";
            userBio.setText(limitedBio);
        }
        else {
            userBio.setText(SathiUserHolder.getSathiUser().getBio());
        }

        TextView userStatus = view.findViewById(R.id.userStatus_editProfileFragment);
        TextView userStatusEditTextField = view.findViewById(R.id.editUserStatusTextView_EditProfile);
        if(SathiUserHolder.getSathiUser().getStatus()!=null){
            userStatus.setText(SathiUserHolder.getSathiUser().getStatus());
        }
        else {
            userStatusEditTextField.setText("Add");
        }

        final LinearLayout changeNameLayout = view.findViewById(R.id.toHide_enterNameEditProfile);
        changeNameLayout.setVisibility(View.GONE);
        final TextInputEditText newName = view.findViewById(R.id.enterNewName_editProfile);
        Button changeNameButton = view.findViewById(R.id.newNameDoneButton_editProfile);
        CardView changeName = view.findViewById(R.id.userNameCardView_EditProfile);
        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changeNameLayout.getVisibility()==View.GONE){
                    changeNameLayout.setVisibility(View.VISIBLE);
                    newName.requestFocus();
                }
                else {
                    changeNameLayout.setVisibility(View.GONE);
                }
            }
        });
        changeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempName = newName.getText().toString();
                if(tempName.trim().isEmpty()){
                    newName.requestFocus();
                    newName.setError("Enter Your Name");
                }
                else if(tempName.matches(".*\\d.*")){
                    newName.requestFocus();
                    newName.setError("Enter Valid Name");
                }
                else{
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                databaseReference.child("Name").setValue(tempName);
                                userName.setText(tempName);
                                SathiUserHolder.getSathiUser().setName(tempName);
                                newName.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    changeNameLayout.setVisibility(View.GONE);
                    HideKeyboard.hideFragment(getActivity(),getView());
                }
            }
        });


        final LinearLayout changeAgeLayout = view.findViewById(R.id.toHide_enterAgeEditProfile);
        changeAgeLayout.setVisibility(View.GONE);
        final TextInputEditText newAge = view.findViewById(R.id.enterNewAge_editProfile);
        Button changeAgeButton = view.findViewById(R.id.newAgeDoneButton_editProfile);
        CardView changeAge = view.findViewById(R.id.ageCardView_EditProfile);
        changeAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changeAgeLayout.getVisibility()==View.GONE){
                    changeAgeLayout.setVisibility(View.VISIBLE);
                    newAge.requestFocus();
                }
                else {
                    changeAgeLayout.setVisibility(View.GONE);
                }
            }
        });
        changeAgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempAge = newAge.getText().toString();
                if(tempAge.trim().isEmpty()){
                    newAge.requestFocus();
                    newAge.setError("Enter Your Age");
                }
                else if(!tempAge.matches(".*\\d.*")){
                    newAge.requestFocus();
                    newAge.setError("Enter valid Age. ");
                }
                else if(Integer.parseInt(tempAge)<=17){
                    Toast.makeText(getContext(),"You must be registered as 18 years of age.",Toast.LENGTH_LONG).show();
                    newAge.requestFocus();
                }
                else{
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                databaseReference.child("Age").setValue(tempAge);
                                userAge.setText(tempAge);
                                SathiUserHolder.getSathiUser().setAge(Integer.parseInt(tempAge));
                                newAge.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    changeAgeLayout.setVisibility(View.GONE);
                    HideKeyboard.hideFragment(getActivity(),getView());

                }
            }
        });


        final LinearLayout changeBioLayout = view.findViewById(R.id.toHide_enterBioEditProfile);
        changeBioLayout.setVisibility(View.GONE);
        final TextInputEditText newBio = view.findViewById(R.id.enterNewBio_editProfile);
        newBio.setText(SathiUserHolder.getSathiUser().getBio());
        Button changeBioButton = view.findViewById(R.id.newBioDoneButton_editProfile);
        final CardView changeBio = view.findViewById(R.id.bioCardView_EditProfile);
        changeBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changeBioLayout.getVisibility()==View.GONE){
                    changeBioLayout.setVisibility(View.VISIBLE);
                    newBio.requestFocus();
                }
                else {
                    changeBioLayout.setVisibility(View.GONE);
                }
            }
        });
        changeBioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempBio = newBio.getText().toString();
                if(tempBio.trim().isEmpty()){
                    newBio.requestFocus();
                    newBio.setError("Enter Your Bio.");
                }
                else{
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                databaseReference.child("Bio").setValue(tempBio);
                                if(newBio.getText().toString().length()>25){
                                    String limitedBio = newBio.getText().toString().substring(0,25)+"...";
                                    userBio.setText(limitedBio);
                                }
                                else {
                                    userBio.setText(newBio.getText().toString());
                                }
                                SathiUserHolder.getSathiUser().setBio(tempBio);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    changeBioLayout.setVisibility(View.GONE);
                    HideKeyboard.hideFragment(getActivity(),getView());
                }
            }
        });



        final LinearLayout changeJobLayout = view.findViewById(R.id.toHide_enterOccupationEditProfile);
        changeJobLayout.setVisibility(View.GONE);
        final  TextInputEditText newJob = view.findViewById(R.id.enterNewJob_editProfile);
        Button changeJobButton = view.findViewById(R.id.newOccupationDoneButton_editProfile);
        final CardView changeJob = view.findViewById(R.id.occupationCardView_EditProfile);
        changeJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changeJobLayout.getVisibility()==View.GONE){
                    changeJobLayout.setVisibility(View.VISIBLE);
                    newJob.requestFocus();
                }
                else {
                    changeJobLayout.setVisibility(View.GONE);
                }
            }
        });
        changeJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempJob = newJob.getText().toString();
                if(tempJob.trim().isEmpty()){
                    newJob.requestFocus();
                    newJob.setError("Enter Your Job");
                }
                else {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                extraInformationDatabase.child("Job").setValue(tempJob);
                                userJob.setText(tempJob);
                                SathiUserHolder.getSathiUser().setUserJob(tempJob);
                                newJob.setText("");
                                userJobEditTextField.setText("Edit");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    changeJobLayout.setVisibility(View.GONE);
                    HideKeyboard.hideFragment(getActivity(),getView());
                }
            }
        });

        final LinearLayout changeSchoolLayout = view.findViewById(R.id.toHide_enterUserSchoolEditProfile);
        changeSchoolLayout.setVisibility(View.GONE);
        final  TextInputEditText newSchool = view.findViewById(R.id.enterNewUserSchool_editProfile);
        Button changeSchoolButton = view.findViewById(R.id.newUserSchoolDoneButton_editProfile);
        final CardView changeUserSchool = view.findViewById(R.id.userSchoolCardView_EditProfile);
        changeUserSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changeSchoolLayout.getVisibility()==View.GONE){
                    changeSchoolLayout.setVisibility(View.VISIBLE);
                    newSchool.requestFocus();
                }
                else {
                    changeSchoolLayout.setVisibility(View.GONE);
                }
            }
        });
        changeSchoolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempSchool = newSchool.getText().toString();
                if(tempSchool.trim().isEmpty()){
                    newSchool.requestFocus();
                    newSchool.setError("Enter Your School.");
                }
                else {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                extraInformationDatabase.child("School").setValue(tempSchool);
                                userSchool.setText(tempSchool);
                                SathiUserHolder.getSathiUser().setUserSchool(tempSchool);
                                newSchool.setText("");
                                userSchoolEditTextField.setText("Edit");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    changeSchoolLayout.setVisibility(View.GONE);
                    HideKeyboard.hideFragment(getActivity(),getView());
                }
            }
        });

        final LinearLayout changeCollegeLayout = view.findViewById(R.id.toHide_enterUserCollegeEditProfile);
        changeCollegeLayout.setVisibility(View.GONE);
        final  TextInputEditText newCollege = view.findViewById(R.id.enterNewUserCollege_editProfile);
        Button changeCollegeButton = view.findViewById(R.id.newUserCollegeDoneButton_editProfile);
        final CardView changeUserCollege = view.findViewById(R.id.userCollegeCardView_EditProfile);
        changeUserCollege.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changeCollegeLayout.getVisibility()==View.GONE){
                    changeCollegeLayout.setVisibility(View.VISIBLE);
                    newCollege.requestFocus();
                }
                else {
                    changeCollegeLayout.setVisibility(View.GONE);
                }
            }
        });
        changeCollegeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempCollege = newCollege.getText().toString();
                if(tempCollege.trim().isEmpty()){
                    newCollege.requestFocus();
                    newCollege.setError("Enter Your College");
                }
                else{
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                extraInformationDatabase.child("College").setValue(tempCollege);
                                userCollege.setText(tempCollege);
                                SathiUserHolder.getSathiUser().setUserCollege(tempCollege);
                                newCollege.setText("");
                                userCollegeEditTextField.setText("Edit");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    changeCollegeLayout.setVisibility(View.GONE);
                    HideKeyboard.hideFragment(getActivity(),getView());
                }
            }
        });

        final LinearLayout changeLookingForLayout = view.findViewById(R.id.toHide_enterLookingForEditProfile);
        changeLookingForLayout.setVisibility(View.GONE);
        final TextInputEditText newLookingFor = view.findViewById(R.id.enterNewLookingFor_editProfile);
        Button changeLookingForButton = view.findViewById(R.id.newLookingForDoneButton_editProfile);
        final CardView changeLookingFor = view.findViewById(R.id.lookingForCardView_EditProfile);
        changeLookingFor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changeLookingForLayout.getVisibility()==View.GONE){
                    changeLookingForLayout.setVisibility(View.VISIBLE);
                    newLookingFor.requestFocus();
                }
                else {
                    changeLookingForLayout.setVisibility(View.GONE);
                }
            }
        });
        changeLookingForButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempLooking = newLookingFor.getText().toString();
                if(tempLooking.trim().isEmpty()){
                    newLookingFor.requestFocus();
                    newLookingFor.setError("Enter what are you looking for.");
                }
                else{
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                extraInformationDatabase.child("Looking For").setValue(tempLooking);
                                userLookingFor.setText(tempLooking);
                                SathiUserHolder.getSathiUser().setLookingFor(tempLooking);
                                newLookingFor.setText("");
                                userLookingForEditTextField.setText("Edit");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    changeLookingForLayout.setVisibility(View.GONE);
                    HideKeyboard.hideFragment(getActivity(),getView());
                }
            }
        });

        final LinearLayout changeHobbyLayout = view.findViewById(R.id.toHide_enterHobbiesEditProfile);
        changeHobbyLayout.setVisibility(View.GONE);
        final CardView changeHobby = view.findViewById(R.id.hobbiesCardView_EditProfile);
        changeHobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changeHobbyLayout.getVisibility()==View.GONE){
                    changeHobbyLayout.setVisibility(View.VISIBLE);
                }
                else {
                    changeHobbyLayout.setVisibility(View.GONE);
                }
            }
        });



        TextView userPreference = view.findViewById(R.id.userPreference_editProfileFragment);
        userPreference.setText(SathiUserHolder.getSathiUser().getPreference());

        final LinearLayout changePreferenceLayout = view.findViewById(R.id.toHide_enterPreferenceEditProfile);
        changePreferenceLayout.setVisibility(View.GONE);
        final Spinner newPreference = view.findViewById(R.id.spinnerPreference_editProfile);
        ArrayAdapter<CharSequence> preferenceAdapter = ArrayAdapter.createFromResource(getContext(),R.array.list_of_preference,R.layout.spinner_center_layout);
        preferenceAdapter.setDropDownViewResource(R.layout.spinner_center_layout);
        newPreference.setAdapter(preferenceAdapter);
        newPreference.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenPreference = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button changePreferenceButton = view.findViewById(R.id.newPreferenceDoneButton_editProfile);
        final CardView changePreference = view.findViewById(R.id.preferenceCardView_EditProfile);
        changePreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changePreferenceLayout.getVisibility()==View.GONE){
                    changePreferenceLayout.setVisibility(View.VISIBLE);
                    newPreference.requestFocus();
                }
                else {
                    changePreferenceLayout.setVisibility(View.GONE);
                }
            }
        });
        changePreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            databaseReference.child("Preference").setValue(chosenPreference).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        DatabaseReference locationDb = FirebaseDatabase.getInstance().getReference().child("Locations")
                                                .child(SathiUserHolder.getSathiUser().getLocation()).child(SathiUserHolder.getSathiUser().getUserId());
                                        String toWrite = SathiUserHolder.getSathiUser().getGender()+","+chosenPreference;
                                        locationDb.setValue(toWrite);
                                    }
                                }
                            });
                            userPreference.setText(chosenPreference);
                            SathiUserHolder.setShownPeople(new ArrayList<PotentialMatchesInfo>());
                            SathiUserHolder.getSathiUser().setPreference(chosenPreference);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                changePreferenceLayout.setVisibility(View.GONE);
            }
        });

        final LinearLayout changeStatusLayout = view.findViewById(R.id.toHide_enterStatusEditProfile);
        changeStatusLayout.setVisibility(View.GONE);
        Spinner statusSpinner = view.findViewById(R.id.editUserStatusSpinner_EditProfile);
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(getContext(),R.array.list_of_status,R.layout.spinner_center_layout);
        statusAdapter.setDropDownViewResource(R.layout.spinner_center_layout);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenStatus = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button changeStatusButton = view.findViewById(R.id.newStatusDoneButton_editProfile);
        final CardView changeStatus = view.findViewById(R.id.statusCardView_EditProfile);
        changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changeStatusLayout.getVisibility()==View.GONE){
                    changeStatusLayout.setVisibility(View.VISIBLE);
                }
                else {
                    changeStatusLayout.setVisibility(View.GONE);
                }
            }
        });
        changeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            extraInformationDatabase.child("Status").setValue(chosenStatus);
                            userStatus.setText(chosenStatus);
                            SathiUserHolder.getSathiUser().setStatus(chosenStatus);
                            userStatusEditTextField.setText("Edit");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                changeStatusLayout.setVisibility(View.GONE);
            }
        });

        showAgeCheckBox = view.findViewById(R.id.showAgeCheckBox_editProfile);
        if(SathiUserHolder.getSathiUser().getShowAge().equals("true")){
            showAgeCheckBox.setChecked(true);
        }

        loadHobbies();

        return view;
    }

    private void loadHobbies() {
        readAllExtras(new OnGetDataListener() {
            @Override
            public void onSuccess(ArrayList<String> allExtras) {
                hobbiesRecycler = view.findViewById(R.id.recyclerViewHobbies_editProfile);
                layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
                hobbiesRecycler.setLayoutManager(layoutManager);
                adapter = new UserHobbiesAdapter(getExtras(),getContext());
                adapter.setHide("Nada");
                hobbiesRecycler.setAdapter(adapter);
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void onSuccess(@Nullable InstagramBDUserData data) {
        Log.d("QW",data.getFullName());
        Log.d("ANS",instaHelper.getUserData().getFullName()+instaHelper.getUserData().getProfilePicture());
        Toast.makeText(getContext(),"UserName"+data.getFullName()+data.getFollowersCount(),Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onFailure(@Nullable String error) {
        Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
    }

    public interface OnGetDataListener{
        void onSuccess(ArrayList<String> allExtras);
        void onStart();
        void onFailure();
    }

    public void readAllExtras(final EditProfileFragment.OnGetDataListener listener){
        listener.onStart();
        DatabaseReference hobbyDb = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId()).child("Extra Information").child("Hobbies");
        hobbyDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot post:dataSnapshot.getChildren()){
                        hobbies.add(post.getKey());
                    }
                    listener.onSuccess(hobbies);
                }
                else {
                    hobbies.add("NullValue");
                    listener.onSuccess(hobbies);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private List<String> getExtras(){
        return hobbies;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            DatabaseReference changeShowStatus = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId()).child("ShowAge");
            if(showAgeCheckBox.isChecked() && SathiUserHolder.getSathiUser().getShowAge().equals("false")){
                changeShowStatus.setValue("true");
                SathiUserHolder.getSathiUser().setShowAge("true");
            }
            else if(!showAgeCheckBox.isChecked() && SathiUserHolder.getSathiUser().getShowAge().equals("true")){
                changeShowStatus.setValue("false");
                SathiUserHolder.getSathiUser().setShowAge("false");
            }
        }
        catch (NullPointerException e){

        }
    }
}
