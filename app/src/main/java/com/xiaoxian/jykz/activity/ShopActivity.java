package com.xiaoxian.jykz.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.xiaoxian.jykz.adapter.RecylerViewAdapter;
import com.xiaoxian.jykz.adapter.ShopCommodityAdapter;
import com.xiaoxian.jykz.config.AppConfigProper;
import com.xiaoxian.jykz.util.model.JsonResp;
import com.xiaoxian.jykz.util.okhttputil.HttpUtils;
import com.xiaoxian.jykz.util.okhttputil.OkHttp3Util;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import com.xiaoxian.jykz.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ShopActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView img_electric_appliance_shop,img_dress_shop,img_shoe_bag_shop,img_beauty_makeup_shop,img_books_shop,img_all_class_shop;

    private LinearLayout linearShopSearch;
    private SwipeRefreshLayout swipeRefreshLayout;//下拉刷新

    private Banner banner;//轮播图
    private ArrayList<Integer> list_path;

    private RecyclerView recyclerCommodity;//猜你喜欢列表
    private LinearLayoutManager linearLayoutManager;
    private ShopCommodityAdapter shopCommodityAdapter;

    private String[] cnxhSize;//猜你喜欢网络图片地址
    private List<Map> goodsInfoList;//商品列表

    private initDataTask mAuthTask=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        initData();
        initLinear();
        initswipeLayout();
        initBanner();
        initView();


    }

    private void initView(){
        img_electric_appliance_shop=(ImageView)findViewById(R.id.img_electric_appliance_shop);
        img_electric_appliance_shop.setOnClickListener(this);
        img_dress_shop=(ImageView)findViewById(R.id.img_dress_shop);
        img_dress_shop.setOnClickListener(this);
        img_shoe_bag_shop=(ImageView)findViewById(R.id.img_shoe_bag_shop);
        img_shoe_bag_shop.setOnClickListener(this);
        img_beauty_makeup_shop=(ImageView)findViewById(R.id.img_beauty_makeup_shop);
        img_beauty_makeup_shop.setOnClickListener(this);
        img_books_shop=(ImageView)findViewById(R.id.img_books_shop);
        img_books_shop.setOnClickListener(this);
        img_all_class_shop=(ImageView)findViewById(R.id.img_all_class_shop);
        img_all_class_shop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(ShopActivity.this,CommodityListActivity.class);
        switch (v.getId()){
            case R.id.img_electric_appliance_shop:
                intent.putExtra( "searchInfo","电" );
                startActivity(intent);
                break;
            case R.id.img_dress_shop:
                intent.putExtra( "searchInfo","服" );
                startActivity(intent);
                break;
            case R.id.img_shoe_bag_shop:
                intent.putExtra( "searchInfo","鞋" );
                startActivity(intent);
                break;
            case R.id.img_beauty_makeup_shop:
                intent.putExtra( "searchInfo","妆" );
                startActivity(intent);
                break;
            case R.id.img_books_shop:
                intent.putExtra( "searchInfo","书" );
                startActivity(intent);
                break;
            case R.id.img_all_class_shop:
                intent.putExtra( "searchInfo"," " );
                startActivity(intent);
                break;


        }
    }

    //初始化点击
    private void initLinear(){

        linearShopSearch=(LinearLayout)findViewById(R.id.linear_shop_search);
        linearShopSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShopActivity.this, "搜索", Toast.LENGTH_SHORT).show();
            }
        });
        linearShopSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ShopActivity.this,SearchShopActivity.class);
                startActivity(intent);
            }
        });
    }

    //下拉刷新
    private void initswipeLayout(){
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_layout_shop);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //模拟网络请求需要1500毫秒，请求完成，设置setRefreshing 为false
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        initData();
                        initRecyclerCommodity();
                    }
                }, 1500);
            }
        });
    }

    //初始化轮播图
    private void initBanner(){
        banner = (Banner) findViewById(R.id.banner);
        list_path = new ArrayList<Integer>();
        list_path.add(R.drawable.image_lb_1);
        list_path.add(R.drawable.image_lb_2);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setImageLoader(new MyLoader());
        banner.setBannerAnimation(Transformer.Default);
        banner.setDelayTime(3000);
        banner.isAutoPlay(true);
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setImages(list_path).setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {

            }
        });
        banner.start();
    }



    private class MyLoader extends ImageLoader{
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context.getApplicationContext()).load(path).into(imageView);
        }
    }

    //备用方法 猜你喜欢listView
    private void initRecyclerCommodity_back(){
        recyclerCommodity=(RecyclerView)findViewById(R.id.recycler_commodity_shop);
        linearLayoutManager = new LinearLayoutManager(ShopActivity.this,LinearLayoutManager.VERTICAL,false);
        recyclerCommodity.setLayoutManager(linearLayoutManager);
        recyclerCommodity.setNestedScrollingEnabled(false);
        recyclerCommodity.setHasFixedSize(true);
        recyclerCommodity.setFocusable(false);
        final RecylerViewAdapter adapter = new RecylerViewAdapter(ShopActivity.this);
        recyclerCommodity.setAdapter(adapter);
        /*adapter.setOnItemClickListener(new RecylerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int id) {
                Intent intent = new Intent( ShopActivity.this,Details.class);
                intent.putExtra( "id",id );
                startActivity( intent );
                getActivity().overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            }
            @Override
            public void onLongClick(int id) {
                Intent intent = new Intent( getActivity(),Details.class);
                intent.putExtra( "id",id );
                startActivity( intent );
                getActivity().overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            }
        });*/
    }

    //猜你喜欢listView
    private void initRecyclerCommodity(){
        recyclerCommodity=(RecyclerView)findViewById(R.id.recycler_commodity_shop);
        linearLayoutManager = new LinearLayoutManager(ShopActivity.this,LinearLayoutManager.VERTICAL,false);
        recyclerCommodity.setLayoutManager(linearLayoutManager);
        recyclerCommodity.setNestedScrollingEnabled(false);
        recyclerCommodity.setHasFixedSize(true);
        recyclerCommodity.setFocusable(false);

        shopCommodityAdapter=new ShopCommodityAdapter(ShopActivity.this,ShopActivity.this,goodsInfoList);
        recyclerCommodity.setAdapter(shopCommodityAdapter);
        shopCommodityAdapter.setOnItemClickListener(new ShopCommodityAdapter.OnItemClickListener() {
            @Override
            public void onClick(int id) {
                Intent intent=new Intent(ShopActivity.this,CommodityDetailsActivity.class);
                intent.putExtra( "goodsid",id );
                startActivity( intent );
                ShopActivity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            }

            @Override
            public void onLongClick(int id) {
                //Toast.makeText(ShopActivity.this, "text"+id, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData(){
        mAuthTask = new initDataTask();
        mAuthTask.execute((Void) null);
    }

    private class initDataTask extends AsyncTask<Void, Void, String>{

        private String string="";
        @Override
        protected String doInBackground(Void... voids) {
            try {
                Map map=new HashMap();
                map.put("goodsName","");
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
            List<Map> goodsList=JSONObject.parseArray(resultStr,Map.class);

            goodsInfoList=goodsList;
            initRecyclerCommodity();

        }
    }

}
