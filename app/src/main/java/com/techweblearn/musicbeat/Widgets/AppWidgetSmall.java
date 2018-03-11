package com.techweblearn.musicbeat.Widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.techweblearn.musicbeat.Base.AppWidgetBase;
import com.techweblearn.musicbeat.Glide.audiocover.AudioCover.AudioFileCover;
import com.techweblearn.musicbeat.Glide.audiocover.GlideApp;
import com.techweblearn.musicbeat.HomeActivity;
import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.Service.MusicPlayBackService;


public class AppWidgetSmall extends AppWidgetBase {


    private static AppWidgetSmall mInstance;

    public static synchronized AppWidgetSmall getInstance() {
        if (mInstance == null) {
            mInstance = new AppWidgetSmall();
        }
        return mInstance;
    }

    @Override
    protected void defaultWidget(Context context,AppWidgetManager appWidgetManager,int[]appwidgetids) {

        RemoteViews remoteViews=new RemoteViews(context.getPackageName(),R.layout.app_widget_small);
        linkButtons(context,remoteViews);
        performUpdate(context,appwidgetids,remoteViews);

    }

    @Override
    public void onMetaDataChanged(final Context context, final MediaMetadataCompat mediaMetadataCompat) {

        final RemoteViews remoteViews=new RemoteViews(context.getPackageName(),R.layout.app_widget_small);
        final ComponentName componentName=new ComponentName(context,AppWidgetSmall.class);
        remoteViews.setTextViewText(R.id.song_name,mediaMetadataCompat.getDescription().getTitle());

        GlideApp.with(context)
                .asBitmap()
                .override(400,400)
                .load(new AudioFileCover(mediaMetadataCompat.getDescription().getIconUri().toString()))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        remoteViews.setTextViewText(R.id.song_name,mediaMetadataCompat.getDescription().getTitle());
                        remoteViews.setImageViewBitmap(R.id.song_art,resource);
                        linkButtons(context,remoteViews);
                        performUpdate(context,componentName,remoteViews);
                    }
                });
    }

    @Override
    public void onPlayBackStateChange(Context context, @NonNull PlaybackStateCompat playbackStateCompat) {
        RemoteViews remoteViews=new RemoteViews(context.getPackageName(),R.layout.app_widget_small);
        ComponentName componentName=new ComponentName(context,AppWidgetSmall.class);
        linkButtons(context,remoteViews);

        switch (playbackStateCompat.getState())
        {
            case PlaybackStateCompat.STATE_PAUSED:
                remoteViews.setImageViewResource(R.id.play_pause,R.drawable.ic_play_arrow_white_24dp);
                break;

            case PlaybackStateCompat.STATE_PLAYING:
                remoteViews.setImageViewResource(R.id.play_pause,R.drawable.ic_pause_white_24dp);
                break;
        }

        performUpdate(context,componentName,remoteViews);
    }


    public void unlinkButtons(Context context)
    {
        Intent intent;
        PendingIntent pendingIntent;

        intent=new Intent(context, HomeActivity.class);
        pendingIntent=PendingIntent.getActivity(context,0,intent,0);

        RemoteViews remoteViews=new RemoteViews(context.getPackageName(),R.layout.app_widget_small);
        remoteViews.setOnClickPendingIntent(R.id.play_pause,pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.next,pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.prev,pendingIntent);

        performUpdate(context,new ComponentName(context,AppWidgetSmall.class),remoteViews);

    }

    public void linkButtons(Context context,RemoteViews remoteView)
    {
        Intent intent;
        PendingIntent pendingIntent;
        RemoteViews remoteViews=remoteView;

        intent=new Intent(context, HomeActivity.class);
        pendingIntent=PendingIntent.getActivity(context,0,intent,0);
        remoteViews.setOnClickPendingIntent(R.id.song_name,pendingIntent);



        pendingIntent= MediaButtonReceiver.buildMediaButtonPendingIntent(
             context   ,
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        remoteViews.setOnClickPendingIntent(R.id.prev,pendingIntent);


        pendingIntent= MediaButtonReceiver.buildMediaButtonPendingIntent(
                context   ,
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT);
        remoteViews.setOnClickPendingIntent(R.id.next,pendingIntent);


       /* pendingIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(
                context,
                PlaybackStateCompat.ACTION_PLAY_PAUSE);*/

       Intent intent1=new Intent(context.getApplicationContext(),MusicPlayBackService.class);
       intent.setAction("PLAY_PAUSE");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pendingIntent=PendingIntent.getForegroundService(context.getApplicationContext(),0,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else pendingIntent=PendingIntent.getService(context.getApplicationContext(),0,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.play_pause, pendingIntent);

    }


}

