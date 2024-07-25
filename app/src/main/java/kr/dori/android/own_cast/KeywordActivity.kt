package kr.dori.android.own_cast

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .add(binding.keywordFragmentFrm.id, KeywordSearchFragment())
                .commitAllowingStateLoss()
        }
    }
}