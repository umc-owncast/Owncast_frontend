package kr.dori.android.own_cast.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.data.CastPlayerData
import kr.dori.android.own_cast.databinding.ActivityPlayCastBinding
import kr.dori.android.own_cast.forApiData.Cast
import java.util.concurrent.TimeUnit

class PlayCastActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayCastBinding
    private val playCastViewModel: PlayCastViewModel by viewModels { PlayCastViewModelFactory(application) }
    private lateinit var speedTableViewModel: SpeedTableViewModel
    private var isSeeking = false
    private var service: BackgroundPlayService? = null
    private var isBound = false

    private val handler = Handler()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            val localBinder = binder as BackgroundPlayService.LocalBinder
            service = localBinder.getService()
            isBound = true
            initializePlayer()
            startSeekBarUpdate() // 시크바 업데이트 시작
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            service = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayCastBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        speedTableViewModel = ViewModelProvider(this).get(SpeedTableViewModel::class.java)

        // 배속 초기값 설정
        if (speedTableViewModel.data.value == null) {
            speedTableViewModel.setData(1.0f)
        }

        // 서비스 바인딩
        val intent = Intent(this, BackgroundPlayService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)

        // 초기 Fragment 설정
        supportFragmentManager.beginTransaction()
            .add(R.id.play_cast_frm, CastAudioFragment())
            .commit()

        // SeekBar 변경 리스너 설정
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && !isSeeking) {
                    service?.seekTo(progress * 1000L)
                    CastPlayerData.updatePlaybackPosition(service?.getCurrentPosition() ?: 0L)
                    binding.startTv.text = formatTime(service?.getCurrentPosition() ?: 0L)
                    updateLyricsHighlight()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isSeeking = false
                service?.seekTo(seekBar?.progress?.times(1000L) ?: 0L)
                CastPlayerData.updatePlaybackPosition(service?.getCurrentPosition() ?: 0L)
                binding.startTv.text = formatTime(service?.getCurrentPosition() ?: 0L)
                updateLyricsHighlight()
            }
        })

        // Play 버튼 클릭 이벤트 처리
        binding.playCastPlayIv.setOnClickListener {
            binding.playCastPlayIv.visibility = View.GONE
            binding.playCastPauseIv.visibility = View.VISIBLE
            service?.resumeAudio()
        }

        // Pause 버튼 클릭 이벤트 처리
        binding.playCastPauseIv.setOnClickListener {
            binding.playCastPauseIv.visibility = View.GONE
            binding.playCastPlayIv.visibility = View.VISIBLE
            service?.pauseAudio()
        }

        // Next 버튼 클릭 이벤트 처리
        binding.to10next.setOnClickListener {
            val nextCast = CastPlayerData.playNext()
            nextCast?.let {
                stopCurrentAudio()  // 기존 음원 중지
                playCast(nextCast)  // 새로운 캐스트 재생
            }
        }

        // Previous 버튼 클릭 이벤트 처리
        binding.to10back.setOnClickListener {
            val previousCast = CastPlayerData.playPrevious()
            previousCast?.let {
                stopCurrentAudio()  // 기존 음원 중지
                playCast(previousCast)  // 새로운 캐스트 재생
            }
        }

        // 배속 설정 이벤트 리스너
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

        speedTableViewModel.data.observe(this, Observer { value ->
            binding.realSpeedTv.text = formatSpeed(value.toString())
            Log.d("speed", "$value")
            updateSpeedUI(value, targetView)
            service?.setPlaybackSpeed(value)
            CastPlayerData.playbackSpeed = value
        })

        clickImageView.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
                val speed = speeds[index]
                service?.setPlaybackSpeed(speed)
                binding.realSpeedTv.text = "${speed}x"
                updateSpeedUI(speed, targetView)
                CastPlayerData.playbackSpeed = speed
            }
        }

        // Fragment 전환 부분 추가
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
            service?.pauseAudio() // 음원 재생 중지
            finish() // Activity 종료
        }

        binding.activityPlayCastNotAudioExit.setOnClickListener {
            scriptToAudio()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        updateUI() // 기존 Activity로 돌아올 때 UI 업데이트
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
        stopSeekBarUpdate()
    }

    private fun initializePlayer() {
        val currentCast = CastPlayerData.currentCast

        if (currentCast != null) {
            // 현재 재생 중인 캐스트가 있는 경우
            service?.getCastInfo(currentCast.castId) { url, audioLength ->
                url?.let {
                    service?.playAudio(it)
                    binding.endTv.text = formatTime(audioLength)
                    binding.seekBar.max = (audioLength / 1000).toInt()
                    CastPlayerData.currentPosition = service?.getCurrentPosition() ?: 0L
                    CastPlayerData.playbackSpeed = service?.getPlaybackSpeed() ?: 1.0f
                    updateUI()
                }
            }
        } else {
            // 두 번째 케이스: 다른 캐스트를 클릭한 경우
            playCast(CastPlayerData.currentCast!!)
        }
    }

    private fun playCast(cast: Cast) {
        CastPlayerData.addCast(cast)
        service?.getCastInfo(cast.castId) { url, audioLength ->
            url?.let {
                service?.playAudio(it)
                binding.endTv.text = formatTime(audioLength)
                binding.seekBar.max = (audioLength / 1000).toInt()
            }
        }
    }


    private fun stopCurrentAudio() {
        service?.stopAudio()  // 새로운 메서드를 BackgroundPlayService에 추가
    }


    private fun updateUI() {
        val currentCast = CastPlayerData.currentCast
        if (currentCast != null) {
            //binding.ti.text = currentCast.castTitle
            binding.seekBar.max = (service?.getDuration() ?: 0L / 1000).toInt()
            binding.seekBar.progress = (CastPlayerData.currentPosition / 1000).toInt()
            binding.realSpeedTv.text = "${CastPlayerData.playbackSpeed}x"

            if (service?.isPlaying() == true) {
                binding.playCastPlayIv.visibility = View.GONE
                binding.playCastPauseIv.visibility = View.VISIBLE
            } else {
                binding.playCastPlayIv.visibility = View.VISIBLE
                binding.playCastPauseIv.visibility = View.GONE
            }
        }
    }

    private fun startSeekBarUpdate() {
        handler.postDelayed(updateSeekBar, 1000)
    }

    private fun stopSeekBarUpdate() {
        handler.removeCallbacks(updateSeekBar)
    }

    private val updateSeekBar = object : Runnable {
        override fun run() {
            service?.let {
                val currentPosition = it.getCurrentPosition()
                binding.seekBar.progress = (currentPosition / 1000).toInt()
                binding.startTv.text = formatTime(currentPosition)
                //updateLyricsHighlight()
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
            (it.binding.scriptRv.adapter as? ScriptAdapter)?.updateCurrentTime(service?.getCurrentPosition() ?: 0L)
        }
    }

    // Fragment 전환 함수들
    private fun audioToScript() {
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

    private fun scriptToAudio() {
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

    private fun audioToPlaylist() {
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

    private fun playlistToAudio() {
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
