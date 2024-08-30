package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        enableEdgeToEdge()

        val backButton = findViewById<ImageView>(R.id.backButton)
        val btnLogin = findViewById<Button>(R.id.login_btn)
        val etId = findViewById<EditText>(R.id.etId)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val errorMsg = findViewById<TextView>(R.id.error_msg)

        // 입력값에 따라 로그인 버튼 활성화
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val id = etId.text.toString().trim()
                val password = etPassword.text.toString().trim()
                btnLogin.isEnabled = id.isNotEmpty() && password.isNotEmpty()
                btnLogin.setBackgroundTintList(
                    if (btnLogin.isEnabled) {
                        ContextCompat.getColorStateList(this@LoginActivity, R.color.main_purple)
                    } else {
                        ContextCompat.getColorStateList(this@LoginActivity, R.color.button_unclick)
                    }
                )
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        etId.addTextChangedListener(textWatcher)
        etPassword.addTextChangedListener(textWatcher)

        // 뒤로 가기 버튼 클릭 시
        backButton.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }

        // 로그인 버튼 클릭 시
        btnLogin.setOnClickListener {
            val enteredId = etId.text.toString().trim()
            val enteredPassword = etPassword.text.toString().trim()

            // 로그인 시도
            isValidLogin(enteredId, enteredPassword) { isSuccess, errorMessage ->
                if (isSuccess) {
                    // 모든 이전 액티비티를 종료하고 메인 액티비티로 이동
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish() // 현재 액티비티 종료
                } else {
                    // 에러 메시지 표시
                    errorMsg.visibility = TextView.VISIBLE
                    errorMsg.text = errorMessage
                }
            }
        }
    }


    // 로그인 유효성 검사 및 시도
    private fun isValidLogin(id: String, password: String, callback: (Boolean, String) -> Unit) {
        val loginRequest = LoginRequest(loginId = id, password = password)

        // 로그인 API 호출
        val call = RetrofitClient.instance.login(loginRequest)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.isSuccess == true) {
                        SignupData.token = loginResponse.result?.accessToken ?: ""

                        // 로그인 성공 시 사용자 정보 가져오기
                        fetchUserInfo(SignupData.token) { userInfo, errorMessage ->
                            if (userInfo != null) {
                                // 서버에서 받아온 정보를 변환 후 전역변수에 저장
                                storeUserInfo(userInfo)

                                // 성공 콜백 호출
                                callback(true, "")
                            } else {
                                // 사용자 정보 가져오기 실패
                                callback(false, errorMessage ?: "사용자 정보를 가져오지 못했습니다.")
                            }
                        }
                    } else {
                        // 로그인 실패 시 콜백 호출
                        callback(false, "로그인 실패: ${loginResponse?.message ?: "아이디 또는 비밀번호가 올바르지 않습니다."}")
                    }
                } else {
                    // 서버 오류 또는 응답 실패
                    callback(false, "서버 오류: ${response.message()} 아이디 또는 비밀번호가 올바르지 않습니다.")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // 네트워크 오류
                callback(false, "네트워크 오류: ${t.message}")
            }
        })
    }


    // 사용자 정보 가져오기
    private fun fetchUserInfo(token: String, callback: (UserInfo?, String?) -> Unit) {
        // 사용자 정보 API 호출
        val call = RetrofitClient.instance.getUserInfo("Bearer $token")
        call.enqueue(object : Callback<UserInfoResponse> {
            override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                if (response.isSuccessful) {
                    val userInfoResponse = response.body()
                    if (userInfoResponse?.isSuccess == true) {
                        callback(userInfoResponse.result, null)
                    } else {
                        callback(null, "사용자 정보 가져오기 실패: ${userInfoResponse?.message}")
                    }
                } else {
                    callback(null, "사용자 정보 가져오기 실패: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                callback(null, "네트워크 오류: ${t.message}")
            }
        })
    }


    // 서버에서 받아온 사용자 정보를 전역 변수에 저장
    private fun storeUserInfo(userInfo: UserInfo) {
        SignupData.id = userInfo.loginId
        SignupData.name = userInfo.username
        SignupData.nickname = userInfo.nickname

        // 서버에서 받은 데이터를 전역 변수에 맞게 변환
        SignupData.accent = when (userInfo.language) {
            "ES" -> "sp"
            "ES_US" -> "usa"
            "US" -> "usa"
            "UK" -> "eng"
            "AUS" -> "aus"
            "IND" -> "ind"
            "JA" -> "jp"
            else -> "usa" // 기본값
        }

        SignupData.language = when {
            userInfo.language == "ES" && SignupData.accent == "sp" -> "Spanish"
            userInfo.language == "ES_US" && SignupData.accent == "usa" -> "Spanish"
            userInfo.language == "JA" -> "Japanese"
            else -> "English" // 기본값
        }

        SignupData.interest = when (userInfo.mainCategory) {
            "드라마/영화" -> "movie"
            "스포츠" -> "sports"
            "음악" -> "music"
            "음식" -> "food"
            "책" -> "book"
            "시사/뉴스" -> "news"
            "미술" -> "art"
            "직접 입력" -> "self"
            else -> "movie" // 기본값
        }

        // 서브 카테고리는 그대로 저장
        SignupData.detail_interest = userInfo.subCategory
    }
}