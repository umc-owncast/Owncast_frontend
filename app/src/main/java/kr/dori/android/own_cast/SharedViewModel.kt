package kr.dori.android.own_cast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.dori.android.own_cast.keyword.KeywordData

class SharedViewModel : ViewModel() {
    private val _data = MutableLiveData<MutableList<SongData>>(mutableListOf())
    private val _keywordData = MutableLiveData<KeywordData>()

    val data: LiveData<MutableList<SongData>> get() = _data
    val keywordData: LiveData<KeywordData> get() = _keywordData

    fun setKeywordData(data: KeywordData) {
        _keywordData.value = data
    }

    fun getKeywordListData() : Array<String>? {
        return keywordData.value?.keywordList
    }

    fun setData(newData: MutableList<SongData>) {
        _data.value = newData
    }

    fun updateDataAt(position: Int, newData: SongData) {
        _data.value?.set(position, newData)
        _data.value = _data.value // 트리거를 발생시키기 위해 데이터 변경 통지
    }

    fun addData(newData: SongData) {
        _data.value?.add(newData)
        _data.value = _data.value // 트리거를 발생시키기 위해 데이터 변경 통지
    }
}
