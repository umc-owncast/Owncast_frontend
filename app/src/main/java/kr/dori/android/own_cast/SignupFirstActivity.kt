package kr.dori.android.own_cast


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat

class SignupFirstActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_first)

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }

        // 체크박스 상태 변경 리스너 설정
        findViewById<CheckBox>(R.id.ok_btn).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 체크박스가 체크된 경우: 버튼 클릭 가능 및 배경색 변경
                findViewById<Button>(R.id.btn_next).isClickable = true
                findViewById<Button>(R.id.btn_next).setBackgroundColor(ContextCompat.getColor(this, R.color.main_purple))
            } else {
                // 체크박스가 체크 해제된 경우: 버튼 클릭 불가능 및 배경색 변경
                findViewById<Button>(R.id.btn_next).isClickable = false
                findViewById<Button>(R.id.btn_next).setBackgroundColor(ContextCompat.getColor(this, R.color.button_unclick))
            }
        }

        findViewById<Button>(R.id.btn_next).setOnClickListener {
            startActivity(Intent(this, SignupSecondActivity::class.java))
        }

    }
}