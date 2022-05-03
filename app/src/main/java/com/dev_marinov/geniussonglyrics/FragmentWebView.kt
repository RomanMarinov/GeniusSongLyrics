package com.dev_marinov.geniussonglyrics

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebView.HitTestResult
import android.webkit.WebViewClient
import android.webkit.WebSettings

@SuppressLint("SetJavaScriptEnabled")
class FragmentWebView : Fragment() {

    var myUrl: String? = ""
    var mLastUrl: String? = ""
    lateinit var webView: WebView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e("333", "зашел в FragmentWebView")
        val view: View

        view = inflater.inflate(R.layout.fragment_web_view, container, false)
        (activity as MainActivity).flag = true
        webView = view.findViewById<WebView>(R.id.webview)

        webView.settings.javaScriptEnabled = true // поддержка джава скрипт для работы с сайтами

        Log.e("333", "webView.loadUrl = " + myUrl)
        webView.loadUrl(myUrl!!)

        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Log.e("333", "onPageStarted $url")
                mLastUrl = url
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                Log.e("333", "OnPageFinished $url")
                mLastUrl = url
                super.onPageFinished(view, url)
            }
        }

        webView.setOnTouchListener(object:View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                Log.e("333", "setOnTouchListener")
                val hitTestResult: HitTestResult = (v as WebView).hitTestResult

                if (hitTestResult != null && mLastUrl != null) {
                    Log.e("333", "setOnTouchListener первый if")
                        // isEmpty - значит массив пуст
                    if ((activity as MainActivity?)!!.previous.isEmpty() ||
                        !(activity as MainActivity?)!!.previous[(activity as MainActivity?)!!.previous.size - 1].equals(mLastUrl))
                    {
                        Log.e("333", "setOnTouchListener второй if")
                        (activity as MainActivity?)!!.previous.add(mLastUrl!!)
                    }
                }
                return false
            }
        })

        return view
    }

    fun setParam(url: String?){
        myUrl = url
    }
}