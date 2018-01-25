package com.steven.hy.bridge.client;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.steven.hy.bridge.callback.HyResponseCallBack;
import com.steven.hy.bridge.plugin.HyPlugin;
import com.steven.hy.bridge.webview.HyView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * steven
 * 2017.12.19
 * hybrid bridge
 */
@SuppressLint({"SetJavaScriptEnabled", "NewApi"})
public class HyWebViewClient extends WebViewClient {

    private static final String kTag = "WVJB";
    private static final String kCustomProtocolScheme = "wvjbscheme";
    private static final String kQueueFlushMessage = "__WVJB_FLUSH_MESSAGE__";
    private static final String kQueueFlushMessageBegin = kCustomProtocolScheme + "://" + kQueueFlushMessage + "/";
    public static String kCustomInjectJsScheme="https://__get__bridge__/mobile/bridge.html";

    /**
     * 是否打印日志
     */
    private static boolean logging = false;


    /**
     * 当前weView
     */
    protected WebView webView;

    /**
     * webView和bridge的封装
     */
    private HyView hyView;

    /**
     * 桥没有ready之前存放message
     * 桥load之后，开发取消息，发送给js
     */
    private ArrayList<WVJBMessage> startupMessageQueue = null;

    /**
     * native调用js插件，注册的回调，等待js返回数据回调
     */
    private Map<String, HyResponseCallBack> responseCallbacks = null;

    /**
     * 当前webView包含的插件。
     * 包括全局插件的引用（全局插件公用同一个实体）
     * 当前webView独立插件
     */
    private Map<String, HyPlugin> plugins = null;
    /**
     * native->js 回调的标识。每次消息递增
     */
    private long uniqueId = 0;
    /**
     * 当前页面的默认插件
     */
    private HyPlugin defaultPlugin;


    public HyWebViewClient(HyView hyView) {
        this(hyView, null);
    }

    public HyWebViewClient(HyView hyView, HyPlugin defaultPlugin) {
        this.hyView = hyView;
        this.webView = hyView.getWebView();
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.responseCallbacks = new HashMap<>();
        this.plugins = new HashMap<>();
        this.startupMessageQueue = new ArrayList<>();
        this.defaultPlugin = defaultPlugin;
    }

    public void setLog(boolean log) {
        logging = log;
    }

    /**
     * 清空插件列表和消息列表
     */
    public void onDestroy(){
        responseCallbacks.clear();
        plugins.clear();
        defaultPlugin=null;
        webView=null;
        hyView=null;
    }

    /**
     * native -> js 调用js插件
     *
     * @param jsPluginName
     */
    public void callJsPlugin(String jsPluginName) {
        callJsPlugin(jsPluginName, null, null);
    }

    /**
     * native -> js 调用js插件
     *
     * @param jsPluginName
     * @param data
     */
    public void callJsPlugin(String jsPluginName, Object data) {
        callJsPlugin(jsPluginName, data, null);
    }

    /**
     * native -> js 调用js插件
     *
     * @param jsPluginName
     * @param data
     * @param responseCallback
     */
    public void callJsPlugin(String jsPluginName, Object data,
                             HyResponseCallBack responseCallback) {
        sendData(data, responseCallback, jsPluginName);
    }


    /**
     * 注册native的插件
     *
     * @param pluginName
     * @param plugin
     */
    public void registerPlugin(String pluginName, HyPlugin plugin) {
        if (pluginName == null || pluginName.length() == 0 || plugin == null)
            return;
        if(plugins!=null){
            plugins.put(pluginName, plugin);
        }
    }

    /**
     * 添加默认插件
     * 如果一个js->native的消息没有指明插件名称
     * 或者插件没有找到，js发送的消息则发送给这个默认插件
     * 一般用不到。。
     * @param plugin
     */
    public void setDefaultPlugin(HyPlugin plugin){
        this.defaultPlugin=plugin;
    }

    /**
     * 获取当前webView所有的插件
     * @return
     */
    public List<HyPlugin> getAllPlugin(){
        List<HyPlugin> allPlugin=new ArrayList<>();
        if(plugins!=null){
            Collection<HyPlugin> collection=plugins.values();
            allPlugin.addAll(collection);
        }
        return allPlugin;
    }

    private void sendData(Object data, HyResponseCallBack responseCallback,
                          String jsPluginName) {
        if (data == null && (jsPluginName == null || jsPluginName.length() == 0))
            return;
        WVJBMessage message = new WVJBMessage();
        if (data != null) {
            message.data = data;
        }
        if (responseCallback != null) {
            String callbackId = "objc_cb_" + (++uniqueId);
            if(responseCallbacks!=null){
                responseCallbacks.put(callbackId, responseCallback);
            }
            message.callbackId = callbackId;
        }
        if (jsPluginName != null) {
            message.handlerName = jsPluginName;
        }
        queueMessage(message);
    }

    private void queueMessage(WVJBMessage message) {
        if (startupMessageQueue != null) {
            startupMessageQueue.add(message);
        } else {
            dispatchMessage(message);
        }
    }

    private void dispatchMessage(WVJBMessage message) {
        String messageJSON = message2JSONObject(message).toString();

        log("SEND", messageJSON);

        executeJavascript("NJWebViewJavascriptBridge._handleMessageFromObjC('"
                + messageJSON + "');");
    }

