package kr.dori.android.own_cast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupViewModel : ViewModel() {
    private val _nickNameError = MutableLiveData<String>()
    val nickNameError: LiveData<String> get() = _nickNameError

    fun checkNickName(nickName: String) {
        RetrofitClient.instance.checkNickName(nickName).enqueue(object : Callback<CheckNickNameResponse> {
            override fun onResponse(call: Call<CheckNickNameResponse>, response: Response<CheckNickNameResponse>) {
                if (response.isSuccessful) {
                    _nickNameError.value = if (response.body()?.result == true) {
                        "이미 존재하는 닉네임입니다"
                    } else {
                        null
                    }
                } else {
                    _nickNameError.value = "API 호출 실패"
                }
            }

            override fun onFailure(call: Call<CheckNickNameResponse>, t: Throwable) {
                _nickNameError.value = "네트워크 오류: ${t.localizedMessage}"
            }
        })
    }
}