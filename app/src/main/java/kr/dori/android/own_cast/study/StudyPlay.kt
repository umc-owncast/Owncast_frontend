package kr.dori.android.own_cast.study

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
class AudioPlayer(private val context: Context) {

    interface AudioPlayerListener {
        fun onAudioPlayFinished()
    }

    var listener: AudioPlayerListener? = null

    private var player: ExoPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isLooping = false // 반복 재생을 위한 변수

    fun initializePlayer() {
        player = ExoPlayer.Builder(context).build()
    }

    fun releasePlayer() {
        player?.release()
        player = null
        handler.removeCallbacksAndMessages(null) // 모든 지연된 작업 제거
    }

    fun playAudio(url: String, start: Double, end: Double) {
        val dataSourceFactory = DefaultDataSource.Factory(context)
        val mediaItem = MediaItem.fromUri(url)
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaItem)

        player?.apply {
            setMediaSource(mediaSource)
            prepare()
            seekTo((start * 1000).toLong()) // 시작 지점으로 이동 (밀리초)
            playWhenReady = true

            // 종료 지점 설정
            val endPosition = (end * 1000).toLong()

            // 리스너를 사용하여 종료 지점에 도달했을 때 재생을 중지하거나 반복
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_READY) {
                        // 현재 재생 지점에서 종료 지점까지의 시간을 계산
                        val currentPosition = currentPosition
                        val duration = endPosition - currentPosition
                        if (duration > 0) {
                            handler.postDelayed({
                                if (isLooping) {
                                    seekTo((start * 1000).toLong())
                                    playWhenReady = true
                                } else {
                                    stopAudio()
                                    listener?.onAudioPlayFinished() // 재생 종료 알림
                                }
                            }, duration)
                        }
                    } else if (state == Player.STATE_ENDED) {
                        listener?.onAudioPlayFinished() // 재생이 종료되었을 때
                    }
                }
            })
        }
    }

    fun stopAudio() {
        player?.stop()
        listener?.onAudioPlayFinished() // 재생 중지 알림
    }

    fun setLooping(loop: Boolean) {
        isLooping = loop
    }
}
