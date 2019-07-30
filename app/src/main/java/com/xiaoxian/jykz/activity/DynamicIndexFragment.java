package com.xiaoxian.jykz.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoxian.jykz.R;

/**
 * 简介：动态Fragmen
 * 作者：郑现文
 * 创建时间：2019/3/9/ 0009 11:32
 **/
public class DynamicIndexFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_index_dynamic,container,false);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
}
