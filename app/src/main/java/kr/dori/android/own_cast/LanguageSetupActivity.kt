package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class LanguageSetupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_setup)
    }

    fun onBackClick(view: View) {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    fun onLanguageClick(view: View) {
        val intent = Intent(this, LanguageAccentActivity::class.java)
        startActivity(intent)
    }
}