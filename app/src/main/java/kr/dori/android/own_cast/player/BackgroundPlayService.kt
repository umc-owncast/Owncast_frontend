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
    private var loopStartTime: Long? = null
    private var loopEndTime: Long? = null


    inner class LocalBinder : Binder() {
        fun getService(): BackgroundPlayService = this@BackgroundPlayService
    }

    fun clearLoop() {
        loopStartTime = null
        loopEndTime = null
        handler.removeCallbacks(loopRunnable)
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
    }

    fun prepareAudio(url: String) {
        player.stop()  // 기존 재생 중지
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()  // 오디오 준비
        // 재생은 하지 않음
    }

    fun isPlaying(): Boolean {
        return player.isPlaying
    }

    fun playAudio(url: String) {
        // 이전 재생을 중지하고 새로 재생
        player.stop()  // 기존 재생 중지
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()  // 재생 시작
        startSeekBarUpdate()
    }

    fun pauseAudio() {
        player.pause()
    }

    fun resumeAudio() {
        player.play()
    }

    // 반복 모드 설정
    fun setRepeatMode(repeatMode: Int) {
        player.repeatMode = repeatMode
    }

    fun stopAudio() {
        player.stop()  // 현재 재생 중인 음원을 중지
    }

    fun setLoopForSegment(startTimeMs: Long, endTimeMs: Long) {
        loopStartTime = startTimeMs
        loopEndTime = endTimeMs
        startLooping()
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

    fun getCastInfo(castId: Long, onInfoReceived: (String?, Int) -> Unit) {
        val getCastInfo = getRetrofit().create(Playlist::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = getCastInfo.getCast(castId)
                if (response.isSuccessful) {
                    val castInfo = response.body()?.result
                    castInfo?.let {
                        val audioUrl = it.fileUrl ?: ""
                        val audioLength = parseTimeToSeconds(it.audioLength) // 초 단위로 변환
                        Log.d("test", "${audioUrl}, ${audioLength}, ${it.audioLength}")

                        withContext(Dispatchers.Main) {
                            onInfoReceived(audioUrl, audioLength) // URL과 총 시간을 초 단위로 전달
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

    fun formatTime(input: String): String {
        return if (input.contains(":")) {
            // 입력이 이미 "분:초" 형식인 경우
            input
        } else {
            // 입력이 초 단위로 들어오는 경우
            val totalSeconds = input.toIntOrNull() ?: return "00:00" // 입력이 숫자가 아닌 경우 대비
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun parseTimeToSeconds(input: String): Int {
        return if (input.contains(":")) {
            // 입력이 "분:초" 형식인 경우
            val parts = input.split(":")
            val minutes = parts[0].toIntOrNull() ?: 0
            val seconds = parts[1].toIntOrNull() ?: 0
            (minutes * 60) + seconds
        } else {
            // 입력이 이미 초 단위인 경우
            input.toIntOrNull() ?: 0
        }
    }

    private fun startLooping() {
        handler.post(loopRunnable)
    }

    private val loopRunnable = object : Runnable {
        override fun run() {
            loopStartTime?.let { start ->
                loopEndTime?.let { end ->
                    if (player.currentPosition >= end) {
                        player.seekTo(start)
                    }
                }
            }
            handler.postDelayed(this, 100)  // 짧은 간격으로 반복 실행
        }
    }

}