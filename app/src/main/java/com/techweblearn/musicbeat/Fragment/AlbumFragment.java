package com.techweblearn.musicbeat.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techweblearn.musicbeat.Adapters.AlbumGridAdapter;
import com.techweblearn.musicbeat.Adapters.ArtistGridAdapter;
import com.techweblearn.musicbeat.Loader.AlbumLoader;
import com.techweblearn.musicbeat.Loader.ArtistLoader;
import com.techweblearn.musicbeat.Models.Album;
import com.techweblearn.musicbeat.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AlbumFragment extends Fragment implements AlbumGridAdapter.OnItemClicked {


    @BindView(R.id.album_recyclerview)RecyclerView recyclerView;
    Unbinder unbind;
    AlbumGridAdapter albumGridAdapter;

    public AlbumFragment() {



    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_album, container, false);
        unbind= ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        albumGridAdapter=new AlbumGridAdapter(getActivity(), AlbumLoader.getAllAlbums(getActivity()));
        albumGridAdapter.setItemClickedListener(this);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(albumGridAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbind.unbind();
    }

    @Override
    public void onItemClicked(int id) {
        Bundle bundle=new Bundle();
        bundle.putInt("album_id",id);

        AlbumFragmentFullView fragmentFullView=new AlbumFragmentFullView();
        fragmentFullView.setArguments(bundle);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("album_view")
                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out, R.anim.slide_right_in, R.anim.slide_right_out)
                .add(R.id.content_layout_container,fragmentFullView)
                .commit();
    }
}
