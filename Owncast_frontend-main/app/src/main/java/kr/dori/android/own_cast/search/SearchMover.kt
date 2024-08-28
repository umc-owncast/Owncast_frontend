package kr.dori.android.own_cast.search

import kr.dori.android.own_cast.forApiData.CastHomeDTO

interface SearchMover {
    fun goPlayCast(list: List<CastHomeDTO>, id:Long)

    fun goAddCast( castHomeDTO: CastHomeDTO)

    fun backSearch()
}