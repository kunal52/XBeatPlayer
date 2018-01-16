package com.techweblearn.musicbeat.network.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by Kunal on 15-01-2018.
 */

public class LyricsFetcherService extends Service {


    private static String BASE="http://lyrics.wikia.com/wiki/";
    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        getLyrics("Camila Cabello","Havana");
        return START_NOT_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void getLyrics(final String song_name, final String artist_name)
    {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Document document=Jsoup.connect(getGeneratedLink(song_name, artist_name)).get();
                    document.title();
                    Elements elemets=document.getElementsByClass("lyricbox");

                    Log.d("Lyrics",elemets.text());

                } catch (IOException e) {
                    e.printStackTrace();
                }






                stopSelf();
            }
        });

        thread.start();
    }



    private String getGeneratedLink(String song_name,String artist_name)
    {

        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(BASE);

        String[]song=song_name.split(" ");
        String[]artist=artist_name.split(" ");

        for (int i=0;i<song.length;i++)
        {
            if(i==song.length-1)
                stringBuilder.append(song[i]);
            else
            {
                stringBuilder.append(song[i]);
                stringBuilder.append("_");
            }
        }

        stringBuilder.append(":");

        for (int i=0;i<artist.length;i++)
        {
            if(i==artist.length-1)
                stringBuilder.append(artist[i]);
            else
            {
                stringBuilder.append(artist[i]);
                stringBuilder.append("_");
            }
        }


        return stringBuilder.toString();




    }
}
