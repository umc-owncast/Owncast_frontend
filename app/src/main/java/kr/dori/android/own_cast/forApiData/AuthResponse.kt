package kr.dori.android.own_cast.forApiData


import com.google.gson.annotations.SerializedName
import java.io.Serial
//자신이 필요한 수신 데이터 계층 쓰기
//이런 형식으로 result 타입을 또 다른 데이터 클래스로 만듬으로서 계층구조를 형상화할 수 있습니다. / 서버에 보내는 값은 앞에 U를 붙였으며 dataClass의 이름은 해당 API 명세서를 따라갔습니다.
//최상위 계층, 수신



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


data class GetUserPlaylist(
    @SerializedName(value = "name") val name: String,
    @SerializedName(value = "imagePath") val imagePath: String,
    @SerializedName(value = "playlistId") val playlistId: Long,
    @SerializedName(value = "totalCast") val totalCast: Long

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

data class Script(
    @SerializedName(value = "originalSentence") val originalSentence: String,
    @SerializedName(value = "translatedSentence") val translatedSentence: String,
    @SerializedName(value = "timePoint") val timePoint: Float
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








