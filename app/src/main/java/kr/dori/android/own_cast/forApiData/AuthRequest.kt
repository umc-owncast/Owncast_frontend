package kr.dori.android.own_cast.forApiData

import com.google.gson.annotations.SerializedName

// 송신
data class UserPostPlaylist(
    @SerializedName("playlistName") val playlistName: String
)
