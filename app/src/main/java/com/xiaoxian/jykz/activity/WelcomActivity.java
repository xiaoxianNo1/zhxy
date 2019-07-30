package com.xiaoxian.jykz.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.xiaoxian.jykz.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 欢迎页
 */
public class WelcomActivity extends AppCompatActivity {
    private TextView txtCountDown;
    private int recLen = 3;//跳过倒计时提示5秒
    Timer timer = new Timer();
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //定义全屏参数
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag, flag);
        setContentView(R.layout.activity_welcom);
        txtCountDown=(TextView)findViewById(R.id.txt_count_down);
        txtCountDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从闪屏界面跳转到首界面
                Intent intent = new Intent(WelcomActivity.this, IndexActivity.class);
                startActivity(intent);
                finish();
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }

            }
        });

        timer.schedule(task, 1000, 1000);//等待时间一秒，停顿时间一秒
        /**
         * 正常情况下不点击跳过
         */
        handler = new Handler();
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                //从闪屏界面跳转到首界面
                Intent intent = new Intent(WelcomActivity.this, IndexActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);//延迟3S后发送handler信息

    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() { // UI thread
                @Override
                public void run() {
                    recLen--;
                    txtCountDown.setText("跳过 " + recLen);
                    if (recLen < 0) {
                        timer.cancel();
                        txtCountDown.setVisibility(View.GONE);//倒计时到0隐藏字体
                    }
                }
            });
        }
    };

}
