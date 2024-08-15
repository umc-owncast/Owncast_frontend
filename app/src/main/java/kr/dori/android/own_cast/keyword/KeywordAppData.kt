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

        fun getDetailTopic(key: String, defaultValue: String): List<String> {
            return KeywordAppData.detailTopic ?: listOf("로딩중","로딩중","로딩중","로딩중","로딩중","로딩중")
        }
        fun setDetailTopic(key: String, defaultValue: String) {
            initDetailKeyword()
        }

        var detailTopic : List<String> = listOf("로딩중","로딩중","로딩중","로딩중","로딩중","로딩중")


        /*var chooseIndex = -1
        var title: String = ""
        var thumbnail :Int? = null
        var user: String? = ""
        var duration: Int? = null
        var exposure:Boolean? = false
        var category:String = ""

        fun clearData(){
            chooseIndex = -1
            title = ""
            thumbnail = null
            user = ""
            duration = null
            exposure = false
            category = ""

        }*/


        fun initDetailKeyword(){
            val apiService = getRetrofit().create(CastInterface::class.java)
            //1. apiService후, 자신이 만들어놓은 인터페이스(함수 지정해주기)
            //2. AuthResponse에 응답으로 넘어오는 result 값의 제네릭 넣어주기 AuthResponse<List<CastHomeDTO>>
            //3. COMMON200이 성공 코드이고, resp에서 필요한 값 받기
            apiService.getKeywordHome().enqueue(object: Callback<AuthResponse<List<String>>> {
                override fun onResponse(call: Call<AuthResponse<List<String>>>, response: Response<AuthResponse<List<String>>>) {
                    Log.d("apiTest1", response.toString())
                    val resp = response.body()!!
                    when(resp.code) {
                        "COMMON200" -> {
                            Log.d("apiTest1","연결성공")
                            Log.d("apiTest1",resp.result.toString())
                            if(resp.result!=null){//키워드를 변경
                                detailTopic = resp.result
                            }
                        }
                        else ->{
                            Log.d("apiTest1","연결실패 코드 : ${resp.code}")

                        }
                    }
                }

                override fun onFailure(call: Call<AuthResponse<List<String>>>, t: Throwable) {
                    Log.d("apiTest1", t.message.toString())
                }
            })
        }

    }


}