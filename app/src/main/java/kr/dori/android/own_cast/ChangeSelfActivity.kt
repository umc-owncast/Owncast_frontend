package kr.dori.android.own_cast

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity


class ChangeSelfActivity : ComponentActivity() {
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_language)
    }
}
