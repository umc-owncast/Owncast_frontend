package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val loginButton: Button = findViewById(R.id.loginBtn)
        val signUpButton: Button = findViewById(R.id.signUpBtn)

        loginButton.setOnClickListener {
            // Navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        signUpButton.setOnClickListener {
            // Navigate to SignUpActivity
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}