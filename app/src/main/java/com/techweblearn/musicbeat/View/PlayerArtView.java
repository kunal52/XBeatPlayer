package com.techweblearn.musicbeat.View;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.techweblearn.musicbeat.Glide.audiocover.AudioCover.AudioFileCover;
import com.techweblearn.musicbeat.Glide.audiocover.GlideApp;
import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.Utils.Util;

/**
 * Created by Kunal on 08-12-2017.
 */

public class PlayerArtView extends RelativeLayout {

    private int position=0;
    ImageView imageView;
    public PlayerArtView(Context context) {
        super(context);
        initialize();
    }

    public PlayerArtView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public PlayerArtView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }


    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.player_album_art, this);
        imageView =  findViewById(R.id.song_image);
    }

    public int getPosition() {
        return position;
    }

    public void setViewData(int position,String path) {
        Drawable drawable= Util.getArtistDrawable(getContext());
        this.position = position;
        GlideApp.with(getContext())
                .load(new AudioFileCover(path))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .error(drawable)
                .placeholder(imageView.getDrawable())
                .useAnimationPool(true)
                .into(imageView);
    }


}
