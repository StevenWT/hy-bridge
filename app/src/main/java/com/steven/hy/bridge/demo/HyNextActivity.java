package com.steven.hy.bridge.demo;

import android.os.Bundle;

import com.steven.hy.bridge.activity.HyBaseActivity;

/**
 * Created by steven on 18/1/4.
 * hybrid测试
 */
public class HyNextActivity extends HyBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData(){
        mHyView.loadUrl("file:///android_asset/ExampleApp.html");
    }
}
