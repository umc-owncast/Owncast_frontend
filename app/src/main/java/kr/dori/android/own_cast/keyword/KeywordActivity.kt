package kr.dori.android.own_cast.keyword

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.databinding.ActivityKeywordBinding

/*
이 뒤로 오디오 생성 및 저장 부분
 */
class KeywordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKeywordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKeywordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val isSearch = intent.getBooleanExtra("isSearch",true)
        if(savedInstanceState == null&&isSearch==true){
            supportFragmentManager.beginTransaction()
                .add(binding.keywordFragmentFrm.id, KeywordSearchFragment())
                .commitAllowingStateLoss()
        }
        else if(savedInstanceState == null&&isSearch==false){
            supportFragmentManager.beginTransaction()
                .add(binding.keywordFragmentFrm.id, KeywordInputFragment())
                .commitAllowingStateLoss()
        }
    }


}