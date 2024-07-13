package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.dori.android.own_cast.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val goLanguage = Intent(this, LanguageSetupActivity::class.java)

        binding.goLanguageIv.setOnClickListener {
            startActivity(goLanguage)
        }

        binding.backSignupIv.setOnClickListener {
            finish()
        }
    }
}