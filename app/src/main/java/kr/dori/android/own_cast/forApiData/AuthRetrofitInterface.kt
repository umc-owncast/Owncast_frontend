package kr.dori.android.own_cast.forApiData

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Objects

//여기다가는 자기가 써야하는 함수 쓰기
//AuthResponse에 써놓은 DTO(result의 구조체) 넣어줘야 함
//ex)AuthResponse<List<CastHomeDTO>>>
interface PlaylistInterFace {
    @DELETE("/api/playlist/{playlistId}")
    fun deleteCast(@Path("playlistId") playlistId:Long): Call<AuthResponse<String>>
}
interface CastInterface{
    @DELETE("/api/cast/{castId}")
    fun deleteCast(@Path("castId") castId:Long): Call<AuthResponse<String>>
    @GET("/api/cast/{castId}/scripts")
    fun getCastScript(@Path("castId") castId:Long): Call<AuthResponse<String>>
    @GET("/api/cast/{castId}/audio")//이거 내 생각엔 파일인거같은데
    fun getCastPlay(@Path("castId") castId:Long): Call<AuthResponse<String>>
    @GET("/api/cast/search/home") // 검색 홈 API(검색 화면 상위 4개 castdata받아옴)
    fun searchHome(): Call<AuthResponse<List<CastHomeDTO>>>
    @GET("/api/cast/home")// 홈화면 키워드 6개 받아오기
    fun getKeywordHome() : Call<AuthResponse<List<String>>>
    @Multipart
    @PATCH("/api/cast/{castId}")//캐스트 수정 api, 이미지 파일로 보내야함
    fun patchCast(@Path("castId") castId:Long,@Part("updateInfo") updateInfo: UpdateInfo, @Part image: MultipartBody.Part):Call<AuthResponse<Objects>>

    @Multipart
    @POST("/api/cast/{castId}")//캐스트 저장 api keyvpSaveFragment에서 쓰인다
    fun postCast(@Path("castId") castId:Long,@Part("saveInfo") saveInfo: SaveInfo, @Part image: MultipartBody.Part):Call<AuthResponse<Objects>>
    /*@POST("/api/cast/{castId}")//캐스트 저장 api keyvpSaveFragment에서 쓰인다
    fun postCast(@Path("castId") castId:Long,@Body postCast: PostCast):Call<AuthResponse<Objects>>*/

    @POST("/api/cast/search")//검색 API
    fun postSearchAPI(@Query("keyword") keyword: String):Call<AuthResponse<Objects>>
    @POST("/api/cast/script")//스크립트로 캐스트를 생성하는 API, 반환되는 타입이 PostCastFromKeyword
    suspend fun postCastByScript(@Body postCastByScript: PostCastByScript):Response<AuthResponse<PostCastForResponse>>
    @POST("/api/cast/other")
    fun postOtherPlaylistCast(@Body postOtherPlaylistCast: PostOtherPlaylistCast):Call<AuthResponse<Long>>
    @POST("/api/cast/keyword")//키워드로 캐스트를 생성하는 API
    suspend fun postCastByKeyword(@Body postCastByKeyword: PostCastByKeyword):Response<AuthResponse<PostCastForResponse>>
    @POST("/api/cast/keyword-test")//스크립트로 캐스트를 생성하는 API
    //postCastByKeyword랑 같은거 쓰고 있음
    fun postScriptList(@Body postCastByKeyword: PostCastByKeyword):Call<String>
}

