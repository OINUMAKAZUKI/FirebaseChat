package namanuma.com.firebasechat.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import namanuma.com.firebasechat.R

class WebViewActivity : AppCompatActivity() {

    lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val url = intent.getStringExtra("URL")

        webView = findViewById(R.id.webView) as WebView
        webView.loadUrl(url)
    }
}
