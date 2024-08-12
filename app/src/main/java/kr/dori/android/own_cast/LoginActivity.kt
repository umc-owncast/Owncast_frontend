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
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

            // 유효성 검사
            if (isValidLogin(enteredId, enteredPassword)) {
                // 메인 페이지로 이동
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // 에러 메시지 표시
                errorMsg.visibility = TextView.VISIBLE
            }
        }
    }

    private fun isValidLogin(id: String, password: String): Boolean {

        // 이곳에 서버 연결하기 -> 아이디와 비번 쌍을 줘서 해당 정보가 서버에 있는지 확인하기, 서버에서 9개 전역변수들의 값을 불러와서 전역변수에 각각 저장하기
        val validIds = listOf("user1", "user2", "user3")
        val validPasswords = listOf("password1!", "password2@", "password3#")

        return validIds.contains(id) && validPasswords.contains(password)
    }

}