package kr.dori.android.own_cast

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import kr.dori.android.own_cast.databinding.ActivityInterestSetupBinding

class InterestSetupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInterestSetupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterestSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var data = mutableListOf(
            Signup(R.drawable.ic_launcher_foreground,"드라마/영화"),
            Signup(R.drawable.ic_launcher_foreground,"스포츠"),
            Signup(R.drawable.ic_launcher_foreground,"음악"),
            Signup(R.drawable.ic_launcher_foreground,"음식"),
            Signup(R.drawable.ic_launcher_foreground,"책"),
            Signup(R.drawable.ic_launcher_foreground,"시사"),
            Signup(R.drawable.ic_launcher_foreground,"더보기")
        )
        var adapter = InterestAdapter()
        adapter.dataList = data
        binding.interestRv.adapter = adapter
        binding.interestRv.layoutManager = GridLayoutManager(this,2)



        binding.backLanguageIv.setOnClickListener {
            finish()
        }
    }
}