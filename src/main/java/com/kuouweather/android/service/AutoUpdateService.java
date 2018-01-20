package com.kuouweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.kuouweather.android.gson.Weather;
import com.kuouweather.android.http.HttpClient;
import com.kuouweather.android.util.Utility;

/**
 * Created by Administrator on 2018/1/20 0020.
 */

public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000;//这是8小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 跟新天气信息
     */
    private void updateWeather() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather", null);
        if (weatherString != null) {

//            有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;

            String weatherURL = "http://guolin.tech/api/weather?cityid=" + weatherId +
                    "&key=c0221f1a325f49f9a58e721281ecd11a";
            HttpClient httpClient = new HttpClient();
            httpClient.post(weatherURL, null, new HttpClient.OnResponseListener() {
                @Override
                public void onResponse(String result) {
                    Weather weather1 = Utility.handleWeatherResponse(result);
                    if (weather1 != null && "ok".equals(weather1.status)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("weather", result);
                        editor.apply();
                    }
                }

                @Override
                public void onError() {

                }
            });
        }
    }

    /**
     * 更新每日一图
     */
    private void updateBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpClient httpClient = new HttpClient();
        httpClient.post(requestBingPic, null, new HttpClient.OnResponseListener() {
            @Override
            public void onResponse(String result) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic", result);
                editor.apply();
            }

            @Override
            public void onError() {

            }
        });
    }
}
