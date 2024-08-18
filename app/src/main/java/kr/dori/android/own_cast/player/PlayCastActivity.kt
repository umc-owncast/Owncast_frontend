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

            val currentCast = CastPlayerData.currentCast
            playCast(currentCast.castId)
            startSeekBarUpdate()

            //  startSeekBarUpdate() // 시크바 업데이트 시작
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


        // 처음 앱에 들어갔을 때의 초기 UI 설정
        binding.playCastPlayIv.visibility = View.VISIBLE
        binding.playCastPauseIv.visibility = View.GONE

        speedTableViewModel = ViewModelProvider(this).get(SpeedTableViewModel::class.java)

        // 배속 초기값 설정
        if (speedTableViewModel.data.value == null) {
            speedTableViewModel.setData(1.0f)
        }

        /* 캐스트 초기화 설정인데, ㅈ박았죠! -> 이유는 너가 멍청해서 그렇습니다~ -> 초기화 설정은 Service가 끝난후에 진행되어야 합니다. 따라서 connection 객체의 onServiceConnected 안에서 작동할 수 있도록 수정해야 합니다.
        var currentCast = CastPlayerData.currentCast
        playCast(currentCast.castId)
        startSeekBarUpdate()
         */

        // 새로운 액티비티가 시작될 때, 기존 재생 중지 -> 서비스 바인딩 전에 호출함으로서 기존에 재생된 음원 멈추기
        stopCurrentAudio()

        // 서비스 바인딩
        val intent = Intent(this, BackgroundPlayService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)

        // 초기 Fragment 설정
        supportFragmentManager.beginTransaction()
            .add(R.id.play_cast_frm, CastAudioFragment())
            .commit()

        // SeekBar 변경 리스너 설정 -> 사람이 seekbar를 움직였을때 기능합니다.
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && !isSeeking) {
                    service?.seekTo(progress * 1000L)
                    // CastPlayerData.updatePlaybackPosition(service?.getCurrentPosition() ?: 0L)
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
                //CastPlayerData.updatePlaybackPosition(service?.getCurrentPosition() ?: 0L)
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
        binding.next.setOnClickListener {
            binding.playCastPlayIv.visibility = View.GONE
            binding.playCastPauseIv.visibility = View.VISIBLE
            // service?.resumeAudio()
            val nextCast = CastPlayerData.playNext() // type: Cast
            nextCast?.let {
                stopCurrentAudio()  // 기존 음원 중지
                playCast(nextCast.castId)  // 새로운 캐스트 재생
                Log.d("test","currentPosition: ${CastPlayerData.currentPosition}, currentCast: ${CastPlayerData.currentCast}")
            }
        }

        // Previous 버튼 클릭 이벤트 처리
        binding.previous.setOnClickListener {
            binding.playCastPlayIv.visibility = View.GONE
            binding.playCastPauseIv.visibility = View.VISIBLE
            // service?.resumeAudio()
            val previousCast = CastPlayerData.playPrevious()
            previousCast?.let {
                stopCurrentAudio()  // 기존 음원 중지
                playCast(previousCast.castId)  // 새로운 캐스트 재생
                Log.d("test","currentPosition: ${CastPlayerData.currentPosition}, currentCast: ${CastPlayerData.currentCast}")
            }
        }

        binding.to10next.setOnClickListener {
            service?.seekTo(service?.getCurrentPosition()?.plus(10000L) ?: 0L)
        }

        binding.to10back.setOnClickListener {
            service?.seekTo(service?.getCurrentPosition()?.minus(10000L) ?: 0L)
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
            //CastPlayerData.playbackSpeed = value
        })

        clickImageView.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
                val speed = speeds[index]
                service?.setPlaybackSpeed(speed)
                binding.realSpeedTv.text = "${speed}x"
                updateSpeedUI(speed, targetView)
                //  CastPlayerData.playbackSpeed = speed
                speedTableViewModel.setData(speed) // ViewModel에 배속 값을 저장
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


        binding.speedTableOffIv.setOnClickListener {
            binding.speedTable.visibility = View.VISIBLE
            binding.speedTable.bringToFront()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        // 기존 재생 중지
        stopCurrentAudio()

        // 기존 UI 업데이트
        updateUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
        stopSeekBarUpdate()
    }

    private fun playCast(castId: Long) {
        service?.getCastInfo(castId) { url, audioLength ->
            url?.let {
                service?.playAudio(it)
                binding.endTv.text = formatTime(audioLength.toInt())
                binding.seekBar.max = audioLength // 시크바 최대값 설정 (초 단위)
                startSeekBarUpdate() // 시크바 업데이트 시작
            }
        }
    }

    private fun stopCurrentAudio() {
        // 서비스가 이미 바인딩 되어 있는지 확인하고 중지
        service?.let {
            it.stopAudio()
        }
    }


    private fun updateUI() {
        val currentCast = CastPlayerData.currentCast
        currentCast?.let {
            binding.seekBar.max = service?.getDuration()?.toInt()?.div(1000) ?: 0
            binding.seekBar.progress = (service?.getCurrentPosition()?.div(1000))?.toInt() ?: 0

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
                binding.seekBar.progress = (currentPosition / 1000).toInt() // 현재 위치를 초 단위로 설정
                binding.startTv.text = formatTime(currentPosition)
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
    override fun onResume() {
        super.onResume()
        updateUI() // UI 업데이트 시 현재 상태를 반영하여 조정
    }
    fun formatTime(input: Int): String {
        val minutes = input / 60
        val seconds = input % 60
        return String.format("%02d:%02d", minutes, seconds)
    }



}