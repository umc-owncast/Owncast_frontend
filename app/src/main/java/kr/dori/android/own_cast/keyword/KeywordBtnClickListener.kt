package kr.dori.android.own_cast.keyword

import kr.dori.android.own_cast.forApiData.PostCastByKeyword
import kr.dori.android.own_cast.forApiData.PostCastByScript

interface KeywordBtnClickListener {
    fun onButtonClick()

    fun createCastByScript(postCastByScript: PostCastByScript)

    fun createCastByKeyword(postCastByKeyword: PostCastByKeyword)
}