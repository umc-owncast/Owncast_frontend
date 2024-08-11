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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

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

        // 확인 버튼 클릭 시 처리
        btnNext.setOnClickListener {
            SignupData.id = etId.text.toString()
            SignupData.name = etName.text.toString()
            SignupData.nickname = etNickName.text.toString()

            SignupData.profile_detail_interest = "완료"
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 초기 입력값 검증
        validateInputs()
    }

    // TextWatcher 생성 함수
    private fun createTextWatcher(validator: (String) -> String?) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // 각 필드의 Clear 버튼 가시성 업데이트
            clearIdButton.visibility = if (etId.text.isNullOrEmpty()) View.GONE else View.VISIBLE
            clearNameButton.visibility = if (etName.text.isNullOrEmpty()) View.GONE else View.VISIBLE
            clearNickNameButton.visibility = if (etNickName.text.isNullOrEmpty()) View.GONE else View.VISIBLE

            // 입력값 검증 및 버튼 상태 업데이트
            validateInputs()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    // 입력값 검증 및 버튼 상태 업데이트
    private fun validateInputs() {
        val idError = validateId(etId.text.toString())
        val nameError = validateName(etName.text.toString())
        val nickNameError = validateNickName(etNickName.text.toString())

        // 오류 메시지 설정
        errorId.text = idError
        errorId.visibility = if (idError != null) View.VISIBLE else View.GONE

        errorName.text = nameError
        errorName.visibility = if (nameError != null) View.VISIBLE else View.GONE

        errorNickName.text = nickNameError
        errorNickName.visibility = if (nickNameError != null) View.VISIBLE else View.GONE

        // 확인 버튼 활성화 여부 설정
        if (idError == null && nameError == null && nickNameError == null) btnNext.isClickable = true else btnNext.isClickable = false
        if (btnNext.isClickable) btnNext.backgroundTintList = ContextCompat.getColorStateList(this, R.color.main_purple) else btnNext.backgroundTintList = ContextCompat.getColorStateList(this, R.color.button_unclick)

    }

    // 이름 유효성 검사
    private fun validateName(name: String): String? {
        val regex_1 = "^[가-힣]+$".toRegex() // 한글 전체 문자열
        val regex_2 = "^[a-zA-Z]+$".toRegex() // 영어 전체 문자열

        return when {
            name.isEmpty() -> "이름을 입력해주세요"
            regex_1.matches(name) && name.length > 5 -> "한글 기준 5자 이내로 입력해주세요"
            regex_2.matches(name) && name.length > 20 -> "영어 기준 20자 이내로 입력해주세요"
            !regex_1.matches(name) && !regex_2.matches(name) -> "한글 또는 영어만 입력해주세요"
            else -> null
        }
    }

    // 아이디 유효성 검사
    private fun validateId(id: String): String? {

        val existingIds = listOf("exid1", "exid2") // 서버 연결하기

        return when {
            id.isEmpty() -> "아이디를 입력해주세요"
            existingIds.contains(id) -> "이미 존재하는 아이디입니다"
            id.length !in 5..15 -> "5~15자의 영문 대/소문자, 숫자만 사용해 주세요"
            !Regex("^[a-zA-Z0-9]+$").matches(id) -> "5~15자의 영문 대/소문자, 숫자만 사용해 주세요"
            else -> null
        }
    }

    // 닉네임 유효성 검사
    private fun validateNickName(nickName: String): String? {
        val lengthError = if (nickName.length > 10) "10자 이내로 구성해주세요" else null
        val characterError = if (!"^[a-zA-Z0-9가-힣-_]+$".toRegex().matches(nickName)) "영문 대/소문자, 한글, 숫자, 하이픈(-), 언더스코어(_)만 사용해주세요" else null

        val existingError = if (listOf("111", "asdf").contains(nickName)) "이미 존재하는 닉네임입니다" else null // 서버 연결하기

        return lengthError ?: characterError ?: existingError
    }
}