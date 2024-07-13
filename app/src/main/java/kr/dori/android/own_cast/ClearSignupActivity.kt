package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.dori.android.own_cast.databinding.ActivityClearSignupBinding

class ClearSignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClearSignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClearSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this,LoginActivity::class.java)
        binding.goLoginIv.setOnClickListener {
            startActivity(intent)
        }

    }
}