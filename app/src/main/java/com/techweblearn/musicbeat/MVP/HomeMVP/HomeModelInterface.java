package com.techweblearn.musicbeat.MVP.HomeMVP;

import com.techweblearn.musicbeat.Models.Playlist;
import com.techweblearn.musicbeat.Models.Song;

import java.util.ArrayList;

/**
 * Created by Kunal on 03-12-2017.
 */

public interface HomeModelInterface {

    interface OnLoadSongDataListener
    {
        void onLoadRecentlyAddedSongs(ArrayList<Song> songs);
        void onLoadTopPlayedSong(ArrayList<Song>songs);
        void onLoadPlaylists(ArrayList<Playlist>playlists);
        void onRecentlyAddedChange(ArrayList<Song>songs);
        void onTopPlayedChange(ArrayList<Song>songs);
        void onPlaylistsChange(ArrayList<Playlist>playlists);
    }

    void LoadRecentlyAddedSong(OnLoadSongDataListener listener);
    void LoadTopRecentlySong(OnLoadSongDataListener listener);
    void LoadPlaylist(OnLoadSongDataListener listener);


}
