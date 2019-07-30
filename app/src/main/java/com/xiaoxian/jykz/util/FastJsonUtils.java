package com.xiaoxian.jykz.util;

import android.content.Context;
import android.content.res.AssetManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 简介：json读取 从文件中读取
 * 作者：郑现文
 * 创建时间：2019/3/12/ 0012 14:54
 **/
public class FastJsonUtils {

    public static String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 解析JsonObject数据
     *
     * @param jsonString
     *            Json格式字符串
     * @param cls
     *            封装类
     *
     */
    public static <T> T parseObject(String jsonString, Class<T> cls) {
        T t = null;
        try {
            t = JSON.parseObject(jsonString, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 解析JsonArray数据
     *
     * @param jsonString
     * @param cls
     * @return
     */
    public static <T> List<T> parseArray(String jsonString, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            list = JSON.parseArray(jsonString, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 解析JsonArray数据，返回Map类型的List
     *
     * @param jsonString
     * @return
     */
    public static List<Map<String, Object>> parseObjectListKeyMaps(
            String jsonString) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = JSON.parseObject(jsonString,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
