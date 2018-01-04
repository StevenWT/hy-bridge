package com.steven.hy.bridge.plugin;


import com.steven.hy.bridge.callback.HyResponseCallBack;
import com.steven.hy.bridge.webview.HyView;

/**
 * Created by steven on 17/12/19.
 * native 插件接口类
 */

public interface HyPlugin {
    String getPluginName();
    int getPluginVersion();
    void handlerRequest(Object data, HyResponseCallBack callBack, HyView hyView);
}
