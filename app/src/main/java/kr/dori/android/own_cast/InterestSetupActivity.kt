package kr.dori.android.own_cast

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import kr.dori.android.own_cast.databinding.ActivityInterestSetupBinding
import kr.dori.android.own_cast.databinding.ActivityKeywordBinding

class InterestSetupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKeywordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKeywordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backMainIv.setOnClickListener {
            finish()
        }
    }
}