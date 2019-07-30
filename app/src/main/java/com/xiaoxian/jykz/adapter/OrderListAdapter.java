package com.xiaoxian.jykz.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoxian.jykz.R;
import com.xiaoxian.jykz.config.AppConfigProper;
import com.xiaoxian.jykz.util.imagesdownload.AsyncImageLoader;
import com.xiaoxian.jykz.util.imagesdownload.FileCache;
import com.xiaoxian.jykz.util.imagesdownload.MemoryCache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 简介：商品列表Adapter
 * 作者：郑现文
 * 创建时间：2019/4/8/ 0008 13:46
 **/
public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder>  {


    private static LayoutInflater inflater=null;
    private List<Map> orderInfoList = new ArrayList<Map>(  );
    private Context context;
    private AsyncImageLoader imageLoader;//异步组件

    private OnItemClickListener onItemClickListener;

    public OrderListAdapter (Context context , List<Map> orderInfoList){
        this.orderInfoList=orderInfoList;
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        MemoryCache mcache=new MemoryCache();//内存缓存
        File sdCard = android.os.Environment.getExternalStorageDirectory();//获得SD卡
        File cacheDir = new File(sdCard, "jereh_cache" );//缓存根目录
        FileCache fcache=new FileCache(context, cacheDir, "news_img");//文件缓存
        imageLoader = new AsyncImageLoader(context, mcache,fcache);

    }

    @NonNull
    @Override
    public OrderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_list_layout,viewGroup,false);
        ViewHolder holder = new ViewHolder(view,context);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull OrderListAdapter.ViewHolder viewHolder, int position) {
        Map map=orderInfoList.get(position);

        viewHolder.good_info.setText( map.get("goodsname").toString() );
        viewHolder.good_price.setText( map.get("goodsprice").toString());
        viewHolder.good_num.setText( map.get("goodsnum").toString());
        viewHolder.total_num.setText( "共"+map.get("goodsnum").toString()+"件商品  合计：" );
        viewHolder.total_price.setText( map.get("concost").toString() );

        //从配置文件读取imageUrl
        Properties proper = AppConfigProper.getProperties(context);
        String GoodsImgListServerUrl = proper.getProperty("GoodsImgListServerUrl");
        String imageUrl=GoodsImgListServerUrl+map.get("goodsid")+".jpg";

        viewHolder.image.setTag(imageUrl);
        //异步加载图片，先从一级缓存、再二级缓存、最后网络获取图片
        Bitmap bmp = imageLoader.loadBitmap(viewHolder.image,imageUrl);

        if(bmp == null) {
            viewHolder.image.setImageResource(R.drawable.good1);
        }else {
            viewHolder.image.setImageBitmap(bmp);
        }

        if(onItemClickListener!=null){
            viewHolder.linear_order_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if(orderInfoList==null){
            return 0;
        }else {
            return orderInfoList.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView good_num,good_price,good_info,total_num,total_price;
        public final ImageView image;
        private LinearLayout linear_order_list;

        public ViewHolder(View itemView, Context context){
            super(itemView);
            good_info = (TextView)itemView.findViewById( R.id.good_info );
            good_price = (TextView)itemView.findViewById( R.id.good_price );
            good_num = (TextView)itemView.findViewById( R.id.good_num );
            image = (ImageView)itemView.findViewById( R.id.image );
            total_num = (TextView)itemView.findViewById( R.id.total_num );
            total_price = (TextView)itemView.findViewById( R.id.total_price );
            linear_order_list=(LinearLayout)itemView.findViewById(R.id.linear_order_list);
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
