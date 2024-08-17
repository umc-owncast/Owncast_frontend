package kr.dori.android.own_cast.data

import kr.dori.android.own_cast.forApiData.Cast

object CastPlayerData {
    private val castList = mutableListOf<Cast>()
    private var currentIndex = -1
    var currentPosition: Long = 0L
    var playbackSpeed: Float = 1.0f

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
