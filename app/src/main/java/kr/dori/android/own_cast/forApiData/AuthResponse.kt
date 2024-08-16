package kr.dori.android.own_cast.forApiData


import com.google.gson.annotations.SerializedName
import java.io.Serial

//자신이 필요한 수신 데이터 계층 쓰기

/*data class AuthResponse<T> (
    @SerializedName(value = "isSuccess") val isSuccess: Boolean,
    @SerializedName(value = "code") val code: String,
    @SerializedName(value = "message") val message: String,
    @SerializedName(value = "result") val result: List<CastHomeDTO>? = null
)*/


data class AuthResponse<T> (
    @SerializedName(value = "isSuccess") val isSuccess: Boolean,
    @SerializedName(value = "code") val code: String,
    @SerializedName(value = "message") val message: String?,
    @SerializedName(value = "result") val result: T? = null
)

// 2 계층
data class CastHomeDTO(//검색 홈 API,
    @SerializedName(value = "id") val id: Long,
    @SerializedName(value = "audioLength") val audioLength: String,
    @SerializedName(value = "title") val title: String,
    @SerializedName(value = "memberName") val memberName : String,
    @SerializedName(value = "playlistName") val playlistName : String
)

data class PostPlaylist(
    @SerializedName(value = "playlistId") val playlistId: Long
)

//리스트 처리는 이런식으로 하세요
data class GetPlayList(
    @SerializedName(value = "castList") val castList: List<Cast>
)

data class DeletePlaylist(
    @SerializedName(value = "playlistId") val playlistId: Long,
    @SerializedName(value = "totalCast") val totalCast: Int
)

data class PatchPlaylist(
    @SerializedName(value = "playlistId") val playlistId: Long,
    @SerializedName(value = "playlistName") val playlistName: String
)

data class PostCastForResponse(
    @SerializedName(value = "id") val id: Long,
    @SerializedName(value = "sentences") val sentences: List<Sentences>
)

data class GetAllPlaylist(
    val name: String,
    val imagePath: String,
    val playlistId: Long,
    val totalCast: Int
)




// 3 계층

data class Cast(
    @SerializedName(value = "castId") val castId: Long,
    @SerializedName(value = "castTitle") val castTitle: String,
    @SerializedName(value = "isPublic") val isPublic: Boolean,
    @SerializedName(value = "castCreator") val castCreator: String,
    @SerializedName(value = "castCategory") val castCategory: String,
    @SerializedName(value = "audioLength") val audioLength: String
)

data class Sentences(
    @SerializedName(value = "originalSentence") val originalSentence: String,
    @SerializedName(value = "translatedSentence") val translatedSentence: String,
    @SerializedName(value = "timePoint") val audioLength: Double
)








