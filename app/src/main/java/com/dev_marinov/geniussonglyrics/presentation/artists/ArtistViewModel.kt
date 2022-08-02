package com.dev_marinov.geniussonglyrics.presentation.artists

import android.util.Log
import androidx.lifecycle.*
import com.dev_marinov.geniussonglyrics.SingleLiveEvent
import com.dev_marinov.geniussonglyrics.domain.IArtistSongRepository
import com.dev_marinov.geniussonglyrics.domain.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    private val artistSongRepository: IArtistSongRepository
) : ViewModel() {

    private val _artist: MutableLiveData<List<Song>> = MutableLiveData()
    val artist: LiveData<List<Song>> = _artist

    private val _uploadData = SingleLiveEvent<String>()
    val uploadData: SingleLiveEvent<String> = _uploadData

    private val _flagLoading: MutableLiveData<Boolean> = MutableLiveData()
    val flagLoading: LiveData<Boolean> = _flagLoading

    private val limiter = 5
    private val itemsPerPageCount = 20
    private var num = itemsPerPageCount  // переменная для увеличения значения для метода getData

    init {
        getArtists(num)
    }

    fun onClick(url: String){
        uploadData.postValue(url)
    }

    fun onScroll(itemCount: Int, lastVisibleItemPosition: Int) {
        if (_flagLoading.value == false && (itemCount - limiter) == lastVisibleItemPosition) {
            num += itemsPerPageCount
            getArtists(num)
        }
    }

    private fun getArtists(num: Int){
        _flagLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            artistSongRepository.getArtistsSongs(num).let {
                val list: MutableList<Song> = _artist.value?.toMutableList()?: mutableListOf()
                list.addAll(it)
                _artist.postValue(list)

                _flagLoading.postValue(false)
            }
        }
    }
}