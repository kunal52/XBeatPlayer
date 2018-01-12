package com.techweblearn.musicbeat.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.techweblearn.musicbeat.Glide.audiocover.AlbumCover.AlbumFileCover;
import com.techweblearn.musicbeat.Glide.audiocover.AudioCover.AudioFileCover;
import com.techweblearn.musicbeat.Glide.audiocover.GlideApp;
import com.techweblearn.musicbeat.Models.Album;
import com.techweblearn.musicbeat.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kunal on 16-12-2017.
 */

public class ArtistsHorizontalAdapter extends RecyclerView.Adapter<ArtistsHorizontalAdapter.ViewHolder> {

    Context context;
    ArrayList<Album>albumArrayList;

    public ArtistsHorizontalAdapter(Context context, ArrayList<Album> albumArrayList) {
        this.context = context;
        this.albumArrayList = albumArrayList;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.horizontal_recyclerview,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.album_name.setText(albumArrayList.get(position).getTitle());
        holder.song_count.setText(albumArrayList.get(position).getYear()+" Year . "+ albumArrayList.get(position).getSongCount()+" Songs");
        GlideApp.with(context)
                .asBitmap()
                .thumbnail(0.5f)
                .load(new AudioFileCover(albumArrayList.get(position).safeGetFirstSong().data))
                .into(holder.background_image);
    }

    @Override
    public int getItemCount() {
        return albumArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_background)
        ImageView background_image;
        @BindView(R.id.song_name)
        TextView album_name;
        @BindView(R.id.artist_name)
        TextView song_count;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onItemClicked(albumArrayList.get(getAdapterPosition()).getId());
                }
            });
        }
    }


    OnItemClicked callback;
    public void setItemClickedListener(OnItemClicked callback)
    {
        this.callback=callback;
    }
    public interface OnItemClicked
    {
        void onItemClicked(int id);
    }
}
