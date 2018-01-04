package com.steven.hy.bridge.plugin.imp;


import com.steven.hy.bridge.callback.HyResponseCallBack;
import com.steven.hy.bridge.plugin.HyPlugin;
import com.steven.hy.bridge.util.HyDataUtil;
import com.steven.hy.bridge.webview.HyView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by steven on 17/12/19.
 * PageLoader 插件
 */

public class PageLoaderPlugin implements HyPlugin {
    /**
     * "status" 0 // 0=显示loading＋隐藏webView, 1=显示错误&retry + 隐藏webView, 2=隐藏loading＋显示webView
     */
    public static final String STATUS="status";
    public static final int STATUS_SHOW_LOADING=0;
    public static final int STATUS_SHOW_RETRY=1;
    public static final int STATUS_DISMISS_LOADING=2;

    @Override
    public String getPluginName() {
        return "PageLoader";
    }

    @Override
    public int getPluginVersion() {
        return 1;
    }

    @Override
    public void handlerRequest(Object jsonObject, HyResponseCallBack callBack, HyView hyView) {
        if(jsonObject!=null&&jsonObject instanceof JSONObject){
            int result=-1;
            try {
                result=((JSONObject) jsonObject).getInt(STATUS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(result==STATUS_SHOW_LOADING){
                hyView.showLoading();
                callBack.callback(HyDataUtil.getHySuccessData());
            }else if(result==STATUS_SHOW_RETRY){
                hyView.showRetry();
                callBack.callback(HyDataUtil.getHySuccessData());
            }else if(result==STATUS_DISMISS_LOADING){
                hyView.dismissLoading();
                callBack.callback(HyDataUtil.getHySuccessData());
            }else {
                callBack.callback(HyDataUtil.getHyFailData());
            }
        }else {
            callBack.callback(HyDataUtil.getHyFailData());
        }

    }
}
