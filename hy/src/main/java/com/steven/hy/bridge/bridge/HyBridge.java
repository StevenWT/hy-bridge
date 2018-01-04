package com.steven.hy.bridge.bridge;

import com.steven.hy.bridge.callback.HyResponseCallBack;
import com.steven.hy.bridge.plugin.HyPlugin;

/**
 * Created by steven on 17/12/19.
 * native<->js通信的桥接口
 */

public interface HyBridge {
    /**
     * 注册当前webView的插件
     * 全局插件注册到HyGlobalPluginManager
     * @param hyPlugin
     */
    void addViewPlugin(HyPlugin hyPlugin);

    /**
     * 添加默认插件
     * 如果一个js->native的消息没有指明插件名称
     * 或者插件没有找到，js发送的消息则发送给这个默认插件
     * 一般用不到。。
     * @param defaultPlugin
     */
    void setDefaultPlugin(HyPlugin defaultPlugin);

    /**
     * 调用js的插件，有参数，有回调
     * @param jsPluginName
     * @param object
     * @param callBack
     */
    void callJsPlugin(String jsPluginName, Object object, HyResponseCallBack callBack);

    /**
     * 调用js的插件，有参数，无回调
     * @param jsPluginName
     * @param object
     */
    void callJsPlugin(String jsPluginName, Object object);

    /**
     * 调用js的插件，无参数，无回调
     * @param jsPluginName
     */
    void callJsPlugin(String jsPluginName);

}
