package kr.dori.android.own_cast.keyword

import android.util.Log
import kr.dori.android.own_cast.forApiData.AuthResponse

import kr.dori.android.own_cast.forApiData.CastHomeDTO
import kr.dori.android.own_cast.forApiData.getRetrofit

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface Connector{
    fun onSuccess()
    fun onFailure()
}

//이거 액티비티마다 원하는 기능으로 쓰면 돼용
/*fun apiexecute(){
    val apiService = getRetrofit().create(AuthRetrofitInterFace::class.java)
    //1. apiService후, 자신이 만들어놓은 인터페이스(함수 지정해주기)
    //2. AuthResponse에 응답으로 넘어오는 result 값의 제네릭 넣어주기 AuthResponse<List<CastHomeDTO>>
    //3. COMMON200이 성공 코드이고, resp에서 필요한 값 받기
    apiService.searchHome().enqueue(object: Callback<AuthResponse<List<CastHomeDTO>>> {
        override fun onResponse(call: Call<AuthResponse<List<CastHomeDTO>>>, response: Response<AuthResponse<List<CastHomeDTO>>>) {
            Log.d("SIGNUP/SUCCESS", response.toString())
            val resp: AuthResponse<List<CastHomeDTO>> = response.body()!!

            when(resp.code) {
                "COMMON200" -> {
                    Log.d("apiTest","연결성공")
                }
                else ->{
                    Log.d("apiTest","연결실패 코드 : ${resp.code}")
                }
            }
        }

        override fun onFailure(call: Call<AuthResponse<List<CastHomeDTO>>>, t: Throwable) {
            Log.d("apiTest", t.message.toString())
        }
    })
}*/


//밑에 코드 쓰는 코드 아님!!!!!!!!!!!!!!!!!!!

class AuthService  {

    private lateinit var connector: Connector

    fun setConnector(connector: Connector){
        this.connector = connector
    }

    //주석친게 사용법인데 아까워서 둠
    /*val authService = AuthService()
    authService.setConnector(this)
    val apiService = getRetrofit().create(AuthRetrofitInterFace::class.java)
    authService.apiExecute<List<CastHomeDTO>> { apiService.searchHome() }*/


    /*fun <T> apiExecute(apiCall: () -> Call<AuthResponse<T>>) {
        //val authService = getRetrofit().create(AuthRetrofitInterFace::class.java)
        //이걸 사용하는 activity에 써주면 됨!
        var resp: AuthResponse<T>
        apiCall().enqueue(object: Callback<AuthResponse<T>> {
            override fun onResponse(call: Call<AuthResponse<T>>, response: retrofit2.Response<AuthResponse<T>>) {
                Log.d("SIGNUP/SUCCESS", response.toString())
                resp = response.body()!!

                when(resp.code) {
                    "COMMON200" -> {
                        connector.onSuccess()
                    }
                    else ->{
                        connector.onFailure()
                    }

                }

            }
            override fun onFailure(call: Call<AuthResponse<T>>, t: Throwable) {
                Log.d("apiTest", t.message.toString())
            }
        })

    }*/

}





