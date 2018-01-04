package com.steven.hy.bridge.webview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.steven.hy.bridge.HyEnv;
import com.steven.hy.bridge.R;
import com.steven.hy.bridge.callback.HyResponseCallBack;
import com.steven.hy.bridge.client.HyWebViewClient;
import com.steven.hy.bridge.plugin.HyGlobalPluginManager;
import com.steven.hy.bridge.plugin.HyPlugin;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by steven on 17/12/19.
 * webView的封装：
 * 包括loading,retry,sslError
 * 和js的通信
 */

public class HyWebView extends LinearLayout implements HyView{
    protected static int appCacheMaxSize = 1024 * 1024 * 8;
    protected View mRoot;
    protected RelativeLayout mRlLoading;
    protected RelativeLayout mRlRetry;
    protected WebView mWebView;
    protected TextView mTvRetryMessage;
    protected Button mBtRetry;
    protected Button mBtNext;

    protected SslErrorHandler mSslErrorHandler;
    protected HyWebViewClient webViewClient;

    protected JSONObject mWebInitData;
    //30秒内如果js没有取消处理loading,默认显示重试页面
    protected boolean isJsDismissLoading;
    protected String url;

    public HyWebView(Context context) {
        super(context);
        initView();
    }

    public HyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public HyWebView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(21)
    public HyWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        mRoot = LayoutInflater.from(getContext()).inflate(R.layout.hy_view_content_layout, null);
        mRlLoading = (RelativeLayout) mRoot.findViewById(R.id.hy_view_loading_rl);
        mRlRetry = (RelativeLayout) mRoot.findViewById(R.id.hy_view_retry_rl);
        mBtRetry = (Button) mRoot.findViewById(R.id.hy_view_retry_bt_retry);
        mBtNext = (Button) mRoot.findViewById(R.id.hy_view_retry_bt_next);
        mWebView = (WebView) mRoot.findViewById(R.id.hy_view_web_view);
        mTvRetryMessage = (TextView) mRoot.findViewById(R.id.hy_view_retry_tip_tv);
        addView(mRoot, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        initWebView();
        initListener();
    }

    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        settings.setUseWideViewPort(true);
        settings.setTextSize(WebSettings.TextSize.NORMAL);
        settings.setDomStorageEnabled(true);
        settings.setSavePassword(false);
        settings.setSupportZoom(true);
        settings.setAppCacheMaxSize(appCacheMaxSize);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        setWebViewClient();
        setWebViewChromeClient();
        showView(VISIBLE,GONE,null);
        mWebView.setVerticalScrollBarEnabled(false);
        /**
         * 设置浏览器是否可以调试
         */
        if(HyEnv.getIns().isDebug){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                mWebView.setWebContentsDebuggingEnabled(true);
            }
            webViewClient.setLog(true);
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                mWebView.setWebContentsDebuggingEnabled(false);
            }
            webViewClient.setLog(false);
        }
    }

    private void setWebViewClient() {
        webViewClient= new HyWebViewClient(this) {
            boolean urlError = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                urlError = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //onPageFinished 不在取消loading,由js发送消息来控制
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (!urlError) {
//                            showView(View.GONE, View.GONE, null);
//                        }
//                    }
//                }, 100);

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                mSslErrorHandler = handler;
                showView(View.GONE, View.VISIBLE, error);
                urlError = true;
            }

            @TargetApi(21)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                showView(View.GONE, View.VISIBLE, null);
                urlError = true;
            }

            @TargetApi(21)
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
            }

        };
        mWebView.setWebViewClient(webViewClient);
        initGlobalPlugin(webViewClient);
    }

    /**
     * 把全局的Plugin添加到当前webVeiw
     * @param hyWebViewClient
     */
    private void initGlobalPlugin(HyWebViewClient hyWebViewClient){
        List<HyPlugin> list= HyGlobalPluginManager.getIns().getGlobalPlugins();
        if(list!=null&&list.size()>0){
            for (HyPlugin hyPlugin:list){
                hyWebViewClient.registerPlugin(hyPlugin.getPluginName(),hyPlugin);
            }
        }
    }

    private void setWebViewChromeClient() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                return super.onJsAlert(view,url,message,result);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message,
                                      String defaultValue, JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });
    }


    /**
     * 控制loading 显示
     * @param loading
     * @param retry
     * @param error
     */
    private void showView(int loading, int retry,SslError error) {
        mRlRetry.setVisibility(retry);
        mRlLoading.setVisibility(loading);
        if (retry == View.VISIBLE) {
            mTvRetryMessage.setText(getResources().getString(R.string.hy_load_fail));
        }
        if(error!=null){
            mBtNext.setVisibility(View.VISIBLE);
            if(error.getPrimaryError()== SslError.SSL_DATE_INVALID||
                    error.getPrimaryError()== SslError.SSL_NOTYETVALID||
                    error.getPrimaryError()== SslError.SSL_EXPIRED){
                mTvRetryMessage.setText(getResources().getString(R.string.hy_ssl_error_date));
            }else {
                mTvRetryMessage.setText(getResources().getString(R.string.hy_ssl_error_cer));
            }
        }else {
            mBtNext.setVisibility(View.GONE);
        }
    }

    /**
     * 重试按钮
     */
    private void initListener(){
        mBtRetry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSslErrorHandler!=null){
                    mSslErrorHandler.cancel();
                }
                retryLoad();
            }
        });
        mBtNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSslErrorHandler!=null){
                    showView(View.GONE, View.GONE,null);
                    mSslErrorHandler.proceed();
                    mSslErrorHandler=null;
                }
            }
        });
    }

    private void retryLoad(){
        showView(View.VISIBLE, View.GONE,null);
        loadUrl(url);
    }

    @Override
    public WebView getWebView() {
        return mWebView;
    }

    @Override
    public void loadUrl(String url){
        if(!TextUtils.isEmpty(url)){
            this.url=url;
            isJsDismissLoading=false;
            registerDismissLoading();
            //todo 自己处理同步cookie操作
//            WebViewCookieManager.synCookies(mWebView,url);
            mWebView.loadUrl(url);
        }
    }

    private void registerDismissLoading(){
        //30秒前端不取消loading,显示retry页面
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isJsDismissLoading){
                    showView(GONE,VISIBLE,null);
                }
            }
        },30*1000);
    }

    @Override
    public JSONObject getWebInitData() {
        return mWebInitData;
    }

    @Override
    public void setWebInitData(JSONObject object) {
        this.mWebInitData=object;
    }

    @Override
    public void showLoading() {
        isJsDismissLoading=true;
        showView(VISIBLE,GONE,null);
    }

    @Override
    public void dismissLoading() {
        isJsDismissLoading=true;
        showView(GONE,GONE,null);
    }

    @Override
    public void showRetry() {
        isJsDismissLoading=true;
        showView(GONE,VISIBLE,null);
    }

    @Override
    public List<HyPlugin> getAllPluginList() {
        return webViewClient.getAllPlugin();
    }

    @Override
    public void addViewPlugin(HyPlugin hyPlugin) {
        webViewClient.registerPlugin(hyPlugin.getPluginName(),hyPlugin);
    }

    @Override
    public void setDefaultPlugin(HyPlugin defaultPlugin) {
        webViewClient.setDefaultPlugin(defaultPlugin);
    }

    @Override
    public void callJsPlugin(String jsPluginName, Object object, HyResponseCallBack callBack) {
        webViewClient.callJsPlugin(jsPluginName,object,callBack);
    }

    @Override
    public void callJsPlugin(String jsPluginName, Object object) {
        webViewClient.callJsPlugin(jsPluginName,object);
    }

    @Override
    public void callJsPlugin(String jsPluginName) {
        webViewClient.callJsPlugin(jsPluginName);
    }


    /**
     * 销毁webView和回调消息
     */
    public void onDestroy(){
        mWebView.removeAllViews();
        mWebView.setWebViewClient(null);
        mWebView.destroy();
        mWebView=null;
        webViewClient.onDestroy();
        webViewClient=null;
    }
}
