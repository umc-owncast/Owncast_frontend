package kr.dori.android.own_cast.playlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    // 방법을 캐싱으로 바꿨습니다. 서버 업데이트는 정상적으로 됩니다.
    fun updateDataAt(position: Long, name: String) {
        _data.value?.let { currentList ->
            if (position >= 0 && position < currentList.size) {
                val item = currentList[position.toInt()]
                item.name = name
                _data.value = currentList // 변경된 데이터를 관찰 가능하도록 다시 할당합니다. 오류가 생긴다면, 그냥 새로운 GetAllPlaylist 객체를 생성하는 방식으로 수정해야 될 겁니다. 지금은 name필드를 var로 설정함
                Log.d("SharedViewModel", "updateDataAt($position): name updated to $name")
            } else {
                Log.d("SharedViewModel", "updateDataAt($position): Invalid position")
            }
        }
    }


    fun addData(newData: GetAllPlaylist) {
        _data.value?.add(newData)
        _data.value = _data.value // 트리거를 발생시키기 위해 데이터 변경 통지
        Log.d("SharedViewModel", "addData: $newData")
    }


}
