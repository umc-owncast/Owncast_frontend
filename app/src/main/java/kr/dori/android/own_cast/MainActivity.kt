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
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kr.dori.android.own_cast.data.CastPlayerData
import kr.dori.android.own_cast.databinding.ActivityMainBinding
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


import kr.dori.android.own_cast.player.PlayCastActivity
import kr.dori.android.own_cast.playlist.PlaylistFragment
import kr.dori.android.own_cast.search.SearchFragment
import kr.dori.android.own_cast.study.StudyFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var playCastActivityResultLauncher: ActivityResultLauncher<Intent>

    private var playlistTableVisible: Boolean = false // playlistTable의 현재 상태를 저장하는 변수

    private var service: BackgroundPlayService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as BackgroundPlayService.LocalBinder
            this@MainActivity.service = binder.getService()
            isBound = true
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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 주석 처리된 부분 - 키워드 이동
        // binding.goKeywordIv.setOnClickListener {
        //     initKeyword()
        // }
        initBottomNavigation()
        KeywordAppData.initDetailKeyword()

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
        }
    }

    private fun initBottomNavigation() {
        val bottomNavigationView: BottomNavigationView =
            findViewById(R.id.main_bnv) as BottomNavigationView
        bottomNavigationView.selectedItemId = R.id.homeFragment

        supportFragmentManager.beginTransaction()
            .add(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    restorePlaylistTableVisibility()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.playlistFragment -> {
                    restorePlaylistTableVisibility()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, PlaylistFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.studyFragment -> {
                    hidePlaylistTable()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, StudyFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.searchFragment -> {
                    restorePlaylistTableVisibility()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.profileFragment -> {
                    restorePlaylistTableVisibility()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, ProfileFragment())
                        .commitAllowingStateLoss()
                    true
                }
                else -> false
            }
        }
    }

    private fun restorePlaylistTableVisibility() {
        if (playlistTableVisible) {
            binding.playlistTable.visibility = View.VISIBLE
            binding.playlistTable.bringToFront()
            binding.playlistTable.invalidate()
            binding.playlistTable.requestLayout()
        }
    }

    private fun hidePlaylistTable() {
        playlistTableVisible = binding.playlistTable.visibility == View.VISIBLE
        binding.playlistTable.visibility = View.GONE
    }

    fun handleActivityResult(result: ActivityResult){
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("ifsuccess","success")
            val data: Intent? = result.data
            val isSuccess = data?.getBooleanExtra("result", false) ?: false
            if (isSuccess) {
                binding.playlistTable.visibility = View.VISIBLE
                binding.playlistTable.bringToFront()
                binding.playlistTable.invalidate()
                binding.playlistTable.requestLayout()
            } else {
                binding.playlistTable.visibility = View.GONE
            }
            playlistTableVisible = binding.playlistTable.visibility == View.VISIBLE
        }
    }

    fun setPlaylistTableVisibility(visible: Boolean) {
        playlistTableVisible = visible
        if (visible) {
            binding.playlistTable.visibility = View.VISIBLE
            binding.playlistTable.bringToFront()//뷰를 가장 최상위로
            binding.playlistTable.invalidate()//화면 무효화 및 재작성
            binding.playlistTable.requestLayout()//크기 위치 재계산
        } else {
            binding.playlistTable.visibility = View.GONE
        }
    }

    private fun pauseAudio() {
        if (service != null && isBound) {
            service?.pauseAudio()
            binding.activityMainPauseIv.visibility = View.GONE
            binding.activityMainPlayIv.visibility = View.VISIBLE
        }
    }

    private fun playAudio(){
        if(service != null && isBound){
            service?.resumeAudio()
            binding.activityMainPauseIv.visibility = View.VISIBLE
            binding.activityMainPlayIv.visibility = View.GONE
        }
    }

    private fun playNextAudio() {
        if (service != null && isBound) {
            val nextCast = CastPlayerData.playNext()
            nextCast?.let {
                service?.playAudio(it.castTitle)
                binding.activityMainPauseIv.visibility = View.VISIBLE
                binding.activityMainPlayIv.visibility = View.GONE
            }
        }
    }


}

