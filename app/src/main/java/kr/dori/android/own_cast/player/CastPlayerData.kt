package kr.dori.android.own_cast.data


import android.util.Log
import kr.dori.android.own_cast.forApiData.Cast
import kr.dori.android.own_cast.player.CastWithPlaylistId

object CastPlayerData {
    private val allCastList = mutableListOf<CastWithPlaylistId>()
    var currentPosition : Int = 0
    lateinit var currentCast: CastWithPlaylistId

    // 이 부분에 id검사 기능도 넣어야 됨
    fun setCast(testList: List<CastWithPlaylistId>) {
        allCastList.addAll(testList)

        // testPosition 계산 후 유효한지 확인합니다. -> position의 초기 설정입니다.
        currentPosition = allCastList.size - testList.size

        if (currentPosition in 0 until allCastList.size) {
            currentCast = allCastList[currentPosition]
            Log.d("test", "currentPosition: ${currentPosition}, receiveCastListSize: ${testList.size}, AllCastListSize: ${allCastList.size}, currentCast:${currentCast}, AllCast: ${allCastList}")
        } else {
            Log.e("test", "Invalid testPosition: $currentPosition, testList size: ${testList.size}, ${allCastList.size},${currentPosition}")
        }
    }


    fun playNext(): CastWithPlaylistId {
        if(currentPosition +1 in 0 until allCastList.size){
            currentPosition++
            currentCast = allCastList[currentPosition]

        }else{
            Log.e("playNext", "Next position is out of bounds: $currentPosition")
        }
        return currentCast
    }

    fun playPrevious(): CastWithPlaylistId {
        if(currentPosition -1 in 0 until allCastList.size){
            currentPosition--
            currentCast = allCastList[currentPosition]

        }else{
            Log.e("playNext", "Next position is out of bounds: $currentPosition")
        }
        return currentCast
    }


}
