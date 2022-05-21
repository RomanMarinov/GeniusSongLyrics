package com.dev_marinov.geniussonglyrics.presentation

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebView.HitTestResult
import android.webkit.WebViewClient
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dev_marinov.geniussonglyrics.R

@SuppressLint("SetJavaScriptEnabled")
class FragmentWebView : Fragment() {

    lateinit var viewModelSharedUrl: ViewModelSharedUrl
    lateinit var viewModelWebViewPages: ViewModelWebViewPages
    lateinit var viewModelWebViewUrlOriginal: ViewModelWebViewUrlOriginal
    lateinit var viewModelWebViewLastUrl: ViewModelWebViewLastUrl

    lateinit var webView: WebView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e("333", "зашел в FragmentWebView")
        val view: View

        view = inflater.inflate(R.layout.fragment_web_view, container, false)

        viewModelWebViewPages = ViewModelProvider(requireActivity()).get(ViewModelWebViewPages::class.java)
        viewModelWebViewUrlOriginal = ViewModelProvider(requireActivity()).get(ViewModelWebViewUrlOriginal::class.java)
        viewModelWebViewLastUrl = ViewModelProvider(requireActivity()).get(ViewModelWebViewLastUrl::class.java)
        viewModelSharedUrl = ViewModelProvider(requireActivity()).get(ViewModelSharedUrl::class.java)

        // посылаем в mainactivity - true для flag (это значит мы открыли webview)
        MainActivity.myInterFaceFlagWebView.methodMyInterFaceFlagWebView(true)

        webView = view.findViewById<WebView>(R.id.webview)
        webView.settings.javaScriptEnabled = true // поддержка джава скрипт для работы с сайтами

        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Log.e("333", "onPageStarted $url")
                viewModelWebViewLastUrl.mLastUrl = url
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                Log.e("333", "OnPageFinished $url")
               // viewModelWebViewLastUrl.mLastUrl = url
                super.onPageFinished(view, url)
            }
        }

        webView.setOnTouchListener(object:View.OnTouchListener{
            override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                Log.e("333", "setOnTouchListener")
                val hitTestResult: HitTestResult = (view as WebView).hitTestResult

                Log.e("333","hitTestResult=" + hitTestResult + "=mLastUrl=" + viewModelWebViewLastUrl.mLastUrl)

                if (hitTestResult != null && viewModelWebViewLastUrl.mLastUrl != null) {
                    Log.e("333", "setOnTouchListener первый if")


                    //Log.e("333", "(activity as MainActivity?)!!.previous.isEmpty()" + (activity as MainActivity?)!!.previous.isEmpty())

//                    Log.e("333", "(activity as MainActivity?)!!.previous[(activity as MainActivity?)!!.previous.size - 1]" + (activity as MainActivity?)!!.previous[(activity as MainActivity?)!!.previous.size - 1])
                    Log.e("333", "viewModelWebViewLastUrl.mLastUrl" + viewModelWebViewLastUrl.mLastUrl)

                    // isEmpty - true если эта последовательность символов пуста
                    // если массив пуст или ЗНАЧЕНИЕ previous[previous.size - 1] равно
                    if (viewModelWebViewUrlOriginal.arrayListPreviousUrlOriginal.isEmpty() ||
                        // или если последний элемент не равен текущей ссылке
                        !viewModelWebViewUrlOriginal.arrayListPreviousUrlOriginal
                                [viewModelWebViewUrlOriginal.arrayListPreviousUrlOriginal.size - 1]
                            .equals(viewModelWebViewLastUrl.mLastUrl)) {

                        Log.e("333", "setOnTouchListener второй if")
                        viewModelWebViewUrlOriginal.arrayListPreviousUrlOriginal.add(viewModelWebViewLastUrl.mLastUrl!!)

                        Log.e("333","=finish viewModelWebViewLastUrl.mLastUrl= " + viewModelWebViewLastUrl.mLastUrl)
                        // интерфейс для отправки в майнактивити массива
                        MainActivity.myInterFaceWebViewSendArrayListUrl
                            .methodMyInterFaceWebViewSendArrayListUrl(viewModelWebViewUrlOriginal
                                .arrayListPreviousUrlOriginal)
                    }
                }
                return false
            }
        })

            // наблюдатель от клика
        viewModelSharedUrl.url.observe(viewLifecycleOwner, Observer {
            setUrl(it)
        })

        return view
    }

    fun setUrl(url: String) {

        requireActivity().runOnUiThread { // в гланом потоке
            viewModelWebViewPages.urlPage = url

            // если пуста последняя ссылка в массиве (с переходами), то отображаем по которой кликнули
            if (viewModelWebViewLastUrl.mLastUrl.equals("")) {
                webView.loadUrl(viewModelWebViewPages.urlPage)
            } else {
                // если есть последняя ссылка в массиве (с переходами), то ее отображаем
                webView.loadUrl(viewModelWebViewLastUrl.mLastUrl!!)
            }

        }
    }

}