    private JSONObject message2JSONObject(WVJBMessage message) {
        JSONObject jo = new JSONObject();
        try {
            if (message.callbackId != null) {
                jo.put("callbackId", message.callbackId);
            }
            if (message.data != null) {
                jo.put("data", message.data);
            }
            if (message.handlerName != null) {
                jo.put("handlerName", message.handlerName);
            }
            if (message.responseId != null) {
                jo.put("responseId", message.responseId);
            }
            if (message.responseData != null) {
                jo.put("responseData", message.responseData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }

    private WVJBMessage JSONObject2WVJBMessage(JSONObject jo) {
        WVJBMessage message = new WVJBMessage();
        try {
            if (jo.has("callbackId")) {
                message.callbackId = jo.getString("callbackId");
            }
            if (jo.has("data")) {
                message.data = jo.get("data");
            }
            if (jo.has("handlerName")) {
                message.handlerName = jo.getString("handlerName");
            }
            if (jo.has("responseId")) {
                message.responseId = jo.getString("responseId");
            }
            if (jo.has("responseData")) {
                message.responseData = jo.get("responseData");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }

    private void processQueueMessage(String messageQueueString) {
        try {
            JSONArray messages = new JSONArray(messageQueueString);
            for (int i = 0; i < messages.length(); i++) {
                JSONObject jo = messages.getJSONObject(i);

                log("RCVD", jo);

                WVJBMessage message = JSONObject2WVJBMessage(jo);
                if (message.responseId != null) {
                    HyResponseCallBack responseCallback = responseCallbacks
                            .remove(message.responseId);
                    if (responseCallback != null) {
                        responseCallback.callback(message.responseData);
                    }
                } else {
                    HyResponseCallBack responseCallback = null;
                    if (message.callbackId != null) {
                        final String callbackId = message.callbackId;
                        responseCallback = new HyResponseCallBack() {
                            @Override
                            public void callback(Object data) {
                                WVJBMessage msg = new WVJBMessage();
                                msg.responseId = callbackId;
                                msg.responseData = data;
                                queueMessage(msg);
                            }
                        };
                    }

                    HyPlugin plugin;
                    if (message.handlerName != null) {
                        plugin = plugins.get(message.handlerName);
                    } else {
                        plugin = defaultPlugin;
                    }
                    if (plugin != null) {
                        plugin.handlerRequest(message.data, responseCallback,hyView);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void log(String action, Object json) {
        if (!logging)
            return;
        String jsonString = String.valueOf(json);
        if (jsonString.length() > 500) {
            Log.i(kTag, action + ": " + jsonString.substring(0, 500) + " [...]");
        } else {
            Log.i(kTag, action + ": " + jsonString);
        }
    }


    public void executeJavascript(final String script) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(webView!=null){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        webView.evaluateJavascript(script, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {

                            }
                        });
                    } else {
                        webView.loadUrl("javascript:" + script);
                    }
                }
            }
        });

    }

    private void injectJs(){
        try {
            InputStream is = webView.getContext().getAssets()
                    .open("WebViewJavascriptBridge.js.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String js = new String(buffer);
            executeJavascript(js);

            if (startupMessageQueue != null) {
                for (int i = 0; i < startupMessageQueue.size(); i++) {
                    dispatchMessage(startupMessageQueue.get(i));
                }
                startupMessageQueue = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith(kCustomProtocolScheme)) {
            if (url.indexOf(kQueueFlushMessage) > 0) {
                try {
                    url = URLDecoder.decode(url, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                processQueueMessage(url.replace(kQueueFlushMessageBegin, ""));
                return true;
            }

        }else if(url.equals(kCustomInjectJsScheme)){
            //页面通过固定iframe的URL，可以实现注入Bridge JS，默认为 https://__get__bridge__
            injectJs();
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    // 增加shouldOverrideUrlLoading在api》=24时
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String url = request.getUrl().toString();
            try {
                url = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
            if (url.startsWith(kCustomProtocolScheme)) {
                if (url.indexOf(kQueueFlushMessage) > 0) {
                    try {
                        url = URLDecoder.decode(url, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    processQueueMessage(url.replace(kQueueFlushMessageBegin, ""));
                    return true;
                }

            }else if(url.equals(kCustomInjectJsScheme)){
                //页面通过固定iframe的URL，可以实现注入Bridge JS，默认为 https://__get__bridge__
                injectJs();
                return true;
            }
            return super.shouldOverrideUrlLoading(view, request);
        } else {
            return super.shouldOverrideUrlLoading(view, request);
        }
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if(request!=null&&request.getUrl()!=null&&request.getUrl().toString().equals(kCustomInjectJsScheme)){
            //页面里面的iframe只是页面的一部分,没有触发shouldOverrideUrlLoading 触发了shouldInterceptRequest
            //页面通过固定iframe的URL，可以实现注入Bridge JS，默认为 https://__get__bridge__
            injectJs();
        }
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if(!TextUtils.isEmpty(url)&&url.equals(kCustomInjectJsScheme)){
            //页面里面的iframe只是页面的一部分,没有触发shouldOverrideUrlLoading 触发了shouldInterceptRequest
            //页面通过固定iframe的URL，可以实现注入Bridge JS，默认为 https://__get__bridge__
            injectJs();
        }
        return super.shouldInterceptRequest(view, url);
    }


    /**
     * java<-->js之间发送的消息
     */
    private class WVJBMessage {
        Object data = null;
        String callbackId = null;
        String handlerName = null;
        String responseId = null;
        Object responseData = null;
    }

}
