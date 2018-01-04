package com.steven.hy.bridge.plugin;

import com.steven.hy.bridge.plugin.imp.BackPlugin;
import com.steven.hy.bridge.plugin.imp.GetDataPlugin;
import com.steven.hy.bridge.plugin.imp.NopPlugin;
import com.steven.hy.bridge.plugin.imp.PageLoaderPlugin;
import com.steven.hy.bridge.plugin.imp.PluginListPlugin;
import com.steven.hy.bridge.plugin.imp.ToastPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steven on 17/12/19.
 * 全局plugin
 * 每一个webView共享
 */

public class HyGlobalPluginManager {

    private static HyGlobalPluginManager hyGlobalPluginManager;

    private List<HyPlugin> hyPlugins=new ArrayList<>();

    private HyGlobalPluginManager(){
        hyPlugins.add(new BackPlugin());
        hyPlugins.add(new GetDataPlugin());
        hyPlugins.add(new PageLoaderPlugin());
        hyPlugins.add(new PluginListPlugin());
        hyPlugins.add(new ToastPlugin());
        hyPlugins.add(new NopPlugin());
    }

    public static HyGlobalPluginManager getIns(){
        if(hyGlobalPluginManager ==null){
            synchronized (HyGlobalPluginManager.class){
                if(hyGlobalPluginManager ==null){
                    hyGlobalPluginManager =new HyGlobalPluginManager();
                }
            }
        }
        return hyGlobalPluginManager;
    }

    /**
     * 获取全部plugin
     * @return
     */
    public List<HyPlugin> getGlobalPlugins(){
        return hyPlugins;
    }

    /**
     * 注册全局插件
     * @param hyPlugin
     */
    public void rigisterGlobalPlugin(HyPlugin hyPlugin){
        hyPlugins.add(hyPlugin);
    }






}
