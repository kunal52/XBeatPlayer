package com.techweblearn.musicbeat.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.techweblearn.musicbeat.Models.Playlist;
import com.techweblearn.musicbeat.Models.Song;
import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.Utils.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kunal on 26-12-2017.
 */

public class PlaylistHorizontalRecyclerview extends RecyclerView.Adapter<PlaylistHorizontalRecyclerview.ViewHolder> {

    Context context;
    ArrayList<Playlist>playlists;
    int selected=-1;

    public PlaylistHorizontalRecyclerview(Context context, ArrayList<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.horizontal_recyclerview,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.primary_text.setText(playlists.get(position).name);
        holder.background.setImageDrawable(Util.getGradientDrawable(context,position));
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public void notifyChange(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    public void setSelected(int position)
    {
        selected=position;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image_background) ImageView background;
        @BindView(R.id.song_name) TextView primary_text;
        @BindView(R.id.artist_name) TextView secoundry_text;
        @BindView(R.id.cardview)CardView cardView;

        public ViewHolder(View itemView)  {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            callback.onItemClicked(getAdapterPosition(),playlists.get(getAdapterPosition()).id);
        }
    }

    public void removeItem(int id)
    {
        for(int i=0;i<playlists.size();i++)
        {
            if(playlists.get(i).id==id)
            {
                playlists.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    Callback callback;
    public void setCallback(Callback callback)
    {
        this.callback=callback;
    }

    public interface Callback
    {
        void onItemClicked(int position,int playlist_id);
    }
}
