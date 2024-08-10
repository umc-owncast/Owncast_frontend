package kr.dori.android.own_cast

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.bottomnavigation.BottomNavigationView
import kr.dori.android.own_cast.HomeFragment
import kr.dori.android.own_cast.PlayCastActivity
import kr.dori.android.own_cast.PlayCastViewModel
import kr.dori.android.own_cast.PlaylistFragment
import kr.dori.android.own_cast.ProfileActivity
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.SearchFragment
import kr.dori.android.own_cast.StudyFragment
import kr.dori.android.own_cast.databinding.ActivityMainBinding
import kr.dori.android.own_cast.keyword.KeywordActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var playCastActivityResultLauncher: ActivityResultLauncher<Intent>

    private var playlistTableVisible: Boolean = false // playlistTable의 현재 상태를 저장하는 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initBottomNavigation()

        //play table call back process
        playCastActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result: ActivityResult ->
            handleActivityResult(result)
        }

        binding.activityMainRealClickConstraint.setOnClickListener {
            val intent = Intent(this, PlayCastActivity::class.java)
            playCastActivityResultLauncher.launch(intent)
        }

        binding.activityMainPauseIv.setOnClickListener {
            binding.activityMainPauseIv.visibility = View.GONE
            binding.activityMainPlayIv.visibility = View.VISIBLE
        }

        binding.activityMainPlayIv.setOnClickListener {
            binding.activityMainPauseIv.visibility = View.VISIBLE
            binding.activityMainPlayIv.visibility = View.GONE
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
                    restorePlaylistTableVisibility() // searchFragment로 이동 시 GONE으로 설정
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.profileFragment -> {
                    restorePlaylistTableVisibility()
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
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
            binding.playlistTable.bringToFront()
            binding.playlistTable.invalidate()
            binding.playlistTable.requestLayout()
        } else {
            binding.playlistTable.visibility = View.GONE
        }
    }
}

