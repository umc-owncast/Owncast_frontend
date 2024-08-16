package kr.dori.android.own_cast.playlist

import kr.dori.android.own_cast.data.SongData
import kr.dori.android.own_cast.forApiData.GetAllPlaylist

interface EditCategoryListener {
    fun onCategoryEdit(position: Long, newItem: GetAllPlaylist)

    fun getCategoryData(position: Long): GetAllPlaylist

}