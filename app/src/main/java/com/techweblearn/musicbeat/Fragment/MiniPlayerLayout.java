package com.techweblearn.musicbeat.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.techweblearn.musicbeat.Base.PlayerLayoutBase;

import com.techweblearn.musicbeat.Glide.audiocover.AudioCover.AudioFileCover;
import com.techweblearn.musicbeat.Glide.audiocover.GlideApp;
import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.Service.MediaBrowserAdapter;
import com.techweblearn.musicbeat.Utils.Extras;
import com.techweblearn.musicbeat.View.MediaSeekBar;
import com.techweblearn.musicbeat.View.PlayPauseDrawable;
import com.techweblearn.musicbeat.provider.MediaItems;
import com.techweblearn.musicbeat.provider.SongPlayCountStore;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ART;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON;

/**
 * Created by Kunal on 10-12-2017.
 */

public class MiniPlayerLayout extends PlayerLayoutBase implements View.OnClickListener {


    @BindView(R.id.media_seekbar)MediaSeekBar mediaSeekBar;
    @BindView(R.id.song_image)ImageView song_art;
    @BindView(R.id.song_name)TextView song_name;
    @BindView(R.id.artist_name)TextView artist_name;
    @BindView(R.id.play_pause)ImageView play_pause;

    PlayPauseDrawable playPauseDrawable;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.mini_bottom_player,container,false);
        unbinder= ButterKnife.bind(this,view);
        play_pause.setOnClickListener(this);
     //   play_pause.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_play_button));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playPauseDrawable=new PlayPauseDrawable(getActivity());
        playPauseDrawable.setPlay(true);
        play_pause.setImageDrawable(playPauseDrawable);
    }


    @Override
    public void onStart() {
        connectSeekBar(mediaSeekBar);
        super.onStart();
    }

    @Override
    public void onplayState() {
        playPauseDrawable.setPause(true);
    }

    @Override
    public void onpauseState() {
        playPauseDrawable.setPlay(true);
    }

    @Override
    public void onnextSong() {

    }

    @Override
    public void onPrevSong() {

    }

    @Override
    public void updatePlayStoreCount(long media_id) {
        SongPlayCountStore.getInstance(getActivity()).bumpPlayCount(media_id);
    }

    @Override
    public void onSongMetadataChanged(MediaMetadataCompat metadataCompat) {
        song_name.setText(metadataCompat.getText(MediaMetadataCompat.METADATA_KEY_TITLE));
        artist_name.setText(metadataCompat.getText(MediaMetadataCompat.METADATA_KEY_ARTIST));
        song_art.setImageBitmap(metadataCompat.getBitmap(METADATA_KEY_ART));
    }






    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onQueueLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
        try {
            song_name.setText(children.get(Extras.getCurrentSongIndex(getActivity())).getDescription().getTitle());
            artist_name.setText(children.get(Extras.getCurrentSongIndex(getActivity())).getDescription().getSubtitle());
            GlideApp.with(getActivity())
                    .load(new AudioFileCover(String.valueOf(children.get(Extras.getCurrentSongIndex(getActivity())).getDescription().getIconUri())))
                    .thumbnail(0.2f)
                    .into(song_art);
        }catch (Exception e){}

    }

    @Override
    public void onClick(View view) {
        play_pause();
    }
}
