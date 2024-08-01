package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SignupLoadingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_loading)

        findViewById<TextView>(R.id.main_text).text = "[${SignupData.nickname}]님의 취향을\nowncast에 반영하고 있어요"

        MainScope().launch {
            delay(3000) // 3 seconds delay - now : logout
            startActivity(Intent(this@SignupLoadingActivity, ClearSignupActivity::class.java))
            finish()
        }
    }
}