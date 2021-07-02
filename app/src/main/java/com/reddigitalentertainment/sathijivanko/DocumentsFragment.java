package com.reddigitalentertainment.sathijivanko;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class DocumentsFragment extends Fragment {
    String type;
    public DocumentsFragment(String type) {
        this.type=type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_documents, container, false);


        TextView headline = view.findViewById(R.id.headline_document);
        TextView text = view.findViewById(R.id.textView_document);

        if(type.equals("S")){
            text.setText(getResources().getString(R.string.saftey));
            headline.setText("Safety");
        }
        else if(type.equals("CG")){
            text.setText(getResources().getString(R.string.community));
            headline.setText("Community Guidelines");

        }

        ImageView closeButton = view.findViewById(R.id.closeButton_documents);
        closeButton.setOnClickListener(v -> getFragmentManager().popBackStackImmediate());

        return view;
    }


}
