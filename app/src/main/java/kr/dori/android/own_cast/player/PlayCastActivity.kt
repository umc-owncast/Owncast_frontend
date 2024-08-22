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
import kr.dori.android.own_cast.forApiData.Cast
import kr.dori.android.own_cast.forApiData.CastInterface
import kr.dori.android.own_cast.forApiData.DeleteOtherDto
import kr.dori.android.own_cast.forApiData.ErrorResponse
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.forApiData.getRetrofit
import kr.dori.android.own_cast.search.SearchAddCategoryActivity
import java.util.concurrent.TimeUnit

class PlayCastActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayCastBinding
    private val playCastViewModel: PlayCastViewModel by viewModels { PlayCastViewModelFactory(application) }
    private lateinit var speedTableViewModel: SpeedTableViewModel
    private var isSeeking = false
    private var service: BackgroundPlayService? = null
    private var isBound = false
    private val handler = Handler()
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
                playCast(currentCast.castId)
            }

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
                xibalCast(nextCast.castId)  // 새로운 캐스트 재생
                Log.d("test","currentPosition: ${CastPlayerData.currentPosition}, currentCast: ${CastPlayerData.currentCast}")
                classifyCast()//추가 안한 캐스트면 창뜨게 해야함
            }
            missFortune()

        }

        // Previous 버튼 클릭 이벤트 처리
        binding.previous.setOnClickListener {
            binding.playCastPlayIv.visibility = View.GONE
            binding.playCastPauseIv.visibility = View.VISIBLE
            // service?.resumeAudio()
            val previousCast = CastPlayerData.playPrevious()
            previousCast?.let {
                stopCurrentAudio()  // 기존 음원 중지
                xibalCast(previousCast.castId)  // 새로운 캐스트 재생
                Log.d("test","currentPosition: ${CastPlayerData.currentPosition}, currentCast: ${CastPlayerData.currentCast}")
                classifyCast()
            }

            missFortune()
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

    fun classifyCast(){//이게 자기 캐스트인지 담아온 캐스트인지 구분
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
            }
        }
    }

    private fun xibalCast(castId: Long) {
        service?.getCastInfo(castId) { url, audioLength ->
            url?.let {
                service?.playAudio(it) // 오디오를 준비하고 바로 재생
                binding.endTv.text = formatTime(audioLength.toInt())
                binding.seekBar.max = audioLength // 시크바 최대값 설정 (초 단위)
                startSeekBarUpdate() // 시크바 업데이트 시작
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


    private fun updateUI() {
        val currentCast = CastPlayerData.currentCast
        currentCast?.let {
            binding.seekBar.max = service?.getDuration()?.toInt()?.div(1000) ?: 0
            binding.seekBar.progress = (service?.getCurrentPosition()?.div(1000))?.toInt() ?: 0

            //  binding.seekbar

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
        handler.postDelayed(updateSeekBar, 10) //이건 첫번째 객체
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

                // CastScriptFragment에 시간 정보 전달
                val fragment = supportFragmentManager.findFragmentById(R.id.play_cast_frm) as? CastScriptFragment
                fragment?.updateCurrentTime(currentPosition)
                Log.d("UpdateTime", "Activity: $currentPosition")
            }
            handler.postDelayed(this, 300) // 주기적으로 업데이트
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


    /*
        private fun updateLyricsHighlight() {
            val fragment = supportFragmentManager.findFragmentById(R.id.play_cast_frm) as? CastScriptFragment
            fragment?.let {
                (it.binding.scriptRv.adapter as? ScriptAdapter)?.updateCurrentTime(service?.getCurrentPosition() ?: 0L)
            }
        }

     */




    // Fragment 전환 함수들
    private fun audioToScript() {
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
            .commitAllowingStateLoss()

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
            .replace(R.id.play_cast_frm, CastAudioFragment(CastPlayerData.currentCast))
            .commitAllowingStateLoss()

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
            .commitAllowingStateLoss()

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
            .commitAllowingStateLoss()

        stateListener = 0
    }

    fun missFortune() {

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
                    .commitAllowingStateLoss()
            }


            2 ->         supportFragmentManager.beginTransaction()
                .replace(R.id.play_cast_frm, CastPlaylistFragment())
                .commitAllowingStateLoss()
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

    // 특정 문장을 반복하도록 설정
    private fun enableLoopForSentence(position: Int) {
        val fragment = supportFragmentManager.findFragmentById(R.id.play_cast_frm) as? CastScriptFragment
        val sentence = fragment?.adapter?.dataList?.get(position)
        val nextSentence = fragment?.adapter?.dataList?.getOrNull(position + 1)  // 다음 문장이 없을 수도 있으므로 null을 처리

        if (sentence != null && nextSentence != null) {
            val startTime = (sentence.timePoint * 1000).toLong()
            val endTime = ((nextSentence.timePoint * 1000)-10).toLong()
            Log.d("loop", "Start: $startTime, End: $endTime")
            service?.setLoopForSegment(startTime, endTime)
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
        classifyCast()
    }

    //담아온 캐스트 제거하는 함수
    private fun deleteCast(){
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
                                    /*binding.addCategoryOffBtn.visibility = View.VISIBLE
                                    binding.addCategoryOnBtn.visibility = View.GONE*/

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
                        }
                    }
                }
            }
        }
    }

}

