package com.steven.hy.bridge.plugin.imp;


import com.steven.hy.bridge.callback.HyResponseCallBack;
import com.steven.hy.bridge.plugin.HyPlugin;
import com.steven.hy.bridge.util.HyDataUtil;
import com.steven.hy.bridge.webview.HyView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by steven on 17/12/19.
 * PluginList 插件
 * 获取所有当前环境的插件和版本
 */

public class PluginListPlugin implements HyPlugin {
    @Override
    public String getPluginName() {
        return "PluginList";
    }

    @Override
    public int getPluginVersion() {
        return 1;
    }

    @Override
    public void handlerRequest(Object jsonObject, HyResponseCallBack callBack, HyView hyView) {
        JSONObject response= HyDataUtil.getHySuccessData();
        List<HyPlugin> allPlugin=hyView.getAllPluginList();
        JSONObject data=new JSONObject();
        if(allPlugin!=null){
            for (HyPlugin hyPlugin:allPlugin){
                try {
                    data.put(hyPlugin.getPluginName(),hyPlugin.getPluginVersion());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            response.put(HyDataUtil.DATA,data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callBack.callback(response);
    }
}
