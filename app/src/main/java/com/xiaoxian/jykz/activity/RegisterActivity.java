package com.xiaoxian.jykz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaoxian.jykz.R;
import com.xiaoxian.jykz.config.AppConfigProper;
import com.xiaoxian.jykz.util.jmjm.getUrl;
import com.xiaoxian.jykz.util.model.JsonResp;
import com.xiaoxian.jykz.util.okhttputil.OkHttp3Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 用户注册Activity
 */
public class RegisterActivity extends AppCompatActivity {
    private View mRegisterFormView;
    private View mProgressView;
    private EditText editNickName;
    private EditText editEmail;
    private EditText editPhoneNumber;
    private EditText editPwd;
    private EditText editNotarize;
    private Button btnRegister;
    private Button btnCancle;


    private UserRegisterTask mUserAuthTask=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        setFullScreen();
        setContentView(R.layout.activity_register);
        init();
        //
    }

    //控件初始化
    private void init(){
        mRegisterFormView=(View)findViewById(R.id.register_from);
        mProgressView=(View)findViewById(R.id.register_progress);
        editNickName=(EditText)findViewById(R.id.edit_nick_name);
        editEmail=(EditText)findViewById(R.id.edit_email);
        //editEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        editPhoneNumber=(EditText)findViewById(R.id.edit_phone_number);
        //editPhoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);//电话
        editPwd=(EditText)findViewById(R.id.edit_pwd);
        editNotarize=(EditText)findViewById(R.id.edit_notarize);
        btnRegister=(Button)findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        btnCancle=(Button)findViewById(R.id.btn_cancle);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void register(){

        editNickName.setError(null);
        editEmail.setError(null);
        editPhoneNumber.setError(null);
        editPwd.setError(null);
        editNotarize.setError(null);

        boolean cancel = false;
        View focusView = null;
        String mNiceName=editNickName.getText().toString();
        if (TextUtils.isEmpty(mNiceName)){
            editNickName.setError(getString(R.string.edit_nick_name));
            focusView = editEmail;
            cancel = true;
        }

        String mEmail=editEmail.getText().toString();
        if (!isEmailValid(mEmail)){
            editEmail.setError(getString(R.string.error_email));
            focusView = editEmail;
            cancel = true;
        }

        String mPhone=editPhoneNumber.getText().toString();
        if(!isPhoneValid(mPhone)){
            editPhoneNumber.setError(getString(R.string.error_phone));
            focusView=editPhoneNumber;
            cancel=true;
        }

        String mPwd=editPwd.getText().toString();
        if(TextUtils.isEmpty(mPwd)){
            editPwd.setError(getString(R.string.edit_pwd));
            focusView=editPhoneNumber;
            cancel=true;
        }

        String mNotarize=editNotarize.getText().toString();
        if(!mPwd.equals(mNotarize)){
            editNotarize.setError(getString(R.string.error_pwd_notarize));
            focusView=editPhoneNumber;
            cancel=true;
        }

        if (cancel) {
            // 有一个错误;不要尝试注册并在第一个表单字段中出现错误
            focusView.requestFocus();
        }else{
            //System.out.println("cancle=false");
            // 显示一个进度旋转器，并启动一个后台任务来执行用户注册尝试。
            showProgress(true);
            mUserAuthTask = new UserRegisterTask(mNiceName,mEmail,mPhone,mPwd);
            mUserAuthTask.execute((Void) null);
        }
        /*if(TextUtils.isEmpty(editPwd.getText().toString())){
            editPwd
        }*/

    }

    //判断邮箱规则
    private boolean isEmailValid(String email) {
        if (null==email || "".equals(email)) return false;
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
        //return email.contains("@");
    }
    //判断手机号码规则
    private boolean isPhoneValid(String phone) {
        Pattern p = Pattern.compile("^((17[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

        Matcher m = p.matcher(phone);

        return m.matches();
        //return phone.length() == 11;
    }
    //隐藏bar
    private void hideActionBar(){
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
    private void setFullScreen(){
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 显示进度UI并隐藏登录表单。
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // 在蜂巢MR2上，我们有ViewPropertyAnimator api，它支持非常简单的动画。如果可用，使用这些api在进度微调器中淡出。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    //用户注册任务
    public class UserRegisterTask extends AsyncTask<Void,Void,Boolean>{
        private final String mNickName;
        private final String mEmail;
        private final String mPhoneNumber;
        private final String mPwd;
        private String userId;
        private Boolean registerState=false;

        UserRegisterTask(String mNickName, String mEmail, String mPhoneNumber, String mPwd) {
            this.mNickName = mNickName;
            this.mEmail = mEmail;
            this.mPhoneNumber = mPhoneNumber;
            this.mPwd = mPwd;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String JsonStr="";
                Map<String,String> map=new HashMap<>();
                map.put("username",mNickName);
                map.put("upassword", getUrl.encrypt(mPwd,1));//0为解密；1为加密
                map.put("uemail",mEmail);
                map.put("uphone",mPhoneNumber);
                JsonStr= JSON.toJSONString(map);

                //从配置文件读取UserRegisterServerUrl
                Properties proper = AppConfigProper.getProperties(getApplicationContext());
                String UserRegisterServerUrl = proper.getProperty("UserRegisterServerUrl");
                OkHttp3Util.doPostJson(UserRegisterServerUrl, JsonStr, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //registerState=false;
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp=response.body().string();
                        JsonResp params=JSON.parseObject(resp,JsonResp.class);
                        if(params.getCode().equals(200)){
                            if(params.getSuccess()){
                                registerState=true;
                            }else if(!params.getSuccess()) {
                                registerState=false;
                            }
                        }

                    }
                });
                Thread.sleep(1500);

            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mUserAuthTask = null;
            showProgress(false);

            if (success) {
                if(registerState){
                    Log.d("Register","注册成功");
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Log.e("Resgister","注册失败");
                    Toast.makeText(RegisterActivity.this, "注册失败,请重试！", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegisterActivity.this, "出错了,请重试！", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onCancelled() {
            mUserAuthTask = null;
            showProgress(false);
        }
    }
}


