package com.techweblearn.musicbeat.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.techweblearn.musicbeat.Glide.audiocover.AudioCover.AudioFileCover;
import com.techweblearn.musicbeat.Glide.audiocover.GlideApp;
import com.techweblearn.musicbeat.Loader.AlbumLoader;
import com.techweblearn.musicbeat.Loader.ArtistLoader;
import com.techweblearn.musicbeat.Loader.SongLoader;
import com.techweblearn.musicbeat.Models.Playlist;
import com.techweblearn.musicbeat.Models.Song;
import com.techweblearn.musicbeat.Notification.MediaNotificationManager;
import com.techweblearn.musicbeat.Utils.Extras;
import com.techweblearn.musicbeat.Utils.PreferencesUtil;
import com.techweblearn.musicbeat.provider.MediaItems;
import com.techweblearn.musicbeat.provider.MusicPlaybackQueueStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.techweblearn.musicbeat.Utils.Constants.ADD_ALL_SONGS_TO_QUEUE;
import static com.techweblearn.musicbeat.Utils.Constants.ADD_SONG_LIST_TO_QUEUE;
import static com.techweblearn.musicbeat.Utils.Constants.ITEM_REMOVED;
import static com.techweblearn.musicbeat.Utils.Constants.PLAY_ALBUM;
import static com.techweblearn.musicbeat.Utils.Constants.PLAY_ARTIST;
import static com.techweblearn.musicbeat.Utils.Constants.PLAY_NEXT;
import static com.techweblearn.musicbeat.Utils.Constants.PLAY_SINGLE_SONG;
import static com.techweblearn.musicbeat.Utils.Constants.PLAY_SONGLIST;
import static com.techweblearn.musicbeat.Utils.Constants.REMOVE_ALL;
import static com.techweblearn.musicbeat.provider.MediaItems.getAllMediaItemsFromQueue;

/**
 * Created by Kunal on 03-12-2017.
 */

public class MusicPlayBackService extends MediaBrowserServiceCompat {


    public static final String CHANGE_TRACK_POSITION = "change_track_position";
    private static final String TAG = MusicPlayBackService.class.getSimpleName();
    public static int queueIndex = -1;
    private MediaPlayerListener.ServiceManager mServiceManager;
    private MediaSessionCompat mSession;
    private PlayerAdapter mPlayback;
    private MediaNotificationManager mMediaNotificationManager;
    private MediaSessionCallback mCallback;
    private boolean mServiceInStartedState;
    private int mQueueIndex = -1;
    private List<MediaSessionCompat.QueueItem> playingQueue = new ArrayList<>();
    private List<MediaSessionCompat.QueueItem> originalQueue = new ArrayList<>();
    private PowerManager.WakeLock wakeLock;


    public MusicPlayBackService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();


        final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        wakeLock.setReferenceCounted(false);

        mQueueIndex = Extras.getCurrentSongIndex(getApplicationContext());
        mSession = new MediaSessionCompat(this, "MusicService");
        mCallback = new MediaSessionCallback();
        mSession.setCallback(mCallback);
        mSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        setSessionToken(mSession.getSessionToken());

        mMediaNotificationManager = new MediaNotificationManager(this);

