package com.techweblearn.musicbeat.Glide.audiocover.AlbumCover;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

/**
 * Created by Kunal on 04-12-2017.
 */

public class AlbumCoverFetcher implements DataFetcher<Bitmap> {

    private static final int MAX_ALBUM_ART_CACHE_SIZE = 24*1024*1024;  // 12 MB
    private static final int MAX_ART_WIDTH = 800;  // pixels
    private static final int MAX_ART_HEIGHT = 480;  // pixels

    AlbumFileCover audioFileCover;

    public AlbumCoverFetcher(AlbumFileCover audioFileCover) {
        this.audioFileCover = audioFileCover;

    }

    @Override
    public void loadData(Priority priority, DataCallback<? super Bitmap> callback) {


        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(audioFileCover.filePaths);
            byte[] picture = retriever.getEmbeddedPicture();

            if (picture != null) {
                callback.onDataReady(BitmapFactory.decodeByteArray(picture,0,picture.length));
            }else callback.onLoadFailed(new Exception());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }

    }

    @Override
    public void cleanup() {

    }

    @Override
    public void cancel() {

    }

    @NonNull
    @Override
    public Class<Bitmap> getDataClass() {
        return Bitmap.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }
}
