package com.xiaoxian.jykz.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoxian.jykz.R;
import com.xiaoxian.jykz.util.AnalysisUtils;
import com.xiaoxian.jykz.util.FastJsonUtils;
import com.xiaoxian.jykz.util.model.City;
import com.xiaoxian.jykz.util.model.Province;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户信息activity
 */

public class UserInfoActivity extends AppCompatActivity {

    private ImageView imgTouxiang;//头像
    private ImageView imgSexUser;//性别图标
    private TextView txtUserNc;//用户昵称
    private TextView txtGxqmUser;//个性签名
    private Spinner spinnerSex;//性别
    private EditText editPhoneUser;//手机号
    private EditText editEmailUser;//邮箱

    private TextView txtAddress;//地址

    private String address;//用来接收intent的参数
    private String allAddress;//用来接收intent参数

    private TextView txtUsserLogout;
    String activityName="";
    static final private int GET_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null){
            activityName=bundle.getString("activityName");
        }
        address=getIntent().getStringExtra("address")==null?"":getIntent().getStringExtra("address");
        allAddress=getIntent().getStringExtra("allAddress")==null?"":getIntent().getStringExtra("allAddress");

        initView();
        initData();
    }

    //初始化View
    private void initView() {

        imgSexUser=(ImageView)findViewById(R.id.img_sex_user);
        imgTouxiang=(ImageView)findViewById(R.id.img_touxiang);
        txtUserNc=(TextView)findViewById(R.id.txt_user_nc);
        txtGxqmUser=(TextView)findViewById(R.id.txt_gxqm_user);
        txtGxqmUser.setVisibility(View.INVISIBLE);//隐藏
        spinnerSex=(Spinner)findViewById(R.id.spinner_sex);
        editPhoneUser=(EditText)findViewById(R.id.edit_phone_user);
        editEmailUser=(EditText)findViewById(R.id.edit_email_user);

        //注销登录
        txtUsserLogout=(TextView)findViewById(R.id.txt_user_logout);
        txtUsserLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalysisUtils.cleanLoginStatus(UserInfoActivity.this);
                setResult(RESULT_OK,(new Intent()).setAction(activityName));
                finish();
            }
        });

        //地址选择
        txtAddress=(TextView)findViewById(R.id.txt_address);
        /*txtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserInfoActivity.this,AddressInfoActivity.class);
                intent.putExtra("address","山东省 临沂市 平邑县");
                //intent.putExtra("allAddress","山东省 临沂市 平邑县");
                startActivity(intent);
            }
        });*/
    }

    //初始化数据
    private void initData(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        String userName=sharedPreferences.getString("loginUserName","");
        String UserPhoneNumber=sharedPreferences.getString("UserPhoneNumber","");
        String UserEmail=sharedPreferences.getString("UserEmail","");
        String UserSex=sharedPreferences.getString("UserSex","");
        txtUserNc.setText(userName);
        //
        editEmailUser.setText(UserEmail);
        editPhoneUser.setText(UserPhoneNumber);
        if(UserSex.equals("女")){
            spinnerSex.setSelection(1,true);
            imgSexUser.setImageResource(R.drawable.ic_sex_nv_24dp);
        }else {
            spinnerSex.setSelection(0,true);
            imgSexUser.setImageResource(R.drawable.ic_sex_nan_24dp);
        }
    }

}
