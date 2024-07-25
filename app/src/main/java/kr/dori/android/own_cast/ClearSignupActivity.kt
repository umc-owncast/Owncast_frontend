package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClearSignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clear_signup)

        findViewById<Button>(R.id.StartBtn).setOnClickListener {
            MainScope().launch {
                delay(3000) // 3 seconds delay
                startActivity(Intent(this@ClearSignupActivity, SplashActivity::class.java))
                Log.d("Check_Signup_Success", "회원가입에 성공하였습니다.")
                Log.d("msg", "회원정보")
                Log.d("check_nickname", SignupData.nickname)
                Log.d("check_name", SignupData.name)
                Log.d("check_id", SignupData.id)
                Log.d("check_password", SignupData.password)
                finish()
            }
        }

    }
}

