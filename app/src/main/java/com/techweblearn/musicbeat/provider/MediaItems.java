package com.techweblearn.musicbeat.provider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.techweblearn.musicbeat.Glide.audiocover.AlbumCover.AlbumFileCover;
import com.techweblearn.musicbeat.Glide.audiocover.GlideApp;
import com.techweblearn.musicbeat.Loader.SongLoader;
import com.techweblearn.musicbeat.Models.Song;
import com.techweblearn.musicbeat.Utils.AlbumArtCache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ART;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ART_URI;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_URI;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_YEAR;

/**
 * Created by Kunal on 08-12-2017.
 */

public class MediaItems {

    public static List<MediaBrowserCompat.MediaItem> getAllMediaItemsFromQueue(Context context,ArrayList<Song> songArrayList)
    {
        List<MediaBrowserCompat.MediaItem>mediaItemList=new ArrayList<>();
        for(Song song:songArrayList)
        {
            mediaItemList.add(new MediaBrowserCompat.MediaItem(createMetaDataFromSong(context,song).getDescription(),FLAG_PLAYABLE));
        }
        return mediaItemList;
    }

    public static List<MediaSessionCompat.QueueItem> getAllQueueItemFromSongList(Context context, ArrayList<Song>songs)
    {
        List<MediaSessionCompat.QueueItem>queueItems=new ArrayList<>();
        for(Song song:songs)
        {
            queueItems.add(queueItemFromSong(context,song));
        }
        return queueItems;

    }

    public static List<Song>getSongListFromMediaItemList(Context context,ArrayList<MediaBrowserCompat.MediaItem>mediaItemArrayList)
    {
        List<Song>songs=new ArrayList<>();
        for(MediaBrowserCompat.MediaItem mediaItem:mediaItemArrayList) {
            songs.add(SongLoader.getSong(context, Integer.parseInt(mediaItem.getMediaId())));
        }
        return songs;
    }


    public static List<Song>getSongListFromQueueItemList(Context context, List<MediaSessionCompat.QueueItem>queueItemArrayList)
    {
        List<Song>songs=new ArrayList<>();
        for(MediaSessionCompat.QueueItem queueItem:queueItemArrayList) {
            songs.add(SongLoader.getSong(context, Integer.parseInt(queueItem.getDescription().getMediaId())));
        }
        return songs;
    }


    public static MediaSessionCompat.QueueItem queueItemFromSong(Context context, Song song)
    {
        MediaMetadataCompat mediaMetadataCompat=createMetaDataFromSong(context,song);
        MediaSessionCompat.QueueItem queueItem=new MediaSessionCompat.QueueItem(mediaMetadataCompat.getDescription(),mediaMetadataCompat.hashCode());
        return queueItem;
    }

    public static MediaSessionCompat.QueueItem queueItemFromMediaId(Context context,String media_id )
    {
        MediaMetadataCompat mediaMetadataCompat=createMetaDataFromSong(context,SongLoader.getSong(context, Integer.parseInt(media_id)));
        MediaSessionCompat.QueueItem queueItem=new MediaSessionCompat.QueueItem(mediaMetadataCompat.getDescription(),mediaMetadataCompat.hashCode());
        return queueItem;
    }

    private static MediaMetadataCompat createMetaDataFromSong(Context context, Song song)
    {
        final MediaMetadataCompat.Builder builder=new MediaMetadataCompat.Builder();
        builder.putLong(METADATA_KEY_DURATION,song.duration);
        builder.putLong(METADATA_KEY_YEAR,song.year);
        builder.putString(METADATA_KEY_ALBUM,song.albumName);
        builder.putString(METADATA_KEY_ARTIST,song.artistName);
        builder.putString(METADATA_KEY_MEDIA_ID, String.valueOf(song.id));
        builder.putLong(METADATA_KEY_TRACK_NUMBER,song.trackNumber);
        builder.putString(METADATA_KEY_ART_URI,song.data);
        builder.putString(METADATA_KEY_ALBUM_ART_URI,song.data);
        builder.putString(METADATA_KEY_TITLE,song.title);
        builder.putString(METADATA_KEY_DISPLAY_ICON_URI,song.data);
        builder.putString(METADATA_KEY_MEDIA_URI,song.data);
        return builder.build();
    }




    private static MediaMetadataCompat createMetaDataWithBitmap(Context context, final Song song)

    {
        final MediaMetadataCompat.Builder builder=new MediaMetadataCompat.Builder();
        builder.putLong(METADATA_KEY_DURATION,song.duration);
        builder.putLong(METADATA_KEY_YEAR,song.year);
        builder.putString(METADATA_KEY_ALBUM,song.albumName);
        builder.putString(METADATA_KEY_ARTIST,song.artistName);
        builder.putString(METADATA_KEY_MEDIA_ID, String.valueOf(song.id));
        builder.putLong(METADATA_KEY_TRACK_NUMBER,song.trackNumber);
        builder.putString(METADATA_KEY_ART_URI,song.data);
        builder.putString(METADATA_KEY_TITLE,song.title);

        /*try {

            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(song.data);
            byte[] bytes = mediaMetadataRetriever.getEmbeddedPicture();

            if (bytes != null) {
                Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                builder.putBitmap(METADATA_KEY_ART,bitmap1);
                builder.putBitmap(METADATA_KEY_ALBUM_ART,bitmap1);
                builder.putBitmap(METADATA_KEY_DISPLAY_ICON, bitmap1);
            }
        }catch (Exception e){}*/

       return builder.build();
    }


