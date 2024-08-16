package kr.dori.android.own_cast.playlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.dori.android.own_cast.data.SongData
import kr.dori.android.own_cast.forApiData.AuthResponse
import kr.dori.android.own_cast.forApiData.GetAllPlaylist
import kr.dori.android.own_cast.keyword.KeywordData

class SharedViewModel : ViewModel() {
    private val _data = MutableLiveData<MutableList<GetAllPlaylist>>(mutableListOf())
    private val _keywordData = MutableLiveData<KeywordData>()

    val data: LiveData<MutableList<GetAllPlaylist>> get() = _data
    val keywordData: LiveData<KeywordData> get() = _keywordData

    fun setKeywordData(data: KeywordData) {
        _keywordData.value = data
    }

    fun getKeywordListData() : Array<String>? {
        return keywordData.value?.keywordList
    }

    fun setData(newData: MutableList<GetAllPlaylist>) {
        _data.value = newData
        Log.d("SharedViewModel", "setData: $newData")
    }

    fun updateDataAt(position: Long, newData: GetAllPlaylist) {
        _data.value?.set(position.toInt(), newData)
        _data.value = _data.value // 트리거를 발생시키기 위해 데이터 변경 통지
        Log.d("SharedViewModel", "updateDataAt($position): $newData")
    }

    fun addData(newData: GetAllPlaylist) {
        _data.value?.add(newData)
        _data.value = _data.value // 트리거를 발생시키기 위해 데이터 변경 통지
        Log.d("SharedViewModel", "addData: $newData")
    }


}
