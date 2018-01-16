package com.kuouweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/1/16 0016.
 */

public class City extends DataSupport {

    private int id;

    private String CityName;

    private int CityCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        this.CityName = cityName;
    }

    public int getCityCode() {
        return CityCode;
    }

    public void setCityCode(int cityCode) {
        this.CityCode = cityCode;
    }
}
