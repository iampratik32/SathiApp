package com.reddigitalentertainment.sathijivanko;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        EditProfileFragment edit = new EditProfileFragment();
        edit.setThatActivity(EditProfileActivity.this);
        fragmentTransaction.add(R.id.frameLayout_EditProfileActivity,edit);
        fragmentTransaction.commit();

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.secondaryColor));

    }
}
