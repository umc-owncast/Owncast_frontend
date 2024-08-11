package kr.dori.android.own_cast

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PlayCastViewModel2(application: Application) : AndroidViewModel(application) {
    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long> get() = _currentTime

    fun updateCurrentTime(time: Long) {
        _currentTime.value = time
    }
}
