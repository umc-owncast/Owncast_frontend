package kr.dori.android.own_cast.data


import android.util.Log
import kr.dori.android.own_cast.forApiData.Cast
import kr.dori.android.own_cast.player.CastWithPlaylistId

object CastPlayerData {
    private val allCastList = mutableListOf<CastWithPlaylistId>()
    var currentPosition : Int = 0
    lateinit var currentCast: CastWithPlaylistId

    private val castImagePath = mutableListOf<String>()

    var currentBookmarkList: List<Long> = emptyList()  // 기본값으로 초기화

    fun setCast(testList: List<CastWithPlaylistId>, position: Int) {
        allCastList.clear()
        allCastList.addAll(testList)

        if(position in 0 until testList.size){
            currentPosition = position
        }
        //currentPosition = allCastList.size - testList.size

        if (currentPosition in 0 until allCastList.size) {
            currentCast = allCastList[currentPosition]
            Log.d("test", "currentPosition: ${currentPosition}, receiveCastListSize: ${testList.size}, AllCastListSize: ${allCastList.size}, currentCast:${currentCast}, AllCast: ${allCastList}")
        } else {
            Log.e("test", "Invalid testPosition: $currentPosition, testList size: ${testList.size}, ${allCastList.size},${currentPosition}")
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

    fun setImagePath(list:List<String>){
        castImagePath.clear()
        castImagePath.addAll(list)
    }


}
