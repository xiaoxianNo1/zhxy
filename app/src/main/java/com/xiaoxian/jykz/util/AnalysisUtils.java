package com.xiaoxian.jykz.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 简介：拓展类 读取用户登录信息 登录状态 清除登录信息
 * 作者：郑现文
 * 创建时间：2019/3/10/ 0010 17:19
 **/
public class AnalysisUtils {

    //读取用户名
    public static String readLoginUserName(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
        String userName=sharedPreferences.getString("loginUserName","");
        return userName;
    }

    public static String readLoginUserId(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
        String userId=sharedPreferences.getString("UserId","");
        return userId;
    }

    //读取登录状态
    public static boolean readLoginStatus(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
        boolean isLogin=sharedPreferences.getBoolean("isLogin",false);
        return isLogin;
    }

    //清除登录状态
    public static void cleanLoginStatus(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLogin",false);
        editor.putString("loginUserName","");
        editor.putString("UserId","");
        editor.putString("UserPhoneNumber","");
        editor.putString("UserEmail","");
        editor.putString("UserSex","");
        editor.commit();
    }

}
