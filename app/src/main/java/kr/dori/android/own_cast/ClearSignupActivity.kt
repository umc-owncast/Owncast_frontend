package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClearSignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clear_signup)

        findViewById<Button>(R.id.StartBtn).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        findViewById<TextView>(R.id.sub_text).text = "owncast와 함께\n[${SignupData.interest}-${SignupData.detail_interest}]이야기로 언어 공부를 시작해 보세요!"

    }
}

