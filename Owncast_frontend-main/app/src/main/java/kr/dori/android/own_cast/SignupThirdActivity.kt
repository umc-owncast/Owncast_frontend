package kr.dori.android.own_cast

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class SignupThirdActivity : AppCompatActivity() {

    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_third)
        enableEdgeToEdge()

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
        findViewById<TextView>(R.id.main_text).text = "${nickname}님,\n로그인에 필요한 정보를 입력해주세요"

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
            if (SignupData.name != getString(R.string.signup_info_first) && SignupData.id != getString(R.string.signup_info_first) && SignupData.password != getString(R.string.signup_info_first) && SignupData.passwordconfirm != getString(R.string.signup_info_first)) {
                SignupData.name = name
                SignupData.id = id
                SignupData.password = password
                SignupData.passwordconfirm = passwordconfirm

                val intent = Intent(this, SignupLanguageActivity::class.java)
                startActivity(intent)
            }
        }


        scrollView = findViewById(R.id.scroll_view)

        // dp를 픽셀로 변환
        var offset = dpToPx(20, this)
        etName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // 스크롤뷰 스크롤
                scrollView.post {
                    scrollView.smoothScrollBy(0, +offset)
                }
            }
        }

        // dp를 픽셀로 변환
        offset = dpToPx(40, this)
        etId.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // 스크롤뷰 스크롤
                scrollView.post {
                    scrollView.smoothScrollBy(0, +offset)
                }
            }
        }

        // dp를 픽셀로 변환
        offset = dpToPx(60, this)
        etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // 스크롤뷰 스크롤
                scrollView.post {
                    scrollView.smoothScrollBy(0, +offset)
                }
            }
        }

        // dp를 픽셀로 변환
        offset = dpToPx(80, this)
        etPasswordConfirm.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // 스크롤뷰 스크롤
                scrollView.post {
                    scrollView.smoothScrollBy(0, +offset)
                }
            }
        }


        // 유효성 검사
        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                name = s.toString()
                val errorMessage = validateName(name)
                if (errorMessage == null) {
                    SignupData.name = name
                }
                updateErrorDisplay(nameError, etName, btnNext, errorMessage)
            }
        })

        etId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                id = s.toString()
                validateId(id, idError, etId, btnNext)
            }
        })

        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                password = s.toString()
                val errorMessage = validatePassword(password)
                if (errorMessage == null) {
                    SignupData.password = password
                }
                updateErrorDisplay(passwordError, etPassword, btnNext, errorMessage)

                val passwordConfirm = etPasswordConfirm.text.toString()
                val confirmErrorMessage = validatePasswordConfirm(password, passwordConfirm)
                updateErrorDisplay(passwordConfirmError, etPasswordConfirm, btnNext, confirmErrorMessage)
            }
        })

        etPasswordConfirm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                passwordconfirm = s.toString()
                val errorMessage = validatePasswordConfirm(password, passwordconfirm)
                if (errorMessage == null) {
                    SignupData.passwordconfirm = passwordconfirm
                }
                updateErrorDisplay(passwordConfirmError, etPasswordConfirm, btnNext, errorMessage)
            }
        })
    }

    private fun dpToPx(dp: Int, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }


    private fun validateId(id: String, idError: TextView, etId: EditText, btnNext: Button) {
        if (id.isEmpty()) {
            updateErrorDisplay(idError, etId, btnNext, "아이디를 입력해주세요")
            return
        }

        if (id.length !in 5..15) {
            updateErrorDisplay(idError, etId, btnNext, "5~15자의 영문 대/소문자, 숫자만 사용해 주세요")
            return
        }

        if (!Regex("^[a-zA-Z0-9]+$").matches(id)) {
            updateErrorDisplay(idError, etId, btnNext, "5~15자의 영문 대/소문자, 숫자만 사용해 주세요")
            return
        }

        RetrofitClient.instance.checkID(id).enqueue(object : Callback<CheckIDResponse> {
            override fun onResponse(call: Call<CheckIDResponse>, response: Response<CheckIDResponse>) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val isIdTaken = response.body()?.result ?: true
                    if (isIdTaken) {
                        updateErrorDisplay(idError, etId, btnNext, "이미 존재하는 아이디입니다")
                    } else {
                        SignupData.id = id
                        updateErrorDisplay(idError, etId, btnNext, null)
                    }
                } else {
                    updateErrorDisplay(idError, etId, btnNext, "아이디 확인 중 오류가 발생했습니다")
                }
            }

            override fun onFailure(call: Call<CheckIDResponse>, t: Throwable) {
                updateErrorDisplay(idError, etId, btnNext, "아이디 확인 중 오류가 발생했습니다")
            }
        })
    }

    private fun validateName(name: String): String? {
        val regex_1 = "^[가-힣]+$".toRegex()
        val regex_2 = "^[a-zA-Z]+$".toRegex()

        return when {
            name.isEmpty() -> "이름을 입력해주세요"
            regex_1.matches(name) && name.length > 5 -> "한글 기준 5자 이내로 입력해주세요"
            regex_2.matches(name) && name.length > 20 -> "영어 기준 20자 이내로 입력해주세요"
            !regex_1.matches(name) && !regex_2.matches(name) -> "한글 또는 영어만 입력해주세요"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> null
            password.length !in 8..16 -> "8~16자로 구성해주세요"
            !password.any { it.isUpperCase() } || !password.any { it.isLowerCase() } || !password.any { it.isDigit() } || !password.any { it in "@#~\$%^&+=!" } -> "영문 대/소문자, 숫자, 특수문자를 각 1자 이상 사용해주세요"
            else -> null
        }
    }

    private fun validatePasswordConfirm(password: String, passwordConfirm: String): String? {
        return if (password != passwordConfirm) {
            "입력한 비밀번호가 서로 다릅니다."
        } else {
            null
        }
    }

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
        val ready = SignupData.name != getString(R.string.signup_info_first) && SignupData.id != getString(R.string.signup_info_first) && SignupData.password != getString(R.string.signup_info_first) && SignupData.passwordconfirm != getString(R.string.signup_info_first)
        nextButton.isClickable = ready
        nextButton.backgroundTintList = ContextCompat.getColorStateList(this, if (ready) R.color.main_purple else R.color.button_unclick)
    }
}