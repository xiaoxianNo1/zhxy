package com.xiaoxian.jykz.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.xiaoxian.jykz.R;
import com.xiaoxian.jykz.config.AppConfigProper;
import com.xiaoxian.jykz.util.AnalysisUtils;
import com.xiaoxian.jykz.util.imagesdownload.AsyncImageLoader;
import com.xiaoxian.jykz.util.imagesdownload.FileCache;
import com.xiaoxian.jykz.util.imagesdownload.MemoryCache;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 简介：
 * 作者：郑现文
 * 创建时间：2019/4/11/ 0011 15:49
 **/
public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {
    private Context context;

    private List<Map> messageList= new ArrayList<Map>();
    private List mapKeyList =new ArrayList();
    private static LayoutInflater inflater=null;
    private AsyncImageLoader imageLoader;//异步组件

    private OnItemClickListener onItemClickListener;



    /*public MessageListAdapter(){}*/

    public MessageListAdapter(Context context,List<Map> messageList){
        this.context=context;
        this.messageList=messageList;

        inflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        MemoryCache mcache=new MemoryCache();//内存缓存
        File sdCard = android.os.Environment.getExternalStorageDirectory();//获得SD卡
        File cacheDir = new File(sdCard, "jereh_cache" );//缓存根目录
        FileCache fcache=new FileCache(context, cacheDir, "news_img");//文件缓存
        imageLoader = new AsyncImageLoader(context, mcache,fcache);

        /*for(int i=0;i<messageList.size();i++){
            System.out.println("消息"+messageList.get(i).toString());

        }*/
        //mapKeyList= GetMapKey(messageList);
        //for ()
        //String mapKey=mapKeyList.get(0).toString();

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(context).inflate(R.layout.message_list_layout,viewGroup,false);
        ViewHolder holder=new ViewHolder(view,context);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListAdapter.ViewHolder viewHolder, int i) {
        /*if(mapKeyList!=null){
            String userId=mapKeyList.get(i).toString();
            viewHolder.txt_name_msg.setText(userId);
        }*/

        Map<String,Object> map=this.messageList.get(i);
        for(String key : map.keySet()){
            String value = map.get(key).toString();
            List<Map> msgForIdList= JSONObject.parseArray(value,Map.class);
            //Log.d("msgListForIdList",msgForIdList.toString());
            String msgFromUserName=(msgForIdList.get(msgForIdList.size()-1)).get("msgfromusername").toString();//msgForIdList.get(msgForIdList.size()-1).get("msgFromUserName").toString();
            String msgToUserName=(msgForIdList.get(msgForIdList.size()-1)).get("msgtousername").toString();//msgForIdList.get(msgForIdList.size()-1).get("msgToUserName").toString();
            String loginUserName= AnalysisUtils.readLoginUserName(context);
            String lastMsg=(msgForIdList.get(msgForIdList.size()-1)).get("msgcontent").toString();
            String msgFromUserId=(msgForIdList.get(msgForIdList.size()-1)).get("msgfromuserid").toString();
            String msgToUserId=(msgForIdList.get(msgForIdList.size()-1)).get("msgtouserid").toString();
            String userFriendId="";
            //Log.d("MessageListAdapter","userFriendId:"+userFriendId);
            Log.d("MessageListAdapter","userFriendId:"+loginUserName);

            if(msgFromUserName.equals(loginUserName)){
                viewHolder.txt_name_msg.setText(msgToUserName);
                userFriendId=msgToUserId;
            }else if(msgToUserName.equals(loginUserName)){
                viewHolder.txt_name_msg.setText(msgFromUserName);
                userFriendId=msgFromUserId;
            }
            viewHolder.txt_last_msg_msg.setText(lastMsg);
            //Log.d("MessageListAdapter","userFriendId:"+userFriendId);
            //从配置文件读取imageUrl
            Properties proper = AppConfigProper.getProperties(context);
            String GetUserPhotoServerUrl = proper.getProperty("GetUserPhotoServerUrl");
            String imageUrl=GetUserPhotoServerUrl+userFriendId+".jpg";
            viewHolder.img_touxiang_msg.setTag(imageUrl);
            //异步加载图片，先从一级缓存、再二级缓存、最后网络获取图片
            Bitmap bmp = imageLoader.loadBitmap(viewHolder.img_touxiang_msg,imageUrl);

            if(bmp == null) {
                viewHolder.img_touxiang_msg.setImageResource(R.drawable.ic_touxiang_24dp);
            }else {
                viewHolder.img_touxiang_msg.setImageBitmap(bmp);
            }
        }

        final int a=i;
        if(onItemClickListener!=null){
            viewHolder.linear_msg_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(a);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if(messageList==null){
            return 0;
        }else {
            return messageList.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ImageView img_touxiang_msg;
        private final TextView txt_name_msg;
        private final TextView txt_last_msg_msg;
        private final LinearLayout linear_msg_msg;
        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            img_touxiang_msg=(ImageView)itemView.findViewById(R.id.img_touxiang_msg);
            txt_name_msg=(TextView)itemView.findViewById(R.id.txt_name_msg);
            txt_last_msg_msg=(TextView)itemView.findViewById(R.id.txt_last_msg_msg);
            linear_msg_msg=(LinearLayout)itemView.findViewById(R.id.linear_msg_msg);
        }
    }

    public interface OnItemClickListener{
        void onClick(int id);
        void onLongClick(int id);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }



}
