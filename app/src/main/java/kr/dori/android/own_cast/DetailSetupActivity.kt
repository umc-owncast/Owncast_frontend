package kr.dori.android.own_cast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat

class DetailSetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_setup)

        fun onBackClick(view: View) {
            val intent = Intent(this, InterestSetupActivity::class.java)
            startActivity(intent)
        }

    }
}