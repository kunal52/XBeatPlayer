package com.techweblearn.musicbeat.Utils;

import android.content.Context;
import android.os.SystemClock;
import android.preference.PreferenceManager;

/**
 * Created by Kunal on 06-01-2018.
 */

public class PreferencesUtil {

    public static int playerLayoutTransformation(Context context)
    {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("playerlayout_viewpager_transformation","0"));
    }
    public static int getLastOpenedScreen(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("last_open",0);
    }

    public static void saveLastOpenedScreen(Context context,int last)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("last_open",last).apply();
    }
    public static int getRecentlyAddedInterval(Context context)
    {
        return 86400*Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("recently_added_interval","90"));
    }

    public static void saveCurrentPosition(Context context,int position)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("current_position",position).apply();
    }

    public static int getCurrentPosition(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("current_position",0);
    }

    public static void setSleepTimer(Context context,long timer)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong("sleep_timer", timer).apply();
    }

    public static long getSleepElapsedTimer(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong("sleep_timer",0);
    }


    public static void setLastSleepTimeSeekbar(Context context,int position)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("sleep_timer_seekbar", position).apply();
    }

    public static int getLastSleepTimeSeekbar(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("sleep_timer_seekbar",15);
    }

}
