package com.kuouweather.android.util;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

/**
 * Created by Administrator on 2018/1/16 0016.
 */

public class HttpUtil {
    public static void setOkHttpRequst(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue((Callback) callback);
    }
}
