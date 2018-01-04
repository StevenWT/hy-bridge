package com.steven.hy.bridge.plugin.imp;

import android.app.Activity;

import com.steven.hy.bridge.callback.HyResponseCallBack;
import com.steven.hy.bridge.plugin.HyPlugin;
import com.steven.hy.bridge.util.HyDataUtil;
import com.steven.hy.bridge.webview.HyView;


/**
 * Created by steven on 17/12/19.
 * Back 插件
 */

public class BackPlugin implements HyPlugin {
    @Override
    public String getPluginName() {
        return "Back";
    }

    @Override
    public int getPluginVersion() {
        return 1;
    }

    @Override
    public void handlerRequest(Object jsonObject, HyResponseCallBack callBack, HyView hyView) {
        if(hyView.getWebView()!=null&&hyView.getWebView().getContext() instanceof Activity){
            ((Activity) hyView.getWebView().getContext()).onBackPressed();
            callBack.callback(HyDataUtil.getHySuccessData());
        }else {
            callBack.callback(HyDataUtil.getHyFailData());
        }

    }
}
