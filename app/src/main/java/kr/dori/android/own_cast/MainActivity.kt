package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import kr.dori.android.own_cast.databinding.ActivityMainBinding
/*
selectedItemId로 초기 화면 설정 가능
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goKeywordIv.setOnClickListener {
            initKeyword()
        }
        initBottomNavigation()
    }

    private fun initKeyword(){
        val goKeyword = Intent(this, KeywordActivity::class.java)
        startActivity(goKeyword)
    }


    private fun initBottomNavigation(){

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.main_bnv) as BottomNavigationView
        bottomNavigationView.selectedItemId=R.id.homeFragment

        supportFragmentManager.beginTransaction()
            .add(R.id.main, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) {

                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    //  return@setOnItemSelectedListener true
                }

                R.id.playlistFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, PlaylistFragment())
                        .commitAllowingStateLoss()
                    //  return@setOnItemSelectedListener true
                }
                R.id.studyFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, StudyFragment())
                        .commitAllowingStateLoss()
                    //   return@setOnItemSelectedListener true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    // return@setOnItemSelectedListener true
                }
                R.id.profileFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, ProfileFragment())
                        .commitAllowingStateLoss()
                }
            }
            true
        }
    }
}