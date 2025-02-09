package kr.dori.android.own_cast.signUp

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kr.dori.android.own_cast.AuthActivity
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.SignupData
import kr.dori.android.own_cast.SplashActivity


class SignupThirdActivity : ComponentActivity() {
    private lateinit var viewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_third)
        enableEdgeToEdge()

        // ViewModel 초기화
        viewModel = ViewModelProvider(this).get(SignupViewModel::class.java)

        val etNickName = findViewById<EditText>(R.id.etNickName)
        val nickNameError = findViewById<TextView>(R.id.NickName_error)
        val btnNext = findViewById<Button>(R.id.btn_next)

        // 이전 내용 복원
        var nickName = SignupData.nickname
        if (nickName != getString(R.string.signup_info_first)) {
            etNickName.setText(nickName)
            btnNext.isClickable = true
            btnNext.backgroundTintList = ContextCompat.getColorStateList(this, R.color.main_purple)
        }

        // 뒤로 가기 버튼 클릭 시 이전 화면으로 이동
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            if(intent.getStringExtra("preActivity") == "signupSecond") {
                finish()
            }
            else {
                startActivity(Intent(this, AuthActivity::class.java))
            }
        }

        // 닉네임 입력 필드에 텍스트 변경 리스너 설정
        etNickName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                nickName = s.toString()

                // 동기적 유효성 검사 수행
                val errorMessage = isValidNickName(nickName)

                // 동기적 검사에서 에러가 없을 경우 비동기 유효성 검사 수행
                if (errorMessage == null) {
                    viewModel.checkNickName(nickName)
                }

                // ViewModel의 LiveData를 관찰하여 API 검증 결과 반영
                viewModel.nickNameError.observe(this@SignupThirdActivity, Observer { apiError ->
                    val finalError = errorMessage ?: apiError

                    // 최종 에러 메시지에 따라 UI 업데이트
                    if (finalError != null) {
                        nickNameError.text = finalError
                        etNickName.setBackgroundResource(R.drawable.button_error)
                        btnNext.isClickable = false
                        btnNext.backgroundTintList = ContextCompat.getColorStateList(this@SignupThirdActivity,
                            R.color.button_unclick
                        )
                    } else {
                        nickNameError.text = ""
                        etNickName.setBackgroundResource(R.drawable.edittext_background)
                        btnNext.isClickable = true
                        btnNext.backgroundTintList = ContextCompat.getColorStateList(this@SignupThirdActivity,
                            R.color.main_purple
                        )
                    }
                })
            }
        })

        // 다음 버튼 클릭 시 다음 화면으로 이동
        btnNext.setOnClickListener {
            if (etNickName.length() != 0) {
                SignupData.nickname = nickName
                val intent = Intent(this, SignupLanguageActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // 닉네임 유효성 검사 종합 함수
    private fun isValidNickName(nickName: String): String? {
        return isValidNickName_4(nickName) ?:
        isValidNickName_2(nickName) ?:
        isValidNickName_1(nickName)
    }

    // 닉네임이 10자 이내인지 확인
    private fun isValidNickName_1(nickName: String): String? {
        if (nickName.length > 10) return "10자 이내로 구성해주세요"
        return null
    }

    // 닉네임이 유효한 문자만 포함하는지 확인
    private fun isValidNickName_2(nickName: String): String? {
        val regex = "^[a-zA-Z0-9가-힣-_]+$".toRegex()
        if (!regex.matches(nickName)) return "영문 대/소문자, 한글, 숫자, 하이픈(-), 언더스코어(_)만 사용해주세요"
        return null
    }

    // 닉네임이 비어있는지 확인
    private fun isValidNickName_4(nickName: String): String? {
        if (nickName.isEmpty()) return "닉네임을 입력하세요"
        return null
    }
}