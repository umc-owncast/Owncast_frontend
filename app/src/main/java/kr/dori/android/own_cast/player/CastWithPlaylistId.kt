package kr.dori.android.own_cast.player

import com.google.gson.annotations.SerializedName
import java.io.Serial
import java.io.Serializable

data class CastWithPlaylistId(
    val castId: Long,
    val playlistId: Long,
    val castTitle: String,
    val isPublic: Boolean,
    val castCreator: String,
    val castCategory: String,
    val audioLength: String,
    val imagePath: String
): Serializable
