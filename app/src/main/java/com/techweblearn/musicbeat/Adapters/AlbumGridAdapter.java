package com.techweblearn.musicbeat.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.techweblearn.musicbeat.Glide.audiocover.AudioCover.AudioFileCover;
import com.techweblearn.musicbeat.Glide.audiocover.GlideApp;
import com.techweblearn.musicbeat.Models.Album;
import com.techweblearn.musicbeat.Models.Artist;
import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.Utils.Extras;
import com.techweblearn.musicbeat.Utils.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kunal on 07-12-2017.
 */

public class AlbumGridAdapter extends RecyclerView.Adapter<AlbumGridAdapter.ViewHolder> {


    private Context contex;
    private ArrayList<Album>albumArrayList;

    public AlbumGridAdapter(Context contex, ArrayList<Album> albumArrayList) {

        this.contex = contex;
        this.albumArrayList = albumArrayList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(contex).inflate(R.layout.grid_album_artist_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.primary_name.setText(albumArrayList.get(position).getTitle());
        holder.secoundry_name.setText(albumArrayList.get(position).getArtistName());
        Drawable error = Util.getSongDrawable(contex);


        GlideApp.with(contex)
                .load(new AudioFileCover(albumArrayList.get(position).safeGetFirstSong().data))
                .error(error)
                .thumbnail(0.2f)
                .transition(new DrawableTransitionOptions().crossFade())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.image_background);


    }

    @Override
    public int getItemCount() {
        return albumArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_background)ImageView image_background;
        @BindView(R.id.primary_name)TextView primary_name;
        @BindView(R.id.secondry_name)TextView secoundry_name;

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
