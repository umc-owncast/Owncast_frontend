package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var imageView: ImageView
    private lateinit var mainSection: LinearLayout
    private lateinit var subSection: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        textView = findViewById(R.id.ic_o_text)
        imageView = findViewById(R.id.ic_o)
        mainSection = findViewById(R.id.main_section)
        subSection = findViewById(R.id.sub_section)

        // 각 단계의 효과를 적용
        showTextViewAndImageView()

        // 화면전환
        prepareForActivityTransition()
    }

    private fun showTextViewAndImageView() {
        // 1초 후에 텍스트 뷰를 숨기고 이미지 뷰를 표시
        Handler(Looper.getMainLooper()).postDelayed({
            textView.visibility = View.GONE
            imageView.visibility = View.VISIBLE

            hideMainSectionAndSubSection()
            moveImageViewOffScreen()

        }, 2000)
    }

    private fun hideMainSectionAndSubSection() {

        // 1초 후에 텍스트 뷰를 숨기고 이미지 뷰를 표시
        Handler(Looper.getMainLooper()).postDelayed({
            mainSection.visibility = View.GONE
            subSection.visibility = View.GONE

        }, 2000)
    }

    private fun moveImageViewOffScreen() {
        val animation = TranslateAnimation(1000f, -1100f, 0f, 0f) // 화면 왼쪽으로 이동
        animation.duration = 3000 // 3초 동안 이동
        animation.fillAfter = true // 애니메이션 완료 후 위치 유지

        findViewById<ImageView>(R.id.sub_ic).visibility = View.VISIBLE
        findViewById<ImageView>(R.id.sub_ic).startAnimation(animation)
    }


    // 화면 전환
    private fun prepareForActivityTransition() {
        MainScope().launch {
            // 4초 대기
            delay(4000)

            // 화면 전환할 액티비티 결정
            val targetActivity = if (SignupData.id == getString(R.string.signup_info_first)) {
                AuthActivity::class.java
            } else {
                AuthAfterActivity::class.java
            }

            // 액티비티 전환
            startActivity(Intent(this@SplashActivity, targetActivity))

            finish()
        }
    }
}