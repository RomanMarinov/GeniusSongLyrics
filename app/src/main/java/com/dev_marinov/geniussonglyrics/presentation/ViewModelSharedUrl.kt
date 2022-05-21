package com.dev_marinov.geniussonglyrics.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelSharedUrl : ViewModel(){

    val url: MutableLiveData<String> = MutableLiveData()

    fun sendMessageUrl(urlPageSong: String) {
        url.value = urlPageSong
    }
}