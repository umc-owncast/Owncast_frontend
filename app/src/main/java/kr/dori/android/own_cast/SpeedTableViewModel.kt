package kr.dori.android.own_cast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SpeedTableViewModel: ViewModel() {
    private val _data = MutableLiveData<Float>()

    val data: LiveData<Float> get() = _data

    fun setData(data: Float){
        _data.value = data
    }

}