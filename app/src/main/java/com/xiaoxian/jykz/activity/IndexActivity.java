package com.xiaoxian.jykz.activity;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;

import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxian.jykz.R;
import com.xiaoxian.jykz.util.PermissionsUtil;

/**
 * 主页 首页
 */
public class IndexActivity extends FragmentActivity {

    private PermissionsUtil permissionsUtil;

    //储存所有权限
    String[] allpermissions=new String[]{
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    String activityName="";

    private TextView mTextMessage;
    private DynamicIndexFragment dynamicIndexFragment;
    private MeIndexFragment meIndexFragment;
    private MessageIndexFragment messageIndexFragment;
    private PlazaIndexFragment plazaIndexFragment;
    private Fragment[] fragments;
    private int lastfragment=0;//用于记录上个选择的Fragment
    private int nowfragment=0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_plaza:
                    if(lastfragment!=0){
                        switchFragment(lastfragment,0);
                        lastfragment=0;
                    }
                    return true;
                case R.id.navigation_dynamic:
                    if(lastfragment!=1){
                        switchFragment(lastfragment,1);
                        lastfragment=1;
                    }
                    return true;
                case R.id.navigation_message:
                    if(lastfragment!=2){
                        switchFragment(lastfragment,2);
                        lastfragment=2;
                    }
                    return true;
                case R.id.navigation_me:
                    if(lastfragment!=3){
                        switchFragment(lastfragment,3);
                        lastfragment=3;
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        applypermission();
        init();
        mTextMessage = (TextView) findViewById(R.id.message);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    @Override
    protected void onStop(){
        //Log.d("TAG消息","onStop:"+lastfragment);
        super.onStop();
    }

    @Override
    protected void onRestart(){
        nowfragment=lastfragment;
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    //初始化
    private void init(){
        dynamicIndexFragment=new DynamicIndexFragment();
        meIndexFragment=new MeIndexFragment();
        messageIndexFragment=new MessageIndexFragment();
        plazaIndexFragment=new PlazaIndexFragment();
        lastfragment=0;
        fragments = new Fragment[]{plazaIndexFragment,dynamicIndexFragment,messageIndexFragment,meIndexFragment};
        getSupportFragmentManager().beginTransaction().replace(R.id.mainview,plazaIndexFragment).show(plazaIndexFragment).commit();

        String string="";
        if(lastfragment!=nowfragment){
            switchFragment(0,nowfragment);
            lastfragment=nowfragment;
        }


    }

    private void switchFragment(int lastfragment,int index){
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastfragment]);//隐藏上个Fragment
        if(fragments[index].isAdded()==false){
            transaction.add(R.id.mainview,fragments[index]);
        }
        transaction.show(fragments[index]).commitAllowingStateLoss();
    }



    /**
     * 动态申请权限
     */
    public void applypermission(){
        permissionsUtil = new PermissionsUtil(this);
        permissionsUtil.shouldShowPermissionRationale(111,allpermissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsUtil.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

}
