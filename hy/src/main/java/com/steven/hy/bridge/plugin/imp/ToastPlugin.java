package com.steven.hy.bridge.plugin.imp;

import android.text.TextUtils;
import android.widget.Toast;

import com.steven.hy.bridge.callback.HyResponseCallBack;
import com.steven.hy.bridge.plugin.HyPlugin;
import com.steven.hy.bridge.util.HyDataUtil;
import com.steven.hy.bridge.webview.HyView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by steven on 17/12/19.
 * Toast 插件
 */

public class ToastPlugin implements HyPlugin {
    /**
     * {
     * "type": 1, // 1=普通， 2=error, 空/默认=1
     * "message": "toast message",
     * }
     */
    public static final String TYPE = "type";
    public static final String MESSAGE = "message";
    public static final int TYPE_SHORT=1;
    public static final int TYPE_LONG=2;

    @Override
    public String getPluginName() {
        return "Toast";
    }

    @Override
    public int getPluginVersion() {
        return 1;
    }

    @Override
    public void handlerRequest(Object jsonObject, HyResponseCallBack callBack, HyView hyView) {
        if (jsonObject != null && jsonObject instanceof JSONObject) {
            int type = -1;
            String message = "";
            try {
                message = ((JSONObject) jsonObject).getString(MESSAGE);
                type = ((JSONObject) jsonObject).getInt(TYPE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(!TextUtils.isEmpty(message)){
                if(type==TYPE_SHORT){
                    Toast.makeText(hyView.getWebView().getContext(), message, Toast.LENGTH_SHORT).show();
                    callBack.callback(HyDataUtil.getHySuccessData());
                    return;
                }else if(type==TYPE_LONG){
                    Toast.makeText(hyView.getWebView().getContext(), message, Toast.LENGTH_LONG).show();
                    callBack.callback(HyDataUtil.getHySuccessData());
                    return;
                }
            }
        }
        callBack.callback(HyDataUtil.getHyFailData());
    }

}
