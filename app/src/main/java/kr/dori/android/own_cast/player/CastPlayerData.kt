package kr.dori.android.own_cast.data


import android.util.Log
import kr.dori.android.own_cast.forApiData.Cast
import kr.dori.android.own_cast.player.CastWithPlaylistId

object CastPlayerData {
    private val allCastList = mutableListOf<CastWithPlaylistId>()
    var currentPosition : Int = 0
    lateinit var currentCast: CastWithPlaylistId
    var currentBookmarkList: List<Long> = emptyList()  // 기본값으로 초기화

    fun setCast(castList: List<CastWithPlaylistId>, position: Int) {
        allCastList.clear()
        allCastList.addAll(castList)

        if(position in 0 until castList.size){
            currentPosition = position
        }
        if (currentPosition in 0 until allCastList.size) {
            currentCast = allCastList[currentPosition]
            Log.d("test", "currentPosition: ${currentPosition}, receiveCastListSize: ${castList.size}, AllCastListSize: ${allCastList.size}, currentCast:${currentCast}, AllCast: ${allCastList}")
        } else {
            Log.e("test", "Invalid testPosition: $currentPosition, testList size: ${castList.size}, ${allCastList.size},${currentPosition}")
        }
    }

    fun swipeCast(castList: List<CastWithPlaylistId>) {
        allCastList.clear()
        allCastList.addAll(castList)

        // currentCast의 새로운 위치를 찾음
        currentPosition = allCastList.indexOfFirst { it.castId == currentCast.castId }
        Log.d("swipeCast", "Updated currentPosition: $currentPosition for currentCast with ID: ${currentCast.castId}")

        if (currentPosition == -1) {
            Log.e("swipeCast", "currentCast not found in updated list")
        }
    }


    fun getAllCastList() : MutableList<CastWithPlaylistId>{
        return allCastList
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

    fun setCurrentPos(id: Long){
        for(i:Int in 0 until allCastList.size){
            if (allCastList[i].castId == id){
                currentPosition = i
                currentCast = allCastList[i]
                return
            }
        }
    }

    fun setCurrentIndex(int: Int){
        currentPosition = int
        currentCast = allCastList[int]
    }


}
