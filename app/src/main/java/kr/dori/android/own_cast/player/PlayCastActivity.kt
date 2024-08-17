package kr.dori.android.own_cast.player

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.datasource.DataSource
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.ActivityPlayCastBinding
import kr.dori.android.own_cast.forApiData.Cast
import kr.dori.android.own_cast.forApiData.CastInterface
import kr.dori.android.own_cast.getRetrofit
import java.io.InputStream
import java.util.concurrent.TimeUnit

class PlayCastActivity : AppCompatActivity() {
    private lateinit var player: ExoPlayer
    private lateinit var binding: ActivityPlayCastBinding
    private val playCastViewModel: PlayCastViewModel by viewModels { PlayCastViewModelFactory(application) }
    private lateinit var speedTableViewModel: SpeedTableViewModel
    private val handler = Handler(Looper.getMainLooper())
    private var isSeeking = false

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayCastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        player = ExoPlayer.Builder(this).build()
        player.volume = 1.0f

        //형 이 부분에서 클릭한 캐스트의 정보를 받아올 수 있어요
        val castList = intent.getSerializableExtra("CAST_ID") as? ArrayList<Cast>

        Log.d("Cast","$castList")
        castList?.let {
            // 각 Cast 객체의 castId를 추출하여 순차적으로 스트리밍을 시작합니다.
            for (cast in it) {
            val castId = cast.castId
            playCast(castId)
        }
        }


