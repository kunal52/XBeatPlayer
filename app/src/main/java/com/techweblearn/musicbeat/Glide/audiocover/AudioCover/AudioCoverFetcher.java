package com.techweblearn.musicbeat.Glide.audiocover.AudioCover;

import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Kunal on 04-12-2017.
 */

public class AudioCoverFetcher implements DataFetcher<InputStream> {


    AudioFileCover audioFileCover;

    public AudioCoverFetcher(AudioFileCover audioFileCover) {
        this.audioFileCover = audioFileCover;
    }


    @Override
    public void loadData(Priority priority, DataCallback<? super InputStream> callback) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(audioFileCover.filePath);
            byte[] picture = retriever.getEmbeddedPicture();

            if (picture != null) {
                callback.onDataReady( new ByteArrayInputStream(picture));
            } else {
                callback.onLoadFailed(new FileNotFoundException());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }

    }


    @Override
    public void cleanup() {
        Log.d("Module","CleanUp");



    }

    @Override
    public void cancel() {
        Log.d("Module","Cancel");
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }
}
