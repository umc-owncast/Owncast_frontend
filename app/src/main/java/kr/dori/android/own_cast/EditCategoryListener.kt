package kr.dori.android.own_cast

interface EditCategoryListener {
    fun onCategoryEdit(position: Int, newItem:SongData)
    fun getCategoryData(position: Int): SongData
}