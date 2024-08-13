package kr.dori.android.own_cast.forApiData

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

//여기다가는 자기가 써야하는 함수 쓰기
//AuthResponse에 써놓은 DTO(result의 구조체) 넣어줘야 함
//ex)AuthResponse<List<CastHomeDTO>>>
interface AuthRetrofitInterFace {
    @GET("/api/cast/search/home") // @Method(api address)
    //@Body castHomeDTO: CastHomeDTO
    fun searchHome(): Call<AuthResponse<List<CastHomeDTO>>>

    @POST("/api/playlist")
    fun postPlaylist(@Body request: UserPostPlaylist): Call<AuthResponse<PostPlaylist>>
}

