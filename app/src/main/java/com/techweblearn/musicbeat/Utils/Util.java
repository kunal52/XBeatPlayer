package com.techweblearn.musicbeat.Utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.techweblearn.musicbeat.Loader.SongLoader;
import com.techweblearn.musicbeat.Models.Song;
import com.techweblearn.musicbeat.R;

import java.io.File;
import java.util.List;

/**
 * Created by Kunal on 06-12-2017.
 */

public class Util {


    public static String getReadableDurationString(long songDurationMillis) {
        long minutes = (songDurationMillis / 1000) / 60;
        long seconds = (songDurationMillis / 1000) % 60;
        if (minutes < 60) {
            return String.format("%01d:%02d", minutes, seconds);
        } else {
            long hours = minutes / 60;
            minutes = minutes % 60;
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
    }

    public static Point getScreenSize(@NonNull Context c) {
        Display display = ((WindowManager) c.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static void deleteTracks(@NonNull final Context context, @NonNull final List<Song> songs) {
        final String[] projection = new String[]{
                BaseColumns._ID, MediaStore.MediaColumns.DATA
        };
        final StringBuilder selection = new StringBuilder();
        selection.append(BaseColumns._ID + " IN (");
        for (int i = 0; i < songs.size(); i++) {
            selection.append(songs.get(i).id);
            if (i < songs.size() - 1) {
                selection.append(",");
            }
        }
        selection.append(")");

        try {
            final Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection.toString(),
                    null, null);
            if (cursor != null) {
                // Step 1: Remove selected tracks from the current playlist, as well
                // as from the album art cache
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    final int id = cursor.getInt(0);
                    final Song song = SongLoader.getSong(context, id);
                    //Have to Remove this song from Queue//Need to Implement
                    cursor.moveToNext();
                }

                // Step 2: Remove selected tracks from the database
                context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        selection.toString(), null);

                // Step 3: Remove files from card
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    final String name = cursor.getString(1);
                    try { // File.delete can throw a security exception
                        final File f = new File(name);
                        if (!f.delete()) {
                            // I'm not sure if we'd ever get here (deletion would
                            // have to fail, but no exception thrown)
                            Log.e("MusicUtils", "Failed to delete file " + name);
                        }
                        cursor.moveToNext();
                    } catch (@NonNull final SecurityException ex) {
                        cursor.moveToNext();
                    } catch (NullPointerException e) {
                        Log.e("MusicUtils", "Failed to find file " + name);
                    }
                }
                cursor.close();
            }
            context.getContentResolver().notifyChange(Uri.parse("content://media"), null);
            Toast.makeText(context, context.getString(R.string.deleted_x_songs, songs.size()), Toast.LENGTH_SHORT).show();
        } catch (SecurityException ignored) {
        }
    }


    public static void hideSoftKeyboard(@Nullable Activity activity) {
        if (activity != null) {
            View currentFocus = activity.getCurrentFocus();
            if (currentFocus != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }
    }

    public static Drawable getGradientDrawable(Context context,int i)
    {
        int gradient_select=i%4;
        switch (gradient_select)
        {
            case 0:return context.getResources().getDrawable(R.drawable.gradient_0);
            case 1:return context.getResources().getDrawable(R.drawable.gradient_1);
            case 2:return context.getResources().getDrawable(R.drawable.gradient_2);
            case 3:return context.getResources().getDrawable(R.drawable.gradient_3);
            default:return context.getResources().getDrawable(R.drawable.gradient_0);
        }
    }

    public static Drawable getSongDrawable(Context context)
    {
        Drawable drawable=context.getResources().getDrawable(R.drawable.ic_song_disc);
        drawable.setColorFilter(getThemeAccentColor(context), PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }

    public static Drawable getArtistDrawable(Context context)
    {
        Drawable drawable= context.getResources().getDrawable(R.drawable.ic_artist_black);
        drawable.setColorFilter(getThemeAccentColor(context), PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }

    public static int getTheme(Context context)
    {

        switch (Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("theme","0")))
        {
            case 0:return R.style.AppTheme;
            case 1:return R.style.AppDarkTheme;
            case 2:return R.style.Chocolate;
            case 3:return R.style.CoolBlue;
            case 4:return R.style.White_Wood;
            default: return R.style.AppTheme;

        }
    }

    public static int getThemeAccentColor (final Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme ().resolveAttribute (R.attr.colorAccent, value, true);
        return value.data;
    }

    public static int getThemePrimaryColor (final Context context) {
        final TypedValue value = new TypedValue ();
        context.getTheme ().resolveAttribute (R.attr.colorPrimary, value, true);
        return value.data;
    }


    public static int getThemeBackgroundColor(final Context context)
    {
        final TypedValue value = new TypedValue ();
        context.getTheme ().resolveAttribute (R.attr.titleTextColor, value, true);
        return value.data;
    }

    public static int getHomeDrawable(final Context context) {
        final TypedValue value = new TypedValue ();
        context.getTheme ().resolveAttribute (R.attr.ic_home, value, true);
        return value.data;
    }

    public static int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }

    public static int darker(int color,float factor)
    {
        int a = (color >> 24) & 0xFF;
        int r = (int) (((color >> 16) & 0xFF) * factor);
        int g = (int) (((color >> 8) & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

}

