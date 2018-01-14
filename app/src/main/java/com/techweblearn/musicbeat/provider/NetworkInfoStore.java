package com.techweblearn.musicbeat.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kunal on 14/1/18.
 */

public class NetworkInfoStore extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "info.db";
    private static final int VERSION = 1;

    public static final String TABLE_ARTIST_NAME = "artist_table";
    public static final String TABLE_ALBUM_NAME = "album_table";

    public static final String ARTIST_NAME = "artist_name";
    public static final String ALBUM_NAME = "album_name";

    public static final String ARTIST_INFO = "artist_info";
    public static final String ALBUM_INFO = "album_info";

    public static final String ARTIST_ART = "artist_art";
    public static final String ALBUM_ART = "album_art";

    String artistQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_ARTIST_NAME + " ( " + ARTIST_NAME + " String UNIQUE," + ARTIST_INFO + " TEXT NOT NULL," + ARTIST_ART + " blob )";

    String albumQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_ALBUM_NAME + " ( " + ALBUM_NAME + " String UNIQUE," + ALBUM_INFO + " TEXT NOT NULL," + ALBUM_ART + " blob )";

    public NetworkInfoStore(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(artistQuery);
        sqLiteDatabase.execSQL(albumQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTIST_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUM_NAME);

    }

    public void insertArtistData(String artist, String info, byte[] art) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ARTIST_NAME, artist);
        contentValues.put(ARTIST_INFO, info);
        contentValues.put(ARTIST_ART, art);
        sqLiteDatabase.insert(TABLE_ARTIST_NAME, null, contentValues);
    }

    public String getArtistInfo(String artist) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE_ARTIST_NAME, new String[]{ARTIST_INFO}, ARTIST_NAME + "=?", new String[]{artist}, null, null, null);
        if (cursor.getCount() == 0)
            return null;
        cursor.moveToNext();
        return cursor.getString(0);
    }

    public byte[] getArtistArt(String artist) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE_ARTIST_NAME, new String[]{ARTIST_ART}, ARTIST_NAME + "=?", new String[]{artist}, null, null, null);
        if (cursor.getCount() == 0)
            return null;
        cursor.moveToNext();
        return cursor.getBlob(0);
    }


}
