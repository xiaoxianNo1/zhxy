package com.xiaoxian.jykz.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
 * 简介：商品listAtapter
 * 作者：郑现文
 * 创建时间：2019/3/23/ 0023 21:45
 **/
public class ShopCommodityAdapter extends RecyclerView.Adapter<ShopCommodityAdapter.ViewHolder> {
    private Activity mActivity;
    private static LayoutInflater inflater=null;
    private AsyncImageLoader imageLoader;//异步组件

    private Context context;
    private OnItemClickListener onItemClickListener;

    List<Map> goodsInfoList=new ArrayList<>();

    public ShopCommodityAdapter(Activity mActivity, Context context, List<Map> goodsInfoList){
        this.context=context;
        this.goodsInfoList=goodsInfoList;
        inflater = (LayoutInflater)mActivity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        MemoryCache mcache=new MemoryCache();//内存缓存
        File sdCard = android.os.Environment.getExternalStorageDirectory();//获得SD卡
        File cacheDir = new File(sdCard, "jereh_cache" );//缓存根目录
        FileCache fcache=new FileCache(mActivity, cacheDir, "news_img");//文件缓存
        imageLoader = new AsyncImageLoader(mActivity, mcache,fcache);
    }

    @NonNull
    @Override
    public ShopCommodityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.goods_item,null,false);
        ViewHolder holder = new ViewHolder(view,context);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ShopCommodityAdapter.ViewHolder viewHolder, int i) {
        Map map=goodsInfoList.get(i);
        viewHolder.textView.setText(map.get("goodsname").toString());

        //从配置文件读取imageUrl
        Properties proper = AppConfigProper.getProperties(context);
        String GoodsImgListServerUrl = proper.getProperty("GoodsImgListServerUrl");
        String imageUrl=GoodsImgListServerUrl+map.get("picture");

        viewHolder.imageButton.setTag(imageUrl);
        //异步加载图片，先从一级缓存、再二级缓存、最后网络获取图片
        Bitmap bmp = imageLoader.loadBitmap(viewHolder.imageButton,imageUrl);
        if(bmp == null) {
            viewHolder.imageButton.setImageResource(R.drawable.good1);
        }else {
            viewHolder.imageButton.setImageBitmap(bmp);
        }

        viewHolder.price.setText(map.get("secprice").toString());
        viewHolder.brought_count.setText(map.get("pop").toString());
        final int goodsId=map.get("goodsid").hashCode();
        if(onItemClickListener!=null){
            viewHolder.linearGoodsItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(goodsId);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(goodsInfoList==null){
            return 0;
        }else {
            return goodsInfoList.size();
        }

    }

    //自定义ViewHolder,包含item的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout linearGoodsItem;
        public final TextView textView;
        public final ImageButton imageButton;
        public final TextView price;
        public final TextView brought_count;
        private  TextView icon_money;
        public ViewHolder(View itemView, Context context) {
            //super(itemView);
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_goods);
            imageButton = (ImageButton) itemView.findViewById(R.id.good_image);
            price = (TextView)itemView.findViewById(R.id.good_price);
            brought_count = (TextView)itemView.findViewById(R.id.brought_count);
            icon_money = (TextView)itemView.findViewById(R.id.icon_money);
            Typeface fonts = Typeface.createFromAsset(context.getAssets(),"fonts/iconfont.ttf");
            icon_money.setTypeface(fonts);
            linearGoodsItem=(LinearLayout)itemView.findViewById(R.id.linear_goods_item);
        }
    }

    //click
    public interface OnItemClickListener{
        void onClick(int id);
        void onLongClick(int id);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
