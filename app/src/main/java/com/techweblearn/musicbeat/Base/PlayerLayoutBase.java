package com.techweblearn.musicbeat.Base;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;
import android.widget.TextView;

import com.techweblearn.musicbeat.Service.MediaBrowserAdapter;
import com.techweblearn.musicbeat.Utils.Extras;
import com.techweblearn.musicbeat.Utils.Util;
import com.techweblearn.musicbeat.View.MediaSeekBar;
import com.techweblearn.musicbeat.provider.HistoryStore;
import com.techweblearn.musicbeat.provider.SongPlayCountStore;

import java.util.List;

/**
 * Created by Kunal on 08-12-2017.
 */

public abstract class PlayerLayoutBase extends Fragment implements SeekBar.OnSeekBarChangeListener, ViewPager.OnPageChangeListener {


    public PlayerLayoutBase() {

    }

    String media_id;
    long updateSongCountThresholdValue= 60000 ;//1 MIN
    boolean startTouch=false;
    ValueAnimator valueAnimator;
    ViewPager viewPager;
    MediaBrowserAdapter mediaBrowserAdapter;
    int PAUSE = PlaybackStateCompat.STATE_PAUSED;
    int PLAY = PlaybackStateCompat.STATE_PLAYING;
    int CURRENT_STATE=PlaybackStateCompat.STATE_PAUSED;
    int STATE;
    MediaSeekBar mediaSeekBar;
    int maxProgress=0;
    TextView currentTimer;
    boolean isSongPlayCountIsUpdated=false; // IF This Update Then We Not Need To Update Anymore when Their Progress IS Running
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaBrowserAdapter = new MediaBrowserAdapter(getContext());
        mediaBrowserAdapter.addListener(new MediaBrowserListener());

    }

    public void pause() {
        mediaBrowserAdapter.getTransportControls().pause();
    }

    public void play() {
        mediaBrowserAdapter.getTransportControls().play();
    }

    public void play_pause()
    {
        if(CURRENT_STATE==PlaybackStateCompat.STATE_PAUSED) {
            mediaBrowserAdapter.getTransportControls().play();
        }else if(CURRENT_STATE==PlaybackStateCompat.STATE_STOPPED){
            mediaBrowserAdapter.onStart();
            mediaBrowserAdapter.getTransportControls().play();
        }else mediaBrowserAdapter.getTransportControls().pause();
    }


    public void seekTo(long ms) {
        mediaBrowserAdapter.getTransportControls().seekTo(ms);
    }

    public void skipToNext() {
        mediaBrowserAdapter.getTransportControls().skipToNext();
    }

    public void skipToPrevious() {
        mediaBrowserAdapter.getTransportControls().skipToPrevious();
    }

    public void setShuffleMode(int shuffleMode) {
        mediaBrowserAdapter.getTransportControls().setShuffleMode(shuffleMode);
    }

    public void setRepeatMode(int repeatMode) {
        mediaBrowserAdapter.getTransportControls().setRepeatMode(repeatMode);
    }



    public void setToFavourite(MediaMetadataCompat metadataCompat)
    {
        mediaBrowserAdapter.getTransportControls();
    }

    public void updatePlayStoreCount(long media_id)
    {

    }

    public void connectSeekBar(MediaSeekBar mediaSeekBar)
    {
        this.mediaSeekBar=mediaSeekBar;
        this.mediaSeekBar.setOnSeekBarChangeListener(this);
    }
    public void connectTimer(TextView currentTimer)
    {
        this.currentTimer=currentTimer;
    }
    public void connectViewPager(ViewPager viewPager){
        this.viewPager=viewPager;
        this.viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(final int position) {

            if(Extras.getCurrentSongIndex(getActivity())<position)
            {
                mediaBrowserAdapter.getTransportControls().skipToNext();
            }
            if(Extras.getCurrentSongIndex(getActivity())>position)
            {
                mediaBrowserAdapter.getTransportControls().skipToPrevious();
            }
            viewPager.setCurrentItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public abstract void onplayState();

    public abstract void onpauseState();

    public abstract void onnextSong();

    public abstract void onPrevSong();

    public abstract void onSongMetadataChanged(MediaMetadataCompat metadataCompat);

    public void onQueueLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children)
    {

    }

    public void onQueueChanged(String title, List<MediaSessionCompat.QueueItem>queueItems)
    {
    }

    public void onExtrasChanged(Bundle bundle){

    }
    private class MediaBrowserListener extends MediaBrowserAdapter.MediaBrowserChangeListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onConnected(@Nullable MediaControllerCompat mediaController) {
            super.onConnected(mediaController);

        }

        @Override
        public void onMetadataChanged(@Nullable MediaMetadataCompat mediaMetadata) {
            //super.onMetadataChanged(mediaMetadata);
            if(mediaMetadata!=null)
            {

            maxProgress= (int) mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
            mediaSeekBar.setMax(maxProgress);
            onSongMetadataChanged(mediaMetadata);
            media_id=mediaMetadata.getDescription().getMediaId();
            }
            if(viewPager!=null)
            onPageSelected(Extras.getCurrentSongIndex(getActivity()));
            isSongPlayCountIsUpdated=false;

        }

        @Override
        public void onPlaybackStateChanged(@Nullable PlaybackStateCompat playbackState) {
            super.onPlaybackStateChanged(playbackState);
            if(valueAnimator!=null)
            {
                valueAnimator.cancel();
                valueAnimator=null;
            }


                int current= (int) (playbackState != null ? playbackState.getPosition() : 0);
                if(maxProgress>current)
                valueAnimator=ValueAnimator.ofInt(current,maxProgress).setDuration(maxProgress-current);
                else {
                    valueAnimator = ValueAnimator.ofInt(0, maxProgress).setDuration(maxProgress);
                }
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.addUpdateListener(this);
                valueAnimator.start();



            if (playbackState != null) {
                STATE = playbackState.getState();
                CURRENT_STATE=STATE;
                if (STATE == PLAY)
                {
                    onplayState();
                }

                else
                {
                    if (STATE == PAUSE) {
                        onpauseState();
                        valueAnimator.pause();
                    }
                }
            }else onpauseState();
        }


        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
              onQueueLoaded(parentId, children);
        }

        @Override
        public void onQueueUpdated(String title, List<MediaSessionCompat.QueueItem> queueItems) {
            onQueueChanged(title,queueItems);
        }


        @Override
        public void onExtras(Bundle bundle) {
            onExtrasChanged(bundle);
        }

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            if(startTouch)
            {
               valueAnimator.cancel();
               return;
            }

            mediaSeekBar.setProgress((Integer) valueAnimator.getAnimatedValue());
            if(currentTimer!=null)
            currentTimer.setText(Util.getReadableDurationString(((Integer) valueAnimator.getAnimatedValue()).longValue()));
            if(updateSongCountThresholdValue< ((Integer) valueAnimator.getAnimatedValue()).longValue()&&!isSongPlayCountIsUpdated)
            {
                isSongPlayCountIsUpdated=true;
                updatePlayStoreCount(Long.parseLong(media_id));
            }
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        startTouch=true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        startTouch=false;
        mediaBrowserAdapter.getTransportControls().seekTo(seekBar.getProgress());
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
    public void onDestroy() {
        super.onDestroy();

        if(valueAnimator!=null) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
        if(mediaSeekBar!=null)
        mediaSeekBar.disconnectController();
    }
}
