package com.xiaoxian.jykz.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xiaoxian.jykz.R;
import com.xiaoxian.jykz.config.AppConfigProper;
import com.xiaoxian.jykz.util.AnalysisUtils;
import com.xiaoxian.jykz.util.imagesdownload.AsyncImageLoader;
import com.xiaoxian.jykz.util.imagesdownload.FileCache;
import com.xiaoxian.jykz.util.imagesdownload.MemoryCache;
import com.xiaoxian.jykz.util.model.JsonResp;
import com.xiaoxian.jykz.util.okhttputil.OkHttp3Util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 买家订单信息
 */
public class BuyerOrderActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edit_consignee_name_buyer,edit_consignee_phone_buyer;
    private TextView txt_consignee_address_buyer,txt_commodity_name_buyer,txt_good_num_buyer,txt_total_amount_buyer,txt_in_distribution;
    private ImageView img_commodity_buyer;
    private Button btn_decline_buyer,btn_add_buyer,btn_submit_order_buyer;

    private int goodsId;
    private String commodityName;
    private Integer numberTemp;//选择商品数量
    private double nowPrice;
    private String sellerId;//卖家ID

    private AsyncImageLoader imageLoader;//异步组件
    private initImgDataTask mImgTask=null;
    private inputOrderTask mInputOrder=null;

    //private final static int REQUESTCODE = 1; // 返回的结果码
    private int resultCode = 103;//返回码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_order);
        Bundle extras = getIntent().getExtras();
        this.commodityName=extras.getString("goodsname");//商品名称
        this.goodsId=extras.getInt("goodsid");//商品id
        this.numberTemp=Integer.parseInt(extras.getString("numberTemp"));//数量
        this.nowPrice=Double.parseDouble(extras.getString("secprice"));//单价
        this.sellerId=extras.getString("userid");

        initView();
        initData();
    }

    private void initView(){
        edit_consignee_name_buyer=(EditText)findViewById(R.id.edit_consignee_name_buyer);
        edit_consignee_phone_buyer=(EditText)findViewById(R.id.edit_consignee_phone_buyer);
        txt_consignee_address_buyer=(TextView) findViewById(R.id.txt_consignee_address_buyer);
        txt_consignee_address_buyer.setOnClickListener(this);

        img_commodity_buyer=(ImageView)findViewById(R.id.img_commodity_buyer);
        txt_commodity_name_buyer=(TextView)findViewById(R.id.txt_commodity_name_buyer);

        btn_decline_buyer=(Button)findViewById(R.id.btn_decline_buyer);
        btn_decline_buyer.setOnClickListener(this);
        txt_good_num_buyer=(TextView)findViewById(R.id.txt_good_num_buyer);
        txt_good_num_buyer.setText(String.valueOf(numberTemp));
        btn_add_buyer=(Button)findViewById(R.id.btn_add_buyer);
        btn_add_buyer.setOnClickListener(this);

        txt_in_distribution=(TextView)findViewById(R.id.txt_in_distribution);

        txt_total_amount_buyer=(TextView)findViewById(R.id.txt_total_amount_buyer);
        txt_total_amount_buyer.setText(String .valueOf(numberTemp*nowPrice));
        btn_submit_order_buyer=(Button)findViewById(R.id.btn_submit_order_buyer);
        btn_submit_order_buyer.setOnClickListener(this);

    }

    //初始化数据
    private void initData(){
        txt_commodity_name_buyer.setText(commodityName);
        mImgTask=new initImgDataTask();
        mImgTask.execute((Void) null);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_consignee_address_buyer:
                Intent intent=new Intent(BuyerOrderActivity.this,AddressInfoActivity.class);
                startActivityForResult(intent,resultCode);
                //startActivity(intent);
                break;
            case R.id.btn_decline_buyer:
                numberTemp =  Integer.parseInt(txt_good_num_buyer.getText().toString());
                numberTemp--;
                if (numberTemp < 1){
                    numberTemp = 1;
                }
                txt_good_num_buyer.setText(String.valueOf( numberTemp ));
                txt_total_amount_buyer.setText(String .valueOf(numberTemp*nowPrice));
                break;
            case R.id.btn_add_buyer:
                numberTemp =  Integer.parseInt(txt_good_num_buyer.getText().toString());
                numberTemp++;
                txt_good_num_buyer.setText(String.valueOf( numberTemp ));
                txt_total_amount_buyer.setText(String .valueOf(numberTemp*nowPrice));
                break;
            case R.id.btn_submit_order_buyer:
                //提交订单到服务器
                mInputOrder=new inputOrderTask(BuyerOrderActivity.this);
                mInputOrder.execute((Void) null);
                finish();
                break;

        }
    }

    //图片异步加载
    private class initImgDataTask extends AsyncTask<Void,Void, Bitmap> {
        Bitmap bitmap;

        public initImgDataTask(){
            MemoryCache mcache=new MemoryCache();//内存缓存
            File sdCard = android.os.Environment.getExternalStorageDirectory();//获得SD卡
            File cacheDir = new File(sdCard, "jereh_cache" );//缓存根目录
            FileCache fcache=new FileCache(BuyerOrderActivity.this, cacheDir, "news_img");//文件缓存
            imageLoader = new AsyncImageLoader(BuyerOrderActivity.this, mcache,fcache);

        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                //从配置文件读取UserRegisterServerUrl
                Properties proper = AppConfigProper.getProperties(BuyerOrderActivity.this);
                String GoodsImgServerUrl = proper.getProperty("GoodsImgServerUrl");
                String imageUrl=GoodsImgServerUrl+goodsId+".jpg";


                img_commodity_buyer.setTag(imageUrl);
                bitmap =imageLoader.loadBitmap(img_commodity_buyer,imageUrl);
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
                img_commodity_buyer.setImageResource(R.drawable.good1);
            }else {
                img_commodity_buyer.setImageBitmap(bitmap);
            }
        }
    }

    //提交订单信息到服务器
    private class inputOrderTask extends AsyncTask<Void,Void,String>{
        Context context;
        private inputOrderTask(Context context){
            this.context=context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                //读取用户信息
                SharedPreferences sharedPreferences = BuyerOrderActivity.this.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);

                //从配置文件读取OrdersFormServerUrl
                Properties proper = AppConfigProper.getProperties(getApplicationContext());
                String OrdersFormServerUrl = proper.getProperty("OrdersFormServerUrl");
                Map map=new HashMap();
                map.put("userid", AnalysisUtils.readLoginUserId(context));//买家ID
                map.put("userid1",sellerId);//卖家ID
                map.put("name",edit_consignee_name_buyer.getText().toString());//收货人姓名
                map.put("address",txt_consignee_address_buyer.getText().toString());//收货地址
                map.put("tel",edit_consignee_phone_buyer.getText().toString());//联系电话
                map.put("email",sharedPreferences.getString("UserEmail",""));//电子邮箱
                map.put("cost",txt_total_amount_buyer.getText().toString());//订单总价
                map.put("state","1");//订单状态
                map.put("send",txt_in_distribution.getText().toString());//配送方式

                map.put("goodsid",goodsId);//商品编号
                map.put("goodsname",commodityName);//商品名称
                map.put("goodsprice",nowPrice);//商品单价
                map.put("goodsnum",numberTemp);//商品数量
                map.put("concost",txt_total_amount_buyer.getText().toString());//商品总价

                OkHttp3Util.doPostJson(OrdersFormServerUrl, JSON.toJSONString(map), new Callback() {
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
                                    System.out.println("生成订单成功");
                                }
                            }
                        }
                    }
                });
                Thread.sleep(1500);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String resultStr ){

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== 103){
            if (resultCode ==102 ) {
                String address=data.getStringExtra("address");
                String allAddress=data.getStringExtra("allAddress");
                if(!allAddress.isEmpty()){
                    txt_consignee_address_buyer.setText(address+" "+allAddress);
                }else {
                    txt_consignee_address_buyer.setText(address);
                }
            }
        }

    }

}
