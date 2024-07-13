package kr.dori.android.own_cast

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kr.dori.android.own_cast.databinding.ActivityLanguageSetupBinding

class LanguageSetupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLanguageSetupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var data = mutableListOf(
            Signup(R.drawable.england, "영국"),
            Signup(R.drawable.japan,"일본"),
            Signup(R.drawable.spain,"스페인")
        )
        var adapter = LanguageAdapter()
        adapter.dataList = data
        binding.languageRv.adapter = adapter
        binding.languageRv.layoutManager = LinearLayoutManager(this)


        binding.backSignupIv.setOnClickListener {
            finish()
        }
    }
}