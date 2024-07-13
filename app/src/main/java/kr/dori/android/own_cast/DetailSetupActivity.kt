package kr.dori.android.own_cast

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import kr.dori.android.own_cast.databinding.ActivityDetailSetupBinding

class DetailSetupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailSetupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var data = mutableListOf(
            Signup(R.drawable.ic_launcher_foreground,"야구"),
            Signup(R.drawable.ic_launcher_foreground,"야구"),
            Signup(R.drawable.ic_launcher_foreground,"야구"),
            Signup(R.drawable.ic_launcher_foreground,"야구"),
            Signup(R.drawable.ic_launcher_foreground,"야구"),
            Signup(R.drawable.ic_launcher_foreground,"야구"),
            Signup(R.drawable.ic_launcher_foreground,"야구"),
            Signup(R.drawable.ic_launcher_foreground,"야구")
        )

        var adapter = DetailAdapter()
        adapter.dataList = data
        binding.detailRv.adapter = adapter
        binding.detailRv.layoutManager = GridLayoutManager(this,3)

        binding.backInterestIv.setOnClickListener {
            finish()
        }
    }
}