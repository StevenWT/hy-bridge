# hy-bridge
Bridge for sending messages between Android Java and JavaScript in WebViews
### 前言
项目需要一个简单的hybrid。参考已经存在的开源项目，修复了一些桥的通信Bug,并进行了插件，loading等使用友好性封装。代码比较简单，注释相对详细。几天完成的模块，想要简单的hybrid项目可以参考。

### 注入
页面通过固定的URL，可以实现注入Bridge JS，默认为 `iframe src="https://__get__bridge__/mobile/bridge.html`，
譬如
`<iframe src="https://__get__bridge__/mobile/bridge.html" style="display:none"></iframe>`

### 前端调用

1. 页面通过判断 `NJWebViewJavascriptBridgeReady` 事件来获取bridge对象，使用方法：

```
	function connectWebViewJavascriptBridge(callback) {
		if (window.NJWebViewJavascriptBridge) {
			callback(NJWebViewJavascriptBridge)
		} else {
			document.addEventListener('NJWebViewJavascriptBridgeReady', function() {
				callback(NJWebViewJavascriptBridge)
			}, false)
		}
	}
```

2. Bridge包含几种调用客户端的方法：
    * `registerHandler(handlerName, handler)` 注册事件，捕获客户端该handlerName的调用
    * `callHandler(handlerName, data, responseCallback)` 调用客户端，传递data参数，并处理回调。

### 客户端API

1. 客户端可以自行设计插件，插件分两种，一种为全局框架插件（插件类全局一个实例），一种为每个WebView独立的插件，譬如弹出Toast为全局插件，业务级的下一步为单一场景使用的插件.
2. 客户端包含几种调用前端或者接收的方法
    * 初始化HyEnv,例如debug控制
    * 注册全局插件HyGlobalPluginManager
    * HyWebView WebView的封装类，js通信，桥，插件，loading控制。

### 策略设计

1. 客户端加载页面出Loading，30s后前端没有主动调用disMissLoading,则客户端出重试按钮。点击重试刷新页面。
2. 客户端SSL错误,页面给出提示，用户判断是否继续。

### 通信数据格式

1. 发送的数据：JSON对象即可

2. 返回的数据：固定格式嵌套返回结果。

    例如获取native所有插件的PluginList，返回的结果：
```
{
  "Outcome": "Success",
  "Message": "",
  "Data": {
    "Toast": 1, // 版本号
    "Back": 1
  }
}
```

| 返回key值 | 名称 | 说明 | 其他 |
| --- | --- | --- | --- |
| Outcome | 插件功能是否成功 | 插件功能是否成功：Success(成功)、Fail(失败) |  |
| Message | 错误信息或者错误码 | 可以用来传递错误信息或者错误码  |  |
| Data | 插件运行成功真正返回的数据（JSON格式） | JSON对象，插件返回的结果 |  |


### 通信原理简要说明
1. native->js 4.4以下：loadUrl() 4.4或者以上：evaluateJavascript（）
2. js->native web通过iFrame 客户端进行拦截得到数据

### 参考资料
1. https://github.com/jesse01/WebViewJavascriptBridge

    项目最初版本是基于这个代码，通信部分存在兼容性问题，修复了4.4以下通信部分的问题。感谢作者。
2. https://github.com/lzyzsd/JsBridge

3. https://github.com/pedant/safe-java-js-webview-bridge

4. https://github.com/marcuswestin/WebViewJavascriptBridge

    ios可以使用这个项目。简单修改可以和当前android项目相匹配。



