package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.dori.android.own_cast.databinding.ActivitySplashBinding
import kr.dori.android.own_cast.presentation.ui.auth.social.SocialLoginUiState
import kr.dori.android.own_cast.presentation.ui.auth.social.SocialLoginViewModel
import kr.dori.android.own_cast.signUp.SignupFirstActivity
import kr.dori.android.own_cast.signUp.SignupThirdActivity


class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val socialLoginViewModel by viewModels<SocialLoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        enableEdgeToEdge()

        // 이미지 이동 애니메이션 실행
        moveImageViewOffScreen()

        addListeners()
        socialLogin()
    }

    private fun addListeners() {
        // 로그인
        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        // 회원가입
        binding.signUpBtn.setOnClickListener {
            startActivity(Intent(this, SignupFirstActivity::class.java))
        }
        // 카카오 로그인
        binding.btnKakaoLogin.setOnClickListener {
            socialLoginViewModel.kakaoLogin()
        }
    }

    private fun socialLogin() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                socialLoginViewModel.socialLoginUiState.collect() { uiState ->
                    when(uiState) {
                        SocialLoginUiState.KakaoLogin -> {
                            handleKakaoLogin()
                            socialLoginViewModel.setUiStateIdle()
                        }
                        SocialLoginUiState.KakaoLoginSuccess -> {
                            // 사용자의 정보 얻어옴
                            startActivity(Intent(baseContext, SignupThirdActivity::class.java))
                        }
                        SocialLoginUiState.LoginFail -> {
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    private fun handleKakaoLogin() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if(error != null) {
                Log.e("handleKakaoLogin", "카카오계정 로그인 실패 $error")
//                if(error.toString().contains("statusCode=302")) {
//                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
//                }
                socialLoginViewModel.kakaoLoginFail()
            } else if(token != null) {
                Log.e("handleKakaoLogin", "카카오계정 로그인 성공 ${token.accessToken}")
                socialLoginViewModel.kakaoLoginSuccess()
            }
        }

        // 카카오톡 설치 확인
        if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if(error != null) {
                    Log.e("handleKakaoLogin", "카카오계정 로그인 실패 $error")

                    // 사용자 취소
                    if(
                        (error is ClientError && error.message?.contains("user cancelled") == true) ||
                        (error is AuthError && error.message?.contains("User denied access") == true)
                    ) {
                        socialLoginViewModel.kakaoLoginFail()
                        return@loginWithKakaoTalk
                    }
                    // 카카오톡에 연결된 카카오계정이 없는 경우 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                } else if(token != null) {
                    Log.i("handleKakaoLogin", "카카오계정 로그인 성공 ${token.accessToken}")
                    socialLoginViewModel.kakaoLoginSuccess()
                }
            }
        }
        else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
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
            binding.btnKakaoLogin.visibility = View.VISIBLE
            binding.btnNaverLogin.visibility = View.VISIBLE
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