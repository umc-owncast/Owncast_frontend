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

        findViewById<TextView>(R.id.main_text).text =
            "[${SignupData.nickname}]님의 취향을\nowncast에 반영하고 있어요"

        val images = listOf(
            findViewById<ImageView>(R.id.loading_inner_1),
            findViewById<ImageView>(R.id.loading_inner_2),
            findViewById<ImageView>(R.id.loading_inner_3),
            findViewById<ImageView>(R.id.loading_inner_4)
        )

        // 이미지 교체 애니메이션
        MainScope().launch {
            for (i in images.indices) {
                images[i].visibility = ImageView.VISIBLE
                delay(700) // 0.7초 동안 이미지 표시
                images[i].visibility = ImageView.GONE
            }
        }

        // 서버로 전송할 language 값을 설정
        val language = when {
            SignupData.accent == "sp" && SignupData.language == "Spanish" -> "ES"
            SignupData.accent == "usa" && SignupData.language == "Spanish" -> "ES_US"
            SignupData.accent == "usa" -> "US"
            SignupData.accent == "eng" -> "UK"
            SignupData.accent == "aus" -> "AUS"
            SignupData.accent == "ind" -> "IND"
            SignupData.accent == "jp"  -> "JA"
            else -> "US"
        }

        // interest 값을 변환
        val mainCategory = when (SignupData.interest) {
            "movie" -> "드라마/영화"
            "sports" -> "스포츠"
            "music" -> "음악"
            "food" -> "음식"
            "book" -> "책"
            "news" -> "시사/뉴스"
            "art" -> "미술"
            "self" -> "직접 입력"
            else -> "드라마/영화"
        }


        // 회원가입 요청을 서버로 전송하고 응답 처리
        val signUpRequest = SignUpRequest(
            nickname = SignupData.nickname,
            username = SignupData.name,
            loginId = SignupData.id,
            password = SignupData.password,
            language = language, // 변환된 language 값을 사용
            mainCategory = mainCategory, // 변환된 mainCategory 값을 사용
            subCategory = SignupData.detail_interest
        )

        RetrofitClient.instance.signUp(signUpRequest).enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(
                call: Call<SignUpResponse>,
                response: Response<SignUpResponse>
            ) {
                if (response.isSuccessful) {
                    val signUpResponse = response.body()
                    if (signUpResponse?.isSuccess == true) {
                        // 회원가입 성공

                        // refreshToken 저장
                        val refreshToken = signUpResponse.result?.refreshToken
                        SignupData.token = refreshToken ?: ""

                        // 0.3초 대기 후 다음 액티비티로 이동
                        MainScope().launch {
                            delay(300)
                            startActivity(
                                Intent(
                                    this@SignupLoadingActivity,
                                    ClearSignupActivity::class.java
                                )
                            )
                            finish()
                        }
                    } else {
                        // 회원가입 실패 처리
                        handleError(signUpResponse?.message ?: "회원가입 실패")
                    }
                } else {
                    handleError("회원가입 요청 실패")
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                handleError("회원가입 요청 중 오류 발생")
            }
        })
    }

    private fun handleError(message: String) {
        // 에러 메시지를 표시 & 처음으로
        MainScope().launch {
            // 전역 변수 초기화
            resetSignupData()

            // 에러 메시지 표시
            Toast.makeText(this@SignupLoadingActivity, message, Toast.LENGTH_LONG).show()

            // SplashActivity로 이동
            startActivity(Intent(this@SignupLoadingActivity, SplashActivity::class.java))
            finish()
        }
    }

    // 전역 변수 초기화 메서드
    private fun resetSignupData() {
        SignupData.ok_btn = 0
        SignupData.temp_interest = ""
        SignupData.temp_detail_interest = ""
        SignupData.temp_language = "닉네임"
        SignupData.temp_accent = "닉네임"
        SignupData.profile_detail_interest = ""

        // SharedPreferences에 저장된 값 초기화
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