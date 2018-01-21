package com.techweblearn.musicbeat.network.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.techweblearn.musicbeat.Loader.ArtistLoader;
import com.techweblearn.musicbeat.Models.Artist;
import com.techweblearn.musicbeat.network.model.LastFmArtist;
import com.techweblearn.musicbeat.provider.NetworkInfoStore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.techweblearn.musicbeat.network.LastFmClient.getApiService;

/**
 * Created by kunal on 14/1/18.
 */

public class ArtistInfoServices extends Service {

    ArrayList<Artist> artists;

    @Override
    public void onCreate() {
        super.onCreate();
        artists = ArtistLoader.getAllArtists(getApplicationContext());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {


        final NetworkInfoStore networkInfoStore = new NetworkInfoStore(getApplicationContext());


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                LastFmArtist lastFmArtist = null;
                for (final Artist artist : artists) {
                    if (networkInfoStore.getArtistInfo(artist.getName()) != null)
                        continue;

                    try {
                        lastFmArtist = getApiService().getArtistInfo(artist.getName()).execute().body();
                        assert lastFmArtist != null;
                        byte[] byteArray = convertImageToByte(lastFmArtist.getArtist().getImage().get(4).getText());
                        networkInfoStore.insertArtistData(artist.getName(), lastFmArtist.getArtist().getBio().getContent(), byteArray);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    stopSelf(startId);
                }
            }
        });
        thread.start();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public byte[] convertImageToByte(String url) {
        byte[] data = null;
        try {

            InputStream input = new URL(url).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            data = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
