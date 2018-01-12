package com.techweblearn.musicbeat.Glide.audiocover.AudioCover;

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

public class AudioCoverLoader implements ModelLoader<AudioFileCover,InputStream> {


    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(AudioFileCover audioFileCover, int width, int height, Options options) {
        return new LoadData<>(new ObjectKey(audioFileCover),new AudioCoverFetcher(audioFileCover));
    }

    @Override
    public boolean handles(AudioFileCover audioFileCover) {
        return true;
    }


    public static class Factory implements ModelLoaderFactory<AudioFileCover, InputStream> {

        @Override
        public ModelLoader<AudioFileCover, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new AudioCoverLoader();
        }

        @Override
        public void teardown() {
        }
    }

}
