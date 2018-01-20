package com.kuouweather.android.tools;

import android.util.Log;

/**
 * Created by Administrator on 2018/1/20 0020.
 */

public class Logger {
    private final static boolean isTAG = true;

    public static void e(String tag, String msg) {
        if (isTAG) {
            Log.e(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (isTAG) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isTAG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isTAG) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isTAG) {
            Log.w(tag, msg);
        }
    }
}
