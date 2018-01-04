package com.steven.hy.bridge.webview;

import android.webkit.WebView;

import com.steven.hy.bridge.bridge.HyBridge;
import com.steven.hy.bridge.plugin.HyPlugin;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by steven on 17/12/19.
 * webView 封装类接口
 */

public interface HyView extends HyBridge {

    /**
     * 获取当前的真正webView
     * @return
     */
    WebView getWebView();

    /**
     * 加载url
     * @param url
     */
    void loadUrl(String url);

    /**
     * 获取web需要的初始化数据
     * @return
     */
    JSONObject getWebInitData();

    /**
     * 设置web初始化数据，Web可通过插件GetDataPlugin获取
     * @param object
     */
    void setWebInitData(JSONObject object);

    /**
     * 显示loading
     */
    void showLoading();

    /**
     * 取消loading
     */
    void dismissLoading();

    /**
     * 显示retry页面
     */
    void showRetry();

    /**
     * 获取当前webView的全部插件
     * @return
     */
    List<HyPlugin> getAllPluginList();

}
