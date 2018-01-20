package com.kuouweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/1/19 0019.
 */

public class ForeCast {

    @SerializedName("date")
    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class More {

        @SerializedName("txt_d")
        public String info;

    }

    public class Temperature {

        @SerializedName("max")
        public String max;

        @SerializedName("min")
        public String min;
    }
}
