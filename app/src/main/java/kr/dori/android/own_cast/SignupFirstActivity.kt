package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat

class SignupFirstActivity : ComponentActivity() {

    private var isChecked = false // 체크 상태를 관리하는 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_first)
        enableEdgeToEdge()

        val backButton = findViewById<ImageView>(R.id.backButton)
        val nextButton = findViewById<Button>(R.id.btn_next)
        val checkBox = findViewById<ImageView>(R.id.checkbox)

        // 상태 복원
        if (SignupData.ok_btn == 1) {
            checkBox.setImageResource(R.drawable.signup_check)
            isChecked = true
            updateNextButtonState(isChecked, nextButton)
        }

        // 체크박스 이미지 클릭 시 상태 변화
        checkBox.setOnClickListener {
            isChecked = !isChecked
            val imageRes = if (isChecked) R.drawable.signup_check else R.drawable.signup_uncheck
            checkBox.setImageResource(imageRes)
            updateNextButtonState(isChecked, nextButton)
        }

        // 백 버튼 클릭 시 AuthActivity로 이동
        backButton.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }

        // 다음 버튼 클릭 시 SignupSecondActivity로 이동
        nextButton.setOnClickListener {
            if (isChecked) {
                // 상태 저장
                SignupData.ok_btn = 1
                startActivity(Intent(this, SignupSecondActivity::class.java))
            }
        }
    }

    // 다음 버튼 상태 업데이트 함수
    private fun updateNextButtonState(isEnabled: Boolean, nextButton: Button) {
        nextButton.isClickable = isEnabled
        val color = if (isEnabled) R.color.main_purple else R.color.button_unclick
        nextButton.backgroundTintList = ContextCompat.getColorStateList(this, color)
    }
}