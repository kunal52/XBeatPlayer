package com.techweblearn.musicbeat.Fragment;


import android.os.Bundle;
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
import android.widget.TextView;

import com.techweblearn.musicbeat.Adapters.SongListRecyclerviewAdapter;
import com.techweblearn.musicbeat.Dialogs.SongDetailDialog;
import com.techweblearn.musicbeat.Loader.ArtistLoader;
import com.techweblearn.musicbeat.Loader.PlaylistLoader;
import com.techweblearn.musicbeat.Loader.RecentlyAddedSongs;
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

import static com.techweblearn.musicbeat.Fragment.SongFragment.ADD_NEW_PLAYLIST_ID;
import static com.techweblearn.musicbeat.Utils.Constants.PLAY_NEXT;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecentlyAdded extends Fragment implements SongListRecyclerviewAdapter.OnItemClick, View.OnClickListener {


    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.play_all)
    ImageView play_all;
    @BindView(R.id.songs)
    RecyclerView songsRecyclerview;
    MediaBrowserAdapter mediaBrowserAdapter;
    ArrayList<Song> arrayListSongs;
    SongListRecyclerviewAdapter songListRecyclerviewAdapter;
    Unbinder unbinder;

    public RecentlyAdded() {

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaBrowserAdapter = new MediaBrowserAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recently, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        arrayListSongs = RecentlyAddedSongs.getLastAddedSongs(getActivity());
        songListRecyclerviewAdapter = new SongListRecyclerviewAdapter(getActivity(), arrayListSongs);
        songListRecyclerviewAdapter.OnSongClick(this);
        play_all.setOnClickListener(this);
        back.setOnClickListener(this);

        songsRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        songsRecyclerview.setAdapter(songListRecyclerviewAdapter);
        title.setText("Recently Added");


    }


    @Override
    public void OnSongClick(Song song, int position) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("song", song);
        mediaBrowserAdapter.getTransportControls().sendCustomAction(Constants.PLAY_SINGLE_SONG, bundle);

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


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back)
            getActivity().onBackPressed();
        else {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("song_list", arrayListSongs);
            mediaBrowserAdapter.getTransportControls().sendCustomAction(Constants.PLAY_SONGLIST, bundle);
        }
    }
}
