package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class ProfileActivity : ComponentActivity() {

    private lateinit var overlayView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val profileGreeting: TextView = findViewById(R.id.profile_greeting)
        val profileIcon: ImageView = findViewById(R.id.profile_icon)

        val setLanguage: TextView = findViewById(R.id.set_language)
        val setLanguageFlag: ImageView = findViewById(R.id.set_lauguage_flag)
        val setLanguageIcon: ImageView = findViewById(R.id.set_lauguage_icon)

        val setInterest: TextView = findViewById(R.id.set_interest)
        val setInterestKeyword: TextView = findViewById(R.id.set_interest_keyword)
        val setInterestIcon: ImageView = findViewById(R.id.set_interest_icon)
        val logout: TextView = findViewById(R.id.logout)

        // 사용자 닉네임 설정
        profileGreeting.text = "${SignupData.nickname}님, 오늘도 즐거운 owncast 생활하세요!"

        // 프로필 아이콘 클릭 시 프로필 설정 페이지로 이동
        profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileSettingActivity::class.java)
            startActivity(intent)
        }

        // 언어 설정 부분
        // 조건에 따라 이미지 변경
        when (SignupData.accent) {
            "usa" -> setLanguageFlag.setImageResource(R.drawable.flag_usa)
            "eng" -> setLanguageFlag.setImageResource(R.drawable.flag_eng)
            "aus" -> setLanguageFlag.setImageResource(R.drawable.flag_aus)
            "ind" -> setLanguageFlag.setImageResource(R.drawable.flag_ind)
            "jp" -> setLanguageFlag.setImageResource(R.drawable.flag_jp)
            else -> setLanguageFlag.setImageResource(R.drawable.flag_sp)
        }

        // 클릭 시 언어 설정 페이지로 이동
        setLanguageIcon.setOnClickListener {
            val intent = Intent(this, ChangeLanguageActivity::class.java)
            startActivity(intent)
        }

        // 관심사 설정 부분
        setInterestKeyword.text = SignupData.detail_interest

        // 클릭 시 관심사 설정 페이지로 이동
        setInterestIcon.setOnClickListener {
            val intent = Intent(this, ChangeInterestActivity::class.java)
            startActivity(intent)
        }

        // 로그아웃 클릭 시 다이얼로그 표시
        overlayView = findViewById(R.id.dialog_logout_confirmation) // 오버레이 뷰 초기화
        val logoutButton: TextView = findViewById(R.id.logout)
        logoutButton.setOnClickListener {
            showLogoutDialog()
        }

    }

    // 로그아웃 다이얼로그 표시
    private fun showLogoutDialog() {
        // 오버레이 보이기
        overlayView.visibility = View.VISIBLE

        // 다이얼로그 뷰를 인플레이트
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_logout_confirmation, null)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // 취소 버튼 클릭 시
        dialogView.findViewById<View>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
            overlayView.visibility = View.GONE
        }

        // 로그아웃 버튼 클릭 시
        dialogView.findViewById<View>(R.id.btn_logout).setOnClickListener {
            SignupData.id = getString(R.string.signup_info_first) // 로그아웃 기능
            dialog.dismiss()
            overlayView.visibility = View.GONE
            // '스플래시' 페이지로 이동
            val intent = Intent(this, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // 다이얼로그가 사라질 때 오버레이 숨기기
        dialog.setOnDismissListener {
            overlayView.visibility = View.GONE
        }

        dialog.show()
    }
}