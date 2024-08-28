package kr.dori.android.own_cast

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kr.dori.android.own_cast.data.CastPlayerData
import kr.dori.android.own_cast.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kr.dori.android.own_cast.forApiData.AuthResponse
import kr.dori.android.own_cast.forApiData.CastHomeDTO
import kr.dori.android.own_cast.forApiData.CastInterface
import kr.dori.android.own_cast.forApiData.PostPlaylist
import kr.dori.android.own_cast.forApiData.UserPostPlaylist
import kr.dori.android.own_cast.forApiData.getRetrofit
import kr.dori.android.own_cast.keyword.KeywordAppData
import kr.dori.android.own_cast.player.BackgroundPlayService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Base64
import androidx.activity.OnBackPressedCallback

import kr.dori.android.own_cast.player.CastWithPlaylistId

import kr.dori.android.own_cast.player.PlayCastActivity
import kr.dori.android.own_cast.playlist.PlaylistFragment
import kr.dori.android.own_cast.search.SearchFragment
import kr.dori.android.own_cast.study.StudyFragment

import org.json.JSONObject
import java.util.Date
import java.util.Timer
import java.util.TimerTask
import kotlin.io.encoding.ExperimentalEncodingApi


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var playCastActivityResultLauncher: ActivityResultLauncher<Intent>
    private var isPlaying: Boolean = false

    private var playlistTableVisible: Boolean = false // playlistTable의 현재 상태를 저장하는 변수

    private var service: BackgroundPlayService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as BackgroundPlayService.LocalBinder
            this@MainActivity.service = binder.getService()
            isBound = true
            updatePlaybackUI()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            service = null
            isBound = false
        }
    }



    override fun onStart() {
        super.onStart()
        val intent = Intent(this, BackgroundPlayService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    override fun onResume() {
        super.onResume()
        // Activity가 다시 포그라운드에 올 때 재생 상태를 UI에 반영
        updatePlaybackUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Log.d("token1", "${SignupData.token}")
        // 주석 처리된 부분 - 키워드 이동
        // binding.goKeywordIv.setOnClickListener {
        //     initKeyword()
        // }


        /*initBottomNavigation()*/


        // 로그인 정보 확인 후 토큰 갱신
        // 토큰 만료 여부 확인
        if (isTokenExpired(SignupData.token)) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }


        //play table call back process
        playCastActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->

            handleActivityResult(result)
        }

        binding.activityMainRealClickConstraint.setOnClickListener {
            val intent = Intent(this, PlayCastActivity::class.java)
            intent.putExtra("fromMainActivity", true)  // MainActivity에서 넘어왔음을 표시
            playCastActivityResultLauncher.launch(intent)
        }


        binding.activityMainPauseIv.setOnClickListener {
            pauseAudio()
        }

        binding.activityMainPlayIv.setOnClickListener {
            playAudio()
        }

        binding.activityMainNextIv.setOnClickListener {
            playNextAudio()
        }

        if (SignupData.profile_detail_interest == "완료") {
            SignupData.profile_detail_interest = ""

            supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, ProfileFragment())
                .commitAllowingStateLoss()
            updateBottomNavigationIcons(ProfileFragment::class.java)
        } else {
            val homeFragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, homeFragment)
                .commitAllowingStateLoss()
            updateBottomNavigationIcons(HomeFragment::class.java)
        }

        initBottomButtons()
        quitApp()
    }



    @OptIn(ExperimentalEncodingApi::class)
    private fun isTokenExpired(token: String?): Boolean {
        if (token.isNullOrEmpty()) return true

        try {
            // JWT 토큰의 payload 부분을 Base64로 디코딩
            val parts = token.split(".")
            if (parts.size < 2) return true
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP))

            // JSON 형식으로 변환 후 exp(만료시간) 추출
            val jsonObject = JSONObject(payload)
            val exp = jsonObject.getLong("exp")

            // 현재 시간과 비교
            val now = Date().time / 1000
            return exp < now
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return true
    }

    private fun renewToken(id: String, password: String) {
        val loginRequest = LoginRequest(loginId = SignupData.id, password = SignupData.password)

        // 서버에 로그인 요청
        val call = RetrofitClient.instance.login(loginRequest)

        call.enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: retrofit2.Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.isSuccess == true) {
                        // 토큰 갱신 성공 시
                        SignupData.token = loginResponse.result?.accessToken ?: ""
                        //Toast.makeText(this@MainActivity, "토큰이 갱신되었습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        // 로그인 실패 시 로그인 화면으로 이동
                        Toast.makeText(
                            this@MainActivity,
                            "로그인 실패: ${loginResponse?.message ?: "아이디 또는 비밀번호가 올바르지 않습니다."}",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finish()
                    }
                } else {
                    // 서버 오류 또는 응답 실패 시 로그인 화면으로 이동
                    Toast.makeText(
                        this@MainActivity,
                        "서버 오류: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // 네트워크 오류 시 로그인 화면으로 이동
                Toast.makeText(
                    this@MainActivity,
                    "네트워크 오류: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        })
    }



    private fun initBottomButtons() {
        val homeButton: ImageView = findViewById(R.id.navi_home)
        val playlistButton: ImageView = findViewById(R.id.navi_playlist)
        val studyButton: ImageView = findViewById(R.id.navi_study)
        val searchButton: ImageView = findViewById(R.id.navi_search)
        val profileButton: ImageView = findViewById(R.id.navi_profile)

        homeButton.setOnClickListener {
            replaceFragment(HomeFragment())
            restorePlaylistTableVisibility()
            updateBottomNavigationIcons(HomeFragment::class.java)
        }

        playlistButton.setOnClickListener {
            replaceFragment(PlaylistFragment())
            restorePlaylistTableVisibility()
            updateBottomNavigationIcons(PlaylistFragment::class.java)
        }

        studyButton.setOnClickListener {
            replaceFragment(StudyFragment())
            hidePlaylistTable()  // 학습 프래그먼트로 이동 시 음원 중단
            updateBottomNavigationIcons(StudyFragment::class.java)
        }

        searchButton.setOnClickListener {
            replaceFragment(SearchFragment())
            restorePlaylistTableVisibility()
            updateBottomNavigationIcons(SearchFragment::class.java)
        }

        profileButton.setOnClickListener {
            replaceFragment(ProfileFragment())
            restorePlaylistTableVisibility()
            updateBottomNavigationIcons(ProfileFragment::class.java)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, fragment)
            .commitAllowingStateLoss()
    }

    private fun updateBottomNavigationIcons(fragmentClass: Class<out Fragment>) {
        val homeButton: ImageView = findViewById(R.id.navi_home)
        val playlistButton: ImageView = findViewById(R.id.navi_playlist)
        val studyButton: ImageView = findViewById(R.id.navi_study)
        val searchButton: ImageView = findViewById(R.id.navi_search)
        val profileButton: ImageView = findViewById(R.id.navi_profile)

        // 초기화: 모든 버튼의 이미지를 비활성화 상태로 설정
        homeButton.setImageResource(R.drawable.bottom_navi_home_unfocused)
        playlistButton.setImageResource(R.drawable.bottom_navi_playlist_unfocused)
        studyButton.setImageResource(R.drawable.bottom_navi_study_unfocused)
        searchButton.setImageResource(R.drawable.bottom_navi_search_unfocused)
        profileButton.setImageResource(R.drawable.bottom_navi_profile_unfocused)

        // 현재 선택된 Fragment에 맞는 아이콘 활성화 상태로 설정
        when (fragmentClass) {
            HomeFragment::class.java -> {
                homeButton.setImageResource(R.drawable.bottom_navi_home_focused)
            }
            PlaylistFragment::class.java -> {
                playlistButton.setImageResource(R.drawable.bottom_navi_playlist_focused)
            }
            StudyFragment::class.java -> {
                studyButton.setImageResource(R.drawable.bottom_navi_study_focused)
            }
            SearchFragment::class.java -> {
                searchButton.setImageResource(R.drawable.bottom_navi_search_focused)
            }
            ProfileFragment::class.java -> {
                profileButton.setImageResource(R.drawable.bottom_navi_profile_focused)
            }
        }
    }


    private fun updatePlaybackUI() {
        if (service?.isPlaying() == true) {
            binding.activityMainPauseIv.visibility = View.VISIBLE
            binding.activityMainPlayIv.visibility = View.GONE
        } else {
            binding.activityMainPauseIv.visibility = View.GONE
            binding.activityMainPlayIv.visibility = View.VISIBLE

        }
    }

    private fun restorePlaylistTableVisibility() {
        Log.d("테이블 테스트",playlistTableVisible.toString())
        if (playlistTableVisible) {
            binding.playlistTable.visibility = View.VISIBLE
            binding.playlistTable.bringToFront()
            binding.playlistTable.invalidate()
            binding.playlistTable.requestLayout()
        }
        updatePlaybackUI()  // 재생 상태에 따라 UI 업데이트
    }

    private fun hidePlaylistTable() {
        playlistTableVisible = binding.playlistTable.visibility == View.VISIBLE
        Log.d("테이블 테스트",playlistTableVisible.toString())
        binding.playlistTable.visibility = View.GONE
        service?.pauseAudio()
        isPlaying = false
        binding.activityMainPauseIv.visibility = View.GONE
        binding.activityMainPlayIv.visibility = View.VISIBLE
        Log.d("테이블 테스트",playlistTableVisible.toString())
    }

    fun handleActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val isSuccess = data?.getBooleanExtra("result", false) ?: false
            Log.d("ActivityResult", "ResultCode: ${result.resultCode}, isSuccess: $isSuccess")

            if (isSuccess) {
                setPlaylistTableVisibility(true)
                playlistTableVisible = true

                updatePlaybackUI()  // 재생 상태에 따라 UI 업데이트
            } else {
                setPlaylistTableVisibility(false)
                playlistTableVisible = false

            }
        } else {
            setPlaylistTableVisibility(false)
            playlistTableVisible = false

        }
    }

    fun setPlaylistTableVisibility(visible: Boolean) {
        playlistTableVisible = visible
        if (visible) {
            binding.playlistTable.visibility = View.VISIBLE
            binding.playlistTable.bringToFront()
            binding.playlistTable.invalidate()
            binding.playlistTable.requestLayout()
            setText(CastPlayerData.currentCast)

        } else {
            binding.playlistTable.visibility = View.GONE
        }
    }

    private fun pauseAudio() {
        if (service != null && isBound) {
            service?.pauseAudio()
            isPlaying = false
            binding.activityMainPauseIv.visibility = View.GONE
            binding.activityMainPlayIv.visibility = View.VISIBLE
        }
    }

    private fun playAudio() {
        if (service != null && isBound) {
            service?.resumeAudio()
            isPlaying = true
            binding.activityMainPauseIv.visibility = View.VISIBLE
            binding.activityMainPlayIv.visibility = View.GONE
        }
    }

    private fun playNextAudio() {
        if (service != null && isBound) {
            val nextCast = CastPlayerData.playNext()
            nextCast?.let {
                stopCurrentAudio()
                setNextCast(it.castId)
                setText(nextCast)
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

    private fun setNextCast(castId: Long) {
        service?.getCastInfo(castId) { url, audioLength ->
            url?.let {
                service?.playAudio(it) // 오디오 재생 시작
                binding.activityMainPauseIv.visibility = View.VISIBLE
                binding.activityMainPlayIv.visibility = View.GONE
            }
        }
    }

    private fun setText(currentCast: CastWithPlaylistId){
        binding.castName.text = currentCast.castTitle
        binding.categoryNameTv.text = currentCast.castCategory
        binding.categoryNameTv.bringToFront()
    }

    private var backPressedOnce = false
    private fun quitApp(){
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedOnce) {
                    // Exit the app
                    finishAffinity() // Or use finish() to close just the current activity
                } else {
                    backPressedOnce = true
                    Toast.makeText(applicationContext, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()

                    // Reset backPressedOnce after 2 seconds
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            backPressedOnce = false
                        }
                    }, 2000)
                }
            }
        })
    }
}




