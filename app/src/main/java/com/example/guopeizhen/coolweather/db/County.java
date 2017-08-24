package com.example.guopeizhen.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/8/24.
 */

public class County extends DataSupport {
    private int id;
    private String countyName;
    private String weatherId;

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getWeatherId() {

        return weatherId;
    }

    private String cityId;


    public void setId(int id) {
        this.id = id;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }


    public void setCityId(String cityId) {
        this.cityId = cityId;
    }



    public int getId() {
        return id;
    }

    public String getCountyName() {
        return countyName;
    }


    public String getCityId() {
        return cityId;
    }
}
