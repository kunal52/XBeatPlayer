package com.techweblearn.musicbeat.Fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.techweblearn.musicbeat.Adapters.QueueSongAdapter;
import com.techweblearn.musicbeat.Glide.audiocover.AudioCover.AudioFileCover;
import com.techweblearn.musicbeat.Glide.audiocover.GlideApp;
import com.techweblearn.musicbeat.Helper.OnStartDragListener;
import com.techweblearn.musicbeat.Helper.SimpleItemTouchHelperCallback;
import com.techweblearn.musicbeat.Loader.PlaylistLoader;
import com.techweblearn.musicbeat.Loader.SongLoader;
import com.techweblearn.musicbeat.Models.Playlist;
import com.techweblearn.musicbeat.Models.Song;
import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.Service.MediaBrowserAdapter;
import com.techweblearn.musicbeat.Utils.Extras;
import com.techweblearn.musicbeat.Utils.ImageCache;
import com.techweblearn.musicbeat.Utils.PlaylistsUtil;
import com.techweblearn.musicbeat.Utils.Util;
import com.techweblearn.musicbeat.provider.MediaItems;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.techweblearn.musicbeat.Fragment.SongFragment.ADD_NEW_PLAYLIST_ID;
import static com.techweblearn.musicbeat.Utils.Constants.ITEM_REMOVED;
import static com.techweblearn.musicbeat.Utils.Constants.REMOVE_ALL;

/**
 * A simple {@link Fragment} subclass.
 */
public class QueueSongFragment extends Fragment implements OnStartDragListener, QueueSongAdapter.Callback,View.OnClickListener {

    public static final String CHANGE_TRACK_POSITION = "change_track_position";
    @BindView(R.id.queue_recyclerview) RecyclerView queue_recyclerview;
    @BindView(R.id.song_name) TextView song_name;
    @BindView(R.id.artist_name) TextView artist_name;
    @BindView(R.id.song_image) ImageView song_image;
    @BindView(R.id.shuffel)ImageView shuffel;
    @BindView(R.id.add_to_playlist)ImageView add_to_playlist;
    @BindView(R.id.clear_queue)ImageView clear;

    QueueSongAdapter queueSongAdapter;
    Unbinder unbinder;
    MediaBrowserAdapter mediaBrowserAdapter;
    ArrayList<MediaBrowserCompat.MediaItem> songArrayList;
    List<MediaSessionCompat.QueueItem>queueItemArrayList;
    private ItemTouchHelper mItemTouchHelper;

    public QueueSongFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaBrowserAdapter = new MediaBrowserAdapter(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_queue_song, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        queueSongAdapter = new QueueSongAdapter(getActivity(), new ArrayList<MediaBrowserCompat.MediaItem>(), null);
        queueSongAdapter.setCallback(this);

        queue_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        queue_recyclerview.setAdapter(queueSongAdapter);

        mediaBrowserAdapter.onStart();

        mediaBrowserAdapter.addListener(new MediaBroswerListener());

        songArrayList = new ArrayList<>();
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(queueSongAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(queue_recyclerview);
        shuffel.setOnClickListener(this);
        add_to_playlist.setOnClickListener(this);
        clear.setOnClickListener(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaBrowserAdapter.onStop();
        unbinder.unbind();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }


    @Override
    public void onItemRemoved(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        mediaBrowserAdapter.getTransportControls().sendCustomAction(ITEM_REMOVED, bundle);
    }

    @Override
    public void onItemPositionChange(int from, int to) {
        Bundle bundle = new Bundle();
        bundle.putInt("from", from);
        bundle.putInt("to", to);
        mediaBrowserAdapter.getTransportControls().sendCustomAction(CHANGE_TRACK_POSITION, bundle);
    }

    @Override
    public void onItemClicked(int position) {
        mediaBrowserAdapter.getTransportControls().skipToQueueItem(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.shuffel:
                switch (Extras.getShuffelMode(getActivity()))
                {
                    case PlaybackStateCompat.SHUFFLE_MODE_ALL:
                        shuffel.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_shuffle_none));
                        mediaBrowserAdapter.getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
                        break;
                    case PlaybackStateCompat.SHUFFLE_MODE_NONE:
                        shuffel.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_shuffle));
                        mediaBrowserAdapter.getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
                        break;
                }
                break;
            case R.id.add_to_playlist:

                showPopUpMenuForPlayList(v);
                break;
            case R.id.clear_queue:
                mediaBrowserAdapter.getTransportControls().sendCustomAction(REMOVE_ALL,null);
                break;
        }
    }


    private void showPopUpMenuForPlayList(View view)
    {
        final ArrayList<Playlist>playlists= PlaylistLoader.getAllPlaylists(getActivity());
        PopupMenu popupMenu=new PopupMenu(getActivity(),view);
        for(int i=0;i<playlists.size();i++)
        {
            popupMenu.getMenu().add(1,playlists.get(i).id,i,playlists.get(i).name);
        }
        popupMenu.getMenu().add(1,ADD_NEW_PLAYLIST_ID,playlists.size(),"Add New Playlist");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==ADD_NEW_PLAYLIST_ID)
                {
                    AddPlayListDialogFragment.create().show(getActivity().getSupportFragmentManager(),"Create Playlist");
                    return false;
                }
                for (int i=0;i<playlists.size();i++)
                {
                    if (playlists.get(i).id==item.getItemId())
                    {

                        if(queueItemArrayList==null)
                        {
                            final int finalI = i;
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                                    PlaylistsUtil.addToPlaylist(getActivity(),MediaItems.getSongListFromMediaItemList(getActivity(),songArrayList),playlists.get(finalI).id,true);
                                }
                            });

                        }
                        else
                        {
                            final int finalI = i;
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                                    PlaylistsUtil.addToPlaylist(getActivity(),MediaItems.getSongListFromQueueItemList(getActivity(),queueItemArrayList),playlists.get(finalI).id,true);
                                }
                            });
                        }
                        return false;
                    }
                }
                return false;
            }
        });
        popupMenu.show();
    }

    class MediaBroswerListener extends MediaBrowserAdapter.MediaBrowserChangeListener {
        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
            queueSongAdapter.notifyDataSetChanged(new ArrayList<>(children));
            songArrayList=new ArrayList<>(children);
            GlideApp.with(getActivity())
                    .load(new AudioFileCover(String.valueOf(children.get(Extras.getCurrentSongIndex(getActivity())).getDescription().getIconUri())))
                    .thumbnail(0.2f)
                    .into(song_image);
        }

        @Override
        public void onMetadataChanged(@Nullable MediaMetadataCompat mediaMetadata) {

            queueSongAdapter.notifyPlayingSongChanged(Extras.getCurrentSongIndex(getActivity()));
            if (mediaMetadata != null) {
                song_name.setText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
                artist_name.setText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
                song_image.setImageBitmap(mediaMetadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ART));
            }
        }

        @Override
        public void onPlaybackStateChanged(@Nullable PlaybackStateCompat playbackState) {

        }

        @Override
        public void onQueueUpdated(String title, List<MediaSessionCompat.QueueItem> queueItems) {
            queueItemArrayList=queueItems;
            queueSongAdapter.notifyDataSetChanged(queueItems);
            queueSongAdapter.notifyPlayingSongChanged(Extras.getCurrentSongIndex(getActivity()));
        }
    }
}
