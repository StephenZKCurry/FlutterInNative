package com.example.flutterinnative;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * desc   : Activity栈管理
 * author : zhukai
 * date   : 2020/5/28
 */
public class ActivityManager {

    private static ActivityManager instance;
    public List<Activity> activities = new ArrayList<>();

    private ActivityManager() {
    }

    public static ActivityManager getInstance() {
        if (instance == null) {
            instance = new ActivityManager();
        }
        return instance;
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activities) {
            if (activity.getClass().equals(cls)) {
                activity.finish();
            }
        }
    }

    /**
     * 获得当前的Activity
     *
     * @return 当前Activity
     */
    public Activity currentActivity() {
        if (activities.size() > 0) {
            return activities.get(activities.size() - 1);
        }
        return null;
    }
}
