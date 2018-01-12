package com.techweblearn.musicbeat.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaControllerCompat;
import android.widget.Toast;

/**
 * Created by Kunal on 10-01-2018.
 */


public class SleepTimer extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Sleep Timer Intent",Toast.LENGTH_SHORT).show();

    }
}
