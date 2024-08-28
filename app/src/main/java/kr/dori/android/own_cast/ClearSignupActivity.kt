package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class ClearSignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clear_signup)
        enableEdgeToEdge()

        findViewById<Button>(R.id.StartBtn).setOnClickListener {
            // 모든 이전 액티비티를 지우고 MainActivity를 시작
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }

        findViewById<TextView>(R.id.sub_text).text = "owncast와 함께\n[${SignupData.detail_interest}]이야기로 언어 공부를 시작해 보세요!"
    }
}