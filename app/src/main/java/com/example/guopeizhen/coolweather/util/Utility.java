package com.example.guopeizhen.coolweather.util;

import com.example.guopeizhen.coolweather.db.City;
import com.example.guopeizhen.coolweather.db.County;
import com.example.guopeizhen.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/24.
 */

public class Utility {

    //解析服务器返回的省份json数据

    public static boolean handleProvinceJson(String response){
        try {
            if (response!=null){
                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonObject;
                for (int i=0;i<jsonArray.length();i++){
                    jsonObject = jsonArray.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceCode(jsonObject.getString("id"));
                    province.setProvinceName(jsonObject.getString("name"));
                    province.save();
                }
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    //解析服务器返回的城市信息

    public static boolean handleCityJson(String response,String provinceId){
        try {
            if (response!=null){
                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonObject;
                for (int i=0;i<jsonArray.length();i++){
                    jsonObject = jsonArray.getJSONObject(i);
                    City city = new City();
                    city.setCityName(jsonObject.getString("name"));
                    city.setProvinceId(provinceId);
                    city.setCityCode(jsonObject.getString("id"));
                    city.save();
                }
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean handleCountyJson(String response,String cityId){
        try {
            if (response!=null){
                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonObject ;
                for (int i=0;i <jsonArray.length();i++){
                    jsonObject = jsonArray.getJSONObject(i);
                    County county = new County();
                    county.setCityId(cityId);
                    county.setCountyName(jsonObject.getString("name"));
                    county.setWeatherId(jsonObject.getString("weather_id"));
                    county.save();
                }
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

}
