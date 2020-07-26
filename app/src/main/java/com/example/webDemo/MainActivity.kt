package com.example.webDemo

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    var tag = "download"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var webClient = LocalFileWebClient(this);
        web.webViewClient = webClient
        WebView.setWebContentsDebuggingEnabled(true)
        web.settings.javaScriptEnabled = true;
        web.settings.allowUniversalAccessFromFileURLs = true
        web.settings.mediaPlaybackRequiresUserGesture = false
        web.settings.allowUniversalAccessFromFileURLs = true
        web.loadUrl("file:///android_asset/index.html");
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun download(item: MenuItem) {
        downloadFile("https://convertcdn.netless.link/dynamicConvert/e1ee27fdb0fc4b7c8f649291010c4882/resources/ppt/media/media1.mp4")
    }

    fun reload(item: MenuItem) {
        web.reload()
    }

    private fun downloadFile(url: String) {

        val file = File("${cacheDir.absolutePath}/${cachedPath(url)}")
        if (file.exists()) {
            Log.i(tag, "file is exists, we don't need download twice")
            return
        } else {
            file.parentFile.mkdirs();
        }

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(tag, e.toString())
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val fos = FileOutputStream("${cacheDir.absolutePath}/${cachedPath(url)}", false)
                fos.write(response.body!!.bytes())
                fos.close()
                Log.i(tag, "finish and will reload webView")
                runOnUiThread { web.reload() }
            }
        })
    }
}
