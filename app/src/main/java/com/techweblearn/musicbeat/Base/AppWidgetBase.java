package com.techweblearn.musicbeat.Base;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.RemoteViews;


import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.Service.MediaBrowserAdapter;


public abstract class AppWidgetBase extends AppWidgetProvider {


    MediaBrowserAdapter mediaBrowserAdapter=null;
    Context context;


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        if(this.context==null)
            this.context=context;
        defaultWidget(context, appWidgetManager, appWidgetIds);
        if(mediaBrowserAdapter==null)
        {
            mediaBrowserAdapter=new MediaBrowserAdapter(context.getApplicationContext());
            mediaBrowserAdapter.addListener(new OnSongUpdate());
            mediaBrowserAdapter.onStart();
        }

        RemoteViews remoteViews=new RemoteViews(context.getPackageName(), R.layout.app_widget_small);
        performUpdate(context,appWidgetIds,remoteViews);

    }

    @Override
    public void onEnabled(Context context) {
        this.context=context;
        if(mediaBrowserAdapter==null) {
            mediaBrowserAdapter = new MediaBrowserAdapter(context.getApplicationContext());
            mediaBrowserAdapter.addListener(new OnSongUpdate());
            mediaBrowserAdapter.onStart();
        }
    }

    @Override
    public void onDisabled(Context context) {
        if(mediaBrowserAdapter!=null)
        mediaBrowserAdapter.onStop();
    }


    abstract protected void defaultWidget(Context context,AppWidgetManager appWidgetManager,int[]appwidgetids);
    abstract public void onMetaDataChanged(Context context,@NonNull MediaMetadataCompat mediaMetadataCompat);
    abstract public void onPlayBackStateChange(Context context,@NonNull PlaybackStateCompat playbackStateCompat);


    private class OnSongUpdate extends MediaBrowserAdapter.MediaBrowserChangeListener
    {
        @Override
        public void onConnected(@Nullable MediaControllerCompat mediaController) {
            Log.d("START","AppWidgetBase");
        }

        @Override
        public void onMetadataChanged(@Nullable MediaMetadataCompat mediaMetadata) {

            if(mediaMetadata!=null)
            onMetaDataChanged(context,mediaMetadata);

        }

        @Override
        public void onPlaybackStateChanged(@Nullable PlaybackStateCompat playbackState) {
            if(playbackState!=null)
            onPlayBackStateChange(context,playbackState);
        }

    }



    public void notifyMusicPlay(Context context)
    {
        if(mediaBrowserAdapter==null)
        {
            if(mediaBrowserAdapter==null) {
                mediaBrowserAdapter = new MediaBrowserAdapter(context);
                mediaBrowserAdapter.addListener(new OnSongUpdate());
                mediaBrowserAdapter.onStart();
            }
        }
    }


    protected void performUpdate(Context context, ComponentName componentName, RemoteViews views)
    {
        AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(componentName,views);
    }
    protected void performUpdate(Context context, int []ids , RemoteViews views)
    {
        AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(ids,views);
    }

    protected  Bitmap createBitmap(Drawable drawable, float sizeMultiplier) {
        Bitmap bitmap = Bitmap.createBitmap((int) (drawable.getIntrinsicWidth() * sizeMultiplier), (int) (drawable.getIntrinsicHeight() * sizeMultiplier), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        drawable.setBounds(0, 0, c.getWidth(), c.getHeight());
        drawable.draw(c);
        return bitmap;
    }
}
