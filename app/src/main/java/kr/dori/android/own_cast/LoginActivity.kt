package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.dori.android.own_cast.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val goMain = Intent(this, MainActivity::class.java)

        binding.backSignupIv.setOnClickListener {
            finish()
        }

        binding.goMain.setOnClickListener {
            startActivity(goMain)
        }
    }
}