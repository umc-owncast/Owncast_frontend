package kr.dori.android.own_cast


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        MainScope().launch {
            delay(3000) // 3 seconds delay
            startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
            finish()
        }
    }
}