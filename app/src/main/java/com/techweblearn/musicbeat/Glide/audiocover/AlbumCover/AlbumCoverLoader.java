package com.techweblearn.musicbeat.Glide.audiocover.AlbumCover;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;

import java.io.InputStream;

/**
 * Created by Kunal on 05-12-2017.
 */

public class AlbumCoverLoader implements ModelLoader<AlbumFileCover, Bitmap> {


    @Nullable
    @Override
    public LoadData<Bitmap> buildLoadData(AlbumFileCover audioFileCover, int width, int height, Options options) {
        return new LoadData<>(new ObjectKey(audioFileCover), new AlbumCoverFetcher(audioFileCover));
    }

    @Override
    public boolean handles(AlbumFileCover audioFileCover) {
        return true;
    }


    public static class Factory implements ModelLoaderFactory<AlbumFileCover, Bitmap> {

        @Override
        public ModelLoader<AlbumFileCover, Bitmap> build(MultiModelLoaderFactory multiFactory) {
            return new AlbumCoverLoader();
        }

        @Override
        public void teardown() {
        }
    }

}
