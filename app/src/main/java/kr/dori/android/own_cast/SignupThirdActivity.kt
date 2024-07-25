package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SignupThirdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_third)

        findViewById<Button>(R.id.btn_next).setOnClickListener {
            startActivity(Intent(this, ClearSignupActivity::class.java))
        }

        // TextWatcher를 추가하여 EditText의 입력 변경 감지
        findViewById<EditText>(R.id.etId).addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트가 변경되기 전의 처리 (필요없으면 빈 구현)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val id = s.toString()
                // 전역 변수 값 수정
                SignupData.id = id
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트가 변경된 후의 처리 (필요없으면 빈 구현)
            }
        })

        // TextWatcher를 추가하여 EditText의 입력 변경 감지
        findViewById<EditText>(R.id.etPassword).addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트가 변경되기 전의 처리 (필요없으면 빈 구현)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                // 전역 변수 값 수정
                SignupData.password = password
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트가 변경된 후의 처리 (필요없으면 빈 구현)
            }
        })

        // TextWatcher를 추가하여 EditText의 입력 변경 감지
        findViewById<EditText>(R.id.etName).addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트가 변경되기 전의 처리 (필요없으면 빈 구현)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val name = s.toString()
                // 전역 변수 값 수정
                SignupData.name = name
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트가 변경된 후의 처리 (필요없으면 빈 구현)
            }
        })
    }
}
   /* override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_third)

        val backButton: ImageView = findViewById(R.id.backButton)
        val etName: EditText = findViewById(R.id.etName)
        val etNickname: EditText = findViewById(R.id.etNickname)
        val etId: EditText = findViewById(R.id.etId)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val etPasswordConfirm: EditText = findViewById(R.id.etPasswordConfirm)
        val btnSignUp: Button = findViewById(R.id.btnSignUp)

        backButton.setOnClickListener {
            finish()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnSignUp.isEnabled = validateInputs()
                etName.error = null
                etNickname.error = null
                etId.error = null
                etPassword.error = null
                etPasswordConfirm.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        etName.addTextChangedListener(textWatcher)
        etNickname.addTextChangedListener(textWatcher)
        etId.addTextChangedListener(textWatcher)
        etPassword.addTextChangedListener(textWatcher)
        etPasswordConfirm.addTextChangedListener(textWatcher)

        btnSignUp.setOnClickListener {
            var hasError = false

            if (!validateName(etName.text.toString())) {
                etName.error = "이름 : 한글 기준 5자 이내, 영문 기준 20자 이내로 입력해주세요"
                hasError = true
            }


            if (!validateNickname_2(etNickname.text.toString())) {
                etNickname.error = "닉네임 : 10자 이내로 구성해주세요"
                hasError = true
            }

            if (!validateNickname_3(etNickname.text.toString())) {
                etNickname.error = "닉네임 : 영문 대/소문자, 한글, 숫자, 하이픈(-), 언더스코어(_)만 사용해주세요"
                hasError = true
            }

            if (!validateId_1(etId.text.toString())) {
                etId.error = "아이디 : 이미 존재하는 아이디입니다"
                hasError = true
            }

            if (!validateId_2(etId.text.toString())) {
                etId.error = "아이디 : 5~15자의 영문 대/소문자, 숫자만 사용해 주세요"
                hasError = true
            }

            if (!validatePassword_1(etPassword.text.toString())) {
                etPassword.error = "비밀번호 : 8~16자로 구성해주세요"
                hasError = true
            }

            if (!validatePassword_2(etPassword.text.toString())) {
                etPassword.error = "비밀번호 : 영문 대/소문자, 숫자, 특수문자를 각 1자 이상 사용해 주세요"
                hasError = true
            }

            if (etPassword.text.toString() != etPasswordConfirm.text.toString()) {
                etPasswordConfirm.error = "비밀번호 : 일치하지 않습니다"
                hasError = true
            }

            if (!hasError) {
                Toast.makeText(this, "회원가입 완료", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LanguageAccentActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun validateInputs(): Boolean {
        val etName: EditText = findViewById(R.id.etName)
        val etNickname: EditText = findViewById(R.id.etNickname)
        val etId: EditText = findViewById(R.id.etId)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val etPasswordConfirm: EditText = findViewById(R.id.etPasswordConfirm)

        return etName.text.isNotEmpty() && etNickname.text.isNotEmpty() &&
                etId.text.isNotEmpty() && etPassword.text.isNotEmpty() &&
                etPasswordConfirm.text.isNotEmpty()
    }

    private fun validateName(name: String): Boolean {
        val regex = Regex("^[가-힣]{1,5}\$|^[a-zA-Z]{1,20}\$")
        return regex.matches(name)
    }


    private fun validateNickname_1(nickname: String): Boolean {
        return nickname.length <= 10
    }

    private fun validateNickname_2(nickname: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9가-힣-_]+$")
        return regex.matches(nickname)
    }


    private fun validateId_1(id: String): Boolean {
        val existingIds = listOf("existingId1", "existingId2") // 2. 이 목록을 실제 서버와 연동 필요
        return !existingIds.contains(id)
    }

    private fun validateId_2(id: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9]{5,15}\$")
        return regex.matches(id)
    }

    private fun validatePassword_1(password: String): Boolean {
        val isValidLength = password.length in 8..16
        return isValidLength
    }

    private fun validatePassword_2(password: String): Boolean {
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { it in "@#\$%^&+=!" }

        return hasLetter && hasDigit && hasSpecialChar
    }*/

