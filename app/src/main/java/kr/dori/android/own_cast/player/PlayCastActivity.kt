package kr.dori.android.own_cast.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.media3.exoplayer.ExoPlayer
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.SignupData
import kr.dori.android.own_cast.data.CastPlayerData
import kr.dori.android.own_cast.databinding.ActivityPlayCastBinding
import kr.dori.android.own_cast.forApiData.AuthResponse
import kr.dori.android.own_cast.forApiData.Cast
import kr.dori.android.own_cast.forApiData.CastInterface
import kr.dori.android.own_cast.forApiData.DeleteOther
import kr.dori.android.own_cast.forApiData.DeleteOtherDto
import kr.dori.android.own_cast.forApiData.ErrorResponse
import kr.dori.android.own_cast.forApiData.GetUserPlaylist
import kr.dori.android.own_cast.forApiData.PlayListInterface
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.forApiData.getRetrofit
import kr.dori.android.own_cast.keyword.PlaylistText
import kr.dori.android.own_cast.search.SearchAddCategoryActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class PlayCastActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayCastBinding
    private lateinit var speedTableViewModel: SpeedTableViewModel
    private var isSeeking = false
    private var service: BackgroundPlayService? = null
    private var isBound = false
    private val seekBarHandler = Handler()
    private val scriptHandler = Handler() // ScriptFragment 업데이트용 핸들러

    var stateListener: Int = 0

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            val localBinder = binder as BackgroundPlayService.LocalBinder
            service = localBinder.getService()
            isBound = true

            service?.setPlaybackSpeed(1.0f)
            val currentCast = CastPlayerData.currentCast

            // MainActivity에서 넘어온 경우 재생 상태를 유지
            if (intent.hasExtra("fromMainActivity") && intent.getBooleanExtra("fromMainActivity", false)) {
                updateUI() // 현재 재생 상태에 맞게 UI 업데이트
                service?.getCastInfo(currentCast.castId) { url, audioLength ->
                    url?.let {
                        binding.endTv.text = formatTime(audioLength.toInt())
                    }
                }

            } else {
                // 새로운 캐스트를 준비
                stopCurrentAudio()
                disableLoopForSentence() //다시 돌아왔을 때 루프 러너블 객체 삭제해서 버그 줄임
                playCast(currentCast.castId)
            }

            startSeekBarUpdate() // 시크바 업데이트 시작
            startScriptFragmentUpdate() // ScriptFragment 업데이트 시작

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
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        // 처음 앱에 들어갔을 때의 초기 UI 설정
        binding.playCastPlayIv.visibility = View.VISIBLE
        binding.playCastPauseIv.visibility = View.GONE
        classifyCast()

        //playlistId가 -1이고 유저와 생성한 사람의 이름이 다르면 add가 뜸, 어 근데 이미 추가 된거면? -> 삭제되게


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
            .add(R.id.play_cast_frm, CastAudioFragment(CastPlayerData.currentCast))
            .commit()


        // SeekBar 변경 리스너 설정 -> 사람이 seekbar를 움직였을때 기능합니다. -> getCurrentPosition을 통해서 현재 진행사황을 서비스에서 가져옵니다.
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && !isSeeking) {
                    service?.seekTo(progress * 1000L)
                    // CastPlayerData.updatePlaybackPosition(service?.getCurrentPosition() ?: 0L)
                    binding.startTv.text = formatTime(service?.getCurrentPosition() ?: 0L)
                    //updateLyricsHighlight()
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

                //  updateLyricsHighlight()

            }
        })

        // 카테고리 추가 버튼
        binding.addCategoryOffBtn.setOnClickListener{
            addCast()

        }
        // 카테고리 해제 버튼
        binding.addCategoryOnBtn.setOnClickListener {
            deleteCast()

        }

        // Play 버튼 클릭 이벤트 처리
        binding.playCastPlayIv.setOnClickListener {
            binding.playCastPlayIv.visibility = View.GONE
            binding.playCastPauseIv.visibility = View.VISIBLE
            service?.resumeAudio()
            Log.d("test","현재 캐스트: ${CastPlayerData.currentCast}, 현재 위치: ${CastPlayerData.currentPosition},총 개수: ${CastPlayerData.getAllCastList().size}, 총 캐스트: ${CastPlayerData.getAllCastList()}")
        }

        // Pause 버튼 클릭 이벤트 처리
        binding.playCastPauseIv.setOnClickListener {
            binding.playCastPauseIv.visibility = View.GONE
            binding.playCastPlayIv.visibility = View.VISIBLE
            service?.pauseAudio()
        }

        // Next 버튼 클릭 이벤트 처리
        binding.next.setOnClickListener {
            CastPlayerData.playNext()
            CastPlayerData.currentCast?.let {
                newCast(CastPlayerData.currentCast.castId)  // 새로운 캐스트 재생 및 UI 초기화
                Log.d("test", "currentPosition: ${CastPlayerData.currentPosition}, currentCast: ${CastPlayerData.currentCast}")
                classifyCast() // 추가 안한 캐스트면 창뜨게 해야함
            }
        }

        // Previous 버튼 클릭 이벤트 처리
        binding.previous.setOnClickListener {
            CastPlayerData.playPrevious()
            CastPlayerData.currentCast?.let {
                newCast(CastPlayerData.currentCast.castId)  // 새로운 캐스트 재생 및 UI 초기화
                Log.d("test", "currentPosition: ${CastPlayerData.currentPosition}, currentCast: ${CastPlayerData.currentCast}")
                classifyCast()
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

        binding.playCastLoofOnIv.setOnClickListener {
            binding.playCastLoofOffIv.visibility = View.VISIBLE
            binding.playCastLoofOnIv.visibility = View.GONE
            service?.setRepeatMode(ExoPlayer.REPEAT_MODE_OFF) // 반복 모드 해제
        }

        binding.playCastLoofOffIv.setOnClickListener {
            binding.playCastLoofOffIv.visibility = View.GONE
            binding.playCastLoofOnIv.visibility = View.VISIBLE
            service?.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE) // 현재 트랙 반복 모드로 설정
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

    fun classifyCast(){//이게 자기 캐스트인지 담아온 캐스트인지 구분 플레이리스트 id가 -1이면 안담긴걸로 생각
        if(CastPlayerData.currentCast.playlistId == -1L&&!SignupData.nickname.equals(CastPlayerData.currentCast.castCreator)){
            binding.addCategoryOffBtn.visibility = View.VISIBLE
        }else if(CastPlayerData.currentCast.playlistId != -1L&&!SignupData.nickname.equals(CastPlayerData.currentCast.castCreator)){
            binding.addCategoryOnBtn.visibility =View.VISIBLE
        }else{
            binding.addCategoryOnBtn.visibility =View.GONE
            binding.addCategoryOffBtn.visibility = View.GONE
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
                service?.prepareAudio(it) // 여기서 prepare만 수행
                binding.endTv.text = formatTime(audioLength.toInt())
                binding.seekBar.max = audioLength // 시크바 최대값 설정 (초 단위)
                startSeekBarUpdate() // 시크바 업데이트 시작
                startScriptFragmentUpdate() // ScriptFragment 업데이트 시작
                saveState()
            }
        }
    }


    /*
    private fun newCast(castId: Long) {
        Log.d("newCast", "Starting new cast with ID: $castId")

        service?.getCastInfo(castId) { url, audioLength ->
            url?.let {
                stopCurrentAudio()
                Log.d("newCast", "Stopping current audio and updating UI.")
               // stopSeekBarUpdate() // 기존 SeekBar 업데이트 중지
                service?.prepareAudio(it).let {
                    service?.resumeAudio()
                    Log.d("newCast", "Preparing new audio and starting updates.")
                  //  startSeekBarUpdate() // 새로운 캐스트 준비 후 SeekBar 업데이트 재개
                    startScriptFragmentUpdate()
                    updateUI()
                    saveState()
                    binding.playCastPauseIv.visibility = View.VISIBLE
                    binding.playCastPlayIv.visibility = View.GONE
                    Log.d("newCast", "New audio started playing.")
                }
            }
        }
    }

     */
    private fun newCast(castId: Long) {
        Log.d("newCast", "Starting new cast with ID: $castId")

        service?.getCastInfo(castId) { url, audioLength ->
            url?.let {
                stopCurrentAudio()
                Log.d("newCast", "Stopping current audio and updating UI.")
                service?.prepareAudio(it).let {
                    service?.resumeAudio()
                    Log.d("newCast", "Preparing new audio and starting updates.")
                    startScriptFragmentUpdate()
                    updateUI()

                    // 시크바 업데이트 코드 추가
                    binding.seekBar.max = audioLength  // 시크바 최대값 설정
                    binding.seekBar.progress = 0  // 시크바 초기값 설정

                    saveState()
                    binding.playCastPauseIv.visibility = View.VISIBLE
                    binding.playCastPlayIv.visibility = View.GONE
                    Log.d("newCast", "New audio started playing.")
                }
            }
        }
    }


    private fun stopCurrentAudio() {
        // 서비스가 이미 바인딩 되어 있는지 확인하고 중지
        Log.d("test","none service")

        service?.let {
            it.stopAudio()
            it.pauseAudio()
            Log.d("test","연결이 성공적으로 끊어졌습니다")
        }
    }


    //여기서 설정하는 progress와 startTv는 일회용이고, 지속적인 업데이트를 위해선 러너블 객체를 사용해야 함
    private fun updateUI() {
        var currentCast = CastPlayerData.currentCast
        currentCast?.let {
            val currentPosition = service?.getCurrentPosition() ?: 0L

            binding.seekBar.max = service?.getDuration()?.toInt()?.div(1000) ?: 0
            binding.seekBar.progress = (currentPosition.div(1000)).toInt()
            binding.endTv.text = currentCast.audioLength
            binding.startTv.text = formatTime(currentPosition)

            if (service?.isPlaying() == true) {
                binding.playCastPlayIv.visibility = View.GONE
                binding.playCastPauseIv.visibility = View.VISIBLE
            } else {
                binding.playCastPlayIv.visibility = View.VISIBLE
                binding.playCastPauseIv.visibility = View.GONE
            }

            // CastAudioFragment 텍스트 값 업데이트
        }
    }


    private fun startSeekBarUpdate() {
        Log.d("SeekBarUpdate", "Starting SeekBar update.")
        seekBarHandler.postDelayed(updateSeekBar, 10)
    }

    private fun stopSeekBarUpdate() {
        Log.d("SeekBarUpdate", "Stopping SeekBar update.")
        seekBarHandler.removeCallbacks(updateSeekBar)
    }


    private val updateSeekBar = object : Runnable {
        override fun run() {
            service?.let {
                val currentPosition = it.getCurrentPosition()
                Log.d("SeekbarUpdateReal", "Current Position: $currentPosition")

                binding.seekBar.progress = (currentPosition.div(1000)).toInt()
                binding.startTv.text = formatTime(currentPosition)

                // CastScriptFragment에 시간 정보 전달
                val fragment = supportFragmentManager.findFragmentById(R.id.play_cast_frm)
                if (fragment is CastScriptFragment && fragment.isAdded) {
                    fragment.updateCurrentTime(currentPosition)
                    Log.d("UpdateTime", "Activity: $currentPosition")
                }
            }
            seekBarHandler.postDelayed(this, 300) // 주기적으로 업데이트
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

    // Fragment 전환 함수들
    private fun audioToScript() {
        stopScriptFragmentUpdate()
        binding.activityPlayCastPlaylistOnIv.visibility = View.GONE
        binding.activityPlayCastPlaylistOffIv.visibility = View.VISIBLE
        binding.activityPlayCastScriptOnIv.visibility = View.VISIBLE
        binding.activityPlayCastScriptOffIv.visibility = View.GONE
        binding.activityPlayCastAudioExitIv.visibility = View.GONE
        binding.activityPlayCastNotAudioExit.visibility = View.VISIBLE
        binding.playcastActivitySaveBackIv.visibility = View.GONE

        disableLoopForSentence()
        // 새로운 CastScriptFragment 생성 및 추가
        val scriptFragment = CastScriptFragment(CastPlayerData.currentCast)
        supportFragmentManager.beginTransaction()
            .replace(R.id.play_cast_frm, scriptFragment)
            .commit()

        // 프래그먼트가 추가된 후에 바로 콜백 메소드 설정
        scriptFragment.adapter.onRepeatToggleListener = { position, isRepeatOn ->
            if (isRepeatOn) {
                enableLoopForSentence(position)
                Log.d("loop","${position},${isRepeatOn}")
            } else {
                disableLoopForSentence()
            }
        }
        stateListener = 1
        startScriptFragmentUpdate()
    }

    private fun scriptToAudio() {
        stopScriptFragmentUpdate()
        binding.activityPlayCastPlaylistOnIv.visibility = View.GONE
        binding.activityPlayCastPlaylistOffIv.visibility = View.VISIBLE
        binding.activityPlayCastScriptOnIv.visibility = View.GONE
        binding.activityPlayCastScriptOffIv.visibility = View.VISIBLE
        binding.activityPlayCastAudioExitIv.visibility = View.VISIBLE
        binding.activityPlayCastNotAudioExit.visibility = View.GONE
        binding.playcastActivitySaveBackIv.visibility = View.VISIBLE

        supportFragmentManager.beginTransaction()
            .replace(R.id.play_cast_frm, CastAudioFragment(CastPlayerData.currentCast))
            .commit()

        stateListener = 0
    }

    //플레이스트 시작
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
            .commit()

        stateListener = 2
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
            .replace(R.id.play_cast_frm, CastAudioFragment(CastPlayerData.currentCast))
            .commit()

        stateListener = 0
    }

    fun saveState() {

        when(stateListener){
            0 ->             supportFragmentManager.beginTransaction()
                .replace(R.id.play_cast_frm, CastAudioFragment(CastPlayerData.currentCast))
                .commit()

            1 -> {
                disableLoopForSentence()
                val scriptFragment = CastScriptFragment(CastPlayerData.currentCast)

                // 콜백 설정
                scriptFragment.adapter.onRepeatToggleListener = { position, isRepeatOn ->
                    if (isRepeatOn) {
                        enableLoopForSentence(position)
                        Log.d("loop","${position},${isRepeatOn}")
                    } else {
                        disableLoopForSentence()
                    }
                }

                // 프래그먼트 교체
                supportFragmentManager.beginTransaction()
                    .replace(R.id.play_cast_frm, scriptFragment)
                    .commit()
            }


            2 ->         supportFragmentManager.beginTransaction()
                .replace(R.id.play_cast_frm, CastPlaylistFragment())
                .commit()
        }

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
        classifyCast()
    }
    fun formatTime(input: Int): String {
        val minutes = input / 60
        val seconds = input % 60
        return String.format("%02d:%02d", minutes, seconds)
    }


    private fun enableLoopForSentence(position: Int) {
        val fragment = supportFragmentManager.findFragmentById(R.id.play_cast_frm) as? CastScriptFragment
        val currentSentence = fragment?.adapter?.dataList?.get(position)
        val nextSentence = fragment?.adapter?.dataList?.getOrNull(position + 1)

        if (currentSentence != null) {
            val startTime = (currentSentence.timePoint * 1000).toLong()
            val endTime = if (nextSentence != null) {
                (nextSentence.timePoint * 1000).toLong()
            } else {
                service?.getDuration() ?: Long.MAX_VALUE // 다음 문장이 없을 경우 전체 오디오 길이로 설정
            }

            service?.setLoopForSegment(startTime, endTime)
            Log.d("loop", "Loop set from $startTime to $endTime for sentence at position $position")
        } else {
            Log.e("loop", "Invalid sentence data for looping")
        }
    }



    // 반복을 해제
    private fun disableLoopForSentence() {
        service?.clearLoop()  // 반복 구간을 해제하는 메소드
    }

    private fun addCast(){
        val intent = Intent(this, SearchAddCategoryActivity::class.java)
        intent.putExtra("id",CastPlayerData.currentCast.castId)
        startActivity(intent)
    }

    private val updateScriptFragment = object : Runnable {
        override fun run() {
            service?.let {
                val currentPosition = it.getCurrentPosition()
                val fragment = supportFragmentManager.findFragmentById(R.id.play_cast_frm)
                if (fragment is CastScriptFragment && fragment.isAdded && !fragment.isRemoving) {
                    fragment.updateCurrentTime(currentPosition)
                    Log.d("UpdateTime", "Activity: $currentPosition")
                }
            }
            scriptHandler.postDelayed(this, 300)
        }
    }

    private fun startScriptFragmentUpdate() {
        scriptHandler.postDelayed(updateScriptFragment, 10)
    }

    private fun stopScriptFragmentUpdate() {
        scriptHandler.removeCallbacks(updateScriptFragment)
    }

    fun deleteCast(){
        val deleteCast = getRetrofit().create(Playlist::class.java)
        deleteCast.deleteOtherCast(CastPlayerData.currentCast.playlistId,
            CastPlayerData.currentCast.castId
        ).enqueue(object: Callback<AuthResponse<DeleteOther>> {
            override fun onResponse(call: Call<AuthResponse<DeleteOther>>, response: Response<AuthResponse<DeleteOther>>) {
                Log.d("apiTest-category", response.toString())
                Log.d("apiTest-category", response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.result?.let{
                        //삭제 했으니깐 버튼 바꿔주기
                        CastPlayerData.currentCast.playlistId = -1L
                    }
                }else{
                    Log.d("캐스트 해제","${response.code()}")
                    response.errorBody()?.let { errorBody ->
                        val gson = Gson()
                        val errorResponse: ErrorResponse = gson.fromJson(errorBody.charStream(), ErrorResponse::class.java)
                        Log.d("캐스트 해제", "${errorResponse.message}, ${errorResponse.code}")
                        Toast.makeText(this@PlayCastActivity, "서버 오류 코드 : ${errorResponse.code} \n${errorResponse.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                classifyCast()
            }
            override fun onFailure(call: Call<AuthResponse<DeleteOther>>, t: Throwable) {
                Toast.makeText(this@PlayCastActivity, "서버 응답 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
    //담아온 캐스트 제거하는 함수
    /*private fun deleteCast(){
        if(CastPlayerData.currentCast.playlistId!=-1L){
            val deleteCast = getRetrofit().create(Playlist::class.java)
            CoroutineScope(Dispatchers.IO).launch() {
                Log.d("캐스트 해제", "플레이 리스트${CastPlayerData.currentCast.playlistId}, 캐스트 아이디${CastPlayerData.currentCast.castId}")
                val response = deleteCast.deleteOtherCast(CastPlayerData.currentCast.playlistId,
                    DeleteOtherDto(CastPlayerData.currentCast.castId)
                )
                launch {
                    withContext(Dispatchers.Main) {
                        try {
                            Log.d("캐스트 해제","try 진입")
                            if (response.isSuccessful) {
                                response.body()?.result?.let{
                                    //삭제 했으니깐 버튼 바꿔주기
                                    CastPlayerData.currentCast.playlistId = -1L
                                }
                            } else{
                                Log.d("캐스트 해제","${response.code()}")
                                response.errorBody()?.let { errorBody ->
                                    val gson = Gson()
                                    val errorResponse: ErrorResponse = gson.fromJson(errorBody.charStream(), ErrorResponse::class.java)
                                    Log.d("캐스트 해제", "${errorResponse.message}, ${errorResponse.code}")
                                    Toast.makeText(this@PlayCastActivity, "서버 오류 코드 : ${errorResponse.code} \n${errorResponse.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            classifyCast()
                        }
                    }
                }
            }
        }
    }*/


