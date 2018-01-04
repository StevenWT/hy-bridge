package com.steven.hy.bridge.plugin.imp;

import com.steven.hy.bridge.callback.HyResponseCallBack;
import com.steven.hy.bridge.plugin.HyPlugin;
import com.steven.hy.bridge.util.HyDataUtil;
import com.steven.hy.bridge.webview.HyView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by steven on 17/12/19.
 * GetData 插件
 * 向客户端获取初始化数据，参数量大或是数据格式不适合放在URL上时使用
 */

public class GetDataPlugin implements HyPlugin {
    @Override
    public String getPluginName() {
        return "GetData";
    }

    @Override
    public int getPluginVersion() {
        return 1;
    }

    @Override
    public void handlerRequest(Object jsonObject, HyResponseCallBack callBack, HyView hyView) {
        JSONObject data=hyView.getWebInitData();
        JSONObject response= HyDataUtil.getHySuccessData();
        try {
            response.put(HyDataUtil.DATA,data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callBack.callback(response);
    }
}
