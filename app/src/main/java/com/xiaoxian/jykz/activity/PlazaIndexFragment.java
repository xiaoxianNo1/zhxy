package com.xiaoxian.jykz.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterViewFlipper;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.xiaoxian.jykz.R;

/**
 * 简介：广场Fragmen
 * 作者：郑现文
 * 创建时间：2019/3/9/ 0009 11:33
 **/
public class PlazaIndexFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;//下拉刷新

    private WebView webView;
    private String webBiewUrl="http://3g.ifeng.com/";

    private AdapterViewFlipper lbImgFlipper ;
    int[] lbImages=new int[]{R.drawable.image_lb_1,R.drawable.image_lb_2};

    private ImageView imgShopPlaza;
    private ImageButton img_news_plaza,img_notice_plaza,img_study_plaza,img_safety_plaza,img_guide_plaza,img_pano_plaza,img_shop_plaza,img_cet_query_plaza;

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        final View view=inflater.inflate(R.layout.fragment_index_plaza,container,false);
        lbInit(view);
        initWebView(view);
        initswipeLayout(view);
        initClickListener(view);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    //下拉刷新
    private void initswipeLayout(final View view){
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe_layout_plaza);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //模拟网络请求需要3000毫秒，请求完成，设置setRefreshing 为false
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);

                        initWebView(view);
                    }
                }, 2000);
            }
        });
    }

    //初始化轮播图
    private void lbInit(View view){
        lbImgFlipper=view.findViewById(R.id.flipper_lb_img);
        BaseAdapter adapter=new BaseAdapter() {
            @Override
            public int getCount() {
                return lbImages.length;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                //创建一个ImageView
                ImageView imageView=new ImageView(getActivity());
                imageView.setImageResource(lbImages[position]);//设置ImageView的缩放类型
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                //为ImageView设置布局参数
                imageView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        };
        lbImgFlipper.setAdapter(adapter);
        //开始自动播放
        lbImgFlipper.startFlipping();

        //显示上一个组件
        //lbImgFlipper.showPrevious();
        //显示下一个组件
        //lbImgFlipper.showNext();
    }

    //webview控件
    private void initWebView(View view){
        webView=view.findViewById(R.id.web_view_plaza);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(webBiewUrl);
    }

    //点击事件初始化
    private void initClickListener(View view){
        img_news_plaza=view.findViewById(R.id.img_news_plaza);
        img_news_plaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),WebViewActivity.class);
                intent.putExtra("url","http://www.qchm.edu.cn/xyxw_598/list.htm");
                startActivity(intent);
            }
        });
        img_notice_plaza=view.findViewById(R.id.img_notice_plaza);
        img_notice_plaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),WebViewActivity.class);
                intent.putExtra("url","http://www.qchm.edu.cn/4508/list.htm");
                startActivity(intent);
            }
        });
        img_study_plaza=view.findViewById(R.id.img_study_plaza);
        img_study_plaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),WebViewActivity.class);
                intent.putExtra("url","http://qq.campusplus.com/qchm/synnews/list/qchm_llxx");
                startActivity(intent);
            }
        });
        img_safety_plaza=view.findViewById(R.id.img_safety_plaza);
        img_safety_plaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),WebViewActivity.class);
                intent.putExtra("url","https://qq.campusplus.com/qchm/synnews/list/qchm_aqtb");
                startActivity(intent);
            }
        });
        img_guide_plaza=view.findViewById(R.id.img_guide_plaza);
        img_guide_plaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),WebViewActivity.class);
                intent.putExtra("url","https://qq.campusplus.com/qchm/synnews/list/qchm_yhzn");
                startActivity(intent);
            }
        });
        img_pano_plaza=view.findViewById(R.id.img_pano_plaza);
        img_pano_plaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),WebViewActivity.class);
                intent.putExtra("url","http://720yun.com/t/9eajkdwfun0?pano_id=3614745");
                startActivity(intent);
            }
        });
        img_shop_plaza=view.findViewById(R.id.img_shop_plaza);
        img_shop_plaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),ShopActivity.class);
                startActivity(intent);
            }
        });
        img_cet_query_plaza=view.findViewById(R.id.img_cet_query_plaza);
        img_cet_query_plaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),WebViewActivity.class);
                intent.putExtra("url","http://cet.neea.edu.cn/cet/");
                startActivity(intent);
            }
        });

    }
}
