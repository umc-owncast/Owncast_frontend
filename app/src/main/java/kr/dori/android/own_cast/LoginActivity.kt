package kr.dori.android.own_cast



import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.ComponentActivity


class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }

        findViewById<Button>(R.id.login_btn).setOnClickListener {
            val etId = findViewById<EditText>(R.id.etId)
            val etPassword = findViewById<EditText>(R.id.etPassword)

            // 입력된 아이디와 비밀번호
            val enteredId = etId.text.toString()
            val enteredPassword = etPassword.text.toString()

            // 아이디와 비밀번호가 각각 유효한지 확인
            if (isValidId(enteredId) && isValidPassword(enteredPassword)) {
                startActivity(Intent(this, AuthAfterActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }


    fun isValidId(id: String): Boolean {
        // 예시 데이터
        val validIds = listOf(
            "user1",
            "user2",
            "user3"
        )
        // 아이디가 유효한지 확인
        return validIds.contains(id)
    }

    fun isValidPassword(password: String): Boolean {
        // 예시 데이터
        val validPasswords = listOf(
            "password1!",
            "password2@",
            "password3#"
        )
        // 비밀번호가 유효한지 확인
        return validPasswords.contains(password)
    }
}
