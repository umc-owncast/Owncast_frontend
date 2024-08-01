package kr.dori.android.own_cast


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AuthAfterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_after)

        MainScope().launch {
            delay(2000) // 2 seconds delay
            startActivity(Intent(this@AuthAfterActivity, MainActivity::class.java))
            finish()
        }
    }
}

