package com.dev_marinov.geniussonglyrics.presentation.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebResourceRequest
import androidx.fragment.app.Fragment
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dev_marinov.geniussonglyrics.R
import com.dev_marinov.geniussonglyrics.databinding.FragmentWebViewBinding
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("SetJavaScriptEnabled")
@AndroidEntryPoint
class WebViewFragment : Fragment() {
    private lateinit var binding: FragmentWebViewBinding
    lateinit var webViewViewModel: WebViewViewModel
    private lateinit var callback: OnBackPressedCallback

    private val args: WebViewFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.e("444", "зашел в FragmentWebView")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_web_view, container, false)
        webViewViewModel = ViewModelProvider(requireActivity())[WebViewViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSettingsWebView()
        addUrlToStack()
        loadUrl()
        setWebViewClient()
        onBackPressedCallback()
    }

    private fun addUrlToStack(){
        if (!webViewViewModel.urlListStack.isEmpty()) {
            if (webViewViewModel.urlListStack.size == 1
                && webViewViewModel.urlListStack.peek().toString() != args.url) {
                webViewViewModel.urlListStack.clear()
                webViewViewModel.urlListStack.add(args.url)
                Log.e("444", "arguments?.let IF it.getString(KEY_URL )" + args.url)
                Log.e("444", "arguments?.let IF")
            }
        } else {
            webViewViewModel.urlListStack.add(args.url)
        }
    }

    private fun loadUrl(){
        binding.webView.loadUrl(webViewViewModel.urlListStack.peek())
        Log.e("444", "arguments?.let размер urlListStack.size=" + webViewViewModel.urlListStack.size +
                " содержимое urlListStack=" + webViewViewModel.urlListStack)
    }

    private fun setWebViewClient(){
        binding.webView.webViewClient = object : WebViewClient(){
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Log.e("444", "----------------- onPageStarted -----------------")
                Log.e("444", "onPageStarted сейчас ты видишь =" +  binding.webView.url)
                webViewViewModel.backStatus = true
                callback.isEnabled = webViewViewModel.urlListStack.size != 1

                super.onPageStarted(view, url, favicon)
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                view.loadUrl(request.url.toString())
                Log.e("444","=shouldOverrideUrlLoading request добавил в urlListStack.add =" + request.url.toString())
                webViewViewModel.urlListStack.add(request.url.toString())
                Log.e("444","=shouldOverrideUrlLoading размер urlListStack.size=" + webViewViewModel.urlListStack.size +
                        " содержимое urlListStack=" + webViewViewModel.urlListStack)
                return true
            }
        }
    }

    private fun onBackPressedCallback() {
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.e("444", "нажал OnBackPressedCallback")
                // можно удалить только после того как сработает onPageStarted
                if (webViewViewModel.backStatus) {
                    Log.e("444", "OnBackPressedCallback удалил последний элемент")
                    webViewViewModel.urlListStack.pop() // удалить последний элемент
                    webViewViewModel.backStatus = false
                }

                if (!webViewViewModel.urlListStack.isEmpty()) {
                    binding.webView.loadUrl(webViewViewModel.urlListStack.peek()) // загрузить последний элемент списка
                    Log.e("444", "OnBackPressedCallback размер urlListStack.size=" + webViewViewModel.urlListStack.size +
                            " содержимое urlListStack=" + webViewViewModel.urlListStack)
                } else {
                    callback.remove()
                }

                if(webViewViewModel.urlListStack.size == 1) {
                    Log.e("444", "OnBackPressedCallback callback.remove urlListStack.size=" + webViewViewModel.urlListStack.size)
                    callback.remove()
                    webViewViewModel.urlListStack.clear()
                    Log.e("444", "OnBackPressedCallback размер ПОСЛЕ CLEAR urlListStack.size=" + webViewViewModel.urlListStack.size +
                            " содержимое urlListStack=" + webViewViewModel.urlListStack)
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setSettingsWebView(){
        binding.webView.settings.javaScriptEnabled = true // поддержка джава скрипт для работы с сайтами
        binding.webView.settings.allowContentAccess = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.useWideViewPort = true
    }
}

