package com.example.webDemo

import android.content.Context
import android.os.Build
import android.util.Log
import android.webkit.*
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

class LocalFileWebClient(private val context: Context) : WebViewClient() {

    private var tag = "download";

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        Log.e(tag, request?.url.toString())
        return super.shouldOverrideUrlLoading(view, request)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        Log.i(tag, "start with ${request?.url?.toString()}")
        if (request == null) {
            return null
        } else if (isRangeRequest(request)) {
            Log.i(tag, "${request.url} is range request")
            return mediaResponse(request)
        } else {
            Log.i(tag, "${request.url} is normal request")
            return normalResponse(request)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun normalResponse(request: WebResourceRequest): WebResourceResponse? {
        var mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(request.url.toString()))
        var file = File("${context.cacheDir.absolutePath}/${cachedPath(request.url.toString())}")
        if (!file.exists()) {
            Log.i(tag,"file ${file.absolutePath} not exist")
            return null
        }
        val tempResponseHeaders: MutableMap<String, String> = HashMap();

        // CORS
        tempResponseHeaders["Access-Control-Allow-Origin"] = "*"
        tempResponseHeaders["Access-Control-Allow-Methods"] = "POST, GET, OPTIONS"
        tempResponseHeaders["Access-Control-Allow-Headers"] = "Content-Type"

        var inputStream = FileInputStream(file)
        return WebResourceResponse(mime, "UTF-8", 200, "ok", tempResponseHeaders, inputStream)

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun isRangeRequest(request: WebResourceRequest?): Boolean {
        return request != null && request.requestHeaders["Range"] != null
    }

    // https://github.com/ionic-team/cordova-plugin-ionic-webview/pull/298/files
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun mediaResponse(request: WebResourceRequest): WebResourceResponse? {
        var mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(request.url.toString()))
        var inputStream = if (CommonVariables.useLocalMedia) {
            context.assets.open("media.mp4")
        } else {
            var media = File("${context.cacheDir.absolutePath}/${cachedPath(request.url.toString())}")
            if (!media.exists()) {
                Log.i(tag,"file ${media.absolutePath} not exist")
                return null
            }
            FileInputStream(media)
        }

        val tempResponseHeaders: MutableMap<String, String> = HashMap();
        try {
            val totalRange: Int = inputStream.available()
            val rangeString = request.requestHeaders["Range"]
            val parts =
                rangeString!!.split("=".toRegex()).toTypedArray()
            val streamParts =
                parts[1].split("-".toRegex()).toTypedArray()
            val fromRange = streamParts[0]
            var range = totalRange - 1
            if (streamParts.size > 1 && streamParts[1] != "") {
                range = streamParts[1].toInt()
            }
            tempResponseHeaders["Accept-Ranges"] = "bytes"
            tempResponseHeaders["Content-Range"] = "bytes $fromRange-$range/$totalRange"
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        Log.i(tag, "request hit $tempResponseHeaders")
        return WebResourceResponse(mime, "UTF-8", 206, "ok", tempResponseHeaders, inputStream)
    }
}