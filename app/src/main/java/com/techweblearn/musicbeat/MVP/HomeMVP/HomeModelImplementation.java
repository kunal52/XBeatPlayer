package com.techweblearn.musicbeat.MVP.HomeMVP;

import android.content.Context;

import com.techweblearn.musicbeat.Loader.PlaylistLoader;
import com.techweblearn.musicbeat.Loader.RecentlyAddedSongs;
import com.techweblearn.musicbeat.Loader.TopAndRecentlyPlayedTracksLoader;

/**
 * Created by Kunal on 04-12-2017.
 */

public class HomeModelImplementation implements HomeModelInterface {

    Context context;

    public HomeModelImplementation(Context context) {
        this.context = context;
    }

    @Override
    public void LoadRecentlyAddedSong(OnLoadSongDataListener listener) {

       listener.onLoadRecentlyAddedSongs(RecentlyAddedSongs.getLastAddedSongs(context));
    }

    @Override
    public void LoadTopRecentlySong(OnLoadSongDataListener listener) {
        listener.onLoadTopPlayedSong(TopAndRecentlyPlayedTracksLoader.getTopTracks(context));
    }

    @Override
    public void LoadPlaylist(OnLoadSongDataListener listener) {
        listener.onLoadPlaylists(PlaylistLoader.getAllPlaylists(context));
    }
}
