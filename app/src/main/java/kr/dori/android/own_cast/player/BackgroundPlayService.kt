package kr.dori.android.own_cast.player

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.getRetrofit
class BackgroundPlayService : Service() {

    private val binder = LocalBinder()
    private lateinit var player: ExoPlayer
    private val handler = Handler()

    inner class LocalBinder : Binder() {
        fun getService(): BackgroundPlayService = this@BackgroundPlayService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
    }

    fun isPlaying(): Boolean {
        return player.isPlaying
    }

    fun playAudio(url: String) {
        player.stop()
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        startSeekBarUpdate()
    }

    fun pauseAudio() {
        player.pause()
    }

    fun resumeAudio() {
        player.play()
    }

    fun stopAudio() {
        player.stop()  // 현재 재생 중인 음원을 중지
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }

    fun setPlaybackSpeed(speed: Float) {
        player.playbackParameters = PlaybackParameters(speed)
    }

    fun getPlaybackSpeed(): Float {
        return player.playbackParameters.speed
    }

    fun getCurrentPosition(): Long {
        return player.currentPosition
    }

    fun getDuration(): Long {
        return player.duration
    }

    private fun startSeekBarUpdate() {
        handler.postDelayed(updateSeekBar, 1000)
    }

    private fun stopSeekBarUpdate() {
        handler.removeCallbacks(updateSeekBar)
    }

    private val updateSeekBar = object : Runnable {
        override fun run() {
            if (player.isPlaying) {
                val currentPosition = player.currentPosition
                // 이 위치에서 UI 업데이트를 Activity로 전송하는 로직 추가
            }
            handler.postDelayed(this, 1000)
        }
    }

    fun getCastInfo(castId: Long, onInfoReceived: (String?, Long) -> Unit) {
        val getCastInfo = getRetrofit().create(Playlist::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = getCastInfo.getCast(castId)
                if (response.isSuccessful) {
                    val castInfo = response.body()?.result
                    castInfo?.let {
                        val audioUrl = it.fileUrl
                        val audioLength = parseAudioLength(it.audioLength)
                        withContext(Dispatchers.Main) {
                            onInfoReceived(audioUrl, audioLength)
                        }
                    }
                } else {
                    Log.e("BackgroundPlayService", "Failed to get cast info: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("BackgroundPlayService", "Exception occurred: ${e.localizedMessage}")
            }
        }
    }

    private fun parseAudioLength(audioLength: String): Long {
        val parts = audioLength.split(":")
        return if (parts.size == 2) {
            val minutes = parts[0].toIntOrNull() ?: 0
            val seconds = parts[1].toIntOrNull() ?: 0
            (minutes * 60 + seconds) * 1000L
        } else {
            0L
        }
    }
}
