package com.kuouweather.android.tools;

import android.app.Activity;
import android.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/16 0016.
 */

public class ActivityCollector {
    private static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        activities.clear();
    }

    /**
     * 添加碎片
     * @param activity
     * @param fragment
     * @param resourceId
     */
    public static void addFragment(Activity activity, Fragment fragment, int resourceId) {
        if (fragment != null)
            activity.getFragmentManager().beginTransaction().add(resourceId, fragment).commit();
    }




}
