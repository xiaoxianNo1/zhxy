package com.xiaoxian.jykz.config;

import android.content.Context;

import java.io.InputStream;
import java.util.Properties;

/**
 * 简介：读取appConfig配置文件
 * 作者：郑现文
 * 创建时间：2019/3/20/ 0020 09:20
 **/
public class AppConfigProper {
    public static Properties getProperties(Context c){
        Properties urlProps;
        Properties props = new Properties();
        try {
            //方法一：通过activity中的context攻取setting.properties的FileInputStream
            //注意这地方的参数appConfig在eclipse中应该是appConfig.properties才对,但在studio中不用写后缀
            //InputStream in = c.getAssets().open("appConfig.properties");
            InputStream in = c.getAssets().open("appConfig");
            //方法二：通过class获取setting.properties的FileInputStream
            //InputStream in = PropertiesUtill.class.getResourceAsStream("/assets/  setting.properties "));
            props.load(in);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        urlProps = props;
        return urlProps;
    }
}
