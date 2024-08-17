package kr.dori.android.own_cast.data

import android.util.Log
import kr.dori.android.own_cast.forApiData.Cast

object CastPlayerData {
    private val castList = mutableListOf<Cast>()
    private var currentIndex = -1
    var currentPosition: Long = 0L
    var playbackSpeed: Float = 1.0f

    private val test = mutableListOf<Cast>()
    var testPosition : Int = 0
    lateinit var testCast: Cast

    fun test(testList: List<Cast>) {
        test.addAll(testList)

        // testPosition 계산 후 유효한지 확인합니다.
        testPosition = test.size - testList.size

        if (testPosition in 0 until test.size) {
            testCast = test[testPosition]
            Log.d("test", "${testList.size}, ${test.size}, ${testCast}, ${test}")
        } else {
            Log.e("test", "Invalid testPosition: $testPosition, testList size: ${testList.size}, ${test.size},${testPosition}")
        }
    }


    val currentCast: Cast?
        get() = if (currentIndex in castList.indices) castList[currentIndex] else null

    fun setCastList(newCastList: List<Cast>) {
        castList.clear()
        castList.addAll(newCastList)
        currentIndex = 0
        resetPlaybackState()
    }

    fun addCast(cast: Cast) {
        if (!castList.contains(cast)) {
            castList.add(cast)
        }
        currentIndex = castList.indexOf(cast)
        resetPlaybackState()
    }

    fun playNext(): Cast? {
        return if (currentIndex + 1 in castList.indices) {
            currentIndex++
            resetPlaybackState()
            currentCast
        } else {
            null
        }
    }

    fun playPrevious(): Cast? {
        return if (currentIndex - 1 >= 0) {
            currentIndex--
            resetPlaybackState()
            currentCast
        } else {
            null
        }
    }

    fun updatePlaybackPosition(position: Long) {
        currentPosition = position
    }

    fun updatePlaybackSpeed(speed: Float) {
        playbackSpeed = speed
    }

    private fun resetPlaybackState() {
        currentPosition = 0L
        playbackSpeed = 1.0f
    }
}
