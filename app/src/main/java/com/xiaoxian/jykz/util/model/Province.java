package com.xiaoxian.jykz.util.model;

/**
 * 简介：province省份实体类
 * 作者：郑现文
 * 创建时间：2019/3/12/ 0012 20:39
 **/
public class Province {
    private String name,city;
    @Override
    public String toString() {
        return "name="+name+",city="+city;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
