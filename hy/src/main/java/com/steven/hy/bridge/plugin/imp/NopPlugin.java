package com.steven.hy.bridge.plugin.imp;


import com.steven.hy.bridge.callback.HyResponseCallBack;
import com.steven.hy.bridge.plugin.HyPlugin;
import com.steven.hy.bridge.util.HyDataUtil;
import com.steven.hy.bridge.webview.HyView;

/**
 * Created by steven on 17/12/19.
 * 删除js的iFrame 插件
 */

public class NopPlugin implements HyPlugin {
    @Override
    public String getPluginName() {
        return "Nop";
    }

    @Override
    public int getPluginVersion() {
        return 1;
    }

    @Override
    public void handlerRequest(Object jsonObject, HyResponseCallBack callBack, HyView hyView) {
        callBack.callback(HyDataUtil.getHySuccessData());
    }
}
