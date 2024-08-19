package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class SignupSelfActivity : ComponentActivity() {

    private val MAX_LENGTH = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_self)

        val etSelf = findViewById<EditText>(R.id.etSelf)
        val textNum = findViewById<TextView>(R.id.text_num)
        val backButton = findViewById<ImageView>(R.id.backButton)
        val btnNext = findViewById<Button>(R.id.btn_next)
        var check = 0

        findViewById<TextView>(R.id.main_text).text = "${SignupData.nickname}님이 가장 많은\n시간을 보내는 분야를 선택해주세요"

        // 초기에는 완료 버튼을 비활성화
        btnNext.isEnabled = false
        btnNext.setOnClickListener {
            startActivity(Intent(this, SignupLoadingActivity::class.java))
        }

        // TextWatcher를 사용하여 EditText의 글자 수에 따라 처리
        etSelf.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 입력된 글자 수
                val inputLength = s?.length ?: 0

                // 최대 길이를 초과하지 않도록 처리
                if (inputLength > MAX_LENGTH) {
                    etSelf.setText(s?.substring(0, MAX_LENGTH))
                    etSelf.setSelection(MAX_LENGTH)
                }

                // 텍스트 뷰에 글자 수 표시
                textNum.text = "(${Math.min(inputLength, MAX_LENGTH)}자/$MAX_LENGTH 자)"
            }

            override fun afterTextChanged(s: Editable?) {
                // 상태 저장
                SignupData.detail_interest = s.toString()

                // 버튼 상태 업데이트
                btnNext.isEnabled = true
                btnNext.setBackgroundTintList(ContextCompat.getColorStateList(this@SignupSelfActivity, R.color.main_purple))
            }
        })

        backButton.setOnClickListener {
            val intent = if (SignupData.interest == "self") {
                Intent(this, SignupFifthActivity::class.java)
            } else {
                Intent(this, SignupDetailInterestActivity::class.java)
            }
            startActivity(intent)
        }

    }
}