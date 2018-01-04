package com.steven.hy.bridge.activity;

import android.app.Activity;
import android.os.Bundle;

import com.steven.hy.bridge.R;
import com.steven.hy.bridge.webview.HyWebView;


/**
 * Created by steven on 17/12/28
 * hy view 基础类
 */

public class HyBaseActivity extends Activity {

    protected HyWebView mHyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hy_basey_layout);
        initHyView();
    }

    private void initHyView() {
        mHyView = (HyWebView) findViewById(R.id.hy_base_hy_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHyView!=null){
            mHyView.onDestroy();
        }
    }
}
