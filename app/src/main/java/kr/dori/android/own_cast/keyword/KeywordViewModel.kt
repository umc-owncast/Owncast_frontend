package kr.dori.android.own_cast.keyword

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kr.dori.android.own_cast.forApiData.Sentences

class KeywordViewModel(application: Application) : AndroidViewModel(application) {

    private val _songDuration = MutableLiveData<String>()
    private val _castId = MutableLiveData<Long>()
    private val _inputKeyword = MutableLiveData<String>()
    private val _sentences = MutableLiveData<MutableList<Sentences>>(mutableListOf())

    /*-----------------------getter----------------------*/
    val songDuration : LiveData<String> get () = _songDuration
    val castId : LiveData<Long> get() = _castId
    val inputKeyword : LiveData<String> get() = _inputKeyword
    val sentences : LiveData<MutableList<Sentences>> get()= _sentences

    /*-----------------------setter----------------------*/
    fun setSongDuration(data: String) {
        _songDuration.value = data
    }
    fun setCastId(data: Long){
        _castId.value = data
    }
    fun setInputKeyword(data : String){
        _inputKeyword.value = data
    }
    fun setSentences(data:MutableList<Sentences>){
        _sentences.value = data
    }


}