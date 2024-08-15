package kr.dori.android.own_cast.playlist

import kr.dori.android.own_cast.data.SongData

interface EditCategoryListener {
    fun onCategoryEdit(position: Int, newItem: SongData)

    fun getCategoryData(position: Int): SongData

}