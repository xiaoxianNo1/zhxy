package com.xiaoxian.jykz.util;

import android.content.Context;

import com.xiaoxian.jykz.config.AppConfigProper;

import java.util.Properties;

/**
 * 简介：从appConfig中读取ServiceUrl
 * 作者：郑现文
 * 创建时间：2019/4/16/ 0016 15:06
 **/
public class PropertiesUtils {
    /*private static  Context context;
    public PropertiesUtils(Context context){
        this.context=context;
    }*/
    public static String getServerUrl(Context context,String serverName){
        Properties proper = AppConfigProper.getProperties(context);
        String serverUrl = proper.getProperty(serverName);
        return serverUrl;
    }
}
