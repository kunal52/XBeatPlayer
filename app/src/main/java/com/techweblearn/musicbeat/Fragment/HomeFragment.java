package com.techweblearn.musicbeat.Fragment;


import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.techweblearn.musicbeat.Adapters.HorizontalRecyclerViewAdapter;
import com.techweblearn.musicbeat.Adapters.PlaylistHorizontalRecyclerview;
import com.techweblearn.musicbeat.Loader.PlaylistLoader;
import com.techweblearn.musicbeat.MVP.HomeMVP.HomePresenterImplementation;
import com.techweblearn.musicbeat.MVP.HomeMVP.HomePresenterInterface;
import com.techweblearn.musicbeat.MVP.HomeMVP.HomeViewInterface;
import com.techweblearn.musicbeat.Models.Playlist;
import com.techweblearn.musicbeat.Models.Song;
import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.Service.MediaBrowserAdapter;
import com.techweblearn.musicbeat.Utils.Constants;
import com.techweblearn.musicbeat.Utils.PreferencesUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements HomeViewInterface ,
        PlaylistHorizontalRecyclerview.Callback,
        HorizontalRecyclerViewAdapter.Callback,
        PlaylistFragment.PlaylistFragmentCallback,
        View.OnClickListener{

    @BindView(R.id.recently_added) RecyclerView recentlyAdded;
    @BindView(R.id.recently_played) RecyclerView recyclerPlayed;
    @BindView(R.id.playlists) RecyclerView playlists_recyclerview;
    @BindView(R.id.text_recently_added) TextView text_recently_added;
    @BindView(R.id.text_recently_played) TextView text_recently_played;
    @BindView(R.id.text_playlists) TextView text_playlists;
    @BindView(R.id.to_library)Button to_library;
    @BindView(R.id.nothing_found)RelativeLayout layout;

    Unbinder unbind;
    HomePresenterInterface homePresenter;
    HorizontalRecyclerViewAdapter recently_added_songs, recently_playedsongs;
    PlaylistHorizontalRecyclerview playlists_adapter;
    MediaBrowserAdapter mediaBrowserAdapter;

    PlaylistFragment playlistFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaBrowserAdapter=new MediaBrowserAdapter(getActivity());
        playlistFragment=new PlaylistFragment();
        playlistFragment.setCallback(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbind = ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        recentlyAdded.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerPlayed.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        playlists_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        homePresenter = new HomePresenterImplementation(getActivity(), this);
        homePresenter.LoadRecentlyAddedSong();
        homePresenter.LoadPlaylists();
        homePresenter.LoadTopRecentlySong();

        to_library.setOnClickListener(this);
        getActivity().getContentResolver().
                registerContentObserver(
                        MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                        true,
                        new PlayListChange(null));

        getActivity().getContentResolver().
                registerContentObserver(
                        MediaStore.Audio.Playlists.INTERNAL_CONTENT_URI,
                        true,
                        new PlayListChange(null));
    }

    @Override
    public void onStart() {
        super.onStart();
        mediaBrowserAdapter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mediaBrowserAdapter.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbind.unbind();
    }

    @Override
    public void onLoadRecentlyAddedSongs(ArrayList<Song> songs) {

        recently_added_songs = new HorizontalRecyclerViewAdapter(getActivity(), songs);
        recently_added_songs.setCallback(this);
        recentlyAdded.setAdapter(recently_added_songs);

        if (songs.size() == 0)
            text_recently_added.setVisibility(View.GONE);
        else layout.setVisibility(View.GONE);

    }

    @Override
    public void onLoadTopPlayedSong(ArrayList<Song> songs) {

        recently_playedsongs=new HorizontalRecyclerViewAdapter(getActivity(),songs);
        recently_playedsongs.setCallback(this);
        recyclerPlayed.setAdapter(recently_playedsongs);

        if (songs.size() == 0)
            text_recently_played.setVisibility(View.GONE);
        else layout.setVisibility(View.GONE);
    }

    @Override
    public void onRecentlyAddedChange(ArrayList<Song> songs) {
        recently_added_songs.notifyChange(songs);
        if (songs.size() == 0)
            text_recently_added.setVisibility(View.GONE);
        else text_recently_added.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTopPlayedChange(ArrayList<Song> songs) {
        recently_playedsongs.notifyChange(songs);
        if (songs.size() == 0)
            text_recently_played.setVisibility(View.GONE);
        else text_recently_played.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadPlaylists(ArrayList<Playlist> playlists) {
        playlists_adapter=new PlaylistHorizontalRecyclerview(getActivity(),playlists);
        playlists_adapter.setCallback(this);
        playlists_recyclerview.setAdapter(playlists_adapter);
        if(playlists.size()==0)
            text_playlists.setVisibility(View.GONE);
        else layout.setVisibility(View.GONE);
    }

    @Override
    public void onPlaylistsChange(ArrayList<Playlist> playlists) {
        playlists_adapter.notifyChange(playlists);
        if(playlists.size()==0)
            text_playlists.setVisibility(View.GONE);
        else text_playlists.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClicked(int position,int playlist_id) {
        Bundle bundle=new Bundle();
        bundle.putInt("playlist_id",playlist_id);
        bundle.putInt("position",position);
        playlistFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.content_layout_container,playlistFragment,"Playlist").commit();
    }

    @Override
    public void onItemClicked(Song song) {
        Bundle bundle=new Bundle();
        bundle.putParcelable("song",song);
        mediaBrowserAdapter.getTransportControls().sendCustomAction(Constants.PLAY_SINGLE_SONG,bundle);

    }

    @Override
    public void onLongPressItem(Song song) {
        //Todo To be Implemented
        //Toast.makeText(getActivity(),"Work in Progress.....:)",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlayListRemove(int id) {
        playlists_adapter.removeItem(id);
    }

    @Override
    public void onPlayListRename() {
        playlists_adapter.notifyChange(PlaylistLoader.getAllPlaylists(getActivity()));

    }

    @Override
    public void onClick(View v) {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        if (getActivity().getSupportFragmentManager().findFragmentByTag("Library") == null) {
            fragmentTransaction.add(R.id.container, new LibraryFragment(), "Library").commit();
            getActivity().getSupportFragmentManager().beginTransaction().hide(getActivity().getSupportFragmentManager().findFragmentByTag("Home")).commit();
        } else {
            fragmentTransaction.show(getActivity().getSupportFragmentManager().findFragmentByTag("Library"));
            if (getActivity().getSupportFragmentManager().findFragmentByTag("Home") != null)
                fragmentTransaction.hide(getActivity().getSupportFragmentManager().findFragmentByTag("Home"));
            fragmentTransaction.commit();
        }
        PreferencesUtil.saveLastOpenedScreen(getActivity(),1);
    }

    private  class PlayListChange extends ContentObserver {
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */

        public PlayListChange(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            playlists_adapter.notifyChange(PlaylistLoader.getAllPlaylists(getActivity()));
        }


        @Override
        public void onChange(boolean selfChange, Uri uri) {
            playlists_adapter.notifyChange(PlaylistLoader.getAllPlaylists(getActivity()));
        }


    }
}
