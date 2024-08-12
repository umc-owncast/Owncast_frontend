package kr.dori.android.own_cast.keyword

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class KeywordViewModel(application: Application) : AndroidViewModel(application) {
    private val _keywordData = MutableLiveData<KeywordData>()
    val keywordData: LiveData<KeywordData> get() = _keywordData

    fun setKeywordData(data: KeywordData) {
        _keywordData.value = data
    }


}