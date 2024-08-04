package kr.dori.android.own_cast

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlayCastViewModel : ViewModel() {
    private val _isPlayVisible = MutableLiveData<Boolean>()
    val isPlayVisible: LiveData<Boolean> get() = _isPlayVisible

    fun showPlay() {
        _isPlayVisible.value = true
    }

    fun closePlay() {
        _isPlayVisible.value = false
    }
}


class PlayCastViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayCastViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayCastViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}