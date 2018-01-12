package com.techweblearn.musicbeat.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.media.MediaBrowserCompat.MediaItem;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.techweblearn.musicbeat.Helper.ItemTouchHelperAdapter;
import com.techweblearn.musicbeat.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kunal on 09-12-2017.
 */

public class QueueSongAdapter extends RecyclerView.Adapter<QueueSongAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private int playingQueueIndex = -1;
    private Context context;
    private ArrayList<MediaItem> songs;
    private List<MediaSessionCompat.QueueItem> queueItemList;
    private Callback listener;
    private boolean darkTheme = false;

    public QueueSongAdapter(Context context, ArrayList<MediaItem> songs, List<MediaSessionCompat.QueueItem> queueItemList) {
        this.context = context;
        this.songs = songs;
        this.queueItemList = queueItemList;
        darkTheme = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_theme", false);
    }

    public static int getPrimaryColor(final Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
        return value.data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.queue_song_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (queueItemList == null) return 0;
        else return 1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (playingQueueIndex == position)
            if (darkTheme)
                holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.curently_playing_gradient_dark));
            else
                holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.curently_playing_gradient));
        else
            holder.itemView.setBackground(new ColorDrawable(getPrimaryColor(context)));


        if (holder.getItemViewType() == 0) {
            holder.song_time.setText("");
            holder.artist_name.setText(songs.get(position).getDescription().getSubtitle());
            holder.song_name.setText(songs.get(position).getDescription().getTitle());
        } else {
            holder.song_time.setText("");
            holder.artist_name.setText(queueItemList.get(position).getDescription().getSubtitle());
            holder.song_name.setText(queueItemList.get(position).getDescription().getTitle());
        }
    }

    @Override
    public int getItemCount() {
        if (songs != null)
            return songs.size();
        else return queueItemList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

        listener.onItemPositionChange(fromPosition, toPosition);
        changePosition(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        if (songs != null) {
            listener.onItemRemoved(position);
            songs.remove(position);
        } else {
            listener.onItemRemoved(position);
            queueItemList.remove(position);
        }

        notifyItemRemoved(position);
    }

    void changePosition(int from, int to) {
        MediaItem fromValue = songs.get(from);
        int delta = from < to ? 1 : -1;
        for (int i = from; i != to; i += delta) {
            songs.set(i, songs.get(i + delta));
        }
        songs.set(to, fromValue);
    }

    public void notifyDataSetChanged(List<MediaSessionCompat.QueueItem> queueItemList) {
        this.queueItemList = queueItemList;
        this.songs = null;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged(ArrayList<MediaItem> songs) {
        this.songs = songs;
        this.queueItemList = null;
        notifyDataSetChanged();
    }

    public void notifyPlayingSongChanged(int index) {
        playingQueueIndex = index;
        notifyDataSetChanged();
    }

    public void setCallback(Callback listener) {
        this.listener = listener;
    }

    public interface Callback {
        void onItemRemoved(int position);

        void onItemPositionChange(int from, int to);

        void onItemClicked(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.total_time)
        TextView song_time;
        @BindView(R.id.drag_position)
        ImageView drag_view;
        @BindView(R.id.song_name)
        TextView song_name;
        @BindView(R.id.artist_name)
        TextView artist_name;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            listener.onItemClicked(getAdapterPosition());
        }
    }
}
