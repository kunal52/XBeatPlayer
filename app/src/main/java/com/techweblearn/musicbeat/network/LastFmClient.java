package com.techweblearn.musicbeat.network;

import com.techweblearn.musicbeat.network.interfaces.LastFmClientInterface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.techweblearn.musicbeat.Utils.Constants.LAST_FM_BASE_URL;

/**
 * Created by Kunal on 14-01-2018.
 */

public class LastFmClient {


    public static LastFmClientInterface getApiService()
    {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(LAST_FM_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(LastFmClientInterface.class);
    }

}
