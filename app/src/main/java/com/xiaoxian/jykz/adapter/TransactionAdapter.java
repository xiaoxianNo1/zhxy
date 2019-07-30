package com.xiaoxian.jykz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoxian.jykz.R;

import java.util.List;

/**
 * 简介：Transaction买卖信息adapter
 * 作者：郑现文
 * 创建时间：2019/3/17/ 0017 07:23
 **/
public class TransactionAdapter extends BaseAdapter {
    private Context contextTransaction;
    private List<Integer> imgTransaction;
    private List<String> titleTransaction;
    private List<String> numTransaction;
    private List<Integer> icoTransaction;

    public TransactionAdapter (Context context,List<Integer> imgTransaction,List<String> titleTransaction,List<String> numTransaction,List<Integer> icoTransaction){
        this.contextTransaction=context;
        this.imgTransaction=imgTransaction;
        this.titleTransaction=titleTransaction;
        this.numTransaction=numTransaction;
        this.icoTransaction=icoTransaction;
    }


    @Override
    public int getCount() {
        return titleTransaction.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(contextTransaction).inflate(R.layout.listview_me_transaction,null);
            ItemVIewCache vIewCache=new ItemVIewCache();
            vIewCache.imgTransactionView=(ImageView)convertView.findViewById(R.id.img_transaction_img);
            vIewCache.titleTransactionView=(TextView)convertView.findViewById(R.id.txt_transaction_title);
            vIewCache.numTransactionView=(TextView)convertView.findViewById(R.id.txt_transaction_num);
            vIewCache.icoTransactionView=(ImageView)convertView.findViewById(R.id.img_transaction_ico);
            convertView.setTag(vIewCache);
        }
        ItemVIewCache cache=(ItemVIewCache)convertView.getTag();
        cache.imgTransactionView.setImageResource(imgTransaction.get(position));
        cache.titleTransactionView.setText(titleTransaction.get(position));
        cache.numTransactionView.setText(numTransaction.get(position));
        cache.icoTransactionView.setImageResource(icoTransaction.get(position));
        return convertView;
    }

    private class ItemVIewCache{
        public ImageView imgTransactionView;
        public TextView titleTransactionView;
        public TextView numTransactionView;
        public ImageView icoTransactionView;
    }
}
