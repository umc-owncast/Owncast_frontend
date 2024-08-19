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

    private val _songDuration = MutableLiveData<String>()//노래의 길이를 저장함
    private val _castId = MutableLiveData<Long>()//재생하고 있는 키워드를 저장함
    private val _inputKeyword = MutableLiveData<String>()//만든 키워드 값을 저장함
    private val _sentences = MutableLiveData<List<Sentences>>()//실시간 가사를 받아온다
    private val _postCastKeyword = MutableLiveData<PostCastByKeyword>()//키워드로 생성했을 때 데이터(재생성 하기 위해서)
    private val _postCastScript = MutableLiveData<PostCastByScript>()//스크립트로 생성했을 때 데이터
    private val _getPlayList = MutableLiveData<MutableList<PlaylistText>>(mutableListOf())

    private var _streamingUrl :String = ""





    /*-----------------------getter----------------------*/
    val songDuration : LiveData<String> get () = _songDuration
    val castId : LiveData<Long> get() = _castId
    val inputKeyword : LiveData<String> get() = _inputKeyword
    val sentences : LiveData<List<Sentences>> get()= _sentences
    val postCastKeyword : LiveData<PostCastByKeyword> get() = _postCastKeyword
    val postCastScript : LiveData<PostCastByScript> get() = _postCastScript


    val getPlayList : LiveData<MutableList<PlaylistText>> get() = _getPlayList

    val streamingUrl : String get() = _streamingUrl


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

    fun setUrl(data :String){
        _streamingUrl = data
    }

}
data class PlaylistText(
    val id: Long,
    val playlistName : String
)