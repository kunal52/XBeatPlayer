package com.techweblearn.musicbeat.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.techweblearn.musicbeat.Glide.audiocover.AudioCover.AudioFileCover;
import com.techweblearn.musicbeat.Glide.audiocover.GlideApp;
import com.techweblearn.musicbeat.Models.Song;
import com.techweblearn.musicbeat.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kunal on 04-12-2017.
 */

public class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Song> songArrayList;

    public HorizontalRecyclerViewAdapter(Context context, ArrayList<Song> songArrayList) {
        this.context = context;
        this.songArrayList = songArrayList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.horizontal_recyclerview, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.song_name.setText(songArrayList.get(position).title);
        holder.artist_name.setText(songArrayList.get(position).artistName);

        Drawable error = context.getResources().getDrawable(R.drawable.album_jazz_blues);

        GlideApp.with(context)
                .load(new AudioFileCover(songArrayList.get(position).data))
                .centerCrop()
                .error(error)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .priority(Priority.HIGH)
                .into(holder.background_image);

    }

    @Override
    public int getItemCount() {
        if (songArrayList.size() <= 10)
            return songArrayList.size();
        else {
            return 10;
        }
    }

    public void notifyChange(ArrayList<Song> songs) {
        songArrayList = songs;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        @BindView(R.id.image_background)
        ImageView background_image;
        @BindView(R.id.song_name)
        TextView song_name;
        @BindView(R.id.artist_name)
        TextView artist_name;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {
            callback.onItemClicked(songArrayList.get(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View v) {
            callback.onLongPressItem((songArrayList.get(getAdapterPosition())));
            return false;
        }
    }


    private Callback callback;
    public void setCallback(Callback callback)
    {
        this.callback=callback;
    }

    public interface Callback
    {
        void onItemClicked(Song song);
        void onLongPressItem(Song song);
    }

}
