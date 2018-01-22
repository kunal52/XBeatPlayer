package com.techweblearn.musicbeat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.techweblearn.musicbeat.Adapters.SearchAdapter;
import com.techweblearn.musicbeat.Dialogs.SongDetailDialog;
import com.techweblearn.musicbeat.Fragment.AddPlayListDialogFragment;
import com.techweblearn.musicbeat.Fragment.AlbumFragmentFullView;
import com.techweblearn.musicbeat.Fragment.ArtistFragmentFullView;
import com.techweblearn.musicbeat.Helper.WrappedAsyncTaskLoader;
import com.techweblearn.musicbeat.Loader.AlbumLoader;
import com.techweblearn.musicbeat.Loader.ArtistLoader;
import com.techweblearn.musicbeat.Loader.PlaylistLoader;
import com.techweblearn.musicbeat.Loader.SongLoader;
import com.techweblearn.musicbeat.Models.Album;
import com.techweblearn.musicbeat.Models.Artist;
import com.techweblearn.musicbeat.Models.Playlist;
import com.techweblearn.musicbeat.Models.Song;
import com.techweblearn.musicbeat.Service.MediaBrowserAdapter;
import com.techweblearn.musicbeat.Utils.PlaylistsUtil;
import com.techweblearn.musicbeat.Utils.Util;
import com.techweblearn.musicbeat.provider.MediaItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.techweblearn.musicbeat.Fragment.SongFragment.ADD_NEW_PLAYLIST_ID;
import static com.techweblearn.musicbeat.Utils.Constants.PLAY_NEXT;
import static com.techweblearn.musicbeat.Utils.Constants.PLAY_SINGLE_SONG;

public class SearchActivity extends Fragment implements TextWatcher, android.support.v4.app.LoaderManager.LoaderCallbacks<List<Object>>, SearchAdapter.CallBack, View.OnClickListener {

    private static final int LOADER_ID = 3;
    @BindView(R.id.search_edittext)
    EditText search_text;
    @BindView(R.id.search_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.clear)
    ImageView clear;

    String query;
    MediaBrowserAdapter mediaBrowserAdapter;
    private SearchAdapter searchAdapter;
    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchAdapter = new SearchAdapter(getActivity(), Collections.emptyList());
        searchAdapter.setCallBack(this);

