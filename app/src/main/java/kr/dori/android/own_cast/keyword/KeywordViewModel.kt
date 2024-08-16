package kr.dori.android.own_cast.keyword

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import kr.dori.android.own_cast.forApiData.GetPlayList

import kr.dori.android.own_cast.forApiData.PostCastByKeyword
import kr.dori.android.own_cast.forApiData.PostCastByScript
import kr.dori.android.own_cast.forApiData.Sentences

class KeywordViewModel(application: Application) : AndroidViewModel(application) {

    private val _songDuration = MutableLiveData<String>()
    private val _castId = MutableLiveData<Long>()
    private val _inputKeyword = MutableLiveData<String>()
    private val _sentences = MutableLiveData<List<Sentences>>()
    private val _postCastKeyword = MutableLiveData<PostCastByKeyword>()
    private val _postCastScript = MutableLiveData<PostCastByScript>()

    private val _getPlayList = MutableLiveData<MutableList<PlaylistText>>(mutableListOf())


    /*-----------------------getter----------------------*/
    val songDuration : LiveData<String> get () = _songDuration
    val castId : LiveData<Long> get() = _castId
    val inputKeyword : LiveData<String> get() = _inputKeyword
    val sentences : LiveData<List<Sentences>> get()= _sentences
    val postCastKeyword : LiveData<PostCastByKeyword> get() = _postCastKeyword
    val postCastScript : LiveData<PostCastByScript> get() = _postCastScript


    val getPlayList : LiveData<MutableList<PlaylistText>> get() = _getPlayList


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
    fun setSentences(data: List<Sentences>){
        _sentences.value = data
    }
    fun setPostCastKeyword(data : PostCastByKeyword){
        _postCastKeyword.value = data
    }
    fun setPostCastScript(data : PostCastByScript){
        _postCastScript.value = data

    }
    fun setGetPlayList(data : List<PlaylistText>){
        _getPlayList.value?.clear()
        _getPlayList.value?.addAll(data)
    }

    fun addGetPlayList(data: PlaylistText){
        _getPlayList.value?.add(data)

    }


}
data class PlaylistText(
    val id: Long,
    val playlistName : String
)