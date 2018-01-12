package com.techweblearn.musicbeat.MVP.HomeMVP;

import android.content.Context;

import com.techweblearn.musicbeat.Models.Playlist;
import com.techweblearn.musicbeat.Models.Song;

import java.util.ArrayList;

/**
 * Created by Kunal on 04-12-2017.
 */

public class HomePresenterImplementation implements HomePresenterInterface,HomeModelInterface.OnLoadSongDataListener {

    Context context;
    HomeViewInterface homeViewInterface;
    HomeModelInterface homeModelInterface;

    public HomePresenterImplementation(Context context, HomeViewInterface homeViewInterface) {
        this.context = context;
        this.homeViewInterface = homeViewInterface;
        homeModelInterface=new HomeModelImplementation(context);
    }

    @Override
    public void LoadRecentlyAddedSong() {
        homeModelInterface.LoadRecentlyAddedSong(this);
    }

    @Override
    public void LoadTopRecentlySong() {
        homeModelInterface.LoadTopRecentlySong(this);
    }

    @Override
    public void LoadPlaylists() {
        homeModelInterface.LoadPlaylist(this);
    }


    @Override
    public void onLoadRecentlyAddedSongs(ArrayList<Song> songs) {
        homeViewInterface.onLoadRecentlyAddedSongs(songs);
    }

    @Override
    public void onLoadTopPlayedSong(ArrayList<Song> songs) {
        homeViewInterface.onLoadTopPlayedSong(songs);
    }

    @Override
    public void onLoadPlaylists(ArrayList<Playlist> playlists) {
        homeViewInterface.onLoadPlaylists(playlists);
    }


    @Override
    public void onRecentlyAddedChange(ArrayList<Song> songs) {

    }

    @Override
    public void onTopPlayedChange(ArrayList<Song> songs) {

    }

    @Override
    public void onPlaylistsChange(ArrayList<Playlist> playlists) {

    }


}
