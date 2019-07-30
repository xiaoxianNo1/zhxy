package com.xiaoxian.jykz.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaoxian.jykz.R;
import com.xiaoxian.jykz.adapter.OrderListAdapter;
import com.xiaoxian.jykz.config.AppConfigProper;
import com.xiaoxian.jykz.util.model.JsonResp;
import com.xiaoxian.jykz.util.okhttputil.OkHttp3Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OrderListActivity extends AppCompatActivity {
    private LinearLayout topbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<String> lists = new ArrayList<String>();
    private View rootView;
    private List<Map> orderList;//订单列表

    private initDataTask mAuthTask=null;

    private String lastTAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        Intent intent=getIntent();
        lastTAG=intent.getStringExtra("TAG");

        SharedPreferences sharedPreferences = OrderListActivity.this.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        String UserId=sharedPreferences.getString("UserId","");
        mAuthTask = new initDataTask(UserId);
        mAuthTask.execute((Void) null);
        //initView();
    }

    private void initRecyclerCommodity(){
        topbar = (LinearLayout)findViewById(R.id.topbar_layout);
        topbar.bringToFront();
        // 初始化item
        recyclerView = (RecyclerView) findViewById(R.id.message_recyclerview);
        linearLayoutManager = new LinearLayoutManager(OrderListActivity.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);

        OrderListAdapter orderListAdapter=new OrderListAdapter(OrderListActivity.this,orderList);
        recyclerView.setAdapter(orderListAdapter);
        //orderListAdapter.setOnCLick

    }

    private class initDataTask extends AsyncTask<Void, Void, String>{
        private String string="";
        String userid=null;
        initDataTask(String userid){
            this.userid=userid;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Map map=new HashMap();
                map.put("userid",userid);
                map.put("lastTAG",lastTAG);
                //map.put()

                //从配置文件读取UserRegisterServerUrl
                Properties proper = AppConfigProper.getProperties(getApplicationContext());
                String getOrdersServerUrl = proper.getProperty("GetOrdersServerUrl");
                OkHttp3Util.doPostJson(getOrdersServerUrl, JSON.toJSONString(map), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp=response.body().string();
                        JsonResp params= JSON.parseObject(resp,JsonResp.class);
                        if(params!=null&& !params.toString().isEmpty()){
                            if(params.getCode().equals(200)){
                                if(params.getSuccess()){
                                    string=params.getResult().toString();
                                }
                            }
                        }
                    }
                });
                Thread.sleep(1500);
            }catch (Exception e){

            }
            return string;
        }

        @Override
        public void onPostExecute(final String resultStr ){
            List<Map> ordersList= JSONObject.parseArray(resultStr,Map.class);
            orderList=ordersList;
            initRecyclerCommodity();
        }
    }
}
