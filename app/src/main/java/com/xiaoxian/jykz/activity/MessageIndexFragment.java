package com.xiaoxian.jykz.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaoxian.jykz.R;
import com.xiaoxian.jykz.adapter.MessageListAdapter;
import com.xiaoxian.jykz.config.AppConfigProper;
import com.xiaoxian.jykz.util.AnalysisUtils;
import com.xiaoxian.jykz.util.model.JsonResp;
import com.xiaoxian.jykz.util.okhttputil.OkHttp3Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 简介：消息Fragmen
 * 作者：郑现文
 * 创建时间：2019/3/9/ 0009 11:32
 **/
public class MessageIndexFragment extends Fragment {
    private boolean isGetData = false;
    private initDataTask mAuthTask=null;

    private RecyclerView recycler_message_msg;

    private List<Map> msgList;

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_index_message,container,false);

        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //initData();
    }

    @Override
    public void onResume(){
        super.onResume();
        //initData();
    }

    //实现进入这个Fragment更新一下数据
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim){
        //   进入当前Fragment
        if (enter && !isGetData) {
            isGetData = true;
            //   这里可以做网络请求或者需要的数据刷新操作
            initData();
        } else {
            isGetData = false;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }
    @Override
    public void onPause(){
        super.onPause();
        isGetData=false;
    }

    private void initData(){
        mAuthTask = new initDataTask();
        mAuthTask.execute((Void) null);
    }

    private class initDataTask extends AsyncTask<Void,Void,String>{
        private String string="";

        @Override
        protected String doInBackground(Void... voids) {
            //初始化数据
            SharedPreferences sharedPreferences=getActivity().getSharedPreferences("loginInfo",0);
            //取出数据，如果取出的数据时空时，只需把getString("","")第二个参数设置成空字符串就行了，不用在判断
            String UserId= AnalysisUtils.readLoginUserId(getActivity());
            try{
                Map map=new HashMap();
                map.put("msguserid",UserId);
                //从配置文件读取UserRegisterServerUrl
                Properties proper = AppConfigProper.getProperties(getContext().getApplicationContext());
                String GetMessageListServerUrl = proper.getProperty("GetMessageListServerUrl");
                OkHttp3Util.doPostJson(GetMessageListServerUrl, JSON.toJSONString(map), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp=response.body().string();
                        JsonResp params= JSON.parseObject(resp,JsonResp.class);
                        if(params!=null&& !params.toString().isEmpty()){
                            if(params.getCode().equals(200)){
                                if(params.getSuccess()){
                                    string=params.getResult().toString();
                                    //Log.d("TAG",params.getResult().toString());
                                }
                            }
                        }
                    }
                });
                Thread.sleep(1500);
            }catch (Exception e){
                e.printStackTrace();
            }
            return string;
        }

        @Override
        public void onPostExecute(final String resultStr ){
            List<Map> msgsList= JSONObject.parseArray(resultStr,Map.class);
            msgList=msgsList;
            //System.out.println("resultStr:"+msgList.toString());
            initRecyclerView(getView());
            /*System.out.println(resultStr);
            if(msgsList!=null){
                System.out.println(msgsList.get(0).toString());
            }*/
        }
    }

    private void  initRecyclerView(View view){
        recycler_message_msg=view.findViewById(R.id.recycler_message_msg);//RecyclerView)
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false );
        //设置布局管理器
        recycler_message_msg.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper. VERTICAL);

        recycler_message_msg.setNestedScrollingEnabled(false);
        recycler_message_msg.setHasFixedSize(true);
        recycler_message_msg.setFocusable(false);

        MessageListAdapter messageListAdapter=new MessageListAdapter(getContext(),msgList);
        //设置Adapter
        recycler_message_msg.setAdapter(messageListAdapter);

        messageListAdapter.setOnItemClickListener(new MessageListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int id) {
                Map<String,Object> map=msgList.get(id);
                Intent intent=new Intent(getActivity(),ChatActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("map",map.toString());
                startActivity(intent);

            }

            @Override
            public void onLongClick(int id) {

            }
        });

    }

}
