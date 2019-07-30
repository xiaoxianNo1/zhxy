package com.xiaoxian.jykz.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.xiaoxian.jykz.R;

public class MainActivity extends AppCompatActivity  {

    private SwipeRefreshLayout swiperereshlayout ;
    private ListView listview ;
    private ArrayAdapter<String> adapter;
    private List<String> data;

    /*private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x101:
                    if (swipeRefreshLayout.isRefreshing()){
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);//设置不刷新
                    }
                    break;
            }
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView (){

        swiperereshlayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        listview = (ListView) findViewById(R.id.list_view);
        swiperereshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //模拟网络请求需要3000毫秒，请求完成，设置setRefreshing 为false
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swiperereshlayout.setRefreshing(false);
                    }
                }, 3000);

            }
        });
        /*data = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            data.add("当前的item为 " + i);
        }
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, data);
        listview.setAdapter(adapter);


        swiperereshlayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light);
        //给swipeRefreshLayout绑定刷新监听
        swiperereshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //设置2秒的时间来执行以下事件
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        data.add(0, "刷新后新增的item");
                        adapter.notifyDataSetChanged();
                        swiperereshlayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });*/


    }
}
