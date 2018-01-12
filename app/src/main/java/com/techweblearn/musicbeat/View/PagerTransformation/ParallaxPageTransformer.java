package com.techweblearn.musicbeat.View.PagerTransformation;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.techweblearn.musicbeat.R;

public class ParallaxPageTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(@NonNull View page, float position) {

        int pageWidth = page.getWidth();


        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.setAlpha(1);

        } else if (position <= 1) { // [-1,1]

            page.findViewById(R.id.song_image).setTranslationX(-position * (pageWidth / 2)); //Half the normal speed

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            page.setAlpha(1);
        }
    }
}
