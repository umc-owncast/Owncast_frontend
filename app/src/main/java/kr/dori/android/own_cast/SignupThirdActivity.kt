package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class SignupThirdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_third)

        val etName = findViewById<EditText>(R.id.etName)
        val etId = findViewById<EditText>(R.id.etId)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etPasswordConfirm = findViewById<EditText>(R.id.etPasswordConfirm)

        val nameError = findViewById<TextView>(R.id.Name_error)
        val idError = findViewById<TextView>(R.id.Id_error)
        val passwordError = findViewById<TextView>(R.id.Password_error)
        val passwordConfirmError = findViewById<TextView>(R.id.PasswordConfirm_error)
        val btnNext = findViewById<Button>(R.id.btn_next)

        var name = SignupData.name
        var id = SignupData.id
        var password = SignupData.password
        var passwordconfirm = SignupData.passwordconfirm

        val nickname = SignupData.nickname
        findViewById<TextView>(R.id.main_text).text = "($nickname)님,\n로그인에 필요한 정보를 입력해주세요"

        // 이전 내용 복원
        if (name != getString(R.string.signup_info_first)) {
            etName.setText(name)
        }
        if (id != getString(R.string.signup_info_first)) {
            etId.setText(id)
        }
        if (password != getString(R.string.signup_info_first)) {
            etPassword.setText(password)
        }
        if (passwordconfirm != getString(R.string.signup_info_first)) {
            etPasswordConfirm.setText(passwordconfirm)
            nextBtnSection(btnNext)
        }


        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, SignupSecondActivity::class.java)
            startActivity(intent)
        }

        btnNext.setOnClickListener {
            if ( SignupData.name != getString(R.string.signup_info_first) && SignupData.id != getString(R.string.signup_info_first) && SignupData.password != getString(R.string.signup_info_first) && SignupData.passwordconfirm != getString(R.string.signup_info_first)) {

                // 현재 내용 저장
                SignupData.name = name
                SignupData.id = id
                SignupData.password = password
                SignupData.passwordconfirm = passwordconfirm

                val intent = Intent(this, SignupLanguageActivity::class.java)
                startActivity(intent)
            }
        }

        // 이름 입력 필드에 대한 텍스트 변경 리스너 설정
        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                name = s.toString()
                val errorMessage = validateName(name) // 유효성 검사
                if (errorMessage == null) {
                    SignupData.name = name
                }
                updateErrorDisplay(nameError, etName, btnNext, errorMessage) // 에러메시지 표시
            }
        })

        // 아이디 입력 필드에 대한 텍스트 변경 리스너 설정
        etId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                id = s.toString()
                val errorMessage = validateId(id) // 유효성 검사
                if (errorMessage == null) {
                    SignupData.id = id
                }
                updateErrorDisplay(idError, etId, btnNext, errorMessage) // 에러메시지 표시
            }
        })

        // 비밀번호 입력 필드에 대한 텍스트 변경 리스너 설정
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                password = s.toString()
                val errorMessage = validatePassword(password) // 유효성 검사
                if (errorMessage == null) {
                    SignupData.password = password
                }
                updateErrorDisplay(passwordError, etPassword, btnNext, errorMessage) // 에러메시지 표시

                // 비밀번호가 변경되면 비밀번호 확인 필드도 확인
                val passwordConfirm = etPasswordConfirm.text.toString()
                val confirmErrorMessage = validatePasswordConfirm(password, passwordConfirm)
                updateErrorDisplay(passwordConfirmError, etPasswordConfirm, btnNext, confirmErrorMessage) // 에러메시지 표시
            }
        })

        // 비밀번호 확인 필드에 대한 텍스트 변경 리스너 설정
        etPasswordConfirm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val password = etPassword.text.toString()
                passwordconfirm = s.toString()
                val errorMessage = validatePasswordConfirm(password, passwordconfirm) // 유효성 검사
                if (errorMessage == null)  {
                    SignupData.passwordconfirm = passwordconfirm
                }
                updateErrorDisplay(passwordConfirmError, etPasswordConfirm, btnNext, errorMessage) // 에러메시지 표시
            }
        })

    }

    // 이름 유효성 검사
    private fun validateName(name: String): String? {
        val regex_1 = "^[가-힣]+$".toRegex() // 한글 전체 문자열
        val regex_2 = "^[a-zA-Z]+$".toRegex() // 영어 전체 문자열

        return when {
            name.isEmpty() -> "이름을 입력해주세요"
            regex_1.matches(name) && name.length > 5 -> "한글 기준 5자 이내로 입력해주세요"
            regex_2.matches(name) && name.length > 20 -> "영어 기준 20자 이내로 입력해주세요"
            !regex_1.matches(name) && !regex_2.matches(name) -> "한글 또는 영어만 입력해주세요"
            else -> null
        }
    }

    // 아이디 유효성 검사
    private fun validateId(id: String): String? {
        val existingIds = listOf("exid1", "exid2") // 이곳에 실제 서버와 연동 필요!!
        return when {
            id.isEmpty() -> "아이디를 입력해주세요"
            existingIds.contains(id) -> "이미 존재하는 아이디입니다"
            id.length !in 5..15 -> "5~15자의 영문 대/소문자, 숫자만 사용해 주세요"
            !Regex("^[a-zA-Z0-9]+$").matches(id) -> "5~15자의 영문 대/소문자, 숫자만 사용해 주세요"
            else -> null
        }
    }

    // 비밀번호 유효성 검사
    private fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> null
            password.length !in 8..16 -> "8~16자로 구성해주세요"
            !password.any { it.isUpperCase() } || !password.any { it.isLowerCase() } || !password.any { it.isDigit() } || !password.any { it in "@#\$%^&+=!" } -> "영문 대/소문자, 숫자, 특수문자를 각 1자 이상 사용해주세요"
            else -> null
        }
    }

    // 비밀번호 확인 유효성 검사
    private fun validatePasswordConfirm(password: String, passwordConfirm: String): String? {
        return if (password != passwordConfirm) {
            "입력한 비밀번호가 서로 다릅니다. 동일한 비밀번호를 입력해 주세요"
        } else {
            null
        }
    }

    // 에러 메시지와 버튼 상태 업데이트
    private fun updateErrorDisplay(errorTextView: TextView, editText: EditText, nextButton: Button, errorMessage: String?) {
        if (errorMessage != null) {
            errorTextView.text = errorMessage
            editText.setBackgroundResource(R.drawable.button_error)
            nextButton.isClickable = false
            nextButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.button_unclick)
        } else {
            errorTextView.text = ""
            editText.setBackgroundResource(R.drawable.edittext_background)
            nextBtnSection(nextButton)
        }
    }

    private fun nextBtnSection(nextButton: Button) {
        if ( SignupData.name != getString(R.string.signup_info_first) && SignupData.id != getString(R.string.signup_info_first) && SignupData.password != getString(R.string.signup_info_first) && SignupData.passwordconfirm != getString(R.string.signup_info_first)){
            nextButton.isClickable = true
            nextButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.main_purple)
        }
    }
}