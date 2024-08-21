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
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kr.dori.android.own_cast.data.CastPlayerData
import kr.dori.android.own_cast.databinding.ActivityMainBinding
import kr.dori.android.own_cast.player.BackgroundPlayService
import kr.dori.android.own_cast.player.CastWithPlaylistId
import kr.dori.android.own_cast.player.PlayCastActivity
import kr.dori.android.own_cast.playlist.PlaylistFragment
import kr.dori.android.own_cast.search.SearchFragment
import kr.dori.android.own_cast.study.StudyFragment
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

        // play table call back process
        playCastActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
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
        binding.playlistTable.visibility = View.GONE
        service?.pauseAudio()
        isPlaying = false
        binding.activityMainPauseIv.visibility = View.GONE
        binding.activityMainPlayIv.visibility = View.VISIBLE
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
}



