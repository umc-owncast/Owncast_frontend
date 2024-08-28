package kr.dori.android.own_cast.playlist

import kr.dori.android.own_cast.forApiData.GetAllPlaylist

interface EditCategoryListener {
    fun onCategoryEdit(position: Long, name: String, playlistId: Long)

    fun getCategoryData(position: Long): GetAllPlaylist

}