package com.reddigitalentertainment.sathijivanko;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DialogChooseDistrict extends AppCompatDialogFragment {
    private Context context;
    private String zone;
    private String chosenCity;
    private TextView locationCity;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TextView getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(TextView locationCity) {
        this.locationCity = locationCity;
    }

    public DialogChooseDistrict(Context context) {
        this.context = context;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_choose_district,null);
        builder.setView(view);

        ImageView closeButton = view.findViewById(R.id.closeButton_chooseDistrict);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView zoneText = view.findViewById(R.id.zone_chooseDistrict);
        zoneText.setText("Choose your City from "+zone+" Region");

        Spinner districtSpinner = view.findViewById(R.id.spinner_chooseDistrict);
        ArrayAdapter<CharSequence> preferenceAdapter = null;
        if(zone.equals("Bagmati")){
            preferenceAdapter = ArrayAdapter.createFromResource(getContext(),R.array.Bagmati,R.layout.spinner_center_layout);
        }
        else if(zone.equals("Lumbini")){
            preferenceAdapter = ArrayAdapter.createFromResource(getContext(),R.array.Lumbini,R.layout.spinner_center_layout);
        }
        else if(zone.equals("Bheri")){
            preferenceAdapter = ArrayAdapter.createFromResource(getContext(),R.array.Bheri,R.layout.spinner_center_layout);
        }
        else if(zone.equals("Mahakali")){
            preferenceAdapter = ArrayAdapter.createFromResource(getContext(),R.array.Mahakali,R.layout.spinner_center_layout);
        }
        else if(zone.equals("Dhawalagiri")){
            preferenceAdapter = ArrayAdapter.createFromResource(getContext(),R.array.Dhawalagiri,R.layout.spinner_center_layout);
        }
        else if(zone.equals("Mechi")){
            preferenceAdapter = ArrayAdapter.createFromResource(getContext(),R.array.Mechi,R.layout.spinner_center_layout);
        }
        else if(zone.equals("Gandaki")){
            preferenceAdapter = ArrayAdapter.createFromResource(getContext(),R.array.Gandaki,R.layout.spinner_center_layout);
        }
        else if(zone.equals("Narayani")){
            preferenceAdapter = ArrayAdapter.createFromResource(getContext(),R.array.Narayani,R.layout.spinner_center_layout);
        }
        else if(zone.equals("Janakpur")){
            preferenceAdapter = ArrayAdapter.createFromResource(getContext(),R.array.Janakpur,R.layout.spinner_center_layout);
        }
        else if(zone.equals("Rapti")){
            preferenceAdapter = ArrayAdapter.createFromResource(getContext(),R.array.Rapti,R.layout.spinner_center_layout);
        }
        else if(zone.equals("Karnali")){
            preferenceAdapter = ArrayAdapter.createFromResource(getContext(),R.array.Karnali,R.layout.spinner_center_layout);
        }
        else if(zone.equals("Seti")){
            preferenceAdapter = ArrayAdapter.createFromResource(getContext(),R.array.Seti,R.layout.spinner_center_layout);
        }
        else if(zone.equals("Koshi")){
            preferenceAdapter = ArrayAdapter.createFromResource(getContext(),R.array.Koshi,R.layout.spinner_center_layout);
        }
        else if(zone.equals("Sagarmatha")){
            preferenceAdapter = ArrayAdapter.createFromResource(getContext(),R.array.Sagarmatha,R.layout.spinner_center_layout);
        }

        preferenceAdapter.setDropDownViewResource(R.layout.spinner_center_layout);
        districtSpinner.setAdapter(preferenceAdapter);
        chosenCity = districtSpinner.getItemAtPosition(0).toString();
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenCity = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button okButton = view.findViewById(R.id.okButton_dialogChooseDistrict);
        ProgressBar progressBar = view.findViewById(R.id.progressBar_district);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = SathiUserHolder.getUserCountry()+"-"+zone+","+chosenCity;
                SathiUserHolder.setUserCity(city);
                if(type==null){
                    if(city.equals(SathiUserHolder.getSathiUser().getLocation())){
                        Toast.makeText(getContext(),"The current location is same as the new one.",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        progressBar.setVisibility(View.VISIBLE);
                        okButton.setVisibility(View.GONE);
                        setCancelable(false);
                        closeButton.setEnabled(false);
                        String prevLocation = SathiUserHolder.getSathiUser().getLocation();
                        SathiUserHolder.getSathiUser().setLocation(city);
                        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId()).child("Location");
                        userDb.setValue(SathiUserHolder.getSathiUser().getLocation()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    DatabaseReference prevDb = FirebaseDatabase.getInstance().getReference().child("Locations").child(prevLocation)
                                            .child(SathiUserHolder.getSathiUser().getUserId());
                                    prevDb.removeValue();
                                    DatabaseReference newDb = FirebaseDatabase.getInstance().getReference().child("Locations").child(SathiUserHolder.getSathiUser().getLocation())
                                            .child(SathiUserHolder.getSathiUser().getUserId());
                                    newDb.setValue(SathiUserHolder.getSathiUser().getGender()+","+SathiUserHolder.getSathiUser().getPreference()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                locationCity.setText(SathiUserHolder.getSathiUser().getLocation());
                                                dismiss();
                                                progressBar.setVisibility(View.GONE);
                                                setCancelable(false);
                                                closeButton.setEnabled(true);

                                            }
                                        }
                                    });

                                }
                                else {
                                    Toast.makeText(getContext(),"Couldn't Update Location",Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    okButton.setVisibility(View.VISIBLE);
                                    setCancelable(false);
                                    closeButton.setEnabled(true);
                                }
                            }
                        });
                    }
                }
                else {
                    dismiss();
                }
            }
        });


        return builder.create();
    }
}
