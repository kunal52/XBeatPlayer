package com.techweblearn.musicbeat.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.techweblearn.musicbeat.Adapters.PlaylistHorizontalRecyclerview;
import com.techweblearn.musicbeat.Adapters.PlaylistSongListRecyclerviewAdapter;
import com.techweblearn.musicbeat.Dialogs.RenamePlaylistDialogFragment;
import com.techweblearn.musicbeat.Dialogs.SongDetailDialog;
import com.techweblearn.musicbeat.Loader.ArtistLoader;
import com.techweblearn.musicbeat.Loader.PlaylistLoader;
import com.techweblearn.musicbeat.Loader.PlaylistSongLoader;
import com.techweblearn.musicbeat.Models.PlaylistSong;
import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.Service.MediaBrowserAdapter;
import com.techweblearn.musicbeat.Utils.Constants;
import com.techweblearn.musicbeat.Utils.PlaylistsUtil;
import com.techweblearn.musicbeat.provider.MediaItems;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.techweblearn.musicbeat.Utils.Constants.PLAY_NEXT;
import static com.techweblearn.musicbeat.Utils.Constants.PLAY_SINGLE_SONG;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment implements PlaylistHorizontalRecyclerview.Callback,
        PlaylistSongListRecyclerviewAdapter.OnItemClick,
        View.OnClickListener,
        RenamePlaylistDialogFragment.RenamePlaylistCallback{

    @BindView(R.id.playlist_list_recyclerview) RecyclerView playlistRecyclerView;
    @BindView(R.id.playlist_songs_recyclerview) RecyclerView songRecyclerView;
    @BindView(R.id.back) ImageView back;
    @BindView(R.id.more_options_playlist)ImageView more_option_playlists;
    @BindView(R.id.more_options_song)ImageView more_option_songs;

    Unbinder unbinder;
    PlaylistSongListRecyclerviewAdapter songListRecyclerviewAdapter;
    PlaylistHorizontalRecyclerview playlists_adapter;
    MediaBrowserAdapter mediaBrowserAdapter;
    int selected_playlistid;

    public PlaylistFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaBrowserAdapter = new MediaBrowserAdapter(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
        back.setOnClickListener(this);
        more_option_playlists.setOnClickListener(this);
        more_option_songs.setOnClickListener(this);
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
        unbinder.unbind();
    }

    @Override
    public void onItemClicked(int position,int playlist_id) {

        onSongsAdapterChange(position,playlist_id);
        playlists_adapter.setSelected(position);
        selected_playlistid=playlist_id;
    }


    private void initUI() {
        playlists_adapter = new PlaylistHorizontalRecyclerview(getActivity(), PlaylistLoader.getAllPlaylists(getActivity()));
        playlists_adapter.setCallback(this);
        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        playlistRecyclerView.setAdapter(playlists_adapter);

        songListRecyclerviewAdapter = new PlaylistSongListRecyclerviewAdapter(getActivity(), new ArrayList<PlaylistSong>());
        songListRecyclerviewAdapter.OnSongClick(this);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        songRecyclerView.setAdapter(songListRecyclerviewAdapter);
        if (getArguments() != null) {
            int id = getArguments().getInt("playlist_id");
            int position = getArguments().getInt("position");
            selected_playlistid = id;
            onItemClicked(position, id);
        }

    }


    private void onSongsAdapterChange(int position,int playlist_id) {
        songListRecyclerviewAdapter.notifyDataSetChange(PlaylistSongLoader.getPlaylistSongList(getActivity(), playlist_id));
    }

    @Override
    public void OnSongClick(PlaylistSong song, int position) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("song", song);
        mediaBrowserAdapter.getTransportControls().sendCustomAction(PLAY_SINGLE_SONG, bundle);

    }

    @Override
    public void OnMoreOptionClick(PlaylistSong song, int position, View view) {
        showPopUpMenu(song, position, view);
    }

    private void showPopUpMenu(final PlaylistSong song, final int position, final View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.playlist_song_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.play_next:
                        Bundle bundle = new Bundle();
                        bundle.putInt("media_id", song.id);
                        mediaBrowserAdapter.getTransportControls().sendCustomAction(PLAY_NEXT, bundle);
                        break;
                    case R.id.add_to_queue:
                        mediaBrowserAdapter.addQueueItem(MediaItems.getMediaMetaDataFromMediaId(getActivity(), String.valueOf(song.id)).getDescription());
                        break;
                    case R.id.remove_from_playlist:
                        PlaylistsUtil.removeFromPlaylist(getActivity(), song, song.playlistId);
                        songListRecyclerviewAdapter.removeAItem(position);
                        break;
                    case R.id.go_to_artist:
                        Bundle extra = new Bundle();
                        extra.putString("artist_name", song.artistName);
                        extra.putInt("artist_id", song.artistId);
                        extra.putParcelableArrayList("albums", ArtistLoader.getArtist(getActivity(), song.artistId).albums);

                        ArtistFragmentFullView artistFragmentFullView = new ArtistFragmentFullView();
                        artistFragmentFullView.setArguments(extra);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack("artist_view")
                                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out, R.anim.slide_right_in, R.anim.slide_right_out)
                                .add(R.id.content_layout_container, artistFragmentFullView)
                                .commit();

                        break;
                    case R.id.go_to_album:
                        Bundle extra1 = new Bundle();
                        extra1.putInt("album_id", song.albumId);
                        AlbumFragmentFullView fragmentFullView = new AlbumFragmentFullView();
                        fragmentFullView.setArguments(extra1);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack("album_view")
                                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out, R.anim.slide_right_in, R.anim.slide_right_out)
                                .add(R.id.content_layout_container, fragmentFullView)
                                .commit();
                        break;
                    case R.id.details:
                        SongDetailDialog.create(song).show(getActivity().getSupportFragmentManager(), "Details");
                        break;
                   /* case R.id.delete_from_device:
                        DeleteAlertFragment.create(song).show(getActivity().getSupportFragmentManager(),"Delete");
                        break;*/
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.back: getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.more_options_playlist:
                showPopUpMenuPlaylist();
                break;
            case R.id.more_options_song:
                break;
        }
    }

    private void showPopUpMenuPlaylist()
    {
        PopupMenu popupMenu=new PopupMenu(getActivity(),more_option_playlists);
        popupMenu.inflate(R.menu.playlistfragment_playlistmenu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.play_all:
                        Bundle bundle=new Bundle();
                        bundle.putParcelableArrayList("song_list",  PlaylistSongLoader.getPlaylistSongList(getActivity(),selected_playlistid));
                        mediaBrowserAdapter.getTransportControls().sendCustomAction(Constants.PLAY_SONGLIST,bundle);
                        break;
                    case R.id.add_to_queue:
                        Bundle bundle1=new Bundle();
                        bundle1.putParcelableArrayList("song_list",  PlaylistSongLoader.getPlaylistSongList(getActivity(),selected_playlistid));
                        mediaBrowserAdapter.getTransportControls().sendCustomAction(Constants.ADD_SONG_LIST_TO_QUEUE,bundle1);
                        break;
                    case R.id.remove_playlist:
                        PlaylistsUtil.deletePlaylist(getActivity(),selected_playlistid);
                        playlists_adapter.removeItem(selected_playlistid);
                        playlistFragmentCallback.onPlayListRemove(selected_playlistid);
                        break;
                    case R.id.rename_playlist:
                        RenamePlaylistDialogFragment renamePlaylistDialogFragment=RenamePlaylistDialogFragment.create(selected_playlistid);
                        renamePlaylistDialogFragment.setCallback(PlaylistFragment.this);
                        renamePlaylistDialogFragment.show(getActivity().getSupportFragmentManager(),"rename playlist");


                        break;

                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void showPopUpMenuSongs()
    {
        PopupMenu popupMenu=new PopupMenu(getActivity(),more_option_songs);
        popupMenu.inflate(R.menu.playlistfragment_songmenu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        popupMenu.show();
    }

    PlaylistFragmentCallback playlistFragmentCallback;
    public void setCallback(PlaylistFragmentCallback playlistFragmentCallback)
    {
        this.playlistFragmentCallback=playlistFragmentCallback;
    }

    @Override
    public void onPlaylistRename() {
        playlistFragmentCallback.onPlayListRename();
        playlists_adapter.notifyChange(PlaylistLoader.getAllPlaylists(getActivity()));
    }

    public interface PlaylistFragmentCallback
    {
        void onPlayListRemove(int id);
        void onPlayListRename();
    }
}
