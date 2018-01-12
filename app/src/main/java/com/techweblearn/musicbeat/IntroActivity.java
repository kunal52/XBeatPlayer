package com.techweblearn.musicbeat;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import java.security.Permission;

/**
 * Created by Kunal on 04-01-2018.
 */

public class IntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("XBeat Player","Lets Continue",R.drawable.icon,Color.parseColor("#FFFFFF")));
        addSlide(AppIntroFragment.newInstance("XBeat Player","Enjoy Your Music  ",R.drawable.icon,Color.parseColor("#484848")));

        showStatusBar(false);
        askForPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }




}
