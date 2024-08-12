package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignupLoadingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_loading)

        findViewById<TextView>(R.id.main_text).text = "[${SignupData.nickname}]님의 취향을\nowncast에 반영하고 있어요"

        val loadingInner = findViewById<ImageView>(R.id.loading_inner)
        val images = listOf(
            R.drawable.loading_img_1,
            R.drawable.loading_img_2,
            R.drawable.loading_img_3,
            R.drawable.loading_img_4
        )

        MainScope().launch {
            // 이미지 교체 애니메이션
            var index = 0
            while (true) {
                loadingInner.setImageResource(images[index])
                index = (index + 1) % images.size
                delay(700) // 0.7초 간격으로 이미지 교체
            }
        }

        MainScope().launch {
            delay(3000) // 3초 대기 후 다음 액티비티로 이동 -> 이 부분을 서버에서 완료 메시지 받은 것으로 교체하기
            startActivity(Intent(this@SignupLoadingActivity, ClearSignupActivity::class.java))
            finish()
        }
    }
}