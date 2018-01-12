package com.techweblearn.musicbeat.Utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.media.session.PlaybackStateCompat;

/**
 * Created by Kunal on 10-12-2017.
 */

public class Extras {


    private static final String PREF_TRACK_CURRENT_TIME="track_current_time";
    private static final String PREF_TRACK_CURRENT_INDEX="track_current_index";
    private static final String PREF_REPEAT_MODE="repeat_mode";
    private static final String PREF_SHUFFEL_MODE="shuffel_mode";

    public static void saveCurrentSongIndex(Context context,int index)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_TRACK_CURRENT_INDEX,index).apply();
    }

    public static void saveCurrentSongTime(Context context,long currenttimemili)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(PREF_TRACK_CURRENT_TIME,currenttimemili).apply();
    }

    public static int getCurrentSongIndex(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_TRACK_CURRENT_INDEX,-1);
    }
    public static long getCurrentSongTime(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_TRACK_CURRENT_TIME,0);
    }

    public static int getRepeatMode(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_REPEAT_MODE,PlaybackStateCompat.REPEAT_MODE_NONE);
    }

    public static void setRepeatMode(Context context,int repeatmode)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_REPEAT_MODE,repeatmode).apply();
    }

    public static void setPrefShuffelMode(Context context,int shuffelMode)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_SHUFFEL_MODE,shuffelMode).apply();
    }

    public static int getShuffelMode(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_SHUFFEL_MODE, PlaybackStateCompat.SHUFFLE_MODE_NONE);
    }
}
