package kr.dori.android.own_cast.keyword

import android.app.Application

import android.util.Log
import kr.dori.android.own_cast.SignupData
import kr.dori.android.own_cast.forApiData.AuthResponse
import kr.dori.android.own_cast.forApiData.CastInterface
import kr.dori.android.own_cast.forApiData.getRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class KeywordAppData: Application() {
    companion object{
        //keyword에서 쓸 전역변수 정리
        var mainTopic = ""
        var detailTopic : List<String> = listOf()
        fun getDetailTopic(key: String, defaultValue: String): List<String> {
            return KeywordAppData.detailTopic
        }
        fun updateDetailTopic(data: List<String>) {
            detailTopic = data
        }


    }


}