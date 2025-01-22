package kr.dori.android.own_cast.signUp


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.SignupData


class SignupLanguageActivity : ComponentActivity() {
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_language)
        enableEdgeToEdge()

        val backButton = findViewById<ImageView>(R.id.backButton)
        nextButton = findViewById(R.id.btn_next)

        backButton.setOnClickListener {
            startActivity(Intent(this, SignupThirdActivity::class.java))
        }

        findViewById<View>(R.id.EnSection).setOnClickListener(::onEnSectionClick)
        findViewById<View>(R.id.JpSection).setOnClickListener(::onJpSectionClick)
        findViewById<View>(R.id.SpSection).setOnClickListener(::onSpSectionClick)

        nextButton.setOnClickListener {
            if ( SignupData.language != getString(R.string.signup_info_first) && SignupData.accent != getString(
                    R.string.signup_info_first
                ) ) {
                startActivity(Intent(this, SignupFifthActivity::class.java))
            }
        }
    }

    private fun onEnSectionClick(view: View) {
        findViewById<TextView>(R.id.main_text).text = "원하는 발음을 선택해주세요"

        setSectionVisibility(R.id.JpSection, R.id.JpSection_togle, false)
        setSectionVisibility(R.id.SpSection, R.id.SpSection_togle, false)
        setSectionVisibility(R.id.EnSection, R.id.EnSection_togle, true)

        findViewById<View>(R.id.EnSection_togle_usa).setOnClickListener(::toggleOnClickEnUsa)
        findViewById<View>(R.id.EnSection_togle_eng).setOnClickListener(::toggleOnClickEnEng)
        findViewById<View>(R.id.EnSection_togle_aus).setOnClickListener(::toggleOnClickEnAus)
        findViewById<View>(R.id.EnSection_togle_ind).setOnClickListener(::toggleOnClickEnInd)
    }

    private fun onJpSectionClick(view: View) {
        findViewById<TextView>(R.id.main_text).text = "원하는 발음을 선택해주세요"

        setSectionVisibility(R.id.EnSection, R.id.EnSection_togle, false)
        setSectionVisibility(R.id.SpSection, R.id.SpSection_togle, false)
        setSectionVisibility(R.id.JpSection, R.id.JpSection_togle, true)

        findViewById<View>(R.id.JpSection_togle_jp).setOnClickListener(::toggleOnClickJpJp)
    }

    private fun onSpSectionClick(view: View) {
        findViewById<TextView>(R.id.main_text).text = "원하는 발음을 선택해주세요"

        setSectionVisibility(R.id.EnSection, R.id.EnSection_togle, false)
        setSectionVisibility(R.id.JpSection, R.id.JpSection_togle, false)
        setSectionVisibility(R.id.SpSection, R.id.SpSection_togle, true)

        findViewById<View>(R.id.SpSection_togle_sp).setOnClickListener(::toggleOnClickSpSp)
        findViewById<View>(R.id.SpSection_togle_usa).setOnClickListener(::toggleOnClickSpUsa)
    }

    private fun setSectionVisibility(sectionId: Int, toggleId: Int, isVisible: Boolean) {
        val section = findViewById<LinearLayout>(sectionId)
        val toggle = findViewById<LinearLayout>(toggleId)
        section.setBackgroundResource(
            if (isVisible) R.drawable.button_purple else R.drawable.button_toggle
        )
        toggle.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun toggleOnClickEnUsa(view: View) {
        activateToggle(R.id.EnSection_togle_usa, true)
        activateToggle(R.id.EnSection_togle_eng, false)
        activateToggle(R.id.EnSection_togle_aus, false)
        activateToggle(R.id.EnSection_togle_ind, false)

        // 상태 저장
        SignupData.language = "English"
        SignupData.accent = "usa"
    }

    private fun toggleOnClickEnEng(view: View) {
        activateToggle(R.id.EnSection_togle_usa, false)
        activateToggle(R.id.EnSection_togle_eng, true)
        activateToggle(R.id.EnSection_togle_aus, false)
        activateToggle(R.id.EnSection_togle_ind, false)

        // 상태 저장
        SignupData.language = "English"
        SignupData.accent = "eng"
    }

    private fun toggleOnClickEnAus(view: View) {
        activateToggle(R.id.EnSection_togle_usa, false)
        activateToggle(R.id.EnSection_togle_eng, false)
        activateToggle(R.id.EnSection_togle_aus, true)
        activateToggle(R.id.EnSection_togle_ind, false)

        // 상태 저장
        SignupData.language = "English"
        SignupData.accent = "aus"
    }

    private fun toggleOnClickEnInd(view: View) {
        activateToggle(R.id.EnSection_togle_usa, false)
        activateToggle(R.id.EnSection_togle_eng, false)
        activateToggle(R.id.EnSection_togle_aus, false)
        activateToggle(R.id.EnSection_togle_ind, true)

        // 상태 저장
        SignupData.language = "English"
        SignupData.accent = "ind"
    }

    private fun toggleOnClickJpJp(view: View) {
        activateToggle(R.id.JpSection_togle_jp, true)

        // 상태 저장
        SignupData.language = "Japanese"
        SignupData.accent = "jp"
    }

    private fun toggleOnClickSpSp(view: View) {
        activateToggle(R.id.SpSection_togle_sp, true)
        activateToggle(R.id.SpSection_togle_usa, false)

        // 상태 저장
        SignupData.language = "Spanish"
        SignupData.accent = "sp"
    }

    private fun toggleOnClickSpUsa(view: View) {
        activateToggle(R.id.SpSection_togle_sp, false)
        activateToggle(R.id.SpSection_togle_usa, true)

        // 상태 저장
        SignupData.language = "Spanish"
        SignupData.accent = "usa"
    }

    private fun activateToggle(toggleId: Int, isVisible: Boolean) {
        val section = findViewById<FrameLayout>(toggleId)
        section.setBackgroundResource(
            if (isVisible) R.drawable.button_purple else R.drawable.button_white
        )

        nextButton.isClickable = true
        nextButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.main_purple)
    }
}