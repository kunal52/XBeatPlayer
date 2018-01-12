package com.techweblearn.musicbeat.Loader;

import android.content.Context;
import android.database.Cursor;
import android.preference.Preference;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.techweblearn.musicbeat.Models.Song;
import com.techweblearn.musicbeat.Utils.PreferencesUtil;
import com.techweblearn.musicbeat.Utils.Util;

import java.util.ArrayList;

/**
 * Created by kunal on 03-12-2017.
 */

public class RecentlyAddedSongs {

    protected static final String BASE_SELECTION = MediaStore.Audio.AudioColumns.IS_MUSIC + "=1" + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''";


    @NonNull
    public static ArrayList<Song> getLastAddedSongs(@NonNull Context context) {
        return getSongs(makeLastAddedCursor(context));
    }

    public static Cursor makeLastAddedCursor(@NonNull final Context context) {
        long cutoff =  (System.currentTimeMillis() / 1000L)- PreferencesUtil.getRecentlyAddedInterval(context);//2419200 Time in Sec 28 Days

        return makeSongCursor(
                context,
                MediaStore.Audio.Media.DATE_ADDED + ">?",
                new String[]{String.valueOf(cutoff)},
                MediaStore.Audio.Media.DATE_ADDED + " DESC");
    }

    @Nullable
    public static Cursor makeSongCursor(@NonNull final Context context, @Nullable final String selection, final String[] selectionValues, final String sortOrder) {
        String baseSelection = BASE_SELECTION;
        if (selection != null && !selection.trim().equals("")) {
            baseSelection += " AND " + selection;
        }

        try {
            return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            BaseColumns._ID,// 0
                            MediaStore.Audio.AudioColumns.TITLE,// 1
                            MediaStore.Audio.AudioColumns.TRACK,// 2
                            MediaStore.Audio.AudioColumns.YEAR,// 3
                            MediaStore.Audio.AudioColumns.DURATION,// 4
                            MediaStore.Audio.AudioColumns.DATA,// 5
                            MediaStore.Audio.AudioColumns.DATE_MODIFIED,// 6
                            MediaStore.Audio.AudioColumns.ALBUM_ID,// 7
                            MediaStore.Audio.AudioColumns.ALBUM,// 8
                            MediaStore.Audio.AudioColumns.ARTIST_ID,// 9
                            MediaStore.Audio.AudioColumns.ARTIST,// 10

                    }, baseSelection, selectionValues, sortOrder);
        } catch (SecurityException e) {
            return null;
        }
    }

    @NonNull
    public static ArrayList<Song> getSongs(@Nullable final Cursor cursor) {
        ArrayList<Song> songs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getSongFromCursorImpl(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();
        return songs;
    }

    @NonNull
    private static Song getSongFromCursorImpl(@NonNull Cursor cursor) {
        final int id = cursor.getInt(0);
        final String title = cursor.getString(1);
        final int trackNumber = cursor.getInt(2);
        final int year = cursor.getInt(3);
        final long duration = cursor.getLong(4);
        final String data = cursor.getString(5);
        final long dateModified = cursor.getLong(6);
        final int albumId = cursor.getInt(7);
        final String albumName = cursor.getString(8);
        final int artistId = cursor.getInt(9);
        final String artistName = cursor.getString(10);

        return new Song(id,title,trackNumber,year,duration,data,dateModified,albumId,albumName,artistId,artistName);
    }



}
