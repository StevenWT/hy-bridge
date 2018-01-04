package com.steven.hy.bridge.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;


import com.steven.hy.bridge.callback.HyResponseCallBack;
import com.steven.hy.bridge.plugin.HyPlugin;
import com.steven.hy.bridge.util.HyDataUtil;
import com.steven.hy.bridge.webview.HyView;
import com.steven.hy.bridge.webview.HyWebView;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by steven on 18/1/4.
 * hybrid测试页
 * 注册一个局部跳转插件Next
 * 调用一个js插件
 */
public class HyDemoActivity extends AppCompatActivity {
    private HyWebView mHyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hy_demo_content);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mHyView = findViewById(R.id.pub_debug_hy_web_view);
    }

    private void initData() {
        /**
         * 添加当前页面的插件.
         * 插件分为全局插件和页面插件。
         * 全局插件：每一个页面都存在的插件，注册一次，全局生效。
         * 局部插件：只在当前页面生效。
         * Toast为全局插件。
         * 注册全局插件调用HyGlobalPluginManager注册。
         */
        mHyView.addViewPlugin(new HyPlugin() {
            @Override
            public String getPluginName() {
                return "Next";
            }

            @Override
            public int getPluginVersion() {
                return 0;
            }

            @Override
            public void handlerRequest(Object data, HyResponseCallBack callBack, HyView hyView) {
                Toast.makeText(HyDemoActivity.this, "Next called:"
                        + data, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(HyDemoActivity.this, HyNextActivity.class);
                startActivity(intent);
                callBack.callback(HyDataUtil.getHySuccessData());
            }
        });

        mHyView.loadUrl("file:///android_asset/ExampleApp.html");
    }

    private void initListener() {
        findViewById(R.id.pub_debug_hy_bt_two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mHyView.callJsPlugin("testJavascriptPlugin",
                            new JSONObject("{\"Result\": \"Hi , JS!\" }"),
                            new HyResponseCallBack() {

                                @Override
                                public void callback(Object data) {
                                    Toast.makeText(HyDemoActivity.this,
                                            "testJavascriptHandler responded: "
                                                    + data, Toast.LENGTH_LONG).show();
                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHyView != null) {
            mHyView.onDestroy();
        }
    }
}
