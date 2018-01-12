package com.techweblearn.musicbeat.Fragment;


import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Process;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.techweblearn.musicbeat.Adapters.SongListRecyclerviewAdapter;
import com.techweblearn.musicbeat.Dialogs.DeleteAlertFragment;
import com.techweblearn.musicbeat.Dialogs.SongDetailDialog;
import com.techweblearn.musicbeat.Loader.AlbumLoader;
import com.techweblearn.musicbeat.Loader.ArtistLoader;
import com.techweblearn.musicbeat.Loader.ArtistSongLoader;
import com.techweblearn.musicbeat.Loader.PlaylistLoader;
import com.techweblearn.musicbeat.Loader.SongLoader;
import com.techweblearn.musicbeat.Models.Playlist;
import com.techweblearn.musicbeat.Models.Song;
import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.Service.MediaBrowserAdapter;
import com.techweblearn.musicbeat.Utils.Constants;
import com.techweblearn.musicbeat.Utils.PlaylistsUtil;
import com.techweblearn.musicbeat.provider.MediaItems;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.techweblearn.musicbeat.Utils.Constants.ADD_ALL_SONGS_TO_QUEUE;
import static com.techweblearn.musicbeat.Utils.Constants.PLAY_NEXT;

/**
 * A simple {@link Fragment} subclass.
 */


public class SongFragment extends Fragment implements SongListRecyclerviewAdapter.OnItemClick {

    public static final int ADD_NEW_PLAYLIST_ID = 641;
    @BindView(R.id.song_list_recyclerview)
    RecyclerView list_recyclerView;
    Unbinder unbinder;
    SongListRecyclerviewAdapter songListRecyclerviewAdapter;
    MediaBrowserAdapter mediaBrowserAdapter;
    SongDataBaseChangeListener songDataBaseChangeListener;

    public SongFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_song, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        songListRecyclerviewAdapter = new SongListRecyclerviewAdapter(getContext(), SongLoader.getAllSongs(getActivity()));
        songListRecyclerviewAdapter.OnSongClick(this);
        list_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list_recyclerView.setAdapter(songListRecyclerviewAdapter);
        songDataBaseChangeListener = new SongDataBaseChangeListener(null);

    }


    @Override
    public void onStart() {
        super.onStart();
        mediaBrowserAdapter = new MediaBrowserAdapter(getActivity());
        mediaBrowserAdapter.onStart();
        mediaBrowserAdapter.addListener(new MediaBrowserListener());
        getActivity().getContentResolver().registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, songDataBaseChangeListener);
        getActivity().getContentResolver().registerContentObserver(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, true, songDataBaseChangeListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        mediaBrowserAdapter.onStop();
        getActivity().getContentResolver().unregisterContentObserver(songDataBaseChangeListener);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void OnSongClick(Song song, int position) {

        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        mediaBrowserAdapter.getTransportControls().sendCustomAction(ADD_ALL_SONGS_TO_QUEUE, bundle);
    }

    @Override
    public void OnMoreOptionClick(Song song, int position, View view) {
        showPopUpMenu(view, position, song);
    }


    private void showPopUpMenu(final View view, final int position, final Song song) {
        android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.song_list_popup_menu);
        popupMenu.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
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
                    case R.id.add_to_playlist:
                        showPopUpMenuForPlayList(view, song);
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
               /*     case R.id.delete_from_device:
                        DeleteAlertFragment.create(song).show(getActivity().getSupportFragmentManager(),"Delete");
                        break;*/
                }
                return false;
            }
        });
        popupMenu.show();
    }


    private void showPopUpMenuForPlayList(View view, final Song song) {
        final ArrayList<Playlist> playlists = PlaylistLoader.getAllPlaylists(getActivity());
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        for (int i = 0; i < playlists.size(); i++) {
            popupMenu.getMenu().add(1, playlists.get(i).id, i, playlists.get(i).name);
        }
        popupMenu.getMenu().add(1, ADD_NEW_PLAYLIST_ID, playlists.size(), "Add New Playlist");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == ADD_NEW_PLAYLIST_ID) {
                    AddPlayListDialogFragment.create(song).show(getActivity().getSupportFragmentManager(), "Create Playlist");
                    return false;
                }
                for (int i = 0; i < playlists.size(); i++) {
                    if (playlists.get(i).id == item.getItemId()) {
                        PlaylistsUtil.addToPlaylist(getContext(), song, playlists.get(i).id, true);
                        return false;
                    }
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private class MediaBrowserListener extends MediaBrowserAdapter.MediaBrowserChangeListener {
        @Override
        public void onConnected(@Nullable MediaControllerCompat mediaController) {
            super.onConnected(mediaController);
        }

        @Override
        public void onMetadataChanged(@Nullable MediaMetadataCompat mediaMetadata) {
            super.onMetadataChanged(mediaMetadata);
        }

        @Override
        public void onPlaybackStateChanged(@Nullable PlaybackStateCompat playbackState) {
            super.onPlaybackStateChanged(playbackState);
        }
    }


    private class SongDataBaseChangeListener extends ContentObserver {
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */

        public SongDataBaseChangeListener(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            //      songListRecyclerviewAdapter.notifySongSetChanged();
        }


        @Override
        public void onChange(boolean selfChange, Uri uri) {
            //      songListRecyclerviewAdapter.notifySongSetChanged();
        }


    }
}
