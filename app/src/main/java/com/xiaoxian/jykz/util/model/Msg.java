package com.xiaoxian.jykz.util.model;

/**
 * 简介：聊天消息实体类
 * 作者：郑现文
 * 创建时间：2019/4/16/ 0016 14:08
 **/
public class Msg {
    public static final int TYPE_RECEIVED=0;
    public static final int TYPE_SENT=1;
    private String content;
    private int type;
    public Msg(String content,int type){
        this.content=content;
        this.type=type;
    }
    public String getContent(){
        return content;
    }

    public int getType(){
        return type;
    }
}