        mediaBrowserAdapter = new MediaBrowserAdapter(getActivity());
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_search,null);
        unbinder=ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        search_text.addTextChangedListener(this);
        clear.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Util.hideSoftKeyboard(getActivity());
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mediaBrowserAdapter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mediaBrowserAdapter.onStop();
    }

    @Override
    public android.support.v4.content.Loader<List<Object>> onCreateLoader(int id, Bundle args) {
        return new AsyncSearchResultLoader(getActivity(), query);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<Object>> loader, List<Object> data) {
        searchAdapter.swapList(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<Object>> loader) {
        searchAdapter.swapList(Collections.emptyList());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {



    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        search(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {



    }


    private void search(@NonNull String query) {
        this.query = query;
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onItemSelected(Object object) {
        if (object instanceof Song) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("song", (Song) object);
            mediaBrowserAdapter.getTransportControls().sendCustomAction(PLAY_SINGLE_SONG, bundle);
        } else if (object instanceof Artist) {
            Artist artist = (Artist) object;
            Bundle bundle = new Bundle();
            bundle.putString("artist_name", artist.getName());
            bundle.putInt("artist_id", artist.getId());
            bundle.putParcelableArrayList("albums", artist.albums);

            ArtistFragmentFullView fragmentFullView = new ArtistFragmentFullView();
            fragmentFullView.setArguments(bundle);

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack("artist_view")
                    .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out, R.anim.slide_right_in, R.anim.slide_right_out)
                    .add(R.id.content_layout_container, fragmentFullView)
                    .commit();


        } else if (object instanceof Album) {
            Album album = (Album) object;
            Bundle bundle = new Bundle();
            bundle.putInt("album_id", album.getId());
            AlbumFragmentFullView fragmentFullView = new AlbumFragmentFullView();
            fragmentFullView.setArguments(bundle);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack("album_view")
                    .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out, R.anim.slide_right_in, R.anim.slide_right_out)
                    .add(R.id.content_layout_container, fragmentFullView)
                    .commit();

        }
    }

    @Override
    public void onMoreOptionClick(Object object, View view) {
        showPopUpMenu(view, (Song) object);
    }

    private void showPopUpMenu(final View view, final Song song) {

        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.song_list_popup_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.play_next:
                        Bundle bundle = new Bundle();
                        bundle.putInt("media_id", song.id);
                        mediaBrowserAdapter.getTransportControls().sendCustomAction(PLAY_NEXT, bundle);
                        break;
                    case R.id.add_to_queue:
                        mediaBrowserAdapter.addQueueItem(MediaItems.getMediaMetaDataFromMediaId(getActivity(), String.valueOf(song.id)).getDescription());
                        break;
                    case R.id.add_to_playlist:
                        showPopUpMenuForPlayList(view, song);
                        break;
                    case R.id.go_to_artist:

                        Bundle extra = new Bundle();
                        extra.putString("artist_name", song.artistName);
                        extra.putInt("artist_id", song.artistId);
                        extra.putParcelableArrayList("albums", ArtistLoader.getArtist(getActivity(), song.artistId).albums);

                        ArtistFragmentFullView artistFragmentFullView = new ArtistFragmentFullView();
                        artistFragmentFullView.setArguments(extra);

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack("artist_view")
                                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out, R.anim.slide_right_in, R.anim.slide_right_out)
                                .add(R.id.content_layout_container, artistFragmentFullView)
                                .commit();

                        break;
                    case R.id.go_to_album:
                        Bundle extra1 = new Bundle();
                        extra1.putInt("album_id", song.albumId);
                        AlbumFragmentFullView fragmentFullView = new AlbumFragmentFullView();
                        fragmentFullView.setArguments(extra1);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack("album_view")
                                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out, R.anim.slide_right_in, R.anim.slide_right_out)
                                .add(R.id.content_layout_container, fragmentFullView)
                                .commit();
                        break;
                    case R.id.details:
                        SongDetailDialog.create(song).show(getActivity().getSupportFragmentManager(),"Details");
                        break;
               /*     case R.id.delete_from_device:
                        break;*/
                }
                return false;
            }
        });
        popupMenu.show();

    }
    private void showPopUpMenuForPlayList(View view, final Song song)
    {
        final ArrayList<Playlist>playlists= PlaylistLoader.getAllPlaylists(getActivity());
        android.support.v7.widget.PopupMenu popupMenu=new android.support.v7.widget.PopupMenu(getActivity(),view);
        for(int i=0;i<playlists.size();i++)
        {
            popupMenu.getMenu().add(1,playlists.get(i).id,i,playlists.get(i).name);
        }
        popupMenu.getMenu().add(1,ADD_NEW_PLAYLIST_ID,playlists.size(),"Add New Playlist");
        popupMenu.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==ADD_NEW_PLAYLIST_ID)
                {
                    AddPlayListDialogFragment.create(song).show(getActivity().getSupportFragmentManager(),"Create Playlist");
                    return false;
                }
                for (int i=0;i<playlists.size();i++)
                {
                    if (playlists.get(i).id==item.getItemId())
                    {
                        PlaylistsUtil.addToPlaylist(getContext(),song,playlists.get(i).id,true);
                        return false;
                    }
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public void onClick(View view) {

        String s = search_text.getText().toString();

        if (search_text.getText() == null || search_text.getText().toString().equals(""))
            getActivity().getSupportFragmentManager().popBackStack();
        else search_text.setText("");

    }

    private static class AsyncSearchResultLoader extends WrappedAsyncTaskLoader<List<Object>> {
        private final String query;

        public AsyncSearchResultLoader(Context context, String query) {
            super(context);
            this.query = query;

        }

        @Override
        public List<Object> loadInBackground() {

            List<Object> results = new ArrayList<>();
            if (!TextUtils.isEmpty(query)) {
                List songs = SongLoader.getSongs(getContext(), query);
                if (!songs.isEmpty()) {
                    results.add(getContext().getResources().getString(R.string.songs));
                    results.addAll(songs);
                }

                List artists = ArtistLoader.getArtists(getContext(), query);
                if (!artists.isEmpty()) {
                    results.add(getContext().getResources().getString(R.string.artists));
                    results.addAll(artists);
                }

                List albums = AlbumLoader.getAlbums(getContext(), query);
                if (!albums.isEmpty()) {
                    results.add(getContext().getResources().getString(R.string.albums));
                    results.addAll(albums);
                }

            }
            return results;
        }
    }
}
