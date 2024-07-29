package kr.dori.android.own_cast


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class SignupFirstActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_first)

        val backButton = findViewById<ImageView>(R.id.backButton)
        val nextButton = findViewById<Button>(R.id.btn_next)
        val checkBox1 = findViewById<CheckBox>(R.id.ok_btn_1)
        val checkBox2 = findViewById<CheckBox>(R.id.ok_btn_2)

        // 상태 복원
        if (SignupData.ok_btn_1 == 1) {
            checkBox1.isChecked = true
        }
        if (SignupData.ok_btn_2 == 1) {
            checkBox2.isChecked = true
        }
        updateNextButtonState(checkBox1.isChecked && checkBox2.isChecked, nextButton)

        // 백 버튼 클릭 시 AuthActivity로 이동
        backButton.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }

        // 체크박스 상태 변화 시 다음 버튼 활성화 & 색변환
        val checkBoxes = listOf(checkBox1, checkBox2)
        for (checkBox in checkBoxes) {
            checkBox.setOnCheckedChangeListener { _, _ ->
                updateNextButtonState(checkBox1.isChecked && checkBox2.isChecked, nextButton)
            }
        }

        // 다음 버튼 클릭 시 SignupSecondActivity로 이동
        nextButton.setOnClickListener {
            // 상태 저장
            SignupData.ok_btn_1 = 1
            SignupData.ok_btn_2 = 1

            if (checkBox1.isChecked && checkBox2.isChecked) {
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