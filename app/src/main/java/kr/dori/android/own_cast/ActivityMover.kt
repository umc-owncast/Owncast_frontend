package kr.dori.android.own_cast

import kr.dori.android.own_cast.forApiData.Cast
import kr.dori.android.own_cast.player.CastWithPlaylistId


interface ActivityMover {
    fun ToPlayCast(castList: List<CastWithPlaylistId>)

    fun ToEditAudio()
//
//    fun ownCastToCast()
}






