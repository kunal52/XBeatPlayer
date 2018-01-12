package com.techweblearn.musicbeat.Glide.audiocover;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.module.AppGlideModule;
import com.techweblearn.musicbeat.Glide.audiocover.AlbumCover.AlbumCoverLoader;
import com.techweblearn.musicbeat.Glide.audiocover.AlbumCover.AlbumFileCover;
import com.techweblearn.musicbeat.Glide.audiocover.AudioCover.AudioCoverLoader;
import com.techweblearn.musicbeat.Glide.audiocover.AudioCover.AudioFileCover;

import java.io.InputStream;


/**
 * Created by Kunal on 05-12-2017.
 */

@com.bumptech.glide.annotation.GlideModule
public class GlideRegisterModel extends AppGlideModule {


    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        super.registerComponents(context, glide, registry);
        registry.append(AudioFileCover.class, InputStream.class, new AudioCoverLoader.Factory());
        registry.append(AlbumFileCover.class, Bitmap.class, new AlbumCoverLoader.Factory());
    }
}
