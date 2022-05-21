package com.dev_marinov.geniussonglyrics.presentation

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dev_marinov.geniussonglyrics.data.ObjectList
import com.dev_marinov.geniussonglyrics.model.RequestData

class ViewModelListArtist : ViewModel() {

    private var hashMapArtist: MutableLiveData<HashMap<Int, ObjectList>> = MutableLiveData()

    //инициализируем список и заполняем его данными пользователей
    init {
        // с помощью value можно получить и отправить данные любым активным подписчикам
        // RequestData.getData()
        // RequestData.getData(dataFromString as String, dataToString as String, 1)
        hashMapArtist.value = RequestData.getHashArtists()
    }

    fun getHashMapArtists() = hashMapArtist


    fun setParams(num: Int, context: Context?) {
        RequestData.getData(num, context)
    }

}