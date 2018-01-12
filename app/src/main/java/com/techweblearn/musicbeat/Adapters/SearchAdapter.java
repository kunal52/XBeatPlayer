package com.techweblearn.musicbeat.Adapters;

import android.content.Context;
import android.support.annotation.Nullable;
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
import com.techweblearn.musicbeat.Models.Song;
import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.Utils.Util;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kunal on 24-12-2017.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {


    private static final int LABEL=0;
    private static final int SONG=1;
    private static final int ARTIST=2;
    private static final int ALBUM=3;

    Context context;
    private List<Object> objectList;

    public SearchAdapter(Context context, List<Object> objectList) {
        this.context = context;
        this.objectList = objectList;
    }

    @Override
    public int getItemViewType(int position) {

        if(objectList.get(position) instanceof Song)
            return SONG;
        if(objectList.get(position) instanceof Artist)
            return ARTIST;
        if(objectList.get(position) instanceof Album)
            return ALBUM;
        return LABEL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType)
        {
            case LABEL:return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.single_textview_layout,null));
            case SONG:return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.search_song_view,null));
            case ALBUM:
            case ARTIST:return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.search_artist_album_view,null));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        switch (holder.getItemViewType())
        {
            case SONG:
                Song song= (Song) objectList.get(position);
                holder.title.setText(song.title);
                holder.sub_title.setText(song.artistName);
                break;
            case ARTIST:
                Artist artist= (Artist) objectList.get(position);
                holder.title.setText(artist.getName());
                holder.sub_title.setText(artist.getAlbumCount()+" Albums . "+artist.getSongCount()+" Songs ");
                holder.imageView.setImageDrawable(Util.getArtistDrawable(context));//TODO Apply Artist Image And To Implement Auto Download Of ART
                break;
            case ALBUM:
                Album album= (Album) objectList.get(position);
                holder.title.setText(album.getTitle());
                holder.sub_title.setText(album.getSongCount()+" Songs . "+album.getYear()+" Year");
                GlideApp.with(context)
                        .load(new AudioFileCover(album.safeGetFirstSong().data))
                        .thumbnail(0.2f)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .error(Util.getSongDrawable(context))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(holder.imageView);
                break;
            default:
                holder.label.setText(objectList.get(position).toString());
        }

    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    public void swapList(List<Object>objectList)
    {
        this.objectList=objectList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Nullable
        @BindView(R.id.title)TextView title;
        @Nullable
        @BindView(R.id.sub_title)TextView sub_title;
        @Nullable
        @BindView(R.id.search_label)TextView label;
        @Nullable
        @BindView(R.id.image_background)ImageView imageView;
        @Nullable
        @BindView(R.id.more_options)ImageView moreoption;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
            try {
                moreoption.setOnClickListener(this);
            }catch (Exception e){}

        }

        @Override
        public void onClick(View v) {
            try {
                if(v.getId()==moreoption.getId())
                    callBack.onMoreOptionClick(objectList.get(getAdapterPosition()),moreoption);
                else
                    callBack.onItemSelected(objectList.get(getAdapterPosition()));
            }catch (Exception e)
            {
                callBack.onItemSelected(objectList.get(getAdapterPosition()));
            }
        }
    }

    CallBack callBack;
    public void setCallBack(CallBack callBack)
    {
        this.callBack=callBack;
    }
    public interface CallBack
    {
        void onItemSelected(Object object);
        void onMoreOptionClick(Object object,View view);
    }

}
