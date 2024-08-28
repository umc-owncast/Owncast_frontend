package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : ComponentActivity() {

    private lateinit var etPassword: EditText
    private lateinit var etNewPs: EditText
    private lateinit var etNewPsConfirm: EditText
    private lateinit var clearPasswordButton: ImageView
    private lateinit var clearNewPsButton: ImageView
    private lateinit var clearNewPsConfirmButton: ImageView
    private lateinit var btnNext: Button
    private lateinit var passwordError: TextView
    private lateinit var newPsError: TextView
    private lateinit var newPsConfirmError: TextView
    private lateinit var newPs : String

    private var result_1 : Int = 0
    private var result_2 : Int = 0
    private var result_3 : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        enableEdgeToEdge()

        etPassword = findViewById(R.id.etPassword)
        etNewPs = findViewById(R.id.etNewPs)
        etNewPsConfirm = findViewById(R.id.etNewPsConfirm)
        clearPasswordButton = findViewById(R.id.clearPasswordButton)
        clearNewPsButton = findViewById(R.id.clearNewPsButton)
        clearNewPsConfirmButton = findViewById(R.id.clearNewPsConfirmButton)
        btnNext = findViewById(R.id.btn_next)
        passwordError = findViewById(R.id.passwordError)
        newPsError = findViewById(R.id.newPsError)
        newPsConfirmError = findViewById(R.id.newPsConfirmError)

        result_1 = 0
        result_2 = 0
        result_3 = 0

        clearPasswordButton.setOnClickListener {
            etPassword.text.clear()
            clearPasswordButton.isVisible = false
        }

        clearNewPsButton.setOnClickListener {
            etNewPs.text.clear()
            clearNewPsButton.isVisible = false
        }

        clearNewPsConfirmButton.setOnClickListener {
            etNewPsConfirm.text.clear()
            clearNewPsConfirmButton.isVisible = false
        }

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        btnNext.setOnClickListener {
            SignupData.password = newPs

            // 비밀번호 업데이트 요청 데이터 클래스 생성
            val updatePasswordRequest = UpdatePasswordRequest(password = SignupData.password)

            // 사용자 토큰 가져오기
            val userToken = "Bearer ${SignupData.token}"

            // 서버에 비밀번호 업데이트 요청
            RetrofitClient.instance.updatePassword(userToken, updatePasswordRequest).enqueue(object :
                Callback<UpdatePasswordResponse> {
                override fun onResponse(call: Call<UpdatePasswordResponse>, response: Response<UpdatePasswordResponse>) {
                    if (response.isSuccessful) {
                        val updatePasswordResponse = response.body()
                        if (updatePasswordResponse?.isSuccess == true) {
                            // 비밀번호 업데이트 성공
                            Toast.makeText(this@ChangePasswordActivity, "비밀번호 변경 완료", Toast.LENGTH_SHORT).show()

                            // 메인 화면으로 이동
                            SignupData.profile_detail_interest = "완료"
                            val intent = Intent(this@ChangePasswordActivity, MainActivity::class.java)
                            startActivity(intent)

                        } else {
                            // 실패 메시지 처리
                            handleError(updatePasswordResponse?.message ?: "비밀번호 업데이트 실패")
                        }
                    } else {
                        handleError("비밀번호 업데이트 요청 실패")
                    }
                }

                override fun onFailure(call: Call<UpdatePasswordResponse>, t: Throwable) {
                    handleError("비밀번호 업데이트 요청 중 오류 발생")
                }
            })
        }


        // 각 EditText의 텍스트 변경 시 이벤트 리스너
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearPasswordButton.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                val currentPasswordValid = validateCurrentPassword()
                result_1 = if (currentPasswordValid) 1 else 0

                validateInputs()
            }
        })

        etNewPs.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트 변경 전 처리할 내용이 있다면 이곳에 작성합니다.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearNewPsButton.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                val newPasswordValid = validateNewPassword()
                result_2 = if (newPasswordValid) 1 else 0

                validateInputs()
            }
        })

        etNewPsConfirm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트 변경 전 처리할 내용이 있다면 이곳에 작성합니다.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearNewPsConfirmButton.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                val confirmPasswordValid = validateConfirmPassword()
                result_3 = if (confirmPasswordValid) 1 else 0

                validateInputs()
            }
        })

    }

    // 서버 연결 확인 오류 토스트메시지 함수
    fun handleError(message: String) {
        Toast.makeText(this@ChangePasswordActivity, message, Toast.LENGTH_SHORT).show()
    }

    // 실시간으로 현재 비밀번호를 검사하는 함수
    private fun validateCurrentPassword() : Boolean {
        val currentPassword = etPassword.text.toString()
        if (currentPassword != SignupData.password) {
            passwordError.text = "현재 비밀번호가 올바르지 않습니다."
            passwordError.isVisible = true

            return false
        } else {
            passwordError.isVisible = false

            return true
        }
    }

    // 실시간으로 새 비밀번호를 검사하는 함수
    private fun validateNewPassword() : Boolean {
        val newPassword = etNewPs.text.toString()
        if (newPassword.length !in 8..16 || !newPassword.matches(Regex(".*[a-zA-Z].*")) ||
            !newPassword.matches(Regex(".*\\d.*")) || !newPassword.matches(Regex(".*[@$!%*?&#].*"))) {
            newPsError.text = "새 비밀번호는 영문/숫자/특수문자 포함 8~16자여야 합니다."
            newPsError.isVisible = true

            return false
        } else {
            newPsError.isVisible = false

            newPs = newPassword

            return true
        }
    }

    // 실시간으로 새 비밀번호 확인을 검사하는 함수
    private fun validateConfirmPassword() : Boolean {
        val newPassword = etNewPs.text.toString()
        val confirmPassword = etNewPsConfirm.text.toString()
        if (newPassword != confirmPassword) {
            newPsConfirmError.text = "새 비밀번호와 확인이 일치하지 않습니다."
            newPsConfirmError.isVisible = true

            return false
        } else {
            newPsConfirmError.isVisible = false

            return true
        }
    }

    // 모든 입력값의 유효성을 확인하여 버튼 활성화 상태를 설정하는 함수
    private fun validateInputs() {
        // 유효성 검사 결과를 가져오기
        if ((result_1 == 1) && (result_2 == 1) && (result_3 == 1))
        {
            btnNext.isClickable = true
            btnNext.backgroundTintList = ContextCompat.getColorStateList(this, R.color.main_purple)
        } else {
            btnNext.isClickable = false
            btnNext.backgroundTintList = ContextCompat.getColorStateList(this, R.color.button_unclick)
        }


    }
}