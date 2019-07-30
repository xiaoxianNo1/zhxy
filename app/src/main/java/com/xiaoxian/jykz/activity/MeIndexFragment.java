package com.xiaoxian.jykz.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxian.jykz.R;
import com.xiaoxian.jykz.adapter.TransactionAdapter;
import com.xiaoxian.jykz.util.AnalysisUtils;
import com.xiaoxian.jykz.util.PropertiesUtils;
import com.xiaoxian.jykz.util.imagesdownload.AsyncImageLoader;
import com.xiaoxian.jykz.util.imagesdownload.FileCache;
import com.xiaoxian.jykz.util.imagesdownload.MemoryCache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 简介：我的 Fragmen
 * 作者：郑现文
 * 创建时间：2019/3/9/ 0009 11:32
 **/
public class MeIndexFragment extends Fragment {
    private TextView txtUserName ,txt_order_list_me,txt_order_sell_me;
    private View LinearUserSet;
    private ImageView img_touxiang_me;
    private ListView listViewTransaction;
    static final private int GET_CODE = 0;

    private SwipeRefreshLayout swipeRefreshLayout;

    private List<Integer> imgTransaction;
    private List<String> titleTransaction;
    private List<String> numTransaction;
    private List<Integer> icoTransaction;

    private AsyncImageLoader imageLoader;//异步组件
    private initImgDataTask mImgTask=null;



    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_index_me,container,false);
        initView(view);
        initSwipeRefreshLayout(view);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        if(AnalysisUtils.readLoginStatus(getActivity())){
            //读取用户信息
            String userName=AnalysisUtils.readLoginUserName(getContext());
            txtUserName.setText(userName);
            mImgTask=new initImgDataTask();
            mImgTask.execute((Void) null);

        }else {
            txtUserName.setText("请登录");
        }
    }

    //初始化下拉刷新
    private void initSwipeRefreshLayout(final View view){
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe_layout_me);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //模拟网络请求需要3000毫秒，请求完成，设置setRefreshing 为false
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    private void initView(View view){
        img_touxiang_me=view.findViewById(R.id.img_touxiang_me);
        txtUserName=view.findViewById(R.id.txt_user_name);

        LinearUserSet=view.findViewById(R.id.linear_userset_me);
        LinearUserSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AnalysisUtils.readLoginStatus(getActivity())){
                    Intent intent=new Intent(getActivity(),UserInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("uid", getActivity().toString());
                    intent.putExtras(bundle);
                    startActivityForResult(intent,GET_CODE);
                }else{
                    Intent intent=new Intent(getActivity(),LoginActivity.class);
                    startActivity(intent);
                }

            }
        });

        txt_order_sell_me=view.findViewById(R.id.txt_order_sell_me);
        txt_order_sell_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),OrderListActivity.class);
                intent.putExtra("TAG","sell");
                startActivity(intent);
            }
        });

        txt_order_list_me=view.findViewById(R.id.txt_order_list_me);
        txt_order_list_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),OrderListActivity.class);
                intent.putExtra("TAG","buy");
                startActivity(intent);
            }
        });
    }

    //图片异步加载
    private class initImgDataTask extends AsyncTask<Void,Void, Bitmap> {
        Bitmap bitmap;

        public initImgDataTask(){
            MemoryCache mcache=new MemoryCache();//内存缓存
            File sdCard = android.os.Environment.getExternalStorageDirectory();//获得SD卡
            File cacheDir = new File(sdCard, "jereh_cache" );//缓存根目录
            FileCache fcache=new FileCache(getContext(), cacheDir, "news_img");//文件缓存
            imageLoader = new AsyncImageLoader(getContext(), mcache,fcache);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            //从配置文件读取
            String ServerUrl = PropertiesUtils.getServerUrl(getContext(),"GetUserPhotoServerUrl");
            String imageUrl=ServerUrl+AnalysisUtils.readLoginUserId(getContext())+".jpg";
            Log.d("MeIndexFragment",imageUrl);
            img_touxiang_me.setTag(imageUrl);
            bitmap =imageLoader.loadBitmap(img_touxiang_me,imageUrl);

            return bitmap;
        }

        @Override
        public void onPostExecute(final Bitmap bitmap){
            if(bitmap == null) {
                img_touxiang_me.setImageResource(R.drawable.ic_touxiang_24dp);
            }else {
                img_touxiang_me.setImageBitmap(bitmap);
            }
        }
    }

}
