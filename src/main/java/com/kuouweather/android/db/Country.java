package com.kuouweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/1/16 0016.
 */

public class Country extends DataSupport {

    private int id;

    private String CountryName;

    private int CountryCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        this.CountryName = countryName;
    }

    public int getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(int countryCode) {
        this.CountryCode = countryCode;
    }
}
