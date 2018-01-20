package com.kuouweather.android.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/16 0016.
 */

public class RequestParams {
    private StringBuffer stringBuffer = new StringBuffer();
    //    用于图片上传
    private Map<String, String> map = new HashMap<>();

    public void put(String key, String value) {
        if (key != null && !"".equals(key) && value != null && !"".equals(value)) {
            stringBuffer.append(key);
            stringBuffer.append("=");
            stringBuffer.append(value);
            stringBuffer.append("&");
            map.put(key, value);
        }
    }

    public String buildRequestStriing() {
        String substring = stringBuffer.toString().substring(0, stringBuffer.toString().length() - 1);
        return substring;
    }

    public Map<String,String>getMap(){
        return map;
    }
}
