package com.techweblearn.musicbeat;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.techweblearn.musicbeat.Adapters.DrawerLayout;
import com.techweblearn.musicbeat.Dialogs.SleepTimerDialogFragment;
import com.techweblearn.musicbeat.Fragment.HomeFragment;
import com.techweblearn.musicbeat.Fragment.LibraryFragment;
import com.techweblearn.musicbeat.Fragment.MiniPlayerLayout;
import com.techweblearn.musicbeat.Fragment.PlayerLayout1;
import com.techweblearn.musicbeat.Fragment.QueueSongFragment;
import com.techweblearn.musicbeat.Utils.PreferencesUtil;
import com.techweblearn.musicbeat.Utils.Util;
import com.yarolegovich.slidingrootnav.SlideGravity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yarolegovich.slidingrootnav.callback.DragStateListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener
        , SlidingUpPanelLayout.PanelSlideListener
        , DrawerLayout.OnCallBack
        , PlayerLayout1.PlayerLayoutCallback {

    @BindView(R.id.menu_icon) ImageView menu;
    @BindView(R.id.search) ImageView search;
    @BindView(R.id.title) TextView title;

    int height;
    int width;
    FrameLayout miniplayerlayout;
    SlidingUpPanelLayout slidingUpPanelLayout;
    Unbinder unbinder;
    boolean isDarkTheme = false;
    boolean changeHomeFrament = false;
    boolean relaodPlayerLayout = false;
    TextView actionBarTitle;
    private FirebaseAnalytics mFirebaseAnalytics;
    private SlidingRootNav slidingRootNav, queueSlidingNav;
    private android.support.v7.app.ActionBar actionBar;
    private Toolbar toolbar;

    //Drawer Layout Views
    private RecyclerView navigation_menu;
    private ImageView sleepIcon;
    private TextView sleepText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDarkTheme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false);
        setTheme(Util.getTheme(this));
        setContentView(R.layout.activity_home);
        runIntro();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        toolbar = findViewById(R.id.toolbar);
        slidingUpPanelLayout = findViewById(R.id.slinding_up_layout);

        miniplayerlayout = findViewById(R.id.mini_player);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.action_bar_layout, null);
        actionBar.setCustomView(view);
        actionBarTitle = view.findViewById(R.id.title);
        actionBarTitle.setText("Home");
        unbinder = ButterKnife.bind(this, view);

        menu.setOnClickListener(this);
        search.setOnClickListener(this);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        initSlidingLayouts(savedInstanceState);

        DrawerLayout drawerLayout = new DrawerLayout(this);
        drawerLayout.setOnItemClicked(this);
        navigation_menu = findViewById(R.id.navigation_menu_recyclerview);
        sleepIcon=findViewById(R.id.icon);
        sleepText=findViewById(R.id.title);
        sleepText.setOnClickListener(this);
        sleepIcon.setOnClickListener(this);
        navigation_menu.setLayoutManager(new LinearLayoutManager(this));
        navigation_menu.setAdapter(drawerLayout);
        sleepIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_av_timer_24dp));
        sleepIcon.setColorFilter(Util.getThemeAccentColor(this), PorterDuff.Mode.SRC_ATOP);
        sleepText.setText("Sleep Timer");

        if (savedInstanceState == null) //Check if App Start then add the Fragment Otherwise Not
            addFragment();
    }

    @Override
    public void onBackPressed() {

        if (slidingRootNav.isMenuOpened())
            slidingRootNav.closeMenu(true);
        else if (queueSlidingNav.isMenuOpened())
            queueSlidingNav.closeMenu(true);
        else if (getSupportFragmentManager().findFragmentByTag("search") != null) {
            getSupportFragmentManager().popBackStack();
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (getSupportFragmentManager().findFragmentByTag("album_view") != null)
            getSupportFragmentManager().popBackStack();
        else if (getSupportFragmentManager().findFragmentByTag("artist_view") != null)
            getSupportFragmentManager().popBackStack();
        else if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        else
            super.onBackPressed();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.menu_icon:

                if (slidingRootNav.isMenuClosed())
                    slidingRootNav.openMenu(true);
                else slidingRootNav.closeMenu(true);

                break;
            case R.id.search:
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .setCustomAnimations(R.anim.slide_up_in, R.anim.slide_up_out, R.anim.slide_up_in, R.anim.slide_up_out)
                        .add(R.id.content_layout_container, new SearchActivity(), "search")
                        .commit();
                break;
            case R.id.icon:
            case R.id.title:
                SleepTimerDialogFragment.create().show(getSupportFragmentManager(),"");
        }
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

        miniplayerlayout.setAlpha(1 - slideOffset);
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

        switch (newState) {
            case EXPANDED:
                miniplayerlayout.setVisibility(View.GONE);
                break;
            case ANCHORED:
                miniplayerlayout.setVisibility(View.VISIBLE);
                break;
            case COLLAPSED:
                miniplayerlayout.setVisibility(View.VISIBLE);
                break;
            case DRAGGING:
                miniplayerlayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClicked(int position) {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        switch (position) {
            case 0:
                if (getSupportFragmentManager().findFragmentByTag("Home") == null) {
                    fragmentTransaction.add(R.id.container, new HomeFragment(), "Home").commit();
                    if (getSupportFragmentManager().findFragmentByTag("Library") != null)
                        getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("Library")).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag("Home")).commit();
                    if (getSupportFragmentManager().findFragmentByTag("Library") != null)
                        getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("Library")).commit();
                }

                actionBarTitle.setText("Home");
                PreferencesUtil.saveLastOpenedScreen(this,0);

                break;
            case 1:
                if (getSupportFragmentManager().findFragmentByTag("Library") == null) {
                    fragmentTransaction.add(R.id.container, new LibraryFragment(), "Library").commit();
                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("Home")).commit();
                } else {
                    fragmentTransaction.show(getSupportFragmentManager().findFragmentByTag("Library"));
                    if (getSupportFragmentManager().findFragmentByTag("Home") != null)
                        fragmentTransaction.hide(getSupportFragmentManager().findFragmentByTag("Home"));
                    fragmentTransaction.commit();
                }
                actionBarTitle.setText("Library");
                PreferencesUtil.saveLastOpenedScreen(this,1);
                break;
            case 2:
                startActivityForResult(new Intent(this, SettingActivity.class), 20);
                break;
            case 3:
                break;
            case 4:
                Toast.makeText(getApplicationContext(), "Work in Progress..", Toast.LENGTH_SHORT).show();
                break;
        }

        slidingRootNav.closeMenu(true);
    }

    @Override
    public void changeSidingUpPanel() {
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    private void addFragment() {
        PlayerLayout1 playerLayout1 = new PlayerLayout1();
        playerLayout1.setCallback(this);

        switch (Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("start_screen","2")))
        {
            case 0:
                getSupportFragmentManager().beginTransaction().add(R.id.container, new HomeFragment(), "Home").commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().add(R.id.container, new LibraryFragment(), "Library").commit();
                break;
            case 2:
                if(PreferencesUtil.getLastOpenedScreen(this)==0)
                    getSupportFragmentManager().beginTransaction().add(R.id.container, new HomeFragment(), "Home").commit();
                else
                    getSupportFragmentManager().beginTransaction().add(R.id.container, new LibraryFragment(), "Library").commit();
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.mini_player, new MiniPlayerLayout(), "MiniPlayer").commit();

        getSupportFragmentManager().beginTransaction().replace(R.id.player_container, playerLayout1, "Player1").commit();

        getSupportFragmentManager().beginTransaction().replace(R.id.queue_container, new QueueSongFragment(), "queue").commit();
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (changeHomeFrament) {
            changeHomeFrament = false;
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("Home")).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.container, new HomeFragment(), "Home").commit();
        }
        if (relaodPlayerLayout) {
            PlayerLayout1 playerLayout1 = new PlayerLayout1();
            playerLayout1.setCallback(this);
            relaodPlayerLayout = false;
            getSupportFragmentManager().beginTransaction().replace(R.id.player_container, playerLayout1).commit();
        }
    }


    private void initSlidingLayouts(Bundle savedInstanceState) {
        getHeightAndWidth();
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.side_menu_drawer)
                .withDragDistancePx(width >> 1)
                .addDragStateListener(new DragStateListener() {
                    @Override
                    public void onDragStart() {

                    }

                    @Override
                    public void onDragEnd(boolean isMenuOpened) {
                        queueSlidingNav.setMenuLocked(isMenuOpened);
                    }
                })
                .inject();


        queueSlidingNav = new SlidingRootNavBuilder(this)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withGravity(SlideGravity.RIGHT)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.playlist_side_layout)
                .withDragDistancePx(width >> 1)
                .addDragStateListener(new DragStateListener() {
                    @Override
                    public void onDragStart() {

                    }

                    @Override
                    public void onDragEnd(boolean isMenuOpened) {
                        slidingRootNav.setMenuLocked(isMenuOpened);
                    }
                })
                .inject();

        slidingUpPanelLayout.addPanelSlideListener(this);
    }

    private void runIntro() {


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    final Intent i = new Intent(HomeActivity.this, IntroActivity.class);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(i);
                            HomeActivity.this.finish();
                        }
                    });

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 20) {
            if (resultCode == RESULT_OK) {
                if (data.getBooleanExtra("is_theme_change", false)) {
                    recreate();
                }
            }
            if (data.getBooleanExtra("recently_added_interval", false))
                changeHomeFrament = true;

            if (data.getBooleanExtra("reload_player_layout", false))
                relaodPlayerLayout = true;
        }
    }

    private void getHeightAndWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
    }
}


