package kr.dori.android.own_cast


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat

class SignupSecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_second)

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            startActivity(Intent(this, SignupFirstActivity::class.java))
        }

        findViewById<Button>(R.id.btn_next).setOnClickListener {
            startActivity(Intent(this, SignupThirdActivity::class.java))
        }


        // TextWatcher를 추가하여 EditText의 입력 변경 감지
        findViewById<EditText>(R.id.etNickName).addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트가 변경되기 전의 처리 (필요없으면 빈 구현)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val nickName = s.toString()
                if (!isValidNickName(nickName)) {
                    // 닉네임이 유효하지 않은 경우
                    findViewById<EditText>(R.id.etNickName).setBackgroundResource(R.drawable.button_error) // 에러 배경으로 설정
                    findViewById<TextView>(R.id.NickName_error).visibility = View.VISIBLE
                    findViewById<Button>(R.id.btn_next).isEnabled = false
                    findViewById<Button>(R.id.btn_next).setBackgroundColor(ContextCompat.getColor(this@SignupSecondActivity, R.color.button_unclick))
                } else {
                    findViewById<EditText>(R.id.etNickName).setBackgroundResource(R.drawable.edittext_background) // 기본 배경으로 설정
                    findViewById<TextView>(R.id.NickName_error).visibility = View.GONE
                    findViewById<Button>(R.id.btn_next).isEnabled = true
                    findViewById<Button>(R.id.btn_next).setBackgroundColor(ContextCompat.getColor(this@SignupSecondActivity, R.color.main_purple))

                    // 전역 변수 값 수정
                    SignupData.nickname = nickName
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트가 변경된 후의 처리 (필요없으면 빈 구현)
            }
        })

    }

    private fun isValidNickName(nickName: String): Boolean {
        // 닉네임이 10자 이내인지 확인
        if (nickName.length > 10) return false

        // 닉네임이 유효한 문자만 포함하는지 확인
        val regex = "^[a-zA-Z0-9-_]+$".toRegex()
        return regex.matches(nickName)
    }
}