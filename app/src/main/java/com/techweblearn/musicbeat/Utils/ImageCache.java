package com.techweblearn.musicbeat.Utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;


/**
 * Created by Kunal on 22-12-2017.
 */

public class ImageCache {

    private static final int MAX_ALBUM_ART_CACHE_SIZE = 24*1024*1024;  // 24 MB
    private LruCache<String, Bitmap> mMemoryCache;


    private static ImageCache instance;

    public static ImageCache getInstance() {
        if(instance != null) {
            return instance;
        }

        instance = new ImageCache();
        instance.initializeCache();

        return instance;
    }

    protected void initializeCache() {

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        int maxSize = Math.min(MAX_ALBUM_ART_CACHE_SIZE,
                (int) (Math.min(Integer.MAX_VALUE, Runtime.getRuntime().maxMemory()/4)));
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(maxSize)
        {
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                oldValue.recycle();
                oldValue=null;
            }
        };

    }

    public Bitmap getImage(String url) {
        return this.mMemoryCache.get(url);
    }

    public void cacheImage(String url, Bitmap image) {
        this.mMemoryCache.put(url, image);
    }
}