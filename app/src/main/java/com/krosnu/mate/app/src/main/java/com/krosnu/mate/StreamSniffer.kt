package com.krosnu.mate

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class StreamSniffer(private val context: Context) {

    private var webView: WebView? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var timeoutRunnable: Runnable? = null

    @SuppressLint("SetJavaScriptEnabled")
    fun sniffStream(
        channel: Channel,
        onFound: (streamUrl: String, referer: String) -> Unit,
        onFailed: (reason: String) -> Unit
    ) {
        cleanup()

        if (channel.url.contains(".m3u8", ignoreCase = true) ||
            channel.url.contains(".mp4", ignoreCase = true)
        ) {
            onFound(channel.url, channel.url)
            return
        }

        var resolved = false
        val wv = WebView(context)
        webView = wv

        wv.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            setSupportMultipleWindows(false)
            javaScriptCanOpenWindowsAutomatically = false
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            userAgentString =
                "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
        }

        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val target = request?.url?.toString()?.lowercase() ?: return true
                if (target.startsWith("intent:") ||
                    target.startsWith("market:") ||
                    target.contains("doubleclick") ||
                    target.contains("googleads") ||
                    target.contains("popads")
                ) {
                    return true
                }
                return false
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                val reqUrl = request?.url?.toString() ?: return null
                if (!resolved && isStreamUrl(reqUrl)) {
                    resolved = true
                    mainHandler.post {
                        cleanup()
                        onFound(reqUrl, channel.url)
                    }
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (channel.matchKeyword.isNotBlank()) {
                    val kw = channel.matchKeyword.replace("'", "\\'")
                    val js = """
                        (function() {
                            var kw = '$kw'.toLowerCase();
                            var els = document.querySelectorAll('button, a, div, li, span');
                            for (var i = 0; i < els.length; i++) {
                                var t = (els[i].innerText || '').trim().toLowerCase();
                                if (t === kw || (t.includes(kw) && t.length < 40)) {
                                    els[i].click();
                                    break;
                                }
                            }
                        })();
                    """.trimIndent()
                    view?.evaluateJavascript(js, null)
                }
            }
        }

        timeoutRunnable = Runnable {
            if (!resolved) {
                resolved = true
                cleanup()
                onFailed("Yayın akışı bulunamadı")
            }
        }
        mainHandler.postDelayed(timeoutRunnable!!, 14000)

        wv.loadUrl(channel.url)
    }

    private fun isStreamUrl(url: String): Boolean {
        val lower = url.lowercase()
        return (lower.contains(".m3u8") || lower.contains(".mpd")) &&
            !lower.contains("googleads") &&
            !lower.contains("doubleclick")
    }

    fun cleanup() {
        timeoutRunnable?.let { mainHandler.removeCallbacks(it) }
        timeoutRunnable = null
        webView?.stopLoading()
        webView?.destroy()
        webView = null
    }
}
