package com.xiaoxian.jykz.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoxian.jykz.R;
import com.xiaoxian.jykz.util.FastJsonUtils;
import com.xiaoxian.jykz.util.model.City;
import com.xiaoxian.jykz.util.model.Province;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选择地址信息activity
 */
public class AddressInfoActivity extends AppCompatActivity {
    private Spinner spinnerProvince;
    private Spinner spinnerCity;
    private Spinner spinnerArea;

    private EditText editDetailed;//输入详细地址

    private Button btnOkAddress;//确认按钮
    private int resultCode = 102;//返回码
    private String[] allProvince;//所有的省

    private ArrayAdapter<String> provinceAdapter;//省份数据适配器
    private ArrayAdapter<String> cityAdapter;//城市数据适配器
    private ArrayAdapter<String> areaAdapter;//区县数据适配器

    private String[] allSpinList;//在spinner中选出来的地址，后面需要用空格隔开省市区

    private String address;//用来接收intent的参数
    private String allAddress;//用来接收intent参数


    private String provinceName;//省的名字
    private String areaName;//区的名字
    private Boolean isFirstLoad = true;//判断是不是最近进入对话框
    private Boolean ifSetFirstAddress = true;//判断是否已经设置了，初始的详细地址
    //省市区的集合
    private Map<String, String[]> cityMap = new HashMap<String, String[]>();//key:省p---value:市n  value是一个集合
    private Map<String, String[]> areaMap = new HashMap<String, String[]>();//key:市n---value:区s    区也是一个集合


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_info);
        initView();
        initDatas();
        //initClick();
        setSpinnerDate();
    }
    //初始化View
    private void initView(){
        spinnerProvince=(Spinner)findViewById(R.id.spinner_province);
        spinnerCity=(Spinner)findViewById(R.id.spinner_city);
        spinnerArea=(Spinner)findViewById(R.id.spinner_area);
        editDetailed=(EditText)findViewById(R.id.edit_Detailed);
        btnOkAddress=(Button)findViewById(R.id.btn_ok_address);
        btnOkAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //把值返回给MainActivity
                Intent intent = new Intent();
                areaName = spinnerArea.getSelectedItem() == null ? "" : spinnerArea.getSelectedItem().toString();
                intent.putExtra("address", spinnerProvince.getSelectedItem() + " " + spinnerCity.getSelectedItem() + " " + areaName);
                intent.putExtra("allAddress", editDetailed.getText().toString());
                setResult(resultCode, intent);
                finish();
            }
        });
    }

    //初始化省市区数据
    private void initDatas() {
        try {
            String jsonData= FastJsonUtils.getJson("region.json",this);
            JSONArray arrayData= JSON.parseArray(jsonData);
            String provincesStr= JSONObject.toJSONString(arrayData);
            List<Province> provincesList=JSONObject.parseArray(provincesStr,Province.class);
            Province province;
            allProvince=new String[provincesList.size()];
            for (int i=0;i<provincesList.size();i++){
                province=provincesList.get(i);
                allProvince[i]=province.getName();//省份 封装所有的省

                JSONArray CityArr=JSON.parseArray(province.getCity());
                City city;
                List<City> cityList=JSONObject.parseArray(JSONObject.toJSONString(CityArr),City.class);
                String[] allCity=new String[cityList.size()];
                for(int j=0;j<cityList.size();j++){
                    city=cityList.get(j);
                    allCity[j]=city.getName();
                    //for (int l=0;)
                    String str=city.getArea();
                    String[] allArea=str.substring(2,str.length()-2).split("\",\"");//(city.getArea().Remove(0,2)).Substring(0,city.getArea()-2);//city.getArea().split("\",\"");
                    areaMap.put(city.getName(), allArea);//某个市取出所有的区集合
                }
                cityMap.put(province.getName(), allCity);//某个省取出所有的市,
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setSpinnerDate(){
        int selectPosition = 0;//有数据传入时
        address = getIntent().getStringExtra("address");
        allAddress = getIntent().getStringExtra("allAddress");
        if (address != null && !address.equals("") && allAddress != null && !allAddress.equals("")){
            allSpinList = address.split(" ");//用空格隔开allSpinList地址
        }
        /**
         * 设置省市区的适配器，进行动态设置
         */
        provinceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);//系统默认的
        for (int i = 0; i < allProvince.length; i++) {
            //给spinner省赋值,设置默认值
            if (address != null && !address.equals("") && allAddress != null && !allAddress.equals("")&& allSpinList.length > 0 && allSpinList[0].equals(allProvince[i])) {
                selectPosition = i;
            }
            provinceAdapter.add(allProvince[i]);//添加每一个省
        }
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvince.setAdapter(provinceAdapter);
        spinnerProvince.setSelection(selectPosition);

        //市
        cityAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(cityAdapter);

        //区
        areaAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArea.setAdapter(areaAdapter);

        setListener();//设置spinner的点击监听
    }
    //设置spinner的点击监听
    private void setListener() {
        //省
        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                provinceName = parent.getSelectedItem() + "";//获取点击列表spinner item的省名字
                if (isFirstLoad) {
                    // 判断是否省市区都存在
                    if (address != null && !address.equals("") && allAddress != null && !allAddress.equals("")&& allSpinList.length > 1 && allSpinList.length < 3) {
                        updateCityAndArea(provinceName, allSpinList[1], null);//更新市和区
                    }else if(address != null && !address.equals("") && allAddress != null && !allAddress.equals("") && allSpinList.length >= 3){
                        //存在省市区
                        //去更新
                        updateCityAndArea(provinceName, allSpinList[1], allSpinList[2]);
                    }else{
                        updateCityAndArea(provinceName,null,null);
                    }
                }else {
                    updateCityAndArea(provinceName,null,null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //市
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isFirstLoad) {
                    //更新区
                    updateArea(parent.getSelectedItem() + "", null);
                } else {
                    if (address != null && !address.equals("") && allAddress != null && !allAddress.equals("") && allSpinList.length == 4) {
                        editDetailed.setText(allSpinList[3]);//没有进入区的对话框，
                    }
                }
                isFirstLoad = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //区
        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //直接获取区的名字
                areaName = parent.getSelectedItem() + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 根据当前的省，更新市和区的信息
     */
    private void updateCityAndArea(Object object, Object city, Object area) {
        int selectPosition = 0;//有数据时，进行匹配城市，默认选中
        String[] cities = cityMap.get(object);
        cityAdapter.clear();//清空adapter的数据
        for (int i = 0; i < cities.length; i++) {
            if (city != null && city.toString().equals(cities[i])) {//判断传入的市在集合中匹配
                selectPosition = i;
            }
            cityAdapter.add(cities[i]);//将这个列表“市”添加到adapter中
        }
        cityAdapter.notifyDataSetChanged();//刷新
        if (city == null) {
            updateArea(cities[0], null);//更新区,没有市则默认第一个给它
        }else{
            spinnerCity.setSelection(selectPosition);
            updateArea(city, area);//穿入的区去集合中匹配
        }
    }

    //根据当前的市，更新区的信息
    private void updateArea(Object object, Object myArea) {
        boolean isArea = false;//判断第三个地址是地区还是详细地址
        int selectPosition = 0;//当有数据时，进行匹配地区，默认选中
        String[] area = areaMap.get(object);
        areaAdapter.clear();//清空
        if (area != null) {
            for (int i = 0; i < area.length; i++) {
                if (myArea != null && myArea.toString().equals(area[i])) {//去集合中匹配
                    selectPosition = i;
                    isArea = true;//地区
                }
                areaAdapter.add(area[i]);//填入到这个列表
            }
            areaAdapter.notifyDataSetChanged();//刷新
            spinnerArea.setSelection(selectPosition);//默认选中
        }
        //第三个地址是详细地址，并且是第一次设置edtext值，正好，地址的长度为3的时候，设置详细地址
        if (!isArea && ifSetFirstAddress && address != null && !address.equals("") && allAddress != null && !allAddress.equals("") && allSpinList.length == 3) {
            //et_detailAddress.setText(allSpinList[2]);
            ifSetFirstAddress = false;
        }
    }
}
