package com.techweblearn.musicbeat.network.interfaces;

import com.techweblearn.musicbeat.network.model.LastFmAlbum;
import com.techweblearn.musicbeat.network.model.LastFmArtist;

import java.security.Key;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.techweblearn.musicbeat.Utils.Constants.LAST_FM_API_KEY;

/**
 * Created by Kunal on 14-01-2018.
 */

public interface LastFmClientInterface {

    String KEY=LAST_FM_API_KEY;
    String BASE_QUERY_PARAMETERS = "?format=json&autocorrect=1&api_key=" + KEY;


    @GET(BASE_QUERY_PARAMETERS + "&method=artist.getinfo")
    Call<LastFmArtist>getArtistInfo(@Query("artist")String artist);

    @GET(BASE_QUERY_PARAMETERS + "&method=album.getinfo")
    Call<LastFmAlbum>getAlbumInfo(@Query("artist")String artist,@Query("album")String album );

}
