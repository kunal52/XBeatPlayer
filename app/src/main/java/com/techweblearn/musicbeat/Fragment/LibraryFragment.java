package com.techweblearn.musicbeat.Fragment;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.techweblearn.musicbeat.Adapters.MusicLibraryPagerAdapter;
import com.techweblearn.musicbeat.Helper.BottomNavigationViewHelper;
import com.techweblearn.musicbeat.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener,ViewPager.OnPageChangeListener {



    @BindView(R.id.pager)ViewPager viewPager;
  //  @BindView(R.id.tabs)TabLayout tabs;
    @BindView(R.id.bottom_navigation)BottomNavigationView bottomNavigationView;
    Unbinder unbinder;
    MusicLibraryPagerAdapter pagerAdapter;
    public LibraryFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_library, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        setUpViewPager();

        intitBottomNavigationSelectorColor();






    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void setUpViewPager()
    {
        pagerAdapter=new MusicLibraryPagerAdapter(getActivity(),getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.songs:
                viewPager.setCurrentItem(0);
                break;
            case R.id.albums:
                viewPager.setCurrentItem(1);
                break;
            case R.id.artists:
                viewPager.setCurrentItem(2);
                break;
        }
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {


        switch (position)
        {
            case 0:bottomNavigationView.getMenu().getItem(0).setChecked(true);
            break;
            case 1:bottomNavigationView.getMenu().getItem(1).setChecked(true);
            break;
            case 2:bottomNavigationView.getMenu().getItem(2).setChecked(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private void intitBottomNavigationSelectorColor()
    {
        int[][] states;
        int[] colors;
        if(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_theme",false)) {


            states = new int[][]{
                    new int[]{android.R.attr.state_checked},
                    new int[]{-android.R.attr.state_checked},
            };

            colors = new int[]{
                    Color.parseColor("#ffffff"),
                    Color.parseColor("#757575")
            };
        }
        else
        {
            states = new int[][]{
                    new int[]{android.R.attr.state_checked},
                    new int[]{-android.R.attr.state_checked},
            };

            colors = new int[]{
                    Color.parseColor("#212121"),
                    Color.parseColor("#757575")
            };

        }

        ColorStateList colorStateList=new ColorStateList(states,colors);
        bottomNavigationView.setItemIconTintList(colorStateList);
        bottomNavigationView.setItemTextColor(colorStateList);
    }
}
