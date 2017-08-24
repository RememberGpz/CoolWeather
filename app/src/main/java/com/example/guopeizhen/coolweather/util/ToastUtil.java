package com.example.guopeizhen.coolweather.util;

import android.widget.Toast;

import org.litepal.LitePalApplication;

/**
 * Created by Administrator on 2017/8/24.
 */

public class ToastUtil {
    public static void showToast(String content){
        Toast.makeText(LitePalApplication.getContext(),content,Toast.LENGTH_SHORT).show();
    }
}
