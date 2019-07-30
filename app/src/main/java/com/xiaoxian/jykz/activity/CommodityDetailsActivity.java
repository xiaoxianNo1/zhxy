package com.xiaoxian.jykz.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaoxian.jykz.R;
import com.xiaoxian.jykz.config.AppConfigProper;
import com.xiaoxian.jykz.util.PropertiesUtils;
import com.xiaoxian.jykz.util.imagesdownload.AsyncImageLoader;
import com.xiaoxian.jykz.util.imagesdownload.FileCache;
import com.xiaoxian.jykz.util.imagesdownload.MemoryCache;
import com.xiaoxian.jykz.util.model.JsonResp;
import com.xiaoxian.jykz.util.okhttputil.HttpUtils;
import com.xiaoxian.jykz.util.okhttputil.OkHttp3Util;
import com.youth.banner.Banner;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 商品详情
 */
public class CommodityDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtCloseDetails,txtMoneyDetails,txtNowpriceDetails,txtOldpriceDetails,txtGoodInfoDetails,txtAddmoreDetails,txtReduceDetails,txtGoodNumDetails;
    private ImageView imgIntroduceDetails;
    private Banner banner_introduce_details;
    private Button btnDeclineDetails,btnAddDetails,btnAddShopCartDetails,btnBuyNowDetails;
    private int goodsId;
    private String info,price,counts;

    private initDataTask mAuthTask=null;
    private initImgDataTask mImgTask=null;

    private AsyncImageLoader imageLoader;//异步组件

    Integer numberTemp;//选择商品数量

    String sellerId;//卖家Id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_details);
        this.goodsId=getIntentInfo();
        initView();
        initData();
    }

    //上一个页面传来的goodsid
    private int getIntentInfo(){
        return (int)getIntent().getSerializableExtra( "goodsid" );
    }

    //初始化数据
    private void initData(){
        mAuthTask = new initDataTask();
        mAuthTask.execute((Void) null);
        mImgTask=new initImgDataTask();
        mImgTask.execute((Void) null);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_close_details:
                finish();
                break;
            /*case R.id.txt_addmore_details:
                break;
            case R.id.txt_reduce_details:
                break;*/
            case R.id.btn_decline_details://数量减
                numberTemp =  Integer.parseInt(txtGoodNumDetails.getText().toString());
                numberTemp--;
                if (numberTemp < 1){
                    numberTemp = 1;
                }
                txtGoodNumDetails.setText(String.valueOf( numberTemp ));
                break;
            case R.id.btn_add_details://数量加
                numberTemp =  Integer.parseInt(txtGoodNumDetails.getText().toString());
                numberTemp++;
                txtGoodNumDetails.setText(String.valueOf( numberTemp ));
                break;
            case R.id.btn_add_shop_cart_details://加入购物车
                break;
            case R.id.btn_buy_now_details://现在购买
                Intent intent=new Intent(CommodityDetailsActivity.this,BuyerOrderActivity.class);
                intent.putExtra("goodsid",goodsId);
                intent.putExtra("goodsname",txtGoodInfoDetails.getText().toString());
                intent.putExtra("numberTemp",txtGoodNumDetails.getText().toString());
                intent.putExtra("secprice",txtNowpriceDetails.getText().toString());
                startActivity(intent);
                break;
                default:
                    Toast.makeText(this, "功能开发中，尽情其他！", Toast.LENGTH_SHORT).show();
                    break;
        }
    }

    //异步加载从服务器加载数据 任务
    private class initDataTask extends AsyncTask<Void, Void, String>{
        private String string="";
        @Override
        protected String doInBackground(Void... voids) {
            try{
                //从配置文件读取UserRegisterServerUrl
                Properties proper = AppConfigProper.getProperties(getApplicationContext());
                String GoodsDetailsByGoodsIdServerUrl = proper.getProperty("GoodsDetailsByGoodsIdServerUrl");
                Map map=new HashMap();
                map.put("goodsId",goodsId);
                OkHttp3Util.doPostJson(GoodsDetailsByGoodsIdServerUrl, JSON.toJSONString(map), new Callback() {
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
            //List<Map> goodsList= JSONObject.parseArray(resultStr,Map.class);
            Map goodsMap=(Map)JSON.parse(resultStr);
            if(goodsMap!=null){
                txtNowpriceDetails.setText(goodsMap.get("secprice").toString());
                txtOldpriceDetails.setText(goodsMap.get("price").toString());
                txtGoodInfoDetails.setText(goodsMap.get("goodsname").toString());
                sellerId=goodsMap.get("userid").toString();
            }

        }
    }

    //图片异步加载
    private class initImgDataTask extends AsyncTask<Void,Void,Bitmap>{
        Bitmap bitmap;

        public initImgDataTask(){
            MemoryCache mcache=new MemoryCache();//内存缓存
            File sdCard = android.os.Environment.getExternalStorageDirectory();//获得SD卡
            File cacheDir = new File(sdCard, "jereh_cache" );//缓存根目录
            FileCache fcache=new FileCache(CommodityDetailsActivity.this, cacheDir, "news_img");//文件缓存
            imageLoader = new AsyncImageLoader(CommodityDetailsActivity.this, mcache,fcache);

        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                //从配置文件读取
                String GoodsImgServerUrl = PropertiesUtils.getServerUrl(getApplicationContext(),"GoodsImgServerUrl");
                String imageUrl=GoodsImgServerUrl+goodsId+".jpg";


                imgIntroduceDetails.setTag(imageUrl);
                bitmap =imageLoader.loadBitmap(imgIntroduceDetails,imageUrl);
                /*if(bitmap == null) {
                    imgIntroduceDetails.setImageResource(R.drawable.good1);
                }else {
                    imgIntroduceDetails.setImageBitmap(bitmap);
                }*/
                //AsyncImageLoader asyncImageLoader=new AsyncImageLoader(CommodityDetailsActivity.this,);
                //Thread.sleep(1500);
            }catch (Exception e){
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        public void onPostExecute(final Bitmap bitmap){
            if(bitmap == null) {
                imgIntroduceDetails.setImageResource(R.drawable.good1);
            }else {
                imgIntroduceDetails.setImageBitmap(bitmap);
            }
        }
    }

    //初始化控件
    private void initView(){
        txtCloseDetails=(TextView)findViewById(R.id.txt_close_details);
        txtCloseDetails.setOnClickListener(this);
        txtMoneyDetails=(TextView)findViewById(R.id.txt_money_details);
        txtNowpriceDetails=(TextView)findViewById(R.id.txt_nowprice_details);
        txtOldpriceDetails=(TextView)findViewById(R.id.txt_oldprice_details);
        txtGoodInfoDetails=(TextView)findViewById(R.id.txt_good_info_details);
        txtAddmoreDetails=(TextView)findViewById(R.id.txt_addmore_details);
        txtReduceDetails=(TextView)findViewById(R.id.txt_reduce_details);
        txtGoodNumDetails=(TextView)findViewById(R.id.txt_good_num_details);
        banner_introduce_details=(Banner)findViewById(R.id.banner_introduce_details);
        btnDeclineDetails=(Button)findViewById(R.id.btn_decline_details);
        btnDeclineDetails.setOnClickListener(this);
        btnAddDetails=(Button)findViewById(R.id.btn_add_details);
        btnAddDetails.setOnClickListener(this);
        btnAddShopCartDetails=(Button)findViewById(R.id.btn_add_shop_cart_details);
        btnAddShopCartDetails.setOnClickListener(this);
        btnBuyNowDetails=(Button)findViewById(R.id.btn_buy_now_details);
        btnBuyNowDetails.setOnClickListener(this);
        imgIntroduceDetails=(ImageView)findViewById(R.id.img_introduce_details);
    }

}
