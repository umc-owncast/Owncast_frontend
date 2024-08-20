package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call

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

            // 유효성 검사 및 로그인 시도
            isValidLogin(enteredId, enteredPassword) { isSuccess, errorMessage ->
                if (isSuccess) {
                    // 로그인 성공 시 메인 페이지로 이동
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // 에러 메시지 표시
                    errorMsg.visibility = TextView.VISIBLE
                    errorMsg.text = errorMessage
                }
            }
        }
    }

    private fun isValidLogin(id: String, password: String, callback: (Boolean, String) -> Unit) {
        // 로그인 요청 데이터 생성
        val loginRequest = LoginRequest(loginId = id, password = password)

        // 서버에 로그인 요청
        val call = RetrofitClient.instance.login(loginRequest)

        // 비동기 처리
        call.enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: retrofit2.Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.isSuccess == true) {
                        // 로그인 성공 시, 토큰과 ID를 저장
                        SignupData.id = id
                        SignupData.token = loginResponse.result ?: ""
                        Log.d("token","${SignupData.token}")


                        // 성공 콜백 호출
                        callback(true, "")
                    } else {
                        // 로그인 실패 시 콜백 호출
                        callback(
                            false,
                            "로그인 실패: ${loginResponse?.message ?: "아이디 또는 비밀번호가 올바르지 않습니다."}"
                        )
                    }
                } else {
                    // 서버 오류 또는 응답 실패
                    callback(false, "서버 오류: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // 네트워크 오류
                callback(false, "네트워크 오류: ${t.message}")
            }
        })
    }
}