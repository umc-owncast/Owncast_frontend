package kr.dori.android.own_cast.player

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.forApiData.getRetrofit
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.session.MediaButtonReceiver
import kr.dori.android.own_cast.MainActivity
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.data.CastPlayerData

class BackgroundPlayService : Service() {

    private val binder = LocalBinder()
    private lateinit var player: ExoPlayer
    private val handler = Handler()
    private var loopStartTime: Long? = null
    private var loopEndTime: Long? = null
    private var isPlaying: Boolean = false  // 재생 상태를 추적하는 변수
    //알림창에서 백그라운드 재생 컨트롤을 위한 변수
    private val CHANNEL_ID = "BackgroundPlayServiceChannel"
    private lateinit var mediaSession: MediaSessionCompat

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
        // 알림창 설정
        mediaSession = MediaSessionCompat(this, "BackgroundPlayService")

        createNotificationChannel()  // Notification Channel 생성 호출 추가
    }


    inner class LocalBinder : Binder() {
        fun getService(): BackgroundPlayService = this@BackgroundPlayService
    }

    fun clearLoop() {
        loopStartTime = null
        loopEndTime = null
        handler.removeCallbacks(loopRunnable)
    }

    override fun onBind(intent: Intent?): IBinder = binder

    fun prepareAudio(url: String) {
        player.stop()  // 기존 재생 중지
        isPlaying = false
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        updateNotification()  // 알림 업데이트
    }

    fun isPlaying(): Boolean {
        return player.isPlaying
    }

    fun playAudio(url: String) {
        player.stop()  // 기존 재생 중지
        isPlaying = false  // 재생 상태를 갱신
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()  // 재생 시작
        isPlaying = true  // 재생 상태를 갱신
        ///startSeekBarUpdate()
        updatePlaybackState(true)
        updateNotification()  // 알림 업데이트
        startForeground(1, createNotification())

    }

    fun pauseAudio() {
        player.pause()
        isPlaying = false  // 재생 상태를 갱신
        updateNotification()  // 알림 업데이트
        updatePlaybackState(false)

    }

    fun resumeAudio() {
        player.play()
        isPlaying = true  // 재생 상태를 갱신
        updatePlaybackState(true)

    }

    fun stopAudio() {
        player.stop()  // 현재 재생 중인 음원을 중지
        isPlaying = false  // 재생 상태를 갱신
        updatePlaybackState(false)

    }

    // 반복 모드 설정
    fun setRepeatMode(repeatMode: Int) {
        player.repeatMode = repeatMode
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
        Log.d("ServiceState", "Current Player Position: ${player.currentPosition}")
        return player.currentPosition
    }


    fun getDuration(): Long {
        return player.duration
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

    /////알림창 컨트롤 함수들
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
                    Log.e(
                        "BackgroundPlayService",
                        "Failed to get cast info: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("BackgroundPlayService", "Exception occurred: ${e.localizedMessage}")
            }
        }
    }

    fun startForegroundService() {
        val notification = createNotification()
        startForeground(1, notification)
    }

    private fun updateNotification() {
        if (CastPlayerData.getAllCastList().isNullOrEmpty()) {
            stopForeground(true)  // 현재 재생 목록이 없으면 알림 제거
        } else {
            val notification = createNotification()
            startForeground(1, notification)  // 알림 생성 및 업데이트
        }
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "ACTION_PLAY" -> resumeAudio()
            "ACTION_PAUSE" -> pauseAudio()
            "ACTION_NEXT" -> playNextTrack()
            "ACTION_PREVIOUS" -> playPreviousTrack()
        }

        // API 레벨에 따라 startService 또는 startForegroundService 사용
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, BackgroundPlayService::class.java))
        } else {
            startService(Intent(this, BackgroundPlayService::class.java))
        }

        // 서비스가 포그라운드에서 실행되고 있음을 보장합니다.
        val notification = createNotification()
        startForeground(1, notification)

        return START_STICKY
    }



    private fun playNextTrack() {
        player.stop()  // 기존 재생 중지
        CastPlayerData.playNext()  // 다음 곡으로 이동
        val currentCast = CastPlayerData.currentCast
        getCastInfoAndPlay(currentCast.castId)  // 다음 곡 정보 가져와서 재생
        updateNotification()  // 알림 업데이트
    }

    private fun playPreviousTrack() {
        player.stop()  // 기존 재생 중지
        CastPlayerData.playPrevious()  // 이전 곡으로 이동
        val currentCast = CastPlayerData.currentCast
        getCastInfoAndPlay(currentCast.castId)  // 이전 곡 정보 가져와서 재생
        updateNotification()  // 알림 업데이트
    }

    private fun getCastInfoAndPlay(castId: Long) {
        val getCastInfo = getRetrofit().create(Playlist::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = getCastInfo.getCast(castId)
                if (response.isSuccessful) {
                    val castInfo = response.body()?.result
                    castInfo?.let {
                        val audioUrl = it.fileUrl ?: ""
                        val audioLength = parseTimeToSeconds(it.audioLength)  // 초 단위로 변환
                        Log.d("BackgroundPlayService", "Audio URL: $audioUrl, Length: $audioLength")

                        withContext(Dispatchers.Main) {
                            prepareAudio(audioUrl)
                            resumeAudio()
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

    private fun createNotification(): Notification {
        val playPauseIcon = if (player.isPlaying) {
            android.R.drawable.ic_media_pause
        } else {
            android.R.drawable.ic_media_play
        }

        val playPauseAction = if (player.isPlaying) {
            NotificationCompat.Action(
                playPauseIcon, "Pause",
                PendingIntent.getService(this, 0, Intent(this, BackgroundPlayService::class.java).apply {
                    action = "ACTION_PAUSE"
                }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            )
        } else {
            NotificationCompat.Action(
                playPauseIcon, "Play",
                PendingIntent.getService(this, 0, Intent(this, BackgroundPlayService::class.java).apply {
                    action = "ACTION_PLAY"
                }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            )
        }

        val nextAction = NotificationCompat.Action(
            android.R.drawable.ic_media_next, "Next",
            PendingIntent.getService(this, 0, Intent(this, BackgroundPlayService::class.java).apply {
                action = "ACTION_NEXT"
            }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        )

        val previousAction = NotificationCompat.Action(
            android.R.drawable.ic_media_previous, "Previous",
            PendingIntent.getService(this, 0, Intent(this, BackgroundPlayService::class.java).apply {
                action = "ACTION_PREVIOUS"
            }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        )

        val castTitle = CastPlayerData.currentCast?.castTitle ?: "No Title"
        val castCreator = CastPlayerData.currentCast?.castCreator?: "somebody"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.screen_two_logo_item)
            .setContentTitle(castTitle)
            .setContentText(castCreator)
            .setContentIntent(createContentIntent())
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(previousAction)
            .addAction(playPauseAction)  // Play/Pause 액션 추가
            .addAction(nextAction)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .build()
    }

    private fun createContentIntent(): PendingIntent {
        val openPlayCastActivityIntent = Intent(this, PlayCastActivity::class.java)
        openPlayCastActivityIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        return PendingIntent.getActivity(this, 0, openPlayCastActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE) // FLAG_IMMUTABLE 추가
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Background Play Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    // 상태 변경 시 알림 업데이트 호출
    private fun updatePlaybackState(isPlaying: Boolean) {
        this.isPlaying = isPlaying
        updateNotification()
    }




}