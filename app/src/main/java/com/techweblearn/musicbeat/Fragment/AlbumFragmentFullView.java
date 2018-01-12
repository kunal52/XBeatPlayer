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
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.techweblearn.musicbeat.Adapters.SongListRecyclerviewAdapter;
import com.techweblearn.musicbeat.Dialogs.SongDetailDialog;
import com.techweblearn.musicbeat.Glide.audiocover.AudioCover.AudioFileCover;
import com.techweblearn.musicbeat.Glide.audiocover.GlideApp;
import com.techweblearn.musicbeat.Loader.AlbumLoader;
import com.techweblearn.musicbeat.Loader.ArtistLoader;
import com.techweblearn.musicbeat.Loader.ArtistSongLoader;
import com.techweblearn.musicbeat.Loader.PlaylistLoader;
import com.techweblearn.musicbeat.Models.Album;
import com.techweblearn.musicbeat.Models.Playlist;
import com.techweblearn.musicbeat.Models.Song;
import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.Service.MediaBrowserAdapter;
import com.techweblearn.musicbeat.Utils.PlaylistsUtil;
import com.techweblearn.musicbeat.Utils.Util;
import com.techweblearn.musicbeat.provider.MediaItems;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.techweblearn.musicbeat.Fragment.SongFragment.ADD_NEW_PLAYLIST_ID;
import static com.techweblearn.musicbeat.Utils.Constants.PLAY_ALBUM;
import static com.techweblearn.musicbeat.Utils.Constants.PLAY_NEXT;
import static com.techweblearn.musicbeat.Utils.Constants.PLAY_SINGLE_SONG;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumFragmentFullView extends Fragment implements SlidingUpPanelLayout.PanelSlideListener,
        SongListRecyclerviewAdapter.OnItemClick,
        View.OnClickListener {


    @BindView(R.id.songs)
    RecyclerView songs_recyclerview;
    @BindView(R.id.album_name)
    TextView album_name;
    @BindView(R.id.album_art)
    ImageView album_art;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.back_2)
    ImageView back_2;
    @BindView(R.id.play_all)
    ImageView play_all;

    MediaBrowserAdapter mediaBrowserAdapter;
    SlidingUpPanelLayout main_content;
    Unbinder unbinder;
    int album_id;
    Album album;

    public AlbumFragmentFullView() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album_fragment_full_view, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getArguments() != null) {
            album_id = getArguments().getInt("album_id");
            album = AlbumLoader.getAlbum(getActivity(), album_id);
        }

        mediaBrowserAdapter = new MediaBrowserAdapter(getActivity());


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        main_content = view.findViewById(R.id.sliding_up_panel);
        main_content.setPanelHeight((int) (Util.getScreenSize(getActivity()).y - Util.getScreenSize(getActivity()).y * 0.40));
        main_content.setCoveredFadeColor(android.R.color.transparent);
        main_content.addPanelSlideListener(this);


        SongListRecyclerviewAdapter songListRecyclerviewAdapter = new SongListRecyclerviewAdapter(getContext(), album.songs);
        songListRecyclerviewAdapter.OnSongClick(this);
        songs_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        songs_recyclerview.setAdapter(songListRecyclerviewAdapter);

        album_name.setText(album.getTitle());
        back.setOnClickListener(this);
        back_2.setOnClickListener(this);
        play_all.setOnClickListener(this);

        GlideApp.with(getActivity())
                .load(new AudioFileCover(album.safeGetFirstSong().data))
                .fitCenter()
                .error(Util.getSongDrawable(getActivity()))
                .into(album_art);

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
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

        switch (newState) {
            case COLLAPSED:
                back_2.setVisibility(View.GONE);
                break;
            case EXPANDED:
                back_2.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void OnSongClick(Song song, int position) {

        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putParcelable("song", song);
        mediaBrowserAdapter.getTransportControls().sendCustomAction(PLAY_SINGLE_SONG, bundle);
    }

    @Override
    public void OnMoreOptionClick(Song song, int position, View view) {

        showPopUpMenu(view, position, song);

    }


    private void showPopUpMenu(final View view, final int position, final Song song) {
        android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.album_song_list_popupmenu);
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
        switch (view.getId()) {
            case R.id.back:
            case R.id.back_2:
                getActivity().onBackPressed();
                break;
            case R.id.play_all:
                Bundle bundle = new Bundle();
                bundle.putInt("album_id", album_id);
                mediaBrowserAdapter.getTransportControls().sendCustomAction(PLAY_ALBUM, bundle);
                break;

        }
    }
}
