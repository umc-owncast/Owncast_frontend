package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var overlayView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileGreeting: TextView = view.findViewById(R.id.profile_greeting)

        val profileIcon: LinearLayout = view.findViewById(R.id.NickName_field)

        val setLanguage: TextView = view.findViewById(R.id.set_language)
        val setLanguageFlag: ImageView = view.findViewById(R.id.set_lauguage_flag)

        val setLanguageIcon: LinearLayout = view.findViewById(R.id.languageFelid)

        val setInterest: TextView = view.findViewById(R.id.set_interest)
        val setInterestKeyword: TextView = view.findViewById(R.id.set_interest_keyword)

        val setInterestIcon: LinearLayout = view.findViewById(R.id.interestField)
        val logout: TextView = view.findViewById(R.id.logout)

        // 사용자 닉네임 설정
        profileGreeting.text = "${SignupData.nickname}님, 오늘도 즐거운 owncast 생활하세요!"

        // 프로필 아이콘 클릭 시 프로필 설정 페이지로 이동
        profileIcon.setOnClickListener {
            val intent = Intent(requireContext(), ProfileSettingActivity::class.java)
            startActivity(intent)
        }

        // 언어 설정 부분
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
            val intent = Intent(requireContext(), ChangeLanguageActivity::class.java)
            startActivity(intent)
        }

        // 관심사 설정 부분
        setInterestKeyword.text = SignupData.detail_interest

        // 클릭 시 관심사 설정 페이지로 이동
        setInterestIcon.setOnClickListener {
            val intent = Intent(requireContext(), ChangeInterestActivity::class.java)
            startActivity(intent)
        }

        // 로그아웃 클릭 시 다이얼로그 표시
        overlayView = view.findViewById(R.id.dialog_logout_confirmation)
        val logoutButton: TextView = view.findViewById(R.id.logout)
        logoutButton.setOnClickListener {
            showLogoutDialog()
        }

        // 회원탈퇴 클릭 시 다이얼로그 표시
        overlayView = view.findViewById(R.id.dialog_logout_confirmation)
        val SignoutButton: TextView = view.findViewById(R.id.sign_out)
        SignoutButton.setOnClickListener {
            showSignoutDialog()
        }

    }

    // 로그아웃 다이얼로그 표시
    private fun showLogoutDialog() {
        // 오버레이 보이기
        overlayView.visibility = View.VISIBLE

        // 다이얼로그 뷰를 인플레이트
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout_confirmation, null)
        val dialog = MaterialAlertDialogBuilder(requireContext())
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

            // 전역 변수 초기화
            resetSignupData()

            dialog.dismiss()
            overlayView.visibility = View.GONE
            // '스플래시' 페이지로 이동
            val intent = Intent(requireContext(), SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // 다이얼로그가 사라질 때 오버레이 숨기기
        dialog.setOnDismissListener {
            overlayView.visibility = View.GONE
        }

        dialog.show()
    }


    // 회원탈퇴 다이얼로그 표시
    private fun showSignoutDialog() {
        // 오버레이 보이기
        overlayView.visibility = View.VISIBLE

        // 다이얼로그 뷰를 인플레이트
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_signout_confirmation, null)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // 취소 버튼 클릭 시
        dialogView.findViewById<View>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
            overlayView.visibility = View.GONE
        }

        // 회원탈퇴 버튼 클릭 시
        dialogView.findViewById<View>(R.id.btn_signout).setOnClickListener {
            // 전역 변수 초기화
            resetSignupData()

            dialog.dismiss()
            overlayView.visibility = View.GONE

            // '스플래시' 페이지로 이동
            val intent = Intent(requireContext(), SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // 다이얼로그가 사라질 때 오버레이 숨기기
        dialog.setOnDismissListener {
            overlayView.visibility = View.GONE
        }

        dialog.show()
    }

    // 전역 변수 초기화 메서드
    private fun resetSignupData() {
        SignupData.ok_btn = 0
        SignupData.temp_interest = ""
        SignupData.temp_detail_interest = ""
        SignupData.temp_language = "닉네임"
        SignupData.temp_accent = "닉네임"
        SignupData.profile_detail_interest = ""

        // SharedPreferences에 저장된 값 초기화
        SignupData.nickname = "닉네임"
        SignupData.name = "닉네임"
        SignupData.id = "닉네임"
        SignupData.password = "닉네임"
        SignupData.passwordconfirm = "닉네임"
        SignupData.language = "닉네임"
        SignupData.accent = "닉네임"
        SignupData.interest = "닉네임"
        SignupData.detail_interest = "닉네임"
        SignupData.token = "닉네임"
    }

}