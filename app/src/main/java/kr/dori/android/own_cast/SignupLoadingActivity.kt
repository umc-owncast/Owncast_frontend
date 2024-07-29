package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SignupDLoadingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_loading)

        MainScope().launch {
            delay(3000) // 3 seconds delay - now : logout
            startActivity(Intent(this@SignupDLoadingActivity, ClearSignupActivity::class.java))
            finish()
        }
    }
}