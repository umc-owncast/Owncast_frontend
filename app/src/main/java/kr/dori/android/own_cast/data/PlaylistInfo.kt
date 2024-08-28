package kr.dori.android.own_cast.data

/*
서버 배포용
data class SongData(var title: String,var Img: Int, var creator: String, var isLock: Boolean, var duration: Int, var Script: Int, var songURL: Int, var lyric: JsonReader,
 var isSave: Boolean, var category: String )
 */

//제목, 이미지, 생성한 유저, 공개유무, 길이, isSave, 카테고리
data class PlaylistInfo(
    val playlistId: Long,
    val playlistName: String,
    val imagePath: String
)