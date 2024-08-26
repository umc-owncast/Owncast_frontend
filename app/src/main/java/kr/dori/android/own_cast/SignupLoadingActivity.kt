package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupLoadingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_loading)
        enableEdgeToEdge()

        // 텍스트뷰 설정
        findViewById<TextView>(R.id.main_text).text =
            "[${SignupData.nickname}]님의 취향을\nowncast에 반영하고 있어요"

        // 애니메이션 설정
        setupImageAnimation()

        // 회원가입 요청
        performSignUp()
    }

    private fun setupImageAnimation() {
        val images = listOf(
            findViewById<ImageView>(R.id.loading_inner_2),
            findViewById<ImageView>(R.id.loading_inner_3),
            findViewById<ImageView>(R.id.loading_inner_4)
        )

        MainScope().launch {
            while (true) {
                for (i in images.indices) {
                    images[i].visibility = ImageView.VISIBLE
                    delay(700) // 0.7초 동안 이미지 표시
                    images[i].visibility = ImageView.GONE
                }
            }
        }
    }

    private fun performSignUp() {
        val language = when (SignupData.accent) {
            "usa" -> "US"
            "eng" -> "UK"
            "aus" -> "AUS"
            "ind" -> "IND"
            "jp" -> "JA"
            "sp" -> "ES"
            else -> "US"
        }

        val mainCategory = when (SignupData.interest) {
            "movie" -> "드라마/영화"
            "sports" -> "스포츠"
            "music" -> "음악"
            "food" -> "음식"
            "book" -> "책"
            "news" -> "시사/뉴스"
            "art" -> "미술"
            "self" -> "직접입력"
            else -> "드라마/영화"
        }

        val signUpRequest = SignUpRequest(
            nickname = SignupData.nickname,
            username = SignupData.name,
            loginId = SignupData.id,
            password = SignupData.password,
            language = language,
            mainCategory = mainCategory,
            subCategory = SignupData.detail_interest
        )

        // Retrofit 요청 예시
        RetrofitClient.instance.signUp(signUpRequest).enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                if (response.isSuccessful) {
                    val signUpResponse = response.body()
                    if (signUpResponse?.isSuccess == true) {
                        // 회원가입 성공
                        val refreshToken = signUpResponse.result?.refreshToken
                        SignupData.token = refreshToken ?: ""

                        // 로그인 후 토큰 교체
                        isValidLogin(SignupData.id, SignupData.password) { success, errorMessage ->
                            if (success) {
                                // 0.3초 대기 후 다음 액티비티로 이동
                                MainScope().launch {
                                    delay(300)
                                    startActivity(Intent(this@SignupLoadingActivity, ClearSignupActivity::class.java))
                                    finish()
                                }
                            } else {
                                // 로그인 실패 처리
                                handleError(errorMessage)
                            }
                        }
                    } else {
                        // 회원가입 실패 처리
                        handleError(signUpResponse?.message ?: "회원가입 실패")
                    }
                } else {
                    handleError("회원가입 요청 실패: HTTP ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                handleError("회원가입 요청 중 오류 발생: ${t.localizedMessage}")
            }
        })
    }

    private fun isValidLogin(id: String, password: String, callback: (Boolean, String) -> Unit) {
        val loginRequest = LoginRequest(loginId = id, password = password)
        RetrofitClient.instance.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.isSuccess == true) {
                        // 로그인 성공
                        SignupData.token = loginResponse.result?.accessToken ?: ""
                        callback(true, "")
                    } else {
                        // 로그인 실패
                        callback(false, "로그인 실패: ${loginResponse?.message ?: "아이디 또는 비밀번호가 올바르지 않습니다."}")
                    }
                } else {
                    callback(false, "서버 오류: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback(false, "네트워크 오류: ${t.message}")
            }
        })
    }

    private fun handleError(errorMessage: String) {
        // 오류 메시지를 구체적인 상황에 맞게 매핑
        val detailedMessage = when {
            errorMessage.contains("회원가입 요청 실패") ->
                "서버에 문제로 인해 회원가입 요청을 처리할 수 없습니다. 잠시 후 다시 시도해 주세요."

            errorMessage.contains("회원가입 요청 중 오류 발생") ->
                "회원가입 요청 처리 중 오류가 발생했습니다. 네트워크 상태를 확인하고 다시 시도해 주세요."

            errorMessage.contains("잘못된 요청") ->
                "잘못된 요청입니다. 입력한 정보를 확인해 주세요."

            errorMessage.contains("인증되지 않았습니다") ->
                "인증되지 않았습니다. 로그인 정보를 확인해 주세요."

            errorMessage.contains("권한이 없습니다") ->
                "이 작업을 수행할 권한이 없습니다. 권한이 필요한 작업이거나 잘못된 요청일 수 있습니다."

            errorMessage.contains("페이지를 찾을 수 없습니다") ->
                "요청한 페이지를 찾을 수 없습니다. URL이 올바른지 확인해 주세요."

            errorMessage.contains("네트워크 연결 문제") ->
                "네트워크 연결 문제로 요청을 처리할 수 없습니다. 인터넷 연결 상태를 확인하고 다시 시도해 주세요."

            errorMessage.contains("타임아웃") ->
                "요청 시간이 초과되었습니다. 네트워크 상태를 확인하고 다시 시도해 주세요."

            else ->
                "알 수 없는 오류가 발생했습니다. 문제가 계속될 경우 고객 지원 팀에 문의해 주세요."
        }

        // 여기서 UI 업데이트 -> 오류 메시지를 표시
        Toast.makeText(this, detailedMessage, Toast.LENGTH_LONG).show()

        // 전역 변수 초기화
        resetSignupData()

        // SplashActivity로 이동
        startActivity(Intent(this@SignupLoadingActivity, SplashActivity::class.java))
        finish()
    }

    private fun resetSignupData() {
        SignupData.ok_btn = 0
        SignupData.temp_interest = ""
        SignupData.temp_detail_interest = ""
        SignupData.temp_language = "닉네임"
        SignupData.temp_accent = "닉네임"
        SignupData.profile_detail_interest = ""

        SignupData.nickname = "닉네임"
        SignupData.name = "닉네임"
        SignupData.id = "닉네임"
        SignupData.password = "닉네임"
        SignupData.passwordconfirm = "닉네임"
        SignupData.language = "닉네임"
        SignupData.accent = "닉네임"
        SignupData.interest = "닉네임"
        SignupData.detail_interest = "닉네임"
        SignupData.token = "닉네임"
    }
}