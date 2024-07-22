package kr.dori.android.own_cast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat

class InterestSetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest_setup)

        val backButton: ImageView = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        fun onMyInterestClick(view: View) {
            val intent = Intent(this, DetailSetupActivity::class.java)
            startActivity(intent)
        }

        fun onMySelfClick(view: View) {
            val intent = Intent(this, DetailSelfActivity::class.java)
            startActivity(intent)
        }

    }
}