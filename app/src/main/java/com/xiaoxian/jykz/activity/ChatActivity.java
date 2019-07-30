package com.xiaoxian.jykz.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaoxian.jykz.R;
import com.xiaoxian.jykz.adapter.MsgAdapter;
import com.xiaoxian.jykz.util.AnalysisUtils;
import com.xiaoxian.jykz.util.CopyOfStringUtil;
import com.xiaoxian.jykz.util.PropertiesUtils;
import com.xiaoxian.jykz.util.model.Msg;
import com.xiaoxian.jykz.util.okhttputil.OkHttp3Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    private List<Msg> msgList=new ArrayList<>();
    private ImageView img_close_chat;
    private TextView txt_friend_name_chat;
    private RecyclerView recycler_msg_chat;
    private EditText edit_content_chat;
    private Button btn_send_chat;

    private MsgAdapter adapter;

    private fromDataTask mAuthTask=null;
    private String friendId;
    private String friendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        Intent intent=getIntent();
        String msgMapStr=intent.getStringExtra("map");

        try {
            Map<String ,Object> msgMap=CopyOfStringUtil.StringToMap(msgMapStr);
            for (Object map : msgMap.entrySet()){
                friendId=((Map.Entry)map).getKey().toString();
                String messageListStr=(((Map.Entry)map).getValue().toString()).substring(0,(((Map.Entry)map).getValue().toString().length()-1));
                List<Map> messageList= JSONObject.parseArray(messageListStr,Map.class);
                for (int i=0;i<messageList.size();i++){
                    Map map1=messageList.get(i);
                    if(friendId.equals(map1.get("msgfromuserid").toString())){
                        Msg msg=new Msg(map1.get("msgcontent").toString(),Msg.TYPE_RECEIVED);
                        msgList.add(msg);
                        friendName=map1.get("msgfromusername").toString();
                    }else {
                        Msg msg=new Msg(map1.get("msgcontent").toString(),Msg.TYPE_SENT);
                        msgList.add(msg);
                        friendName=map1.get("msgtousername").toString();
                    }
                }
            }
            txt_friend_name_chat.setText(friendName);
        }catch (Exception e){
            e.printStackTrace();
        }

        initRecyclerView();
    }

    private void initView(){
        img_close_chat=(ImageView)findViewById(R.id.img_close_chat);
        img_close_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txt_friend_name_chat=(TextView)findViewById(R.id.txt_friend_name_chat);
        recycler_msg_chat=(RecyclerView)findViewById(R.id.recycler_msg_chat);
        edit_content_chat=(EditText)findViewById(R.id.edit_content_chat);
        btn_send_chat=(Button)findViewById(R.id.btn_send_chat);

        btn_send_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=edit_content_chat.getText().toString();              //获取EditText中的内容
                if(!"".equals(content)){//内容不为空则创建一个新的Msg对象，并把它添加到msgList列表中

                    mAuthTask = new fromDataTask(content,friendId);
                    mAuthTask.execute((Void) null);

                    Msg msg=new Msg(content,Msg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size()-1);           //调用适配器的notifyItemInserted()用于通知列表有新的数据插入，这样新增的一条消息才能在RecyclerView中显示
                    recycler_msg_chat.scrollToPosition(msgList.size()-1);     //调用scrollToPosition()方法将显示的数据定位到最后一行，以保证可以看到最后发出的一条消息
                    edit_content_chat.setText("");                                  //调用EditText的setText()方法将输入的内容清空

                }
            }
        });
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);    //LinearLayoutLayout即线性布局，创建对象后把它设置到RecyclerView当中
        recycler_msg_chat.setLayoutManager(layoutManager);
        adapter=new MsgAdapter(msgList);
        recycler_msg_chat.setAdapter(adapter);
    }

    private class fromDataTask extends AsyncTask<Void,Void,Void> {
        String msgcontent;
        String msgtouserid;
        protected fromDataTask(String msgcontent,String msgtouserid ){
            this.msgcontent=msgcontent;
            this.msgtouserid=msgtouserid;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                String sendUrl=PropertiesUtils.getServerUrl(getApplicationContext(),"InsertMsgServerUrl");
                Map map=new HashMap();
                map.put("msgcontent",msgcontent);
                map.put("msgtouserid",msgtouserid);
                map.put("msgfromuserid", AnalysisUtils.readLoginUserId(getApplicationContext()));
                OkHttp3Util.doPostJson(sendUrl, JSON.toJSONString(map), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }






    /**
     * 截取2个指定字符之间的字符串：
     * @param str
     * @param strStart
     * @param strEnd
     * @return
     */
    public static String subString(String str, String strStart, String strEnd) {

        /* 找出指定的2个字符在 该字符串里面的 位置 */
        int strStartIndex = str.indexOf(strStart);
        int strEndIndex = str.indexOf(strEnd);

        /* index 为负数 即表示该字符串中 没有该字符 */
        if (strStartIndex < 0) {
            return "字符串 :---->" + str + "<---- 中不存在 " + strStart + ", 无法截取目标字符串";
        }
        if (strEndIndex < 0) {
            return "字符串 :---->" + str + "<---- 中不存在 " + strEnd + ", 无法截取目标字符串";
        }
        /* 开始截取 */
        String result = str.substring(strStartIndex, strEndIndex).substring(strStart.length());
        return result;
    }




}
