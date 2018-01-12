package com.techweblearn.musicbeat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.techweblearn.musicbeat.Utils.Util;

/**
 * Created by Kunal on 04-01-2018.
 */

public class SettingActivity extends AppCompatActivity {


    static boolean  prevTheme=false;
    int prevRecentlyAddedInterval=0;
    int prevplayerLayoutViewPagerAnimationSelected=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Util.getTheme(this));
        setContentView(R.layout.setting_activity);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.setting_container,new SettingFragment()).commit();

        prevRecentlyAddedInterval= Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("recently_added_interval","1"));
        prevplayerLayoutViewPagerAnimationSelected=Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("playerlayout_viewpager_transformation","0"));
    }


    @Override
    public void onBackPressed() {

        Intent i = new Intent();
        if(prevTheme)
        {
            i.putExtra("is_theme_change",true );
        }


        if(prevRecentlyAddedInterval!=Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("recently_added_interval","1")))
            i.putExtra("recently_added_interval",true);
        else  i.putExtra("recently_added_interval",false);

        if(prevplayerLayoutViewPagerAnimationSelected!=Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("playerlayout_viewpager_transformation","0")))
            i.putExtra("reload_player_layout",true);
        else
            i.putExtra("reload_player_layout",false);

        setResult(RESULT_OK,i);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public static class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            addPreferencesFromResource(R.xml.setting_preference);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }


        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            PreferenceUtil.getInstance(getActivity()).registerOnSharedPreferenceChangedListener(this);
        }


        @Override
        public void onDestroyView() {
            super.onDestroyView();
            PreferenceUtil.getInstance(getActivity()).unregisterOnSharedPreferenceChangedListener(this);
        }




        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if(key.equals("theme"))
            {
                prevTheme=true;
                getActivity().recreate();

            }
            if(key.equals("recently_added_interval"))
            {

            }

            if(key.equals("playerlayout_viewpager_transformation"))
            {

            }
        }
    }

}
