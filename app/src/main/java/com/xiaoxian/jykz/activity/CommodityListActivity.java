package com.xiaoxian.jykz.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaoxian.jykz.R;
import com.xiaoxian.jykz.adapter.ShopCommodityAdapter;
import com.xiaoxian.jykz.config.AppConfigProper;
import com.xiaoxian.jykz.util.model.JsonResp;
import com.xiaoxian.jykz.util.okhttputil.OkHttp3Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CommodityListActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;//下拉刷新

    private RecyclerView recyclerCommodity;

    private List<Map> goodsInfoList;//商品列表
    private initDataTask mAuthTask=null;
    private LinearLayoutManager linearLayoutManager;
    private ShopCommodityAdapter shopCommodityAdapter;

    private LinearLayout linearSearchList;

    private ImageView img_return_commodity_list;

    private String goodsName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_list);
        goodsName=getIntentInfo();
        initData();
        initswipeLayout();
        initLinearSearch();
        initReturn();
    }

    //下拉刷新
    private void initswipeLayout(){
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_layout_commodity_list);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //模拟网络请求需要3000毫秒，请求完成，设置setRefreshing 为false
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    //上一个页面传来的searchInfo
    private String getIntentInfo(){
        return getIntent().getStringExtra( "searchInfo" );
    }

    private void initData(){
        mAuthTask = new initDataTask(goodsName);
        mAuthTask.execute((Void) null);
    }

    private void initRecyclerCommodity(){
        recyclerCommodity=(RecyclerView)findViewById(R.id.recycler_commodity_list);
        linearLayoutManager = new LinearLayoutManager(CommodityListActivity.this,LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerCommodity.setLayoutManager(linearLayoutManager);
        recyclerCommodity.setNestedScrollingEnabled(false);
        recyclerCommodity.setHasFixedSize(true);
        recyclerCommodity.setFocusable(false);

        shopCommodityAdapter=new ShopCommodityAdapter(CommodityListActivity.this,CommodityListActivity.this,goodsInfoList);
        recyclerCommodity.setAdapter(shopCommodityAdapter);
        shopCommodityAdapter.setOnItemClickListener(new ShopCommodityAdapter.OnItemClickListener() {
            @Override
            public void onClick(int id) {
                //Toast.makeText(ShopActivity.this, "text"+id, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(CommodityListActivity.this,CommodityDetailsActivity.class);
                intent.putExtra( "goodsid",id );
                startActivity( intent );
                CommodityListActivity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            }

            @Override
            public void onLongClick(int id) {
                //Toast.makeText(ShopActivity.this, "text"+id, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class initDataTask extends AsyncTask<Void, Void, String> {
        private final String goodsName;
        initDataTask(String goodsName){
            this.goodsName=goodsName;
        }

        private String string="";
        @Override
        protected String doInBackground(Void... voids) {
            try {
                Map map=new HashMap();
                map.put("goodsName",goodsName);
                //System.out.println("消息"+goodsName);
                //从配置文件读取UserRegisterServerUrl
                Properties proper = AppConfigProper.getProperties(getApplicationContext());
                String goodsListServerUrl = proper.getProperty("GoodsListServerUrl");
                OkHttp3Util.doPostJson(goodsListServerUrl, JSON.toJSONString(map), new Callback() {
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
                e.printStackTrace();
            }
            return string;
        }

        @Override
        public void onPostExecute(final String resultStr ){
            //System.out.println("消息resultStr:"+resultStr);
            List<Map> goodsList= JSONObject.parseArray(resultStr,Map.class);

            goodsInfoList=goodsList;
            initRecyclerCommodity();

        }
    }

    private void initLinearSearch(){
        linearSearchList=(LinearLayout)findViewById(R.id.linear_search_list);
        linearSearchList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CommodityListActivity.this,SearchShopActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initReturn(){
        img_return_commodity_list=(ImageView)findViewById(R.id.img_return_commodity_list);
        img_return_commodity_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
