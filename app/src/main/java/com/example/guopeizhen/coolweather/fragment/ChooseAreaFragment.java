package com.example.guopeizhen.coolweather.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guopeizhen.coolweather.R;
import com.example.guopeizhen.coolweather.db.City;
import com.example.guopeizhen.coolweather.db.County;
import com.example.guopeizhen.coolweather.db.Province;
import com.example.guopeizhen.coolweather.util.HttpUtil;
import com.example.guopeizhen.coolweather.util.ToastUtil;
import com.example.guopeizhen.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/24.
 */

public class ChooseAreaFragment extends Fragment {
    private ProgressDialog progressDialog;

    private List<String> list = new ArrayList<>();

    private static final int PROVINCE= 1;
    private static final int CITY = 2;
    private static final int COUNTY = 3;

    private int current_level ;
    private Province selectedProvince;
    private City selectedCity;

    private List<Province> provinces;
    private List<City> cities;
    private List<County> countys;
    private ArrayAdapter<String> adapter ;
    private TextView tvBack,tvTitle;
    private ListView lv;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        tvBack= view.findViewById(R.id.tv_back);
        tvTitle = view.findViewById(R.id.tv_title);
        lv=view.findViewById(R.id.lv_choosearea);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,list);
        lv.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (current_level == PROVINCE){
                    selectedProvince = provinces.get(i);
                    queryCity();
                }else if (current_level == CITY){
                    selectedCity = cities.get(i);
                    queryCounty();
                }
            }
        });

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current_level == COUNTY){
                    queryCity();
                }else if ((current_level == CITY)){
                    queryProvince();
                }
            }
        });

        queryProvince();

    }



    //查询省份的数据
    private void queryProvince(){
        tvTitle.setText("中国");
        tvBack.setVisibility(View.GONE);
        provinces = DataSupport.findAll(Province.class);             //如果数据库存在了，则直接从数据库取数据。若没有则进行网络请求
        if (provinces.size()>0){
            list.clear();
            for (int i=0;i<provinces.size();i++){
                list.add(provinces.get(i).getProvinceName());
            }
            adapter.notifyDataSetChanged();
            lv.setSelection(0);
            current_level = PROVINCE;
        }else {
            String url = "http://guolin.tech/api/china";
            httpRequest(url,PROVINCE);
        }
    }

    //查询城市数据
    private void queryCity(){
        tvTitle.setText(selectedProvince.getProvinceName());
        tvBack.setVisibility(View.VISIBLE);
        cities = DataSupport.where("provinceId=?",selectedProvince.getProvinceCode()).find(City.class);
        if (cities.size()>0){
            list.clear();
            for (City city:cities){
                list.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lv.setSelection(0);
            current_level = CITY;
        }else {
            String url = "http://guolin.tech/api/china/"+selectedProvince.getProvinceCode();
            httpRequest(url,CITY);
        }
    }

    private void queryCounty() {
        tvTitle.setText(selectedCity.getCityName());
        tvBack.setVisibility(View.VISIBLE);
        countys = DataSupport.where("cityId=?",selectedCity.getCityCode()).find(County.class);
        if (countys.size()>0){
            list.clear();
            for (County county:countys) {
                list.add(county.getCountyName());
            }
            lv.setSelection(0);
            adapter.notifyDataSetChanged();
            current_level = COUNTY;
        }else {
            String url = "http://guolin.tech/api/china/"+selectedProvince.getProvinceCode()+"/"+selectedCity.getCityCode();
            httpRequest(url,COUNTY);
        }
    }

    //网络请求的回调实现
    private void httpRequest(String url, final int type){
        showProgressDialog();
        HttpUtil.sendHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast("加载失败!");

//                        Toast.makeText(getContext(),"加载失败！",Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Choose",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (type == PROVINCE){
                    if (Utility.handleProvinceJson(response.body().string())){
                        getActivity().runOnUiThread(new Runnable() {         //okhttp的callback会自动开启新的线程执行网络请求，所以想要操作界面UI必须跳去主线程执行
                            @Override
                            public void run() {
                                queryProvince();
                            }
                        });

                    }
                }else if(type == CITY){
                    if (Utility.handleCityJson(response.body().string(),selectedProvince.getProvinceCode())){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                queryCity();
                            }
                        });

                    }
                }else if(type == COUNTY){
                    if (Utility.handleCountyJson(response.body().string(),selectedCity.getCityCode())){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                queryCounty();
                            }
                        });
                    }
                }
            }
        });
        closeProgressDialog();
    }

    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("提示:");
            progressDialog.setMessage("正在加载，请稍后...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