        mPlayback = new MediaPlayerAdapter(this, new MediaPlayerListener());
        mPlayback.seekTo(PreferencesUtil.getCurrentPosition(getApplicationContext()));
        Log.d(TAG, "onCreate: MusicService creating MediaSession, and MediaNotificationManager");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mMediaNotificationManager.onDestroy();
        mPlayback.stop();
        mSession.release();
        if (wakeLock.isHeld())
            wakeLock.release();
        Log.d(TAG, "onDestroy: MediaPlayerAdapter stopped, and MediaSession released");
    }

    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName,
                                 int clientUid,
                                 Bundle rootHints) {
        return new BrowserRoot(MediaItems.getRoot(), null);
    }

    @Override
    public void onLoadChildren(
            @NonNull final String parentMediaId,
            @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result) {
        List<MediaBrowserCompat.MediaItem> mediaItems = getAllMediaItemsFromQueue(getApplicationContext(), MusicPlaybackQueueStore.getInstance(getApplicationContext()).getSavedOriginalPlayingQueue());
        result.sendResult(mediaItems);
        originalQueue.clear();
        for (MediaBrowserCompat.MediaItem mediaItem : mediaItems) {
            originalQueue.add(new MediaSessionCompat.QueueItem(mediaItem.getDescription(), mediaItem.getDescription().hashCode()));
        }

    }

    void changePosition(int from, int to) {
        MediaSessionCompat.QueueItem fromValue = originalQueue.get(from);
        int delta = from < to ? 1 : -1;
        for (int i = from; i != to; i += delta) {
            originalQueue.set(i, originalQueue.get(i + delta));
        }
        originalQueue.set(to, fromValue);

    }

    public static class SleepTimer extends BroadcastReceiver {


        MediaBrowserAdapter mediaBrowserAdapter;

        public SleepTimer() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Sleep Timer", Toast.LENGTH_SHORT).show();
            mediaBrowserAdapter = new MediaBrowserAdapter(context.getApplicationContext());

            mediaBrowserAdapter.addListener(new MediaBrowserAdapter.MediaBrowserChangeListener() {
                @Override
                public void onConnected(@Nullable MediaControllerCompat mediaController) {
                    mediaController.getTransportControls().pause();
                }
            });
            mediaBrowserAdapter.onStart();
        }
    }

    public class MediaSessionCallback extends MediaSessionCompat.Callback {

        private MediaDescriptionCompat mPreparedMedia;


        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {
            if (isAlreadyInList(description.getMediaId()) > 0) {
                Toast.makeText(getApplicationContext(), "Already in Queue", Toast.LENGTH_SHORT).show();
                return;
            }
            originalQueue.add(new MediaSessionCompat.QueueItem(description, description.hashCode()));
            playingQueue.add(new MediaSessionCompat.QueueItem(description, description.hashCode()));
            mQueueIndex = (mQueueIndex == -1) ? 0 : mQueueIndex;
            Extras.saveCurrentSongIndex(getApplicationContext(), mQueueIndex);
            MusicPlaybackQueueStore musicPlaybackQueueStore = new MusicPlaybackQueueStore(getApplicationContext());
            mSession.setQueue(originalQueue);
            MusicPlaybackQueueStore.SaveOriginalQueueAsyncTask saveOriginalQueueAsyncTask = musicPlaybackQueueStore.new SaveOriginalQueueAsyncTask(getApplicationContext());
            saveOriginalQueueAsyncTask.execute(originalQueue);

        }


        public void onRemoveQueueItem(int index) {
            originalQueue.remove(index);
            if (index < mQueueIndex) {
                mQueueIndex--;
                Extras.saveCurrentSongIndex(getApplicationContext(), mQueueIndex);
            } else {
                mPreparedMedia = null;
                onPlay();
            }
            mSession.setQueue(originalQueue);
            mSession.setQueueTitle("Now Playing");

            MusicPlaybackQueueStore musicPlaybackQueueStore = new MusicPlaybackQueueStore(getApplicationContext());
            musicPlaybackQueueStore.new SaveOriginalQueueAsyncTask(getApplicationContext()).execute(originalQueue);


        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            MusicPlaybackQueueStore musicPlaybackQueueStore = new MusicPlaybackQueueStore(getApplicationContext());
            switch (action) {
                case REMOVE_ALL:
                    originalQueue.clear();
                    mSession.setQueue(originalQueue);
                    break;

                case ITEM_REMOVED:
                    onRemoveQueueItem(extras.getInt("position"));

                case CHANGE_TRACK_POSITION:
                    int from = extras.getInt("from");
                    int to = extras.getInt("to");
                    break;
                case ADD_SONG_LIST_TO_QUEUE:
                    extras.setClassLoader(Playlist.class.getClassLoader());
                    ArrayList<Song> song_list = extras.getParcelableArrayList("song_list");
                    for (MediaSessionCompat.QueueItem queueItem : MediaItems.getAllQueueItemFromSongList(getApplicationContext(), song_list)) {
                        originalQueue.add(queueItem);
                    }
                    mSession.setQueue(originalQueue);
                    musicPlaybackQueueStore.new SaveOriginalQueueAsyncTask(getApplicationContext()).execute(originalQueue);
                    break;

                case PLAY_SONGLIST:
                    mQueueIndex = 0;
                    extras.setClassLoader(Playlist.class.getClassLoader());
                    ArrayList<Song> song_list1 = extras.getParcelableArrayList("song_list");
                    originalQueue.clear();
                    for (MediaSessionCompat.QueueItem queueItem : MediaItems.getAllQueueItemFromSongList(getApplicationContext(), song_list1)) {
                        originalQueue.add(queueItem);
                    }
                    mSession.setQueue(originalQueue);

                    musicPlaybackQueueStore.new SaveOriginalQueueAsyncTask(getApplicationContext()).execute(originalQueue);
                    if (originalQueue.size() > 0) {
                        onPlay();
                    }
                    break;

                case ADD_ALL_SONGS_TO_QUEUE:
                    int playing_index = extras.getInt("position");

                    if (originalQueue.size() == SongLoader.getAllSongs(getApplicationContext()).size()) {
                        mCallback.onSkipToQueueItem(playing_index);
                        return;
                    }
                    originalQueue.clear();
                    originalQueue = MediaItems.getAllQueueItemFromSongList(getApplicationContext(), SongLoader.getAllSongs(getApplicationContext()));
                    mSession.setQueue(originalQueue);
                    mCallback.onSkipToQueueItem(playing_index);
                    musicPlaybackQueueStore.new SaveOriginalQueueAsyncTask(getApplicationContext()).execute(originalQueue);
                    break;

                case PLAY_SINGLE_SONG:
                    extras.setClassLoader(Song.class.getClassLoader());
                    Song song = extras.getParcelable("song");
                    playSingleSong(song);
                    break;

                case PLAY_NEXT:
                    addToNext(String.valueOf(extras.getInt("media_id")));
                    break;
                case PLAY_ARTIST:
                    originalQueue.clear();
                    originalQueue.addAll(MediaItems.getAllQueueItemFromSongList(getApplicationContext(), ArtistLoader.getArtist(getApplicationContext(), extras.getInt("artist_id")).getSongs()));
                    mQueueIndex = 0;
                    mSession.setQueue(originalQueue);
                    mPreparedMedia = null;
                    onPlay();
                    musicPlaybackQueueStore.new SaveOriginalQueueAsyncTask(getApplicationContext()).execute(originalQueue);
                    break;
                case PLAY_ALBUM:
                    originalQueue.clear();
                    originalQueue.addAll(MediaItems.getAllQueueItemFromSongList(getApplicationContext(), AlbumLoader.getAlbum(getApplicationContext(), extras.getInt("album_id")).songs));
                    mQueueIndex = 0;
                    mSession.setQueue(originalQueue);
                    mPreparedMedia = null;
                    onPlay();
                    musicPlaybackQueueStore.new SaveOriginalQueueAsyncTask(getApplicationContext()).execute(originalQueue);
                    break;
            }

        }

        private void addToNext(String mediaId) {
            int index = isAlreadyInList(mediaId);
            if (index < 0) {
                originalQueue.add(mQueueIndex + 1, MediaItems.queueItemFromMediaId(getApplicationContext(), mediaId));
            } else {
                if (index < mQueueIndex) {
                    mQueueIndex--;
                    Extras.saveCurrentSongIndex(getApplicationContext(), mQueueIndex);
                    MediaSessionCompat.QueueItem queueItem = originalQueue.remove(index);
                    originalQueue.add(mQueueIndex + 1, queueItem);
                } else if (index == mQueueIndex) {
                    Toast.makeText(getApplicationContext(), "Already Playing", Toast.LENGTH_SHORT).show();
                } else {
                    MediaSessionCompat.QueueItem queueItem = originalQueue.remove(index);
                    originalQueue.add(mQueueIndex + 1, queueItem);
                }

            }
            mSession.setQueue(originalQueue);
            MusicPlaybackQueueStore musicPlaybackQueueStore = new MusicPlaybackQueueStore(getApplicationContext());
            musicPlaybackQueueStore.new SaveOriginalQueueAsyncTask(getApplicationContext()).execute(originalQueue);
        }


        //Check Song is Already in List Then Return index else return any negative value
        private int isAlreadyInList(String media_id) {
            for (int i = 0; i < originalQueue.size(); i++) {
                if (media_id.equals(originalQueue.get(i).getDescription().getMediaId())) {
                    return i;
                }
            }
            return -1;
        }

        public void playSingleSong(Song song) {

            mPreparedMedia = null;
            originalQueue.clear();
            originalQueue.add(MediaItems.queueItemFromSong(getApplicationContext(), song));
            mQueueIndex = 0;
            onPlay();
            mSession.setQueue(originalQueue);
            MusicPlaybackQueueStore musicPlaybackQueueStore = new MusicPlaybackQueueStore(getApplicationContext());
            musicPlaybackQueueStore.new SaveOriginalQueueAsyncTask(getApplicationContext()).execute(originalQueue);
        }

        @Override
        public void onSkipToQueueItem(long id) {
            mPreparedMedia = null;
            mQueueIndex = (int) id;
            queueIndex = mQueueIndex;
            onPlay();
        }


        @SuppressLint("StaticFieldLeak")
        @Override
        public void onPrepare() {

            if (mQueueIndex < 0 && originalQueue.isEmpty()) {
                return;
            }
            mPreparedMedia = originalQueue.get(mQueueIndex).getDescription();

            if (!mSession.isActive()) {
                mSession.setActive(true);
            }
            updateMetaData(mPreparedMedia);
        }

        public void onPrepareNext() {
            if (mQueueIndex + 1 < 0 && originalQueue.isEmpty()) {
                return;
            }
            final String mediaId = originalQueue.get(mQueueIndex + 1).getDescription().getMediaId();

        }

        @Override
        public void onPlay() {

            if (!isReadyToPlay()) {
                return;
            }
            if (mPreparedMedia == null) {
                onPrepare();
            }
            Extras.saveCurrentSongIndex(getApplicationContext(), mQueueIndex);
            mPlayback.play();

        }

        @Override
        public void onPause() {
            mPlayback.pause();
        }

        @Override
        public void onStop() {
            mPlayback.stop();
            mSession.setActive(false);
        }

        @Override
        public void onSkipToNext() {

            try {
                mQueueIndex = (++mQueueIndex % originalQueue.size());
                mPreparedMedia = null;
                onPlay();
            } catch (Exception e) {
            }
        }

        @Override
        public void onSetRepeatMode(int repeatMode) {
            Extras.setRepeatMode(getApplicationContext(), repeatMode);
        }

        @Override
        public void onSetShuffleMode(int shuffleMode) {
            Extras.setPrefShuffelMode(getApplicationContext(), shuffleMode);
            shuffelPlayingQueue(shuffleMode);
        }

        @Override
        public void onSkipToPrevious() {
            try {
                mQueueIndex = mQueueIndex > 0 ? mQueueIndex - 1 : originalQueue.size() - 1;
                mPreparedMedia = null;
                onPlay();
            } catch (Exception e) {

            }
        }

        @Override
        public void onSeekTo(long pos) {
            mPlayback.seekTo(pos);
        }

        private boolean isReadyToPlay() {
            return (!originalQueue.isEmpty());
        }

        private boolean isLastSong() {
            return mQueueIndex == originalQueue.size() - 1;
        }

        public int getRepeatMode() {
            return Extras.getRepeatMode(getApplicationContext());
        }

        public void updateMetaData(final MediaDescriptionCompat metadataCompat) {

            final MediaMetadataCompat mediaMetadataCompat = MediaItems.getMediaMetaDataFromMediaId(getApplicationContext(), metadataCompat.getMediaId());
            final MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder(mediaMetadataCompat);
            GlideApp.with(getApplicationContext())
                    .asBitmap()
                    .override(300, 300)
                    .load(new AudioFileCover(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ART_URI)))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, resource);

                            MediaMetadataCompat mediaMetadataCompat1 = builder.build();

                            mSession.setMetadata(mediaMetadataCompat1);
                            mPreparedMedia = mediaMetadataCompat1.getDescription();
                            mPlayback.playFromMedia(mPreparedMedia);
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            mSession.setMetadata(builder.build());
                            mPlayback.playFromMedia(mPreparedMedia);
                        }
                    });
        }

        public void shuffelPlayingQueue(int mode) {
            if (PlaybackStateCompat.SHUFFLE_MODE_NONE == mode) {
                MediaSessionCompat.QueueItem queueItem = null;

                if (mQueueIndex < originalQueue.size()) {
                    queueItem = originalQueue.remove(mQueueIndex);
                    Collections.sort(originalQueue, new Comparator<MediaSessionCompat.QueueItem>() {
                        @Override
                        public int compare(MediaSessionCompat.QueueItem o1, MediaSessionCompat.QueueItem o2) {
                            return o1.getDescription().getTitle().toString().compareToIgnoreCase(o2.getDescription().getTitle().toString());
                        }

                    });
                    originalQueue.add(0, queueItem);
                }
            } else {
                MediaSessionCompat.QueueItem queueItem = originalQueue.remove(mQueueIndex);
                Collections.shuffle(originalQueue);
                originalQueue.add(0, queueItem);
            }
            mSession.setQueue(originalQueue);
            mQueueIndex = 0;
            Extras.saveCurrentSongIndex(getApplicationContext(), mQueueIndex);
        }
    }

    // MediaPlayerAdapter Callback: MediaPlayerAdapter state -> MusicService.
    public class MediaPlayerListener extends PlaybackInfoListener {

        MediaPlayerListener() {
            mServiceManager = new ServiceManager();
        }

        @Override
        public void onPlaybackCompleted() {
            switch (Extras.getRepeatMode(getApplicationContext())) {
                case PlaybackStateCompat.REPEAT_MODE_ONE:
                    mCallback.onPlay();
                    return;
                case PlaybackStateCompat.REPEAT_MODE_NONE:
                    if (mCallback.isLastSong()) {
                        mCallback.onPause();
                        return;
                    } else {
                        mCallback.onSkipToNext();
                    }
                    break;
                case PlaybackStateCompat.REPEAT_MODE_ALL:
                default:
                    mCallback.onSkipToNext();
            }
        }


        @Override
        public void onPlaybackStateChange(PlaybackStateCompat state) {

            mSession.setPlaybackState(state);
            // Manage the started state of this service.
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mServiceManager.moveServiceToStartedState(state);
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    mServiceManager.updateNotificationForPause(state);
                    break;
                case PlaybackStateCompat.STATE_STOPPED:
                    mServiceManager.moveServiceOutOfStartedState(state);
                    break;
            }
        }

        class ServiceManager {

            private void moveServiceToStartedState(PlaybackStateCompat state) {
                Notification notification =
                        mMediaNotificationManager.getNotification(
                                mPlayback.getCurrentMedia(), state, getSessionToken());

                if (!mServiceInStartedState) {
                    wakeLock.acquire();
                    ContextCompat.startForegroundService(
                            MusicPlayBackService.this,
                            new Intent(MusicPlayBackService.this, MusicPlayBackService.class));
                    startService(new Intent(getApplicationContext(), MusicPlayBackService.class));
                    mServiceInStartedState = true;
                }

                startForeground(MediaNotificationManager.NOTIFICATION_ID, notification);
            }

            private void updateNotificationForPause(PlaybackStateCompat state) {
                stopForeground(false);
                Notification notification =
                        mMediaNotificationManager.getNotification(
                                mPlayback.getCurrentMedia(), state, getSessionToken());
                mMediaNotificationManager.getNotificationManager()
                        .notify(MediaNotificationManager.NOTIFICATION_ID, notification);
            }

            private void moveServiceOutOfStartedState(PlaybackStateCompat state) {
                if (wakeLock.isHeld())
                    wakeLock.release();
                stopForeground(true);
                mServiceInStartedState = false;
            }
        }
    }


}
