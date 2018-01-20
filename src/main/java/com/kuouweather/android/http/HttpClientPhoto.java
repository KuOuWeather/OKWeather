package com.kuouweather.android.http;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/1/16 0016.
 */

public class HttpClientPhoto {
    private Context context;

    /**
     * 正式使用空构造
     */
    public HttpClientPhoto() {

    }

    /**
     * 测试使用此构造
     *
     * @param context
     */
    public HttpClientPhoto(Context context) {
        this.context = context;
    }

    public interface OnResponseListener {
        void onResponse(String result);
    }

    /**
     * 数据+图片上传
     *
     * @param url
     * @param params
     * @param files
     * @return
     */
    private String postLoad(String url, RequestParams params, File[] files) {
        try {
            String boundary = "___WebKitFromBoundaryOnsdfds";
            String twoDash = "--";
            String end = "\r\n";
            URL mURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) mURL.openConnection();

            connection.setReadTimeout(8000);
            connection.setConnectTimeout(8000);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("ContentType", "multipart/form-data;boundary=" + boundary);

            OutputStream os = connection.getOutputStream();
            if (os != null && params != null) {
//往服务器上写参数
                Set<Map.Entry<String, String>> entries = params.getMap().entrySet();
                Iterator<Map.Entry<String, String>> iterator = entries.iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> next = iterator.next();
                    os.write((twoDash + boundary + end).getBytes());
                    os.write(("Content-Disposition:form-data;name=" + next.getKey()).getBytes());
                    os.write(end.getBytes());
                    os.write(end.getBytes());
                    os.write((next.getValue() + end).getBytes());
//往服务器上传图片
                    for (int i = 0; i < files.length; i++) {
                        os.write((twoDash + boundary + end).getBytes());
                        os.write(("Content-Disposion:from-data;name=file[];filename=img.jpg" + end).getBytes());
                        os.write(("Content-Type:image/*" + end).getBytes());
                        os.write(end.getBytes());

                        FileInputStream fis = new FileInputStream(files[i].getAbsolutePath());
                        byte[] bytes = new byte[1024];
                        int length = 0;
                        while ((length = fis.read(bytes)) != -1) {
                            os.write(bytes, 0, length);
                        }
                        os.write(end.getBytes());
                        os.write(end.getBytes());
                    }
                }
                os.write((twoDash + boundary + twoDash).getBytes());
                os.flush();
                os.close();
            }
            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                StringBuffer stringBuffer = new StringBuffer();
                while ((len = inputStream.read()) != -1) {
                    stringBuffer.append(new String(buffer, 0, len));
                }
                return stringBuffer.toString().trim();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }

    public void post(String url, RequestParams params, File[] files, OnResponseListener onResponseListener) {
        new MyAsyncTask(url, params, files, onResponseListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private class MyAsyncTask extends AsyncTask<String, Void, String> {

        private String url;
        private RequestParams params;
        private File[] files;
        private OnResponseListener onResponseListener;

        public MyAsyncTask(String url, RequestParams params, File[] files, OnResponseListener onResponseListener) {
            this.url = url;
            this.params = params;
            this.files = files;
            this.onResponseListener = onResponseListener;
        }

        /**
         * 支线程
         *
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String... params) {
            String result = postLoad(url, this.params, files);
            return result;
        }

        /**
         * 主线程
         *
         * @param s
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (onResponseListener != null) {
                onResponseListener.onResponse(s);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
