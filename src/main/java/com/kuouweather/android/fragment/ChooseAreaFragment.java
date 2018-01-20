package com.kuouweather.android.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kuouweather.android.R;
import com.kuouweather.android.activity.MainActivity;
import com.kuouweather.android.activity.WeatherActivity;
import com.kuouweather.android.db.City;
import com.kuouweather.android.db.Country;
import com.kuouweather.android.db.Province;
import com.kuouweather.android.http.HttpClient;
import com.kuouweather.android.tools.BaseFragment;
import com.kuouweather.android.tools.Logger;
import com.kuouweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/1/16 0016.
 */

public class ChooseAreaFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTRY = 2;

    private ProgressDialog progressDialog;

    private TextView titleText;

    private Button backButton;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;

    /**
     * 市列表
     */
    private List<City> cityList;

    /**
     * 县列表
     */
    private List<Country> countryList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;

    /**
     * 选择的城市
     */
    private City selectedCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel;

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.choose_area, container, false);
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListener();
        queryProvinces();
    }

    @Override
    protected void initView() {
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void setListener() {
        backButton.setOnClickListener(this);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                if (currentLevel == LEVEL_COUNTRY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (currentLevel == LEVEL_PROVINCE) {
            selectedProvince = provinceList.get(position);
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            selectedCity = cityList.get(position);
            queryCountries();
        } else if (currentLevel == LEVEL_COUNTRY) {
            String weatherId = countryList.get(position).getWeatherId();
            if (getActivity() instanceof MainActivity) {
                Logger.e(TAG,"MainActivity"+getActivity());
                Intent intent = new Intent(getActivity(), WeatherActivity.class);
                intent.putExtra("weather_id", weatherId);
                startActivity(intent);
                getActivity().finish();
            } else if (getActivity() instanceof WeatherActivity) {
                Logger.e(TAG,"requestWeather \n"+getActivity() );
                WeatherActivity activity = (WeatherActivity) getActivity();
                activity.drawerLayout.closeDrawer(GravityCompat.START);
                activity.refreshLayout.setRefreshing(true);
                activity.requestWeather(weatherId);

            }


        }
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查到再去服务器上查询
     */
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china/";
            queryFromService(address, "province");
        }
    }

    /**
     * 查询全国所有的市，优先从数据库查询，如果没有查到再去服务器上查询
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?", String.
                valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromService(address, "city");
        }
    }

    /**
     * 查询全国所有的县，优先从数据库查询，如果没有查到再去服务器上查询
     */
    private void queryCountries() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countryList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(Country.class);
        if (countryList.size() > 0) {
            dataList.clear();
            for (Country country : countryList) {
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTRY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromService(address, "country");
        }
    }

    /**
     * 根据出入的地址和类型从服务器上查询省市县数据
     *
     * @param address
     * @param type
     */
    private void queryFromService(String address, final String type) {
        showProgressDialog();
        HttpClient httpClient = new HttpClient();
        httpClient.post(address, null, new HttpClient.OnResponseListener() {
            @Override
            public void onResponse(String result) {
                boolean isTrue = false;
                if ("province".equals(type)) {
                    isTrue = Utility.handleProvinceResponse(result);
                } else if ("city".equals(type)) {
                    isTrue = Utility.handleCityResponse(result, selectedProvince.getId());
                } else if ("country".equals(type)) {
                    isTrue = Utility.handleCountryResponse(result, selectedCity.getId());
                }

                if (isTrue) {
                    closeProgressDialog();
                    if ("province".equals(type)) {
                        queryProvinces();
                    } else if ("city".equals(type)) {
                        queryCities();
                    } else if ("country".equals(type))
                        queryCountries();
                }
            }

            @Override
            public void onError() {
                closeProgressDialog();
                toast("加载失败···");
            }
        });
//        HttpUtil.setOkHttpRequst(address, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        closeProgressDialog();
//                        toast("加载失败···");
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String responseText = response.body().string();
//                boolean result = false;
//                if ("province".equals(type)) {
//                    result = Utility.handleProvinceResponse(responseText);
//                } else if ("city".equals(type)) {
//                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
//                } else if ("country".equals(type)) {
//                    result = Utility.handleCountryResponse(responseText, selectedCity.getId());
//                }
//
//                if (result) {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            closeProgressDialog();
//                            if ("province".equals(type)) {
//                                queryProvinces();
//                            } else if ("city".equals(type)) {
//                                queryCities();
//                            } else if ("country".equals(type)) {
//                                queryCountries();
//                            }
//                        }
//                    });
//                }
//            }
//        });
    }

    /**
     * 显示对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载···");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
