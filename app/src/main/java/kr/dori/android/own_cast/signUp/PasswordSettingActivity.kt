package kr.dori.android.own_cast.signUp

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import kr.dori.android.own_cast.R


class PasswordSettingActivity : ComponentActivity() {
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_language)
        enableEdgeToEdge()
    }
}