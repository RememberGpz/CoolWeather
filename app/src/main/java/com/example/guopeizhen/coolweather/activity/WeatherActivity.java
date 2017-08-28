package com.example.guopeizhen.coolweather.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.guopeizhen.coolweather.R;
import com.example.guopeizhen.coolweather.gson.Forecast;
import com.example.guopeizhen.coolweather.gson.Weather;
import com.example.guopeizhen.coolweather.util.HttpUtil;
import com.example.guopeizhen.coolweather.util.ToastUtil;
import com.example.guopeizhen.coolweather.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/25.
 */

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity,titleUpdateTime,degreeText,weatherInfoText,aqiText,pm25Text,
                    comfortText,carWashText,sportText;
    private ImageView ivBg;   //主界面的背景图
    private LinearLayout forecastLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT>=21){                     //把任务栏也一起设置图片。只在Android5.0系统以上才支持
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        init();
        String weatherId = getIntent().getStringExtra("weather_id");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather",null);
        weatherLayout.setVisibility(View.INVISIBLE);
        requestWeather(weatherId);
    }

    private void init(){
        weatherLayout = (ScrollView)findViewById(R.id.sv_weatherlayout);
        titleCity = (TextView)findViewById(R.id.tv_title_city);
        titleUpdateTime = (TextView)findViewById(R.id.tv_title_updatetime);
        degreeText = (TextView)findViewById(R.id.tv_degreetext);
        weatherInfoText = (TextView)findViewById(R.id.tv_weatherinfo);
        aqiText = (TextView)findViewById(R.id.tv_aqitext);
        pm25Text = (TextView)findViewById(R.id.tv_pm25text);
        comfortText = (TextView)findViewById(R.id.tv_comforttext);
        carWashText = (TextView)findViewById(R.id.tv_carwash);
        sportText = (TextView)findViewById(R.id.tv_sporttext);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        ivBg = (ImageView)findViewById(R.id.iv_bg);
    }

    private void requestWeather(final String weatherId){
        String url = "http://guolin.tech/api/weather?cityid="+weatherId+
                "&key=8291eeccb8404a2795b2f68a21dd5be5";
        String picUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast("天气信息加载失败！");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.i("WeatherActivity",responseText);
                final Weather weather = Utility.handleWeatherJson(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather!=null&&"ok".equals(weather.status)){
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else {
//                            Log.d("WeatherActivity",weather.status);
                            ToastUtil.showToast("加载天气信息失败！");
                        }
                    }
                });
            }
        });
//        String pic=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).getString("pic","");
//        if (!pic.equals("")){
//            Glide.with(WeatherActivity.this).load(pic).into(ivBg);
//        }else {
            loadingBingPic(picUrl);
//        }
    }

    private void loadingBingPic(final String picUrl){
        HttpUtil.sendHttpRequest(picUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.showToast("加载背景图片失败");
                Log.e("WeatherActivity",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String pic = response.body().string();
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("pic",pic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(pic).into(ivBg);
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather){
        titleCity.setText(weather.basic.cityName);
        titleUpdateTime.setText(weather.basic.update.updateTime.split(" ")[1]);
        degreeText.setText(weather.now.temperature+"℃");
        weatherInfoText.setText(weather.now.more.info);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView date = view.findViewById(R.id.tv_date);
            TextView info = view.findViewById(R.id.tv_info);
            TextView max = view.findViewById(R.id.tv_max);
            TextView min = view.findViewById(R.id.tv_min);
            date.setText(forecast.date);
            info.setText(forecast.more.info);
            max.setText(forecast.temperature.max);
            min.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi!=null){
//            aqiText.setText(weather.aqi.aqiCity.aqi);
//            pm25Text.setText(weather.aqi.aqiCity.pm25);
        }

        comfortText.setText("舒适度:" + weather.suggestion.comfort.info);
        carWashText.setText("洗车指数:" + weather.suggestion.carWash.info);
        sportText.setText("运动建议:"+ weather.suggestion.sport.info);
        weatherLayout.setVisibility(View.VISIBLE);
    }
    long currentTime = 0;
    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis()-currentTime<2000){
            finish();
        }else {
            currentTime = System.currentTimeMillis();
            ToastUtil.showToast("再按一次退出应用=v=");
        }
    }
}
