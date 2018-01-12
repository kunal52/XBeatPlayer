package com.techweblearn.musicbeat.Dialogs;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.AlarmManagerCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.Service.MediaBrowserAdapter;
import com.techweblearn.musicbeat.Service.MusicPlayBackService;
import com.techweblearn.musicbeat.Service.SleepTimer;
import com.techweblearn.musicbeat.Utils.PreferencesUtil;
import com.techweblearn.musicbeat.Utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Kunal on 10-01-2018.
 */

public class SleepTimerDialogFragment extends DialogFragment implements AppCompatSeekBar.OnSeekBarChangeListener,View.OnClickListener {


    @BindView(R.id.set_timer)Button set_timer;
    @BindView(R.id.cancel_button)Button cancel;
    @BindView(R.id.sleep_timer_seekbar)AppCompatSeekBar seekBar;
    @BindView(R.id.sleep_timer_text)TextView sleep_timer_text;
    @BindView(R.id.previos_set_sleeptime_text)TextView previos_set_sleeptime_text;
    @BindView(R.id.done)Button done;

    private TimerUpdater timerUpdater;
    private static final int maxTimer=180; //4 Hours
    Unbinder unbinder;
    public static SleepTimerDialogFragment create()
    {
        return new SleepTimerDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.sleep_timer_layout,container,false);
        unbinder= ButterKnife.bind(this,view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        seekBar.setOnSeekBarChangeListener(this);
        set_timer.setOnClickListener(this);
        cancel.setOnClickListener(this);
        done.setOnClickListener(this);
        seekBar.setMax(maxTimer);
        seekBar.setProgress(PreferencesUtil.getLastSleepTimeSeekbar(getActivity()));
        timerUpdater=new TimerUpdater();
        if(getPreviousPendingIntent()!=null)
        {
            previos_set_sleeptime_text.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
            timerUpdater.start();
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        sleep_timer_text.setText(String.valueOf(progress)+" Min");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        PreferencesUtil.setLastSleepTimeSeekbar(getActivity(),seekBar.getProgress());
        sleep_timer_text.setText(String.valueOf(seekBar.getProgress())+" Min");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.cancel_button:
                cancelCurrentTimer();
                break;
            case R.id.set_timer:
                setTimer(seekBar.getProgress());
                Toast.makeText(getActivity(),"Timer is Set ",Toast.LENGTH_SHORT).show();
                dismiss();
                break;
            case R.id.done:
                dismiss();

                break;
        }
    }


    private void setTimer(int min)
    {
        final long nextSleepTimerElapsedTime = SystemClock.elapsedRealtime() + min * 60 * 1000;
        PendingIntent pendingIntent=PendingIntent.getBroadcast(getActivity(),23,getIntent(),PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextSleepTimerElapsedTime, pendingIntent);
        PreferencesUtil.setSleepTimer(getActivity(),nextSleepTimerElapsedTime);
        timerUpdater.cancel();
    }



    private void cancelCurrentTimer()
    {
        PendingIntent pendingIntent=getPreviousPendingIntent();
        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        if(pendingIntent!=null) {
            am.cancel(pendingIntent);
            pendingIntent.cancel();
        }
        PreferencesUtil.setSleepTimer(getActivity(),0);
        timerUpdater.onFinish();
    }

    private class TimerUpdater extends CountDownTimer {
        public TimerUpdater() {
            super(PreferencesUtil.getSleepElapsedTimer(getActivity()) - SystemClock.elapsedRealtime(), 1000);
        }

        @Override
        public void onTick(long millisUntilFinished) {
           previos_set_sleeptime_text.setText("Elapsed Timer "+Util.getReadableDurationString(millisUntilFinished)+" min");
        }

        @Override
        public void onFinish() {
            cancel.setVisibility(View.GONE);
            previos_set_sleeptime_text.setVisibility(View.GONE);
        }
    }

    private PendingIntent getPreviousPendingIntent()
    {
        return PendingIntent.getBroadcast(getActivity(),23,getIntent(),PendingIntent.FLAG_NO_CREATE);
    }

    private Intent getIntent()
    {
        return new Intent(getActivity(), MusicPlayBackService.SleepTimer.class);
    }

}
