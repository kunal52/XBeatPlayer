package com.techweblearn.musicbeat.Widgets;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.techweblearn.musicbeat.Service.MusicPlayBackService;

/**
 * Created by kunal on 27/1/18.
 */

public class BootReciever extends BroadcastReceiver {

    AppWidgetManager widgetManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (widgetManager.getAppWidgetIds(new ComponentName(context, AppWidgetSmall.class)).length > 0 )
        {
            final Intent serviceIntent = new Intent(context, MusicPlayBackService.class);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) { // not allowed on Oreo
                context.startService(serviceIntent);
            }
        }
    }
}
