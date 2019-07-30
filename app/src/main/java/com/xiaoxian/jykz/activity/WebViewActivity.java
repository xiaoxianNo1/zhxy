package com.xiaoxian.jykz.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.xiaoxian.jykz.R;

public class WebViewActivity extends AppCompatActivity {
    private String webBiewUrl="http://3g.ifeng.com/";
    private ImageView img_close_webview;
    private WebView web_view_webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Intent intent=getIntent();
        String url=intent.getStringExtra("url");
        Log.d("WebViewActivity",url);
        img_close_webview=(ImageView)findViewById(R.id.img_close_webview);
        img_close_webview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        web_view_webview=(WebView)findViewById(R.id.web_view_webview);
       SetViewAsync setViewAsync=new SetViewAsync(url);
       setViewAsync.execute((Void) null);
        //web_view_webview.loadUrl(url);

    }
    private class SetViewAsync extends AsyncTask<Void, Void, Void>{
        private String url;
        public SetViewAsync(String url){
            this.url=url;

        }

        //onPreExecute用于异步处理前的操作
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            web_view_webview.setWebViewClient(new WebViewClient());
            web_view_webview.setWebChromeClient(new WebChromeClient());

            WebSettings webSettings=web_view_webview.getSettings();

            // 支持 Js 使用
            webSettings.setJavaScriptEnabled(true);
            // 开启DOM缓存,默认状态下是不支持LocalStorage的
            webSettings.setDomStorageEnabled(true);

            webSettings.setMediaPlaybackRequiresUserGesture(false);
            web_view_webview.loadUrl(url);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

    }
}
