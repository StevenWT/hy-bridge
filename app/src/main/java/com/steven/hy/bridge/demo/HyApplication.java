package com.steven.hy.bridge.demo;

import android.app.Application;

import com.steven.hy.bridge.HyEnv;
import com.steven.hy.bridge.client.HyWebViewClient;

/**
 * Created by steven on 18/1/2.
 */

public class HyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initHy();
    }

    private void initHy(){
        HyEnv.getIns().isDebug=BuildConfig.DEBUG;
        /**
         * 每个web页面需要通过iFrame添加这个链接。
         * native 监听到以后开始注入桥
         * 确保每个页面都可以注入成功
         */
        HyWebViewClient.kCustomInjectJsScheme="https://__get__bridge__/mobile/bridge.html";
    }
}
