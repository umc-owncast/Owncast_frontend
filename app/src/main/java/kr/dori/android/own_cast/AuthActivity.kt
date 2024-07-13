package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.dori.android.own_cast.databinding.ActivityAuthBinding
/*
Room DB나 유사 도구로 UserInfo를 저장해서 아이디 유효성 검사 때 한번, 회원가입 완료하고 관심사, 언어 정보를 다시 보내야 함
 */
class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val goSign = Intent(this, SignupActivity::class.java)
        val goLogin = Intent(this,LoginActivity::class.java)


        binding.loginIv.setOnClickListener {
            startActivity(goLogin)
        }

        binding.signupIv.setOnClickListener {
            startActivity(goSign)
        }
    }
}