package com.example.tomi.applock;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomi on 2014.12.08..
 */
public class AppLockerService extends Service {
    private ArrayList<String> packageNames = new ArrayList<String>();
    private final Handler handler = new Handler();
    private Runnable runnable;
    public static final String TAG = "com.example.tomi.applock.AppLockerService";

    public AppLockerService() {
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                if(ForegroundApp() == 1)
                {
                    handler.postDelayed(this, 100);
                }
            }
        }, 100);
        return 1;
    }

    public void Stop()
    {
        stopSelf();
    }

    public int ForegroundApp()
    {
        loadArray(this);
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo ar = RunningTask.get(0);
        String activityOnTop = ar.topActivity.getPackageName();
        Log.w("WTFF", "ciklus elott");
        if (activityOnTop.length() > 0) {
            for (String s : packageNames) {
                Log.w("WTFF", s);
                Log.w("WTFF", activityOnTop);
                if (s.matches(activityOnTop)) {
                    Log.w("WTFF", "win is here");

                    Intent startHomeScreen=new Intent(Intent.ACTION_MAIN);
                    startHomeScreen.addCategory(Intent.CATEGORY_HOME);
                    startHomeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startHomeScreen);

                    Intent i = new Intent();
                    i.setClass(this, CodeWindowActivity.class);
                    i.putExtra("foundApp",s);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    Log.w("STOP","Megallo1");
                    return 0;
                }
            }
        }
        return 1;
    }

    public void loadArray(Context mContext)
    {
        SharedPreferences settings = getSharedPreferences("MyPrefFile", 0);
        packageNames.clear();
        int size = settings.getInt("Status_size", 0);

        for(int i=0;i<size;i++)
        {
            packageNames.add(settings.getString("Status_" + i, null));
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
