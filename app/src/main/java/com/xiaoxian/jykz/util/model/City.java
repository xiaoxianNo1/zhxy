package com.xiaoxian.jykz.util.model;

/**
 * 简介：city实体类
 * 作者：郑现文
 * 创建时间：2019/3/12/ 0012 20:38
 **/
public class City {
    private String name,area;
    @Override
    public String toString() {
        return "name="+name+",city="+area;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
