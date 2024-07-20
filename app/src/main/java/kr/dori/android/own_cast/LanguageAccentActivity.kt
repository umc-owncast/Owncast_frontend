package kr.dori.android.own_cast

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class LanguageAccentActivity : AppCompatActivity() {

    private lateinit var englishLayout: LinearLayout
    private lateinit var japaneseLayout: LinearLayout
    private lateinit var spanishLayout: LinearLayout
    private lateinit var englishToggle: LinearLayout
    private lateinit var japaneseToggle: LinearLayout
    private lateinit var spanishToggle: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_accent)

        englishLayout = findViewById(R.id.english_layout)
        japaneseLayout = findViewById(R.id.japanese_layout)
        spanishLayout = findViewById(R.id.spanish_layout)
        englishToggle = findViewById(R.id.english_toggle)
        japaneseToggle = findViewById(R.id.japanese_toggle)
        spanishToggle = findViewById(R.id.spanish_toggle)

        englishLayout.setOnClickListener {
            toggleLanguageSelection(englishLayout, englishToggle)
        }

        japaneseLayout.setOnClickListener {
            toggleLanguageSelection(japaneseLayout, japaneseToggle)
        }

        spanishLayout.setOnClickListener {
            toggleLanguageSelection(spanishLayout, spanishToggle)
        }
    }

    private fun toggleLanguageSelection(selectedLayout: LinearLayout, toggleLayout: LinearLayout) {
        resetLayouts()
        selectedLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.black))
        toggleLayout.visibility = View.VISIBLE

        if (selectedLayout != englishLayout) {
            englishLayout.alpha = 0.4f
        }
        if (selectedLayout != japaneseLayout) {
            japaneseLayout.alpha = 0.4f
        }
        if (selectedLayout != spanishLayout) {
            spanishLayout.alpha = 0.4f
        }
    }

    private fun resetLayouts() {
        englishLayout.setBackgroundColor(Color.parseColor("#ECEFF1"))
        japaneseLayout.setBackgroundColor(Color.parseColor("#ECEFF1"))
        spanishLayout.setBackgroundColor(Color.parseColor("#ECEFF1"))
        englishToggle.visibility = View.GONE
        japaneseToggle.visibility = View.GONE
        spanishToggle.visibility = View.GONE
        englishLayout.alpha = 1.0f
        japaneseLayout.alpha = 1.0f
        spanishLayout.alpha = 1.0f
    }

    fun onBackClick(view: View) {
        val intent = Intent(this, LanguageAccentActivity::class.java)
        startActivity(intent)
    }

    fun onMyLanguageClick(view: View) {
        val intent = Intent(this, InterestSetupActivity::class.java)
        startActivity(intent)
    }
}