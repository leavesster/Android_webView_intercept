# webView 资源拦截

## 资源

在`app/src/main/assets`中已有一个网页和一个本地 mp4 资源。

网页内容：

1. local video 为直接通过 file 路径播放 Android `assets`中的视频。
2. http video 为播放网络资源视频。

## 使用

1. 点击右上角【三点】按钮，选择【使用本地资源替换】，会将 http video 的 mp4 下载到本地，然后重载本地网页。此时由于应用中，已经有对应的视频资源，http video 的请求，将会被`LocalFileWebClient`拦1截，将本地视频内容，返回给 webview。这些操作，可以在 logcat 使用 `download` 标签进行查看。
    * 当未下载时，会提示 `not exsit`，当应用中存在视频时，会提示`request hit`
1. 还可以点击【三点】按钮，选择【使用下载资源替换】，会将 http video 的 mp4 请求响应，替换成 Android `assets` 视频
    * 此时不需要使用 logcat 查看日志，`assets`视频与 http video 资源不同


## 与 iOS（WKWebView）不同点：

1. Android 可以直接拦截 http 请求，不需要像 iOS 一样，使用自定义 scheme 请求进行拦截（iOS 11 API），或使用私有 API 进行拦截。
2. Android 拦截时，可以进行选择回退，直接返回 null，就可以交由系统处理；iOS 不可以。
3. iOS 使用私有 API 拦截 http 请求时，会造成 post 的 body 丢失，Android 也截取不到 body，但是可以通过回退交由系统处理。

## Android 与 iOS 相同点

1. 无法获取到 post 请求的 body 部分。