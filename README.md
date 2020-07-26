# webView 资源拦截

## 使用

在`app/src/main/assets`放入任意 mp4 文件，取名为`media1.mp4`。然后等待 gradle 启动，加载 webView。

>目前表现行为：模拟器上，能够在正常播放声音，但是无法看到画面。

## Android 与 iOS （WKWebView）不同点：

1. Android 可以直接拦截 http 请求，不需要像 iOS 一样，使用自定义 scheme 请求进行拦截（iOS 11 API，或者使用私有 API 进行拦截）
2. Android 拦截时，可以进行选择回退，iOS 不可以。
3. iOS 使用私有 API 拦截 http 请求时，会造成 post 的 body 丢失，Android 如果拦截，也是取不到 body 的。

Android 与 iOS 相同点

1. 无法获取到 post 请求的 body 部分。