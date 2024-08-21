package kr.dori.android.own_cast.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookmarkViewModel: ViewModel() {
    private val _bookMarkId = MutableLiveData<MutableList<Long>>(mutableListOf())

    val bookmarkId: LiveData<MutableList<Long>>get()=_bookMarkId

    fun addBookmark(sentenceId: Long){
        _bookMarkId.value?.add(sentenceId)
    }

    fun deleteBookmark(sentenceId: Long){
        _bookMarkId.value?.let {
            it.remove(sentenceId)
            _bookMarkId.value = it // 변경된 리스트를 MutableLiveData에 반영
        }
    }




    }

