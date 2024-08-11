package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kr.dori.android.own_cast.databinding.ActivityMainBinding
import kr.dori.android.own_cast.keyword.KeywordActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

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

        if (SignupData.profile_detail_interest == "완료") {
            SignupData.profile_detail_interest = ""

            supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, ProfileFragment())
                .commitAllowingStateLoss()
        }
    }

    private fun initKeyword() {
        val goKeyword = Intent(this, KeywordActivity::class.java)
        startActivity(goKeyword)
    }

    private fun initBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.main_bnv)
        bottomNavigationView.selectedItemId = R.id.homeFragment

        supportFragmentManager.beginTransaction()
            .add(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.playlistFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, PlaylistFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.studyFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, StudyFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.profileFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, ProfileFragment())
                        .commitAllowingStateLoss()
                    true
                }
                else -> false
            }
        }
    }
}