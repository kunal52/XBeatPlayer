package com.techweblearn.musicbeat.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techweblearn.musicbeat.Adapters.ArtistGridAdapter;
import com.techweblearn.musicbeat.Loader.ArtistLoader;
import com.techweblearn.musicbeat.Models.Album;
import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.provider.MediaItems;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistFragment extends Fragment implements ArtistGridAdapter.OnCallback {


    @BindView(R.id.artist_recyclerview)RecyclerView artist_recyclerview;

    ArtistGridAdapter artistGridAdapter;
    Unbinder unbinder;
    public ArtistFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_artist, container, false);
        unbinder= ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);

        artistGridAdapter=new ArtistGridAdapter(getActivity(), ArtistLoader.getAllArtists(getActivity()));
        artistGridAdapter.setCallback(this);
        artist_recyclerview.setLayoutManager(new GridLayoutManager(getActivity(),2));
        artist_recyclerview.setAdapter(artistGridAdapter);



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(unbinder!=null)
        unbinder.unbind();
    }

    @Override
    public void onItemClicked(int position, int artist_id, ArrayList<Album>albums,String artist_name) {
        Bundle bundle=new Bundle();
        bundle.putString("artist_name",artist_name);
        bundle.putInt("artist_id",artist_id);
        bundle.putParcelableArrayList("albums",albums);

        ArtistFragmentFullView fragmentFullView=new ArtistFragmentFullView();
        fragmentFullView.setArguments(bundle);


        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("artist_view")
                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out, R.anim.slide_right_in, R.anim.slide_right_out)
                .add(R.id.content_layout_container,fragmentFullView)
                .commit();
    }
}
