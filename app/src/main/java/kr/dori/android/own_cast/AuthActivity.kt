package kr.dori.android.own_cast


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch
import kr.dori.android.own_cast.databinding.ActivityAuthBinding
import kr.dori.android.own_cast.presentation.ui.auth.social.SocialLoginUiState
import kr.dori.android.own_cast.presentation.ui.auth.social.SocialLoginViewModel
import kr.dori.android.own_cast.signUp.SignupFirstActivity
import kr.dori.android.own_cast.signUp.SignupThirdActivity

class AuthActivity : ComponentActivity() {
    private lateinit var binding: ActivityAuthBinding
    private val socialLoginViewModel by viewModels<SocialLoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
        enableEdgeToEdge()

        addListeners()
        socialLogin()
    }

    private fun addListeners() {
        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.signUpBtn.setOnClickListener {
            startActivity(Intent(this, SignupFirstActivity::class.java))
        }
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
}