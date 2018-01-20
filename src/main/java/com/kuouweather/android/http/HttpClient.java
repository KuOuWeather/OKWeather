package com.kuouweather.android.http;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2018/1/17 0017.
 */

public class HttpClient {

    public interface OnResponseListener {
        void onResponse(String result);

        void onError();
    }

    private String post(String url, RequestParams params) {
        InputStream inputStream = null;
        try {
            URL mURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) mURL.openConnection();
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Connection", "Keep-Alive");

            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                byte[] bytes = new byte[1024];
                int len = 0;
                StringBuffer stringBuffer = new StringBuffer();
                while ((len = inputStream.read(bytes)) != -1) {
                    stringBuffer.append(new String(bytes, 0, len));
                }
                String str = stringBuffer.toString().trim();
                com.kuouweather.android.tools.Logger.e(getClass().getSimpleName(),str);
                inputStream.close();
                return str;

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("HttpClient", "Error : " + e.getMessage());
        }
        return "Error";
    }

    public void post(String url, RequestParams params, OnResponseListener onResponseListener) {
        new MyAsyncTask(url, params, onResponseListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class MyAsyncTask extends AsyncTask<String, Void, String> {

        private String url;

        private RequestParams params;

        private OnResponseListener onResponseListener;

        public MyAsyncTask(String url, RequestParams params, OnResponseListener onResponseListener) {
            this.url = url;
            this.params = params;
            this.onResponseListener = onResponseListener;
        }

        @Override
        protected String doInBackground(String... params) {
//            这是支线程
            String result = post(url, this.params);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
//            这是主线程
            super.onPostExecute(s);
            if (onResponseListener != null) {
                if (!"Error".equals(s)) {
                    onResponseListener.onResponse(s);
                } else {
                    onResponseListener.onError();
                }
            }
        }
    }
}
