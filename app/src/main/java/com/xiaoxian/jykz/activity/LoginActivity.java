package com.xiaoxian.jykz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.alibaba.fastjson.JSON;
import com.xiaoxian.jykz.R;
import com.xiaoxian.jykz.config.AppConfigProper;
import com.xiaoxian.jykz.util.jmjm.getUrl;
import com.xiaoxian.jykz.util.model.JsonResp;
import com.xiaoxian.jykz.util.okhttputil.OkHttp3Util;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * 一个登录屏幕，提供登录电子邮件/密码。
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static String TAG="LoginActivity";

    /**
     * Id到身份读取联系人权限请求。
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * 包含knowz用户名和密码的虚拟身份验证存储。
     * TODO: 连接到真实的身份验证系统后删除。
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * 跟踪登录任务，确保我们可以在需要时取消它。
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mUserRegistration;
    private TextView mForgotPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        setFullScreen();
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mUserRegistration=findViewById(R.id.txt_user_registration);
        mUserRegistration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        mForgotPwd=findViewById(R.id.txt_forgot_pwd);
        mForgotPwd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        //初始化数据
        SharedPreferences sharedPreferences=getSharedPreferences("loginInfo",0);
        //取出数据，如果取出的数据时空时，只需把getString("","")第二个参数设置成空字符串就行了，不用在判断
        String name=sharedPreferences.getString("loginUserName","");
        Boolean isLogin=sharedPreferences.getBoolean("isLogin",false);
    }

    //填充自动完成
    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    //可以请求联系人
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * 完成权限请求时接收的回调。
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * 尝试登录或注册登录表单指定的帐户，
     * 如果存在表单错误(无效的电子邮件、丢失的字段等)，则显示错误
     * 并且不进行实际的登录尝试。
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            return;
        }

        // 重置错误。
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // 在尝试登录时存储值。
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //检验有效的密码
        if(TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_pwd_empty));
            focusView = mPasswordView;
            cancel = true;
        }

        // 如果用户输入了有效密码，请检查密码是否有效。
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // 检查有效的账号。
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // 有一个错误;不要尝试登录并在第一个表单字段中出现错误
            focusView.requestFocus();
        } else {
            // 显示一个进度旋转器，并启动一个后台任务来执行用户登录尝试。
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    //限制密码长度
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * 显示进度UI并隐藏登录表单。
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // 在蜂巢MR2上，我们有ViewPropertyAnimator api，它支持非常简单的动画。如果可用，使用这些api在进度微调器中淡出。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // 检索设备用户“配置文件”联系人的数据行。
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // 首先显示主要电子邮件地址。请注意，如果用户没有指定主电子邮件地址，则不会有主电子邮件地址。
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //创建适配器，告诉AutoCompleteTextView在下拉列表中显示什么。
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * 表示用于对用户进行身份验证的异步登录/注册任务。
     */
    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private Boolean loginState=false;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: 尝试对网络服务进行身份验证。
            //验证登录身份
            try {
                String JsonStr="";
                Map<String,String> map=new HashMap<>();
                if(isEmail(mEmail)) map.put("uemail",mEmail);
                else map.put("uphone",mEmail);
                map.put("upassword", getUrl.encrypt(mPassword,1));//0为解密；1为加密
                JsonStr= JSON.toJSONString(map);
                //从配置文件读取UserRegisterServerUrl
                Properties proper = AppConfigProper.getProperties(getApplicationContext());
                String UserLoginServerUrl = proper.getProperty("UserLoginServerUrl");
                OkHttp3Util.doPostJson(UserLoginServerUrl, JsonStr, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp=response.body().string();
                        JsonResp params=JSON.parseObject(resp,JsonResp.class);
                        if(params.getCode().equals(200)){
                            if(params.getSuccess()){
                                loginState=true;
                                Map<String,String> map=JSON.parseObject(params.getResult().toString(),Map.class);
                                //往配置文件中写入用户信息
                                SharedPreferences sp=getSharedPreferences("loginInfo",0);
                                SharedPreferences.Editor editor=sp.edit();
                                //把数据进行保存
                                editor.putString("UserId",map.get("userid"));
                                editor.putString("loginUserName",map.get("username"));
                                editor.putString("UserPhoneNumber",map.get("uphone"));
                                editor.putString("UserEmail",map.get("uemail"));
                                editor.putBoolean("isLogin",true);
                                editor.putString("UserSex",map.get("usex"));
                                //提交数据
                                editor.commit();
                            }else if(!params.getSuccess()) {
                                loginState=false;
                            }
                        }
                    }
                });
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                return false;
            }

            return true;
        }

        /**
         * 这里的Boolean参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
         * 运行在ui线程中，在doInBackground()执行完毕后执行,传入的参数为doInBackground()返回的结果
         */
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                if(loginState){
                    Intent intent=new Intent(LoginActivity.this,IndexActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Log.e(TAG,"消息： 登录失败");
                }
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    //判断是邮箱还是手机号
    private boolean isEmail(String string){
        int result=string.indexOf("@");
        if(result!=-1){
            return true;
        }else {
            return false;
        }
    }

    //EditText 输入回车符自动跳转至下一个EditText
    private class JumpTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str=s.toString();
            if (str.indexOf("\r")>=0 || str.indexOf("\n")>=0){//发现输入回车符或换行符
                mEmailView.setText(str.replace("\r","").replace("\n",""));//去掉回车符和换行符
                mPasswordView.requestFocus();//让editText2获取焦点
                mPasswordView.setSelection(mPasswordView.getText().length());//若editText2有内容就将光标移动到文本末尾
            }
        }
    }

    private void hideActionBar(){
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
    private void setFullScreen(){
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}

