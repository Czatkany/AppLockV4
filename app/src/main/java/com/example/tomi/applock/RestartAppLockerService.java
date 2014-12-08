package com.example.tomi.applock;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

/**
 * Created by Tomi on 2014.12.08..
 */
public class RestartAppLockerService extends Service {

    private final Handler handler = new Handler();
    private Runnable runnable;
    public static final String TAG = "com.example.tomi.applock.RestartAppLockerService";

    public RestartAppLockerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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

    public int ForegroundApp()
    {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo ar = RunningTask.get(0);
        String activityOnTop = ar.topActivity.getPackageName();
        if(activityOnTop.contains("home"))
        {
            Log.w("Para", "HOPPHOPP");
            Intent i = new Intent(RestartAppLockerService.this, AppLockerService.class);
            RestartAppLockerService.this.startService(i);
            return 0;
        }
        Log.w("RESTART", "még nem");
        return 1;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
