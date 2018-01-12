package com.techweblearn.musicbeat.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.techweblearn.musicbeat.Glide.audiocover.AudioCover.AudioFileCover;
import com.techweblearn.musicbeat.Glide.audiocover.GlideApp;
import com.techweblearn.musicbeat.Loader.SongLoader;
import com.techweblearn.musicbeat.Models.Song;
import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.Utils.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kunal on 06-12-2017.
 */

public class SongListRecyclerviewAdapter extends RecyclerView.Adapter<SongListRecyclerviewAdapter.ViewHolder> {



    OnItemClick onItemClick;
    public interface OnItemClick
    {
        void OnSongClick(Song song,int position);
        void OnMoreOptionClick(Song song,int position,View view);
    }


    public void OnSongClick(OnItemClick onItemClick)
    {
        this.onItemClick=onItemClick;
    }


    Context context;
    ArrayList<Song>songArrayList;


    public SongListRecyclerviewAdapter(Context context, ArrayList<Song> songArrayList) {
        this.context = context;
        this.songArrayList = songArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.song_list_recyclerview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.song_name.setText(songArrayList.get(position).title);
        holder.artist_name.setText(songArrayList.get(position).artistName);
        holder.song_time.setText(Util.getReadableDurationString(songArrayList.get(position).duration));

        Drawable error = Util.getSongDrawable(context);
        GlideApp.with(context)
                .load(new AudioFileCover(songArrayList.get(position).data))
                .centerCrop()
                .error(error)
                .thumbnail(0.1f)
                .transition(new DrawableTransitionOptions().crossFade())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.song_background);

    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public void notifyDataSetChange(ArrayList<Song>arrayList)
    {
        songArrayList=arrayList;
        notifyDataSetChanged();
    }

    public void notifySongSetChanged()
    {
       songArrayList= SongLoader.getAllSongs(context);
       notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.song_name)TextView song_name;
        @BindView(R.id.artist_name)TextView artist_name;
        @BindView(R.id.song_background)ImageView song_background;
        @BindView(R.id.song_time)TextView song_time;
        @BindView(R.id.more_options)ImageView more_option;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            song_name.setOnClickListener(this);
            artist_name.setOnClickListener(this);
            song_background.setOnClickListener(this);
            song_time.setOnClickListener(this);
            more_option.setOnClickListener(this);
        }




        @Override
        public void onClick(View view) {

            if(view.getId()==more_option.getId()) {
                onItemClick.OnMoreOptionClick(songArrayList.get(getAdapterPosition()), getAdapterPosition(),more_option);
            }

            else
                onItemClick.OnSongClick(songArrayList.get(getAdapterPosition()),getAdapterPosition());
        }

    }
}