    private static MediaMetadataCompat createMetaDataWithBitmap(Context context, MediaMetadataCompat metadataCompat)

    {
        final MediaMetadataCompat.Builder builder=new MediaMetadataCompat.Builder(metadataCompat);


        Bitmap bitmap1;

       // ExtractBitMapAsyncFromPath extractBitMapAsynch=new ExtractBitMapAsyncFromPath();
      //  AsyncTask<String, Void, Bitmap> bitmap=extractBitMapAsynch.execute();

        try {

            MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(metadataCompat.getString(METADATA_KEY_ART_URI));
            byte[]bytes=mediaMetadataRetriever.getEmbeddedPicture();

            if(bytes!=null)
            {
                bitmap1=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                builder.putBitmap(METADATA_KEY_ART,bitmap1);
                //    builder.putBitmap(METADATA_KEY_ALBUM_ART,bitmap1);
                builder.putBitmap(METADATA_KEY_DISPLAY_ICON,bitmap1);
            }

            else return builder.build();

        }catch (Exception e){e.printStackTrace();}

        return builder.build();
    }



    public static String getRoot() {
        return "root";
    }

    public static MediaMetadataCompat getMediaMetaDataFromMediaId(Context context,String id)
    {
        return createMetaDataFromSong(context,SongLoader.getSong(context, Integer.parseInt(id)));
    }


    public static MediaMetadataCompat getMediaMetaDataWithBitmap(Context context,String id)
    {
        return createMetaDataWithBitmap(context,SongLoader.getSong(context, Integer.parseInt(id)));
    }

    public static MediaMetadataCompat getMediaMetaDataWithBitmap(Context context,MediaMetadataCompat metadataCompat)
    {
        return createMetaDataWithBitmap(context,metadataCompat);
    }

    static class ExtractBitMapAsynch extends AsyncTask<Song,Void,Bitmap>
    {

        @Override
        protected Bitmap doInBackground(Song... songs) {

            MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(songs[0].data);
            byte[]bytes=mediaMetadataRetriever.getEmbeddedPicture();

            if(bytes!=null)
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            else return null;
        }

    }

    static class ExtractBitMapAsyncFromPath extends AsyncTask<String,Void,Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... strings) {
            MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(strings[0]);
            byte[]bytes=mediaMetadataRetriever.getEmbeddedPicture();

            if(bytes!=null)
                return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            else return null;
        }
    }

    public static ArrayList<Song> getSongListFromQueueItems(final Context context, final List<MediaSessionCompat.QueueItem>list)
    {

    //    QueueListToSongList q=new QueueListToSongList(context);
      //  q.execute(list);
       // ArrayList<Song>songs=new ArrayList<>();
        /*try {
            songs=song.get(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
            try {
                songs=song.get(5000,TimeUnit.MILLISECONDS);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (ExecutionException e1) {
                e1.printStackTrace();
            } catch (TimeoutException e1) {
                e1.printStackTrace();
                try {
                    songs=song.get();
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                } catch (ExecutionException e2) {
                    e2.printStackTrace();
                }
            }

        }*/

       /* try {
            q.get(5000,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
*/


       /* ArrayList<Song>songs= null;
        try {
            songs = song.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/

      /* new AsyncTask<List<MediaSessionCompat.QueueItem>,Void,ArrayList<Song>> ()
       {
           @Override
           protected ArrayList<Song> doInBackground(List<MediaSessionCompat.QueueItem>[] lists) {
               ArrayList<Song>arrayList=new ArrayList<>();
               for(MediaSessionCompat.QueueItem queueItem:lists[0])
                   arrayList.add(SongLoader.getSong(context, Integer.parseInt(queueItem.getDescription().getMediaId())));
               return arrayList;
           }

           @Override
           protected void onPostExecute(ArrayList<Song> songs) {
               //super.onPostExecute(songs);
              // Log.d("Songs", String.valueOf(songs.size()));

               song[0] =songs;
           }
       }.execute(list);
*/
        return new ArrayList<>();
    }



    private static class QueueListToSongList extends AsyncTask<List<MediaSessionCompat.QueueItem>,Void,ArrayList<Song>>
    {

        Context context;

        QueueListToSongList(Context context) {
            this.context = context;
        }

        @Override
        protected ArrayList<Song> doInBackground(List<MediaSessionCompat.QueueItem>[] lists) {

            ArrayList<Song>arrayList=new ArrayList<>();
            for(MediaSessionCompat.QueueItem queueItem:lists[0]) {
                arrayList.add(SongLoader.getSong(context, Integer.parseInt(queueItem.getDescription().getMediaId())));
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Song> arrayList) {
            super.onPostExecute(arrayList);



        }
    }


}
