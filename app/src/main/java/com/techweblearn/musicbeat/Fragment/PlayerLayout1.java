package com.techweblearn.musicbeat.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techweblearn.musicbeat.Adapters.SongCoverPagerAdapter;
import com.techweblearn.musicbeat.Adapters.SongCoverPagerAdapterCircular;
import com.techweblearn.musicbeat.Adapters.SongCoverPagerQueueAdapter;
import com.techweblearn.musicbeat.Base.PlayerLayoutBase;
import com.techweblearn.musicbeat.Dialogs.SongDetailDialog;
import com.techweblearn.musicbeat.Helper.FixedSpeedScroller;
import com.techweblearn.musicbeat.Loader.ArtistLoader;
import com.techweblearn.musicbeat.Loader.PlaylistLoader;
import com.techweblearn.musicbeat.Loader.SongLoader;
import com.techweblearn.musicbeat.Models.Playlist;
import com.techweblearn.musicbeat.Models.Song;
import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.Utils.Extras;
import com.techweblearn.musicbeat.Utils.PlaylistsUtil;
import com.techweblearn.musicbeat.Utils.PreferencesUtil;
import com.techweblearn.musicbeat.Utils.Util;
import com.techweblearn.musicbeat.View.MediaSeekBar;
import com.techweblearn.musicbeat.View.PagerTransformation.AccordionTransformer;
import com.techweblearn.musicbeat.View.PagerTransformation.BackgroundToForegroundTransformer;
import com.techweblearn.musicbeat.View.PagerTransformation.CubeInTransformer;
import com.techweblearn.musicbeat.View.PagerTransformation.CubeOutTransformer;
import com.techweblearn.musicbeat.View.PagerTransformation.DefaultTransformer;
import com.techweblearn.musicbeat.View.PagerTransformation.DepthPageTransformer;
import com.techweblearn.musicbeat.View.PagerTransformation.DrawFromBackTransformer;
import com.techweblearn.musicbeat.View.PagerTransformation.FlipHorizontalTransformer;
import com.techweblearn.musicbeat.View.PagerTransformation.FlipVerticalTransformer;
import com.techweblearn.musicbeat.View.PagerTransformation.ForegroundToBackgroundTransformer;
import com.techweblearn.musicbeat.View.PagerTransformation.ParallaxPageTransformer;
import com.techweblearn.musicbeat.View.PagerTransformation.RotateDownTransformer;
import com.techweblearn.musicbeat.View.PagerTransformation.RotateUpTransformer;
import com.techweblearn.musicbeat.View.PagerTransformation.StackTransformer;
import com.techweblearn.musicbeat.View.PagerTransformation.TabletTransformer;
import com.techweblearn.musicbeat.View.PagerTransformation.ZoomInTransformer;
import com.techweblearn.musicbeat.View.PagerTransformation.ZoomOutTranformer;
import com.techweblearn.musicbeat.View.PlayPauseDrawableDark;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.techweblearn.musicbeat.Fragment.SongFragment.ADD_NEW_PLAYLIST_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerLayout1 extends PlayerLayoutBase implements View.OnClickListener {


    @BindView(R.id.songs_viewpager) ViewPager viewPager;
    @BindView(R.id.favourite) ImageView favourite;
    @BindView(R.id.song_name) TextView song_name;
    @BindView(R.id.artist_name) TextView artist_name;
    @BindView(R.id.more_options) ImageView moreoption;
    @BindView(R.id.current_time) TextView current_time;
    @BindView(R.id.total_time) TextView total_time;
    @BindView(R.id.shuffel) ImageView shuffel;
    @BindView(R.id.prev) ImageView prev;
    @BindView(R.id.play_pause) FloatingActionButton play_pause;
    @BindView(R.id.next) ImageView next;
    @BindView(R.id.repeat) ImageView repeat;
    @BindView(R.id.seekbar) MediaSeekBar mediaSeekBar;
    @BindView(R.id.player)LinearLayout linearLayout;


    PlayPauseDrawableDark play_pause_drawable;
    Unbinder unbinder;
    ArrayList<MediaBrowserCompat.MediaItem> mediaItemArrayList;
    ArrayList<MediaSessionCompat.QueueItem> queueItemArrayList;
    SongCoverPagerAdapterCircular songCoverPagerAdapter;
    String currently_playing_mediaid;

    public PlayerLayout1() {

    }

    @Override
    public void onStart() {
        connectSeekBar(mediaSeekBar);
        connectTimer(current_time);
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_layout1, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favourite.setOnClickListener(this);
        moreoption.setOnClickListener(this);
        shuffel.setOnClickListener(this);
        prev.setOnClickListener(this);
        play_pause.setOnClickListener(this);
        next.setOnClickListener(this);
        repeat.setOnClickListener(this);
        connectViewPager(viewPager);
        play_pause_drawable = new PlayPauseDrawableDark(getActivity());
        play_pause.setImageDrawable(play_pause_drawable);
        setViewPagerTransformation();
        songCoverPagerAdapter = new SongCoverPagerAdapterCircular(getFragmentManager(), null);

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(getActivity(), new DecelerateInterpolator());
            mScroller.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (Exception e) {
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onplayState() {
        play_pause_drawable.setPause(true);
    }

    @Override
    public void onpauseState() {
        play_pause_drawable.setPlay(true);
    }

    @Override
    public void onnextSong() {


    }

    @Override
    public void onPrevSong() {


    }

    @Override
    public void onSongMetadataChanged(MediaMetadataCompat metadataCompat) {
        song_name.setText(metadataCompat.getText(MediaMetadataCompat.METADATA_KEY_TITLE));
        artist_name.setText(metadataCompat.getText(MediaMetadataCompat.METADATA_KEY_ARTIST));
        total_time.setText(Util.getReadableDurationString(metadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)));
        currently_playing_mediaid = metadataCompat.getDescription().getMediaId();

    }

    @Override
    public void onQueueLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {

        try {
            viewPager.setAdapter(new SongCoverPagerAdapter(getFragmentManager(), children));
            viewPager.setCurrentItem(Extras.getCurrentSongIndex(getActivity()));
            mediaItemArrayList = new ArrayList<>(children);
            initUI(mediaItemArrayList.get(Extras.getCurrentSongIndex(getActivity())));
        } catch (Exception e) {
        }

    }

    @Override
    public void onExtrasChanged(Bundle bundle) {
        if (bundle.getBoolean("isNowPlayingIndexDecrease")) {
            viewPager.setCurrentItem(viewPager.getCurrentItem());
        }
    }

    @Override
    public void onQueueChanged(String title, List<MediaSessionCompat.QueueItem> queueItems) {
        if (queueItemArrayList != null)
            queueItemArrayList.clear();
        viewPager.setAdapter(new SongCoverPagerQueueAdapter(getFragmentManager(), queueItems));
        viewPager.setCurrentItem(Extras.getCurrentSongIndex(getActivity()), false);
        queueItemArrayList = new ArrayList<>(queueItems);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.prev:
                skipToPrevious();
                break;
            case R.id.next:
                skipToNext();
                break;
            case R.id.play_pause:
                play_pause();
                break;
            case R.id.shuffel:
                switch (Extras.getShuffelMode(getActivity())) {
                    case PlaybackStateCompat.SHUFFLE_MODE_ALL:
                        shuffel.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_shuffle_none));
                        setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
                        break;
                    case PlaybackStateCompat.SHUFFLE_MODE_NONE:
                        shuffel.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_shuffle));
                        setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
                        break;
                }
                break;
            case R.id.repeat:
                switch (Extras.getRepeatMode(getActivity())) {
                    case PlaybackStateCompat.REPEAT_MODE_ALL:
                        repeat.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_repeat_black_24dp));
                        setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
                        break;
                    case PlaybackStateCompat.REPEAT_MODE_NONE:
                        repeat.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_repeat_one_black_24dp));
                        setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
                        break;
                    case PlaybackStateCompat.REPEAT_MODE_ONE:
                        repeat.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_repeat));
                        setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
                        break;
                }
                break;

            case R.id.more_options:
                showMoreOptionPopUpMenu();
                break;
            case R.id.favourite:

                break;
        }
    }

    private void showMoreOptionPopUpMenu() {
        PopupMenu popupMenu = new PopupMenu(getActivity(), moreoption);
        popupMenu.inflate(R.menu.player_layout_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_to_playlist:
                        showPopUpMenuForPlayList(moreoption, getCurrentSong(currently_playing_mediaid));
                        break;
                    case R.id.go_to_artist:

                        changeSlideUp();
                        Bundle extra = new Bundle();
                        extra.putString("artist_name", getCurrentSong(currently_playing_mediaid).artistName);
                        extra.putInt("artist_id", getCurrentSong(currently_playing_mediaid).artistId);
                        extra.putParcelableArrayList("albums", ArtistLoader.getArtist(getActivity(), getCurrentSong(currently_playing_mediaid).artistId).albums);

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

                        changeSlideUp();
                        Bundle extra1 = new Bundle();
                        extra1.putInt("album_id", getCurrentSong(currently_playing_mediaid).albumId);
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
                        SongDetailDialog.create(getCurrentSong(currently_playing_mediaid)).show(getActivity().getSupportFragmentManager(), "Details");
                        break;
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

    private Song getCurrentSong(String currently_playing_mediaid) {
        if(currently_playing_mediaid!=null)
        return SongLoader.getSong(getActivity(), Integer.parseInt(currently_playing_mediaid));
        return null;
    }

    private void initUI(MediaBrowserCompat.MediaItem song) {


        Song song1 = SongLoader.getSong(getActivity(), Integer.parseInt(song.getMediaId()));
        song_name.setText(song1.title);
        artist_name.setText(song1.artistName);
        total_time.setText(Util.getReadableDurationString(song1.duration));

        int current=PreferencesUtil.getCurrentPosition(getActivity());

        mediaSeekBar.setMax((int) song1.duration);
        mediaSeekBar.setProgress(current);
        current_time.setText(Util.getReadableDurationString(current));

        currently_playing_mediaid = String.valueOf(song1.id);

        switch (Extras.getRepeatMode(getActivity())) {
            case PlaybackStateCompat.REPEAT_MODE_NONE:
                repeat.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_repeat_black_24dp));
                break;
            case PlaybackStateCompat.REPEAT_MODE_ALL:
                repeat.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_repeat));
                break;
            case PlaybackStateCompat.REPEAT_MODE_ONE:
                repeat.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_repeat_one_black_24dp));
                break;
        }
        switch (Extras.getShuffelMode(getActivity())) {
            case PlaybackStateCompat.SHUFFLE_MODE_NONE:
                shuffel.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_shuffle_none));
                break;
            case PlaybackStateCompat.SHUFFLE_MODE_ALL:
                shuffel.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_shuffle));
                break;
        }
    }


    private void setViewPagerTransformation() {
        switch (PreferencesUtil.playerLayoutTransformation(getActivity())) {
            case 0:
                viewPager.setPageTransformer(false, new DefaultTransformer());
                break;
            case 1:
                viewPager.setPageTransformer(false, new AccordionTransformer());
                break;
            case 2:
                viewPager.setPageTransformer(false, new BackgroundToForegroundTransformer());
                break;
            case 3:
                viewPager.setPageTransformer(false, new CubeInTransformer());
                break;
            case 4:
                viewPager.setPageTransformer(false, new CubeOutTransformer());
                break;
            case 5:
                viewPager.setPageTransformer(false, new DepthPageTransformer());
                break;
            case 6:
                viewPager.setPageTransformer(false, new DrawFromBackTransformer());
                break;
            case 7:
                viewPager.setPageTransformer(false, new FlipHorizontalTransformer());
                break;
            case 8:
                viewPager.setPageTransformer(false, new FlipVerticalTransformer());
                break;
            case 9:
                viewPager.setPageTransformer(false, new ForegroundToBackgroundTransformer());
                break;
            case 10:
                viewPager.setPageTransformer(false, new StackTransformer());
                break;
            case 11:
                viewPager.setPageTransformer(false, new ParallaxPageTransformer());
                break;
            case 12:
                viewPager.setPageTransformer(false, new RotateDownTransformer());
                break;
            case 13:
                viewPager.setPageTransformer(false, new RotateUpTransformer());
                break;
            case 14:
                viewPager.setPageTransformer(false, new TabletTransformer());
                break;
            case 15:
                viewPager.setPageTransformer(false, new ZoomInTransformer());
                break;
            case 16:
                viewPager.setPageTransformer(false, new ZoomOutTranformer());
                break;
            default:
                viewPager.setPageTransformer(false, new DefaultTransformer());

        }
    }

}
