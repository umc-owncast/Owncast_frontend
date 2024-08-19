package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileSettingActivity : AppCompatActivity() {

    private lateinit var etId: EditText
    private lateinit var etName: EditText
    private lateinit var etNickName: EditText

    private lateinit var clearIdButton: ImageView
    private lateinit var clearNameButton: ImageView
    private lateinit var clearNickNameButton: ImageView

    private lateinit var errorId: TextView
    private lateinit var errorName: TextView
    private lateinit var errorNickName: TextView

    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_profile)
        enableEdgeToEdge()

        // UI 요소 초기화
        etId = findViewById(R.id.etId)
        etName = findViewById(R.id.etName)
        etNickName = findViewById(R.id.etNickName)
        clearIdButton = findViewById(R.id.clearIdButton)
        clearNameButton = findViewById(R.id.clearNameButton)
        clearNickNameButton = findViewById(R.id.clearNickNameButton)
        errorId = findViewById(R.id.IdError)
        errorName = findViewById(R.id.IdName)
        errorNickName = findViewById(R.id.IdNickName)
        btnNext = findViewById(R.id.btn_next)

        // 초기값 설정
        etId.setText(SignupData.id)
        etName.setText(SignupData.name)
        etNickName.setText(SignupData.nickname)
        btnNext.isClickable = false
        btnNext.backgroundTintList = ContextCompat.getColorStateList(this, R.color.button_unclick)

        // 뒤로가기 버튼 클릭 시 처리
        val btnBack = findViewById<ImageView>(R.id.backButton)
        btnBack.setOnClickListener {
            SignupData.profile_detail_interest = "완료"
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Clear 버튼 클릭 시 텍스트 초기화 및 버튼 비활성화
        clearIdButton.setOnClickListener {
            etId.text.clear()
            clearIdButton.visibility = View.GONE
        }

        clearNameButton.setOnClickListener {
            etName.text.clear()
            clearNameButton.visibility = View.GONE
        }

        clearNickNameButton.setOnClickListener {
            etNickName.text.clear()
            clearNickNameButton.visibility = View.GONE
        }

        // 비밀번호 변경 버튼 클릭 시 처리
        val btnPs = findViewById<ImageView>(R.id.go_password_Button)
        btnPs.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        // 텍스트 변경 시 오류 메시지 및 버튼 상태 업데이트
        etId.addTextChangedListener(createTextWatcher { validateId(it) })
        etName.addTextChangedListener(createTextWatcher { validateName(it) })
        etNickName.addTextChangedListener(createTextWatcher { validateNickName(it) })

        // 확인 버튼 클릭 시 프로필 업데이트
        btnNext.setOnClickListener {
            SignupData.id = etId.text.toString()
            SignupData.name = etName.text.toString()
            SignupData.nickname = etNickName.text.toString()

            // 프로필 업데이트 요청 데이터 클래스 생성
            val updateProfileRequest = UpdateProfileRequest(
                loginId = SignupData.id,
                username = SignupData.name,
                nickname = SignupData.nickname
            )

            // 사용자 토큰 가져오기
            val userToken = "Bearer ${SignupData.token}"

            // Retrofit을 통해 서버에 프로필 업데이트 요청
            RetrofitClient.instance.updateProfile(userToken, updateProfileRequest).enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                    if (response.isSuccessful) {
                        val updateProfileResponse = response.body()
                        if (updateProfileResponse?.isSuccess == true) {
                            // 프로필 업데이트 성공
                            Toast.makeText(this@ProfileSettingActivity, "프로필 변경 완료", Toast.LENGTH_SHORT).show()

                            // 메인 화면으로 이동
                            SignupData.profile_detail_interest = "완료"
                            val intent = Intent(this@ProfileSettingActivity, MainActivity::class.java)
                            startActivity(intent)

                        } else {
                            // 실패 메시지 처리
                            handleError(updateProfileResponse?.message ?: "프로필 업데이트 실패")
                        }
                    } else {
                        handleError("프로필 업데이트 요청 실패")
                    }
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    handleError("프로필 업데이트 요청 중 오류 발생")
                }
            })
        }


        // 초기 입력값 검증
        validateInputs()
    }


    // 오류 처리 함수
    private fun handleError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    // TextWatcher 생성 함수
    private fun createTextWatcher(validator: (String) -> Unit) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // 각 필드의 Clear 버튼 가시성 업데이트
            clearIdButton.visibility = if (etId.text.isNullOrEmpty()) View.GONE else View.VISIBLE
            clearNameButton.visibility = if (etName.text.isNullOrEmpty()) View.GONE else View.VISIBLE
            clearNickNameButton.visibility = if (etNickName.text.isNullOrEmpty()) View.GONE else View.VISIBLE

            // 입력값 검증 및 버튼 상태 업데이트
            validator(s.toString())
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    // 입력값 검증 및 버튼 상태 업데이트
    private fun validateInputs() {
        // 서버 응답을 처리하는 함수 호출은 비동기로 처리되므로 UI 업데이트는 각 validate 함수에서 수행
        // 서버 응답을 기다리는 동안 버튼 상태를 비활성화로 유지할 수 있음

        if (errorId.text.isNullOrEmpty() && errorName.text.isNullOrEmpty() && errorNickName.text.isNullOrEmpty()) {
            btnNext.isClickable = true
            btnNext.backgroundTintList = ContextCompat.getColorStateList(this, R.color.main_purple)
        } else {
            btnNext.isClickable = false
            btnNext.backgroundTintList = ContextCompat.getColorStateList(this, R.color.button_unclick)
        }
    }

    // 아이디 유효성 검사
    private fun validateId(id: String) {
        if (id.isEmpty()) {
            errorId.text = "아이디를 입력해주세요"
            errorId.visibility = View.VISIBLE
            validateInputs()
            return
        }

        RetrofitClient.instance.checkID(id).enqueue(object : Callback<CheckIDResponse> {
            override fun onResponse(call: Call<CheckIDResponse>, response: Response<CheckIDResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()?.result
                    errorId.text = when {
                        result == true -> "이미 존재하는 아이디입니다"
                        id.length !in 5..15 -> "5~15자의 영문 대/소문자, 숫자만 사용해 주세요"
                        !Regex("^[a-zA-Z0-9]+$").matches(id) -> "5~15자의 영문 대/소문자, 숫자만 사용해 주세요"
                        else -> null
                    }
                    errorId.visibility = if (errorId.text.isNullOrEmpty()) View.GONE else View.VISIBLE
                } else {
                    errorId.text = "서버 오류 발생"
                    errorId.visibility = View.VISIBLE
                }
                validateInputs()
            }

            override fun onFailure(call: Call<CheckIDResponse>, t: Throwable) {
                errorId.text = "네트워크 오류 발생"
                errorId.visibility = View.VISIBLE
                validateInputs()
            }
        })
    }

    // 닉네임 유효성 검사
    private fun validateNickName(nickName: String) {
        if (nickName.length > 10) {
            errorNickName.text = "10자 이내로 구성해주세요"
            errorNickName.visibility = View.VISIBLE
            validateInputs()
            return
        }

        if (!"^[a-zA-Z0-9가-힣-_]+$".toRegex().matches(nickName)) {
            errorNickName.text = "영문 대/소문자, 한글, 숫자, 하이픈(-), 언더스코어(_)만 사용해주세요"
            errorNickName.visibility = View.VISIBLE
            validateInputs()
            return
        }

        RetrofitClient.instance.checkNickName(nickName).enqueue(object : Callback<CheckNickNameResponse> {
            override fun onResponse(call: Call<CheckNickNameResponse>, response: Response<CheckNickNameResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()?.result
                    errorNickName.text = if (result == true) {
                        "이미 존재하는 닉네임입니다"
                    } else {
                        null
                    }
                    errorNickName.visibility = if (errorNickName.text.isNullOrEmpty()) View.GONE else View.VISIBLE
                } else {
                    errorNickName.text = "서버 오류 발생"
                    errorNickName.visibility = View.VISIBLE
                }
                validateInputs()
            }

            override fun onFailure(call: Call<CheckNickNameResponse>, t: Throwable) {
                errorNickName.text = "네트워크 오류 발생"
                errorNickName.visibility = View.VISIBLE
                validateInputs()
            }
        })
    }

    // 이름 유효성 검사
    private fun validateName(name: String) {
        val regex_1 = "^[가-힣]+$".toRegex() // 한글 전체 문자열
        val regex_2 = "^[a-zA-Z]+$".toRegex() // 영어 전체 문자열

        errorName.text = when {
            name.isEmpty() -> "이름을 입력해주세요"
            regex_1.matches(name) && name.length > 5 -> "한글 기준 5자 이내로 입력해주세요"
            regex_2.matches(name) && name.length > 20 -> "영어 기준 20자 이내로 입력해주세요"
            !regex_1.matches(name) && !regex_2.matches(name) -> "한글 또는 영어만 입력해주세요"
            else -> null
        }
        errorName.visibility = if (errorName.text.isNullOrEmpty()) View.GONE else View.VISIBLE
        validateInputs()
    }
}