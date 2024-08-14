package kr.dori.android.own_cast.forApiData

import com.google.gson.annotations.SerializedName

// 송신
data class UserPostPlaylist(
    @SerializedName("playlistName") val playlistName: String
)
//1계층
data class PatchCast(
    @SerializedName("saveInfo") val saveInfo: SaveInfo,
    @SerializedName("image") val image: String
)

data class PostCast(
    @SerializedName("updateInfo") val updateInfo: UpdateInfo,
    @SerializedName("image") val image: String
)

data class PostCastByScript(
    @SerializedName("script") val script: String,
    @SerializedName("formality") val formality: String,
    @SerializedName("voice") val voice: String
)

data class PostCastByKeyword(//KeywordCastCreationDTO
    @SerializedName("script") val script: String,
    @SerializedName("formality") val formality: String,
    @SerializedName("voice") val voice: String,
    @SerializedName("audioTime") val audioTime:Long
)

data class PostOtherPlaylistCast(//OtherCastRequestDTO
    @SerializedName("castId") val castId: Long,
    @SerializedName("playlistId") val playlistId: Long
)

//2계층
data class UpdateInfo(
    @SerializedName("title") val title: String,
    @SerializedName("imagePath") val imagePath: String,
    @SerializedName("isPublic") val isPublic: Boolean
)

data class SaveInfo(
    @SerializedName("title") val title: String,
    @SerializedName("playlistId") val imagePath: String,
    @SerializedName("isPublic") val isPublic: Boolean
)
