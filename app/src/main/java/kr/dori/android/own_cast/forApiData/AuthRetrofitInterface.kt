package kr.dori.android.own_cast.forApiData


import android.renderscript.Script

import okhttp3.MultipartBody
import okhttp3.ResponseBody
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

interface CastInterface{
    @DELETE("/api/cast/{castId}")
    suspend fun deleteCast(@Path("castId") castId:Long): Response<AuthResponse<String>>
    @GET("/api/cast/{castId}/scripts")
    suspend fun getCastScript(@Path("castId") castId:Long): Response<AuthResponse<List<Script>>>
    @GET("/api/cast/{castId}/audio")
    suspend fun getCastPlay(@Path("castId") castId: Long): Response<ResponseBody>




    @GET("/api/cast/search/home") // 검색 홈 API(검색 화면 상위 4개 castdata받아옴)
    fun searchHome(): Call<AuthResponse<List<CastHomeDTO>>>
    @GET("/api/cast/home")// 홈화면 키워드 6개 받아오기
    suspend fun getKeywordHome() : Response<AuthResponse<List<String>>>
    @Multipart
    @PATCH("/api/cast/{castId}")//캐스트 수정 api, 이미지 파일로 보내야함
    suspend fun patchCast(@Path("castId") castId:Long,@Part("updateInfo") updateInfo: UpdateInfo, @Part image: MultipartBody.Part):Response<AuthResponse<String>>

    @Multipart
    @POST("/api/cast/{castId}")//캐스트 저장 api keyvpSaveFragment에서 쓰인다
    fun postCast(@Path("castId") castId:Long,@Part("saveInfo") saveInfo: SaveInfo, @Part image: MultipartBody.Part):Call<AuthResponse<String>>




    /*@POST("/api/cast/{castId}")//캐스트 저장 api keyvpSaveFragment에서 쓰인다
    fun postCast(@Path("castId") castId:Long,@Body postCast: PostCast):Call<AuthResponse<Objects>>*/

    @POST("/api/cast/search")//검색 API
    suspend fun postSearchAPI(@Query("keyword") keyword: String):Response<AuthResponse<List<CastHomeDTO>>>
    @POST("/api/cast/script")//스크립트로 캐스트를 생성하는 API, 반환되는 타입이 PostCastFromKeyword
    suspend fun postCastByScript(@Body postCastByScript: PostCastByScript):Response<AuthResponse<PostCastForResponse>>
    @POST("/api/cast/other")
    fun postOtherPlaylistCast(@Body postOtherPlaylistCast: PostOtherPlaylistCast):Call<AuthResponse<PostOtherPlaylist>>
    @POST("/api/cast/keyword")//키워드로 캐스트를 생성하는 API
    suspend fun postCastByKeyword(@Body postCastByKeyword: PostCastByKeyword):Response<AuthResponse<PostCastForResponse>>
    @POST("/api/cast/keyword-test")//스크립트로 캐스트를 생성하는 API
    //postCastByKeyword랑 같은거 쓰고 있음

    fun postScriptList(@Body postCastByKeyword: PostCastByKeyword):Call<String>
}

interface PlayListInterface{
    @DELETE("/api/cast/{playlistId}")
    suspend fun deleteCast(@Path("playlistId") playlistId:Long): Response<AuthResponse<DeletePlaylist>>
    @GET("/api/playlist/view")// 사용자 플레이리스트 목록 받아오기
    //GetPlayList잘못만든거같던데..
    fun getPlayList() : Call<AuthResponse<List<GetUserPlaylist>>>

    @GET("/api/playlist/view")// 사용자 플레이리스트 목록 받아오기
    suspend fun getPlayListCorutine() : Response<AuthResponse<List<GetUserPlaylist>>>
    @POST("/api/playlist")
    fun postPlayList(@Query("playlistName") playlistName: String) : Call<AuthResponse<PostPlaylist>>

}



interface Playlist{
    @DELETE("/api/playlist/{playlistId}")
    suspend fun deletePlaylist(@Query("playlistId") playlistId: Long): Response<AuthResponse<DeletePlaylist>>

    @GET("/api/playlist/{playlistId}")
    suspend fun getPlaylistInfo(@Query("playlistId") playlistId: Long, @Query("page") page: Int, @Query("size") size: Int): Response<AuthResponse<GetPlayList>>

    @GET("/api/playlist/view")
    suspend fun getAllPlaylist():Response<AuthResponse<List<GetAllPlaylist>>>

    @PATCH("/api/playlist/{playlistId}")
    suspend fun patchPlaylist(@Query("playlistId") playlistId: Long, @Query("playlistName") playlistName: String): Response<AuthResponse<PatchPlaylist>>

    @POST("/api/playlist")
    suspend fun postPlaylist(@Query("playlistName")playlistName: String): Response<AuthResponse<PostPlaylist>>

    @GET("/api/cast/{castId}")
    suspend fun getCast(@Path("castId")castId: Long): Response<AuthResponse<CastInfo>>
}




interface Bookmark {
    @POST("/api/bookmark")
    suspend fun postBookmark(@Query("sentenceId") sentenceId: Int): Response<AuthResponse<Long>>

    @DELETE("/api/bookmark")
    suspend fun deleteBookmark(@Query("sentenceId") sentenceId: Int):
            Response<AuthResponse<Long>>

    @GET("/api/study/{playlistId}")
    suspend fun getBookmark(@Path("playlistId") playlistId: Int):
            Response<AuthResponse<List<GetBookmark>>>

    @GET("/api/study/savedcast")
    suspend fun getSaved(): Response<AuthResponse<List<GetBookmark>>>

    @GET("/api/study/mycast")
    suspend fun getMy(): Response<AuthResponse<List<GetBookmark>>>
}
