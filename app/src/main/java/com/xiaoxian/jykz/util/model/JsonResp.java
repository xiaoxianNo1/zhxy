package com.xiaoxian.jykz.util.model;

import java.util.Map;

/**
 * 简介：
 * 作者：郑现文
 * 创建时间：2019/3/20/ 0020 15:53
 **/
public class JsonResp {
    private Integer code;//状态码
    private Boolean isSuccess;//状态
    private String massege;//消息
    private String requestAddress;//请求地址
    private Object result;//数据对象

    public JsonResp(){
        super();
    }

    /**
     * 无参构造器
     * @param success
     * @param map
     */
   /* public JsonResp(RespCode success, Map map){
        super();
    }*/

    /**
     * 只返回状态，状态码，消息
     * @param success
     * @param code
     * @param massege
     */
    public JsonResp(Boolean success, Integer code, String massege){
        super();
        this.isSuccess=success;
        this.code=code;
        this.massege=massege;
    }

    /**
     * 只返回状态，状态码，数据对象
     * @param success
     * @param code
     * @param result
     */
    public JsonResp(Boolean success, Integer code, Object result){
        super();
        this.isSuccess=success;
        this.code=code;
        this.result=result;
    }

    /**
     * 返回全部信息即状态，状态码，消息，数据对象
     * @param success
     * @param code
     * @param massege
     * @param result
     */
    public JsonResp(Boolean success, Integer code, String massege, Object result){
        super();
        this.isSuccess=success;
        this.code=code;
        this.massege=massege;
        this.result=result;
    }

    /**
     * 返回全部信息即状态，状态码，消息，数据对象
     * @param success
     * @param code
     * @param massege
     * @param result
     */
    public JsonResp(Boolean success, Integer code, String massege, String requestAddress,Object result){
        super();
        this.isSuccess=success;
        this.code=code;
        this.massege=massege;
        this.requestAddress=requestAddress;
        this.result=result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getMassege() {
        return massege;
    }

    public void setMassege(String massege) {
        this.massege = massege;
    }

    public String getRequestAddress() {
        return requestAddress;
    }

    public void setRequestAddress(String requestAddress) {
        this.requestAddress = requestAddress;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
