package kr.dori.android.own_cast


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (SignupData.nickname == getString(R.string.signup_info_first)) {
            MainScope().launch {
                delay(3000) // 3 seconds delay - now : logout
                startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
                finish()
            }
        } else {
            MainScope().launch {
                delay(2000) // 2 seconds delay - now : login
                startActivity(Intent(this@SplashActivity, AuthAfterActivity::class.java))
                finish()
            }
        }

    }
}