package com.reddigitalentertainment.sathijivanko;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;


public class ThirdEnterDetail_Fragment extends Fragment {

    String chosenGender ="";
    String chosenPreference = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_third_enter_detail_, container, false);

        Spinner genderSpinner = view.findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(getContext(),R.array.list_of_gender,R.layout.spinner_center_layout);
        genderAdapter.setDropDownViewResource(R.layout.spinner_center_layout);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setOnItemSelectedListener(new GenderSpinnerClass());


        Spinner preferenceSpinner = view.findViewById(R.id.preferenceSpinner);
        ArrayAdapter<CharSequence> preferenceAdapter = ArrayAdapter.createFromResource(getContext(),R.array.list_of_preference,R.layout.spinner_center_layout);
        preferenceAdapter.setDropDownViewResource(R.layout.spinner_center_layout);
        preferenceSpinner.setAdapter(preferenceAdapter);
        preferenceSpinner.setOnItemSelectedListener(new PreferenceSpinnerClass());

        final LinearLayout ageLayout = view.findViewById(R.id.ageLayout_newUser);
        ageLayout.setVisibility(View.GONE);

        final TextInputEditText ageOfUser = view.findViewById(R.id.newUserAge_text);
        final CheckBox showAge = view.findViewById(R.id.newUser_showAgeCheckButton);

        final Button nextButton = view.findViewById(R.id.newUserthirdNextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nextButton.getText().equals("Next")){
                    ageLayout.setVisibility(View.VISIBLE);
                    nextButton.setText("Continue");
                }
                else if(nextButton.getText().equals("Continue")){
                    String takenAge = ageOfUser.getText().toString().trim();
                    boolean ageChecked = showAge.isChecked();
                    if(takenAge.isEmpty()){
                        ageOfUser.requestFocus();
                        ageOfUser.setError("Enter Your Age");
                    }
                    else if(!takenAge.matches(".*\\d.*")){
                        ageOfUser.requestFocus();
                        ageOfUser.setError("Enter valid Age. ");
                    }
                    else if(Integer.parseInt(takenAge)<=17){
                        Toast.makeText(getContext(),"You must be at least 18 inorder to continue",Toast.LENGTH_LONG).show();
                        ageOfUser.requestFocus();
                    }
                    else{
                        HideKeyboard.hideFragment(getActivity(),view);
                        RegisterDetailHolder.getAllDetails().add(chosenGender);
                        RegisterDetailHolder.getAllDetails().add(chosenPreference);
                        RegisterDetailHolder.getAllDetails().add(takenAge);
                        RegisterDetailHolder.getAllDetails().add(ageChecked);
                        Fragment fourthDetail = new FourthEnterDetail_Fragment();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().setCustomAnimations(R.anim.right_to_left,R.anim.exit_right,R.anim.left_to_right,R.anim.exit_left)
                                .replace(R.id.frameLayout_newUserSecondFragment,fourthDetail).addToBackStack(null)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                    }
                }
            }
        });


        return view;
    }

        class GenderSpinnerClass implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            chosenGender = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class PreferenceSpinnerClass implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            chosenPreference = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

}
