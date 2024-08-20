package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChangeLanguageActivity: ComponentActivity() {

    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_language)
        enableEdgeToEdge()

        val backButton = findViewById<ImageView>(R.id.backButton)
        nextButton = findViewById(R.id.btn_next)

        backButton.setOnClickListener {
            SignupData.profile_detail_interest = "완료"
            val intent = Intent(this, MainActivity ::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.EnSection).setOnClickListener(::onEnSectionClick)
        findViewById<View>(R.id.JpSection).setOnClickListener(::onJpSectionClick)
        findViewById<View>(R.id.SpSection).setOnClickListener(::onSpSectionClick)

        nextButton.setOnClickListener {
            if ( SignupData.temp_language != getString(R.string.signup_info_first) && SignupData.temp_accent != getString(R.string.signup_info_first) ) {

                // 서버로 전송할 language 값을 설정
                val language = when (SignupData.temp_accent) {
                    "usa" -> "US"
                    "eng" -> "UK"
                    "aus" -> "AUS"
                    "ind" -> "IND"
                    "jp"  -> "JA"
                    "sp"  -> "ES"
                    else -> "US"
                }

                // 요청 객체 생성
                val languageRequest = LanguageRequest(language)

                // Retrofit 클라이언트 인스턴스를 통해 ApiService 호출
                val apiService = RetrofitClient.instance

                // 사용자 토큰 가져오기
                val userToken = "Bearer ${SignupData.token}"

                // 네트워크 호출, 토큰을 헤더에 포함
                apiService.updateLanguage(userToken, languageRequest)
                    .enqueue(object : Callback<LanguageResponse> {
                        override fun onResponse(
                            call: Call<LanguageResponse>,
                            response: Response<LanguageResponse>
                        ) {
                            // 응답이 성공적이고 요청이 성공했는지 확인
                            if (response.isSuccessful && response.body()?.isSuccess == true) {
                                // 성공적인 응답 처리
                                SignupData.language = SignupData.temp_language
                                SignupData.accent = SignupData.temp_accent
                                SignupData.profile_detail_interest = "완료"

                                Toast.makeText(
                                    this@ChangeLanguageActivity,
                                    "언어 변경 완료",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent = Intent(this@ChangeLanguageActivity, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                // 응답 오류 처리
                                Toast.makeText(
                                    this@ChangeLanguageActivity,
                                    "서버 오류: ${response.body()?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<LanguageResponse>, t: Throwable) {
                            // 네트워크 호출 실패 처리
                            t.printStackTrace()
                            Toast.makeText(
                                this@ChangeLanguageActivity,
                                "언어 설정 변경 실패",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }
    }

    private fun onEnSectionClick(view: View) {

        setSectionVisibility(R.id.JpSection, R.id.JpSection_togle, false)
        setSectionVisibility(R.id.SpSection, R.id.SpSection_togle, false)
        setSectionVisibility(R.id.EnSection, R.id.EnSection_togle, true)

        findViewById<View>(R.id.EnSection_togle_usa).setOnClickListener(::toggleOnClickEnUsa)
        findViewById<View>(R.id.EnSection_togle_eng).setOnClickListener(::toggleOnClickEnEng)
        findViewById<View>(R.id.EnSection_togle_aus).setOnClickListener(::toggleOnClickEnAus)
        findViewById<View>(R.id.EnSection_togle_ind).setOnClickListener(::toggleOnClickEnInd)
    }

    private fun onJpSectionClick(view: View) {

        setSectionVisibility(R.id.EnSection, R.id.EnSection_togle, false)
        setSectionVisibility(R.id.SpSection, R.id.SpSection_togle, false)
        setSectionVisibility(R.id.JpSection, R.id.JpSection_togle, true)

        findViewById<View>(R.id.JpSection_togle_jp).setOnClickListener(::toggleOnClickJpJp)
    }

    private fun onSpSectionClick(view: View) {

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
        SignupData.temp_language = "English"
        SignupData.temp_accent = "usa"
    }

    private fun toggleOnClickEnEng(view: View) {
        activateToggle(R.id.EnSection_togle_usa, false)
        activateToggle(R.id.EnSection_togle_eng, true)
        activateToggle(R.id.EnSection_togle_aus, false)
        activateToggle(R.id.EnSection_togle_ind, false)

        // 상태 저장
        SignupData.temp_language = "English"
        SignupData.temp_accent = "eng"
    }

    private fun toggleOnClickEnAus(view: View) {
        activateToggle(R.id.EnSection_togle_usa, false)
        activateToggle(R.id.EnSection_togle_eng, false)
        activateToggle(R.id.EnSection_togle_aus, true)
        activateToggle(R.id.EnSection_togle_ind, false)

        // 상태 저장
        SignupData.temp_language = "English"
        SignupData.temp_accent = "aus"
    }

    private fun toggleOnClickEnInd(view: View) {
        activateToggle(R.id.EnSection_togle_usa, false)
        activateToggle(R.id.EnSection_togle_eng, false)
        activateToggle(R.id.EnSection_togle_aus, false)
        activateToggle(R.id.EnSection_togle_ind, true)

        // 상태 저장
        SignupData.temp_language = "English"
        SignupData.temp_accent = "ind"
    }

    private fun toggleOnClickJpJp(view: View) {
        activateToggle(R.id.JpSection_togle_jp, true)

        // 상태 저장
        SignupData.temp_language = "Japanese"
        SignupData.temp_accent = "jp"
    }

    private fun toggleOnClickSpSp(view: View) {
        activateToggle(R.id.SpSection_togle_sp, true)
        activateToggle(R.id.SpSection_togle_usa, false)

        // 상태 저장
        SignupData.temp_language = "Spanish"
        SignupData.temp_accent = "sp"
    }

    private fun toggleOnClickSpUsa(view: View) {
        activateToggle(R.id.SpSection_togle_sp, false)
        activateToggle(R.id.SpSection_togle_usa, true)

        // 상태 저장
        SignupData.temp_language = "Spanish"
        SignupData.temp_accent = "usa"
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
