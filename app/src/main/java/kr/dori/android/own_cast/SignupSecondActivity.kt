package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class SignupSecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_second)

        val etNickName = findViewById<EditText>(R.id.etNickName)
        val nickNameError = findViewById<TextView>(R.id.NickName_error)
        val btnNext = findViewById<Button>(R.id.btn_next)

        // 이전 내용 복원
        var nickName = SignupData.nickname
        if (nickName != getString(R.string.signup_info_first)) {
            etNickName.setText(nickName)
            btnNext.isClickable = true
            btnNext.backgroundTintList = ContextCompat.getColorStateList(this@SignupSecondActivity, R.color.main_purple)
        }

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, SignupFirstActivity::class.java)
            startActivity(intent)
        }

        // 닉네임 입력 필드의 텍스트 변경을 감지하는 리스너 설정 -> 3가지 유효성 검사
        etNickName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                nickName = s.toString()
                var errorMessage: String? = null

                // 닉네임 유효성 검사
                errorMessage = isValidNickName_4(nickName)
                if (errorMessage == null) {
                    errorMessage = isValidNickName_3(nickName)
                }
                if (errorMessage == null) {
                    errorMessage = isValidNickName_1(nickName)
                }
                if (errorMessage == null) {
                    errorMessage = isValidNickName_2(nickName)
                }

                // 오류 메시지가 있으면 화면에 표시하고 배경 변경
                if (errorMessage != null) {
                    nickNameError.text = errorMessage
                    etNickName.setBackgroundResource(R.drawable.button_error)
                    btnNext.isClickable = false
                    btnNext.backgroundTintList = ContextCompat.getColorStateList(this@SignupSecondActivity, R.color.button_unclick)
                } else {
                    nickNameError.text = ""
                    etNickName.setBackgroundResource(R.drawable.edittext_background)
                    btnNext.isClickable = true
                    btnNext.backgroundTintList = ContextCompat.getColorStateList(this@SignupSecondActivity, R.color.main_purple)
                }
            }
        })

        findViewById<Button>(R.id.btn_next).setOnClickListener {
            if (etNickName.length() != 0) {
                SignupData.nickname = nickName
                val intent = Intent(this, SignupThirdActivity::class.java)
                startActivity(intent)
            }
        }
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

    // 기존 유저 닉네임인지 확인
    private fun isValidNickName_3(nickName: String): String? {
        val userNicks = listOf("111", "asdf") // 이곳에 서버 연결하기
        if (nickName in userNicks) return "이미 존재하는 닉네임입니다"
        return null
    }

    // 공백인지 확인
    private fun isValidNickName_4(nickName: String): String? {
        if (nickName.length == 0) return "닉네임을 입력하세요"
        return null
    }
}