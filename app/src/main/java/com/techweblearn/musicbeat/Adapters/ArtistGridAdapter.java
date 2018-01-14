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
import com.techweblearn.musicbeat.Glide.audiocover.GlideApp;
import com.techweblearn.musicbeat.Models.Album;
import com.techweblearn.musicbeat.Models.Artist;
import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.Utils.Util;
import com.techweblearn.musicbeat.provider.NetworkInfoStore;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kunal on 07-12-2017.
 */

public class ArtistGridAdapter extends RecyclerView.Adapter<ArtistGridAdapter.ViewHolder> {

    private NetworkInfoStore networkInfoStore;
    private Context contex;
    private ArrayList<Artist>artistArrayList;

    public ArtistGridAdapter(Context contex, ArrayList<Artist> artistArrayList) {

        this.contex = contex;
        this.artistArrayList = artistArrayList;
        networkInfoStore = new NetworkInfoStore(contex);

    }

    @Override
    public ArtistGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(contex).inflate(R.layout.grid_album_artist_view,parent,false);
        return new ArtistGridAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ArtistGridAdapter.ViewHolder holder, final int position) {

        holder.primary_name.setText(artistArrayList.get(position).getName());
        String s=artistArrayList.get(position).getAlbumCount()+" Albums . "+artistArrayList.get(position).getSongCount()+" Songs ";
        holder.secoundry_name.setText(s);
        final Drawable error = Util.getArtistDrawable(contex);


        GlideApp.with(contex)
                .load(networkInfoStore.getArtistArt(artistArrayList.get(position).getName()))
                .error(error)
                .thumbnail(0.2f)
                .transition(new DrawableTransitionOptions().crossFade())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.image_background);

    }

    @Override
    public int getItemCount() {
        return artistArrayList.size();
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
                    callback.onItemClicked(getAdapterPosition(),
                            artistArrayList.get(getAdapterPosition()).getId(),
                            artistArrayList.get(getAdapterPosition()).albums,
                            artistArrayList.get(getAdapterPosition()).getName());
                }
            });
        }
    }

    OnCallback callback;
    public void setCallback(OnCallback callback)
    {
        this.callback=callback;
    }
    public interface OnCallback
    {
        void onItemClicked(int position, int artist_id, ArrayList<Album>albums,String artist_name);
    }

}
