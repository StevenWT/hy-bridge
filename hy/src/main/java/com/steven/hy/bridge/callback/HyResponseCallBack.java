package com.steven.hy.bridge.callback;



/**
 * Created by steven on 17/12/19.
 * 调用native->js 回调函数
 * 调用js插件的回调函数
 * js调用native插件的回调
 */

public interface HyResponseCallBack {
     void callback(Object data);
}
