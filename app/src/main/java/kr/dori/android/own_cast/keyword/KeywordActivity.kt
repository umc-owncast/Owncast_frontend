package kr.dori.android.own_cast.keyword

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.R
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
        val searchText: String? = intent.getStringExtra("searchText")
        if(savedInstanceState == null&&searchText!=null){
            var bundle = Bundle()
            var fragment = KeywordAudioSetFragment()
            bundle.putString("searchText",searchText)
            bundle.putBoolean("isHome",true)
            fragment.arguments = bundle
            //검색입력어를 같이 넘겨야해서 bundle넣음
            supportFragmentManager.beginTransaction()
                .add(binding.keywordFragmentFrm.id, fragment)
                .commitAllowingStateLoss()
        }
        else if(savedInstanceState == null&&isSearch==true){
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
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {//editText에서 다른 화면을 누르면 포커스 해제됨ㅕ
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

}