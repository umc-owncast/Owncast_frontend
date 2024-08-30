package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        enableEdgeToEdge()

        // 이미지 이동 애니메이션 실행
        moveImageViewOffScreen()

        // 로그인 버튼 클릭 이벤트 설정
        findViewById<Button>(R.id.loginBtn).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // 회원가입 버튼 클릭 이벤트 설정
        findViewById<Button>(R.id.signUpBtn).setOnClickListener {
            startActivity(Intent(this, SignupFirstActivity::class.java))
        }
    }

    private fun moveImageViewOffScreen() {
        val splash1 = findViewById<ImageView>(R.id.splash_1)
        val splash2 = findViewById<ImageView>(R.id.splash_2)

        val time_1 = 500
        val time_2 = 1000
        val time_3 = 1500

        splash1.post {
            val screenWidth = splash1.width.toFloat()

            // splash_1과 splash_2가 함께 왼쪽으로 이동하는 초기 애니메이션
            val moveTogether = TranslateAnimation(0f, -screenWidth * 0.3f, 0f, 0f).apply {
                duration = time_1.toLong() //  애니메이션 진행
                fillAfter = true
            }
            splash1.startAnimation(moveTogether)
            splash2.startAnimation(moveTogether)

            MainScope().launch {
                delay(time_1.toLong()) // 첫 애니메이션이 끝날 때까지 대기

                // splash_1만 왼쪽으로 추가로 이동하는 애니메이션
                val splash1MoveLeft = TranslateAnimation(
                    -screenWidth * 0.3f,
                    -screenWidth * 1.4f,
                    0f,
                    0f
                ).apply {
                    duration = time_2.toLong()  // 애니메이션 진행
                    fillAfter = true
                }
                splash1.startAnimation(splash1MoveLeft)

                delay(time_2.toLong()) // splash_1 애니메이션이 끝날 때까지 대기

                // splash_2가 왼쪽으로 추가로 이동하는 애니메이션
                val splash2FinalMove = TranslateAnimation(
                    -screenWidth * 0.3f,
                    -screenWidth * 2.1f,
                    0f,
                    0f
                ).apply {
                    duration = time_3.toLong() // 애니메이션 진행
                    fillAfter = true
                }

                splash2FinalMove.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        // 애니메이션 시작 시 동작
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        // splash2를 숨기고 나머지 UI 표시
                        findViewById<HorizontalScrollView>(R.id.splash_view).visibility = View.GONE
                        handleAnimationEnd()
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                        // 애니메이션 반복 시 동작
                    }
                })

                splash2.startAnimation(splash2FinalMove)
            }
        }
    }

    // 애니메이션 종료 후 화면 전환 처리
    private fun handleAnimationEnd() {
        val mainSection = findViewById<LinearLayout>(R.id.main_section)
        val subText = findViewById<TextView>(R.id.sub_text)
        val addText = findViewById<TextView>(R.id.add_text)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val signUpBtn = findViewById<Button>(R.id.signUpBtn)

        // Fade-in 애니메이션 설정
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 1000 // 1초 동안 서서히 나타남
            fillAfter = true
        }

        // UI 요소에 애니메이션 적용
        mainSection.visibility = View.VISIBLE
        subText.visibility = View.VISIBLE
        mainSection.startAnimation(fadeIn)
        subText.startAnimation(fadeIn)


        if (SignupData.id == getString(R.string.signup_info_first)) {
            // 로그인, 회원가입 버튼 표시
            loginBtn.visibility = View.VISIBLE
            signUpBtn.visibility = View.VISIBLE
            addText.visibility = View.VISIBLE
            loginBtn.startAnimation(fadeIn)
            signUpBtn.startAnimation(fadeIn)
            addText.startAnimation(fadeIn)
        } else {
            // 1초 후 메인 화면으로 전환
            MainScope().launch {
                delay(1000) // 1초 대기 후 메인 화면으로 전환
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }
    }
}