        speedTableViewModel = ViewModelProvider(this).get(SpeedTableViewModel::class.java)

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                Log.d("PlayerState", "Playback state changed: $state")
                when (state) {
                    Player.STATE_READY -> {
                        Log.d("PlayerState", "Player is ready to play")
                    }
                    Player.STATE_ENDED -> {
                        Log.d("PlayerState", "Playback ended")
                    }
                    Player.STATE_BUFFERING -> {
                        Log.d("PlayerState", "Buffering...")
                    }
                    Player.STATE_IDLE -> {
                        Log.d("PlayerState", "Player is idle")
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("PlayerError", "Error occurred during playback", error)
            }
        })

        val clickImageView = listOf(
            binding.activityCastSpeed05Iv,
            binding.activityCastSpeed075Iv,
            binding.activityCastSpeed10Iv,
            binding.activityCastSpeed125Iv,
            binding.activityCastSpeed15Iv,
            binding.activityCastSpeed175Iv,
            binding.activityCastSpeed20Iv
        )

        val targetView = listOf(
            binding.activityCastSpeedOn05Iv,
            binding.activityCastSpeedOn075Iv,
            binding.activityCastSpeedOn10Iv,
            binding.activityCastSpeedOn125Iv,
            binding.activityCastSpeedOn15Iv,
            binding.activityCastSpeedOn175Iv,
            binding.activityCastSpeedOn20Iv,
            binding.activityCastSpeedOn05Tv,
            binding.activityCastSpeedOn075Tv,
            binding.activityCastSpeedOn10Tv,
            binding.activityCastSpeedOn125Tv,
            binding.activityCastSpeedOn15Tv,
            binding.activityCastSpeedOn175Tv,
            binding.activityCastSpeedOn20Tv
        )

        // ExoPlayer 리스너를 추가하여 준비 완료와 상태 변화를 감지
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    val duration = player.duration
                    binding.seekBar.max = (duration / 1000).toInt()
                    binding.endTv.text = formatTime(duration)
                    startSeekBarUpdate() // 플레이어가 준비되면 SeekBar 업데이트 시작
                }
            }

            override fun onPositionDiscontinuity(reason: Int) {
                updateLyricsHighlight()
            }
        })

        // SeekBar 변경 리스너 설정
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && !isSeeking) {
                    player.seekTo(progress * 1000L)
                    binding.startTv.text = formatTime(player.currentPosition)
                    updateLyricsHighlight()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isSeeking = false
                player.seekTo(seekBar?.progress?.times(1000L) ?: 0L)
                binding.startTv.text = formatTime(player.currentPosition)
                updateLyricsHighlight()
            }
        })

        // Play 버튼 클릭 이벤트 처리
        binding.playCastPlayIv.setOnClickListener {
            binding.playCastPlayIv.visibility = View.GONE
            binding.playCastPauseIv.visibility = View.VISIBLE
            player.play()
        }

        // Pause 버튼 클릭 이벤트 처리
        binding.playCastPauseIv.setOnClickListener {
            binding.playCastPauseIv.visibility = View.GONE
            binding.playCastPlayIv.visibility = View.VISIBLE
            player.pause()
        }

        // 배속 설정 이벤트 리스너
        clickImageView.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
                val speed = speeds[index]
                player.playbackParameters = PlaybackParameters(speed)
                binding.realSpeedTv.text = "${speed}x"
                updateSpeedUI(speed, targetView)
            }
        }

        // Fragment 초기화
        supportFragmentManager.beginTransaction()
            .add(R.id.play_cast_frm, CastAudioFragment())
            .commit()

        // LiveData 관찰
        speedTableViewModel.data.observe(this, Observer { value ->
            binding.realSpeedTv.text = formatSpeed(value.toString())
            Log.d("speed", "$value")
            targetView.forEach { listener ->
                val viewId = resources.getResourceEntryName(listener.id)
                val regex = "_(\\d+\\.\\d+|\\d+_\\d+)_".toRegex()
                val matchResult = regex.find(viewId)

                if (matchResult != null) {
                    val speed = matchResult.groupValues[1].replace("_", ".").toFloat()
                    if (speed == value) {
                        listener.visibility = View.VISIBLE
                    } else {
                        listener.visibility = View.GONE
                    }
                }
            }
        })

        // 10초 전으로 이동하는 버튼 클릭 이벤트 처리
        binding.to10back.setOnClickListener {
            val currentPosition = player.currentPosition
            player.seekTo((currentPosition - 10000).coerceAtLeast(0))
            updateLyricsHighlight()
        }

        // 10초 후로 이동하는 버튼 클릭 이벤트 처리
        binding.to10next.setOnClickListener {
            val currentPosition = player.currentPosition
            player.seekTo((currentPosition + 10000).coerceAtMost(player.duration))
            updateLyricsHighlight()
        }

        // 데이터 초기 설정
        if (speedTableViewModel.data.value == null) {
            speedTableViewModel.setData(1.0f)
        }

        // speed_table visible 설정
        binding.speedTableOffIv.setOnClickListener {
            binding.speedTable.visibility = View.VISIBLE
            binding.speedTable.bringToFront()
            binding.speedTable.invalidate()
            binding.speedTable.requestLayout()
            binding.realSpeedTv.setTextColor(Color.parseColor("#8050F2"))
        }

        binding.speedTableExitIv.setOnClickListener {
            binding.speedTable.visibility = View.GONE
            binding.activityPlayCastSpeedOn.visibility = View.GONE
            binding.realSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.black))
        }

        // fragment 이동 부분
        binding.activityPlayCastScriptOffIv.setOnClickListener {
            audioToScript()
        }

        binding.activityPlayCastPlaylistOffIv.setOnClickListener {
            audioToPlaylist()
        }

        binding.activityPlayCastPlaylistOnIv.setOnClickListener {
            playlistToAudio()
        }

        binding.activityPlayCastScriptOnIv.setOnClickListener {
            scriptToAudio()
        }

        binding.playCastLoofOffIv.setOnClickListener {
            binding.playCastLoofOffIv.visibility = View.GONE
            binding.playCastLoofOnIv.visibility = View.VISIBLE
        }

        binding.playCastLoofOnIv.setOnClickListener {
            binding.playCastLoofOffIv.visibility = View.VISIBLE
            binding.playCastLoofOnIv.visibility = View.GONE
        }

        binding.playcastActivitySaveBackIv.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("result", true)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.activityPlayCastAudioExitIv.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("result", false)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.activityPlayCastNotAudioExit.setOnClickListener {
            scriptToAudio()
        }
    }
    private fun playCast(castId: Long){
        val getCastInfo = getRetrofit().create(CastInterface::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val playResponse = getCastInfo.getCastPlay(castId)
                if (playResponse.isSuccessful) {
                    playResponse.body()?.let { responseBody ->
                        val stream = responseBody.byteStream()
                        withContext(Dispatchers.Main) {
                            player.playStream(stream)
                        }
                    }
                } else {
                    Log.e("player", "Failed to get cast play: ${playResponse.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("player", "Exception during playback setup", e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release() // ExoPlayer 자원 해제
        stopSeekBarUpdate()
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
                binding.seekBar.progress = (currentPosition / 1000).toInt()
                binding.startTv.text = formatTime(currentPosition)
                updateLyricsHighlight()
            }
            handler.postDelayed(this, 1000)
        }
    }

    private fun formatTime(ms: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ms)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateSpeedUI(selectedSpeed: Float, views: List<View>) {
        views.forEach { view ->
            view.visibility = View.GONE
            binding.speedTable.visibility = View.GONE
            binding.realSpeedTv.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
        when (selectedSpeed) {
            0.5f -> {
                binding.activityCastSpeedOn05Iv.visibility = View.VISIBLE
                binding.activityCastSpeedOn05Tv.visibility = View.VISIBLE
            }
            0.75f -> {
                binding.activityCastSpeedOn075Iv.visibility = View.VISIBLE
                binding.activityCastSpeedOn075Tv.visibility = View.VISIBLE
            }
            1.0f -> {
                binding.activityCastSpeedOn10Iv.visibility = View.VISIBLE
                binding.activityCastSpeedOn10Tv.visibility = View.VISIBLE
            }
            1.25f -> {
                binding.activityCastSpeedOn125Iv.visibility = View.VISIBLE
                binding.activityCastSpeedOn125Tv.visibility = View.VISIBLE
            }
            1.5f -> {
                binding.activityCastSpeedOn15Iv.visibility = View.VISIBLE
                binding.activityCastSpeedOn15Tv.visibility = View.VISIBLE
            }
            1.75f -> {
                binding.activityCastSpeedOn175Iv.visibility = View.VISIBLE
                binding.activityCastSpeedOn175Tv.visibility = View.VISIBLE
            }
            2.0f -> {
                binding.activityCastSpeedOn20Iv.visibility = View.VISIBLE
                binding.activityCastSpeedOn20Tv.visibility = View.VISIBLE
            }
        }
    }



    private fun updateLyricsHighlight() {
        val fragment = supportFragmentManager.findFragmentById(R.id.play_cast_frm) as? CastScriptFragment
        fragment?.let {
            (it.binding.scriptRv.adapter as? ScriptAdapter)?.updateCurrentTime(player.currentPosition)
        }
    }

    fun audioToScript() {
        binding.activityPlayCastPlaylistOnIv.visibility = View.GONE
        binding.activityPlayCastPlaylistOffIv.visibility = View.VISIBLE
        binding.activityPlayCastScriptOnIv.visibility = View.VISIBLE
        binding.activityPlayCastScriptOffIv.visibility = View.GONE
        binding.activityPlayCastAudioExitIv.visibility = View.GONE
        binding.activityPlayCastNotAudioExit.visibility = View.VISIBLE
        binding.playcastActivitySaveBackIv.visibility = View.GONE

        supportFragmentManager.beginTransaction()
            .replace(R.id.play_cast_frm, CastScriptFragment())
            .commitAllowingStateLoss()
    }

    fun scriptToAudio() {
        binding.activityPlayCastPlaylistOnIv.visibility = View.GONE
        binding.activityPlayCastPlaylistOffIv.visibility = View.VISIBLE
        binding.activityPlayCastScriptOnIv.visibility = View.GONE
        binding.activityPlayCastScriptOffIv.visibility = View.VISIBLE
        binding.activityPlayCastAudioExitIv.visibility = View.VISIBLE
        binding.activityPlayCastNotAudioExit.visibility = View.GONE
        binding.playcastActivitySaveBackIv.visibility = View.VISIBLE

        supportFragmentManager.beginTransaction()
            .replace(R.id.play_cast_frm, CastAudioFragment())
            .commitAllowingStateLoss()
    }

    fun audioToPlaylist() {
        binding.activityPlayCastPlaylistOnIv.visibility = View.VISIBLE
        binding.activityPlayCastPlaylistOffIv.visibility = View.GONE
        binding.activityPlayCastScriptOnIv.visibility = View.GONE
        binding.activityPlayCastScriptOffIv.visibility = View.VISIBLE
        binding.activityPlayCastAudioExitIv.visibility = View.GONE
        binding.activityPlayCastNotAudioExit.visibility = View.VISIBLE
        binding.playcastActivitySaveBackIv.visibility = View.GONE
        supportFragmentManager.beginTransaction()
            .replace(R.id.play_cast_frm, CastPlaylistFragment())
            .commitAllowingStateLoss()
    }

    fun playlistToAudio() {
        binding.activityPlayCastPlaylistOnIv.visibility = View.GONE
        binding.activityPlayCastPlaylistOffIv.visibility = View.VISIBLE
        binding.activityPlayCastScriptOnIv.visibility = View.GONE
        binding.activityPlayCastScriptOffIv.visibility = View.VISIBLE
        binding.activityPlayCastAudioExitIv.visibility = View.VISIBLE
        binding.activityPlayCastNotAudioExit.visibility = View.GONE
        binding.playcastActivitySaveBackIv.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.play_cast_frm, CastAudioFragment())
            .commitAllowingStateLoss()
    }

    fun formatSpeed(speed: String): String {
        return if (speed.endsWith(".0")) {
            speed.replace(".0", "") + "x"
        } else {
            speed + "x"
        }
    }
}

@OptIn(UnstableApi::class)
private fun ExoPlayer.setMediaSource(mediaSource: MediaSource, startPositionMs: Long = 0L) {
    // ExoPlayer의 기본 메서드를 호출하도록 수정
    this.setMediaSource(mediaSource, startPositionMs)
}

@OptIn(UnstableApi::class)
private fun createMediaSource(factory: ProgressiveMediaSource.Factory, mediaItem: MediaItem): MediaSource {
    // ProgressiveMediaSource.Factory를 사용하여 MediaSource 생성
    return factory.createMediaSource(mediaItem)
}

@OptIn(UnstableApi::class)
private fun ExoPlayer.playStream(inputStream: InputStream) {
    val dataSourceFactory = DataSource.Factory { InputStreamDataSource(inputStream) }
    val mediaSource = createMediaSource(
        ProgressiveMediaSource.Factory(dataSourceFactory),
        MediaItem.Builder().setUri(Uri.EMPTY).build()
    )

    // 미디어 소스를 설정하고 재생 준비
    this.setMediaSource(mediaSource)
    this.prepare()
    this.playWhenReady = true
}





