package kr.dori.android.own_cast.signUp


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.SignupData


class SignupDetailInterestActivity : ComponentActivity() {

    // 각 체크박스 ImageView를 선언
    private lateinit var checkboxFirst: ImageView
    private lateinit var checkboxSecond: ImageView
    private lateinit var checkboxThird: ImageView
    private lateinit var checkboxFourth: ImageView
    private lateinit var checkboxFifth: ImageView
    private lateinit var checkboxSixth: ImageView

    // 완료 버튼을 선언
    private lateinit var confirmButton: Button

    // 선택된 체크박스의 ID를 저장하는 변수
    private var selectedCheckboxId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_detail_interest)
        enableEdgeToEdge()

        val num = when (SignupData.interest) {
            "movie" -> 1
            "sports" -> 2
            "music" -> 3
            "food" -> 4
            "book" -> 5
            "news" -> 6
            "art" -> 7
            "self" -> 8
            else -> 0
        }

        // Title 설정
        val titleTextView: TextView = findViewById(R.id.title)
        val titleArray = arrayOf(
            getString(R.string.detail_0_1),
            getString(R.string.detail_0_2),
            getString(R.string.detail_0_3),
            getString(R.string.detail_0_4),
            getString(R.string.detail_0_5),
            getString(R.string.detail_0_6),
            getString(R.string.detail_0_7)
        )

        if (num in 1..titleArray.size) {
            titleTextView.text = titleArray[num - 1]
        } else {
            titleTextView.text = getString(R.string.detail_0_1) // 기본 텍스트 설정
        }

        // Sections 설정
        val detailArray = when (num) {
            1 -> resources.getStringArray(R.array.detail_1)
            2 -> resources.getStringArray(R.array.detail_2)
            3 -> resources.getStringArray(R.array.detail_3)
            4 -> resources.getStringArray(R.array.detail_4)
            5 -> resources.getStringArray(R.array.detail_5)
            6 -> resources.getStringArray(R.array.detail_6)
            7 -> resources.getStringArray(R.array.detail_7)
            else -> emptyArray()
        }

        setSectionText(R.id.section_1, detailArray[0])
        setSectionText(R.id.section_2, detailArray[1])
        setSectionText(R.id.section_3, detailArray[2])
        setSectionText(R.id.section_4, detailArray[3])
        setSectionText(R.id.section_5, detailArray[4])
        setSectionText(R.id.section_6, detailArray[5])


        // 체크박스와 버튼을 초기화
        checkboxFirst = findViewById(R.id.checkbox_first)
        checkboxSecond = findViewById(R.id.checkbox_second)
        checkboxThird = findViewById(R.id.checkbox_third)
        checkboxFourth = findViewById(R.id.checkbox_fourth)
        checkboxFifth = findViewById(R.id.checkbox_fifth)
        checkboxSixth = findViewById(R.id.checkbox_sixth)
        confirmButton = findViewById(R.id.confirm_button)

        // 각 체크박스에 클릭 리스너를 설정
        checkboxFirst.setOnClickListener { onCheckboxClicked(it) }
        checkboxSecond.setOnClickListener { onCheckboxClicked(it) }
        checkboxThird.setOnClickListener { onCheckboxClicked(it) }
        checkboxFourth.setOnClickListener { onCheckboxClicked(it) }
        checkboxFifth.setOnClickListener { onCheckboxClicked(it) }
        checkboxSixth.setOnClickListener { onCheckboxClicked(it) }

        // 초기에는 완료 버튼을 비활성화
        confirmButton.isEnabled = false
        confirmButton.setOnClickListener {
            startActivity(Intent(this, SignupLoadingActivity::class.java))
        }

        // 닫기 버튼
        findViewById<ImageView>(R.id.pop_exit_button).setOnClickListener {
            val intent = Intent(this, SignupFifthActivity::class.java)
            startActivity(intent)
        }

        // 직접 입력
        findViewById<TextView>(R.id.self).setOnClickListener {
            val intent = Intent(this, SignupSelfActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setSectionText(textViewId: Int, text: String) {
        val textView = findViewById<TextView>(textViewId)
        textView.text = text
    }

    // 체크박스를 클릭하면 호출되는 함수
    private fun onCheckboxClicked(view: View) {
        val clickedCheckbox = view as ImageView
        val clickedCheckboxId = clickedCheckbox.id
        val sectionNumber = SignupData.interest

        // 클릭된 체크박스가 이미 선택된 상태인지 확인
        if (selectedCheckboxId == clickedCheckbox.id) {
            // 이미 선택된 체크박스를 다시 클릭하면 선택 해제
            clickedCheckbox.setImageResource(R.drawable.signup_detail_uncheckbtn)
            selectedCheckboxId = null
            confirmButton.isEnabled = false
            confirmButton.setBackgroundTintList(ContextCompat.getColorStateList(this,
                R.color.button_unclick
            ))
        } else {
            // 새로운 체크박스를 선택하면 기존 선택을 해제하고 새로운 체크박스를 선택
            resetAllCheckboxes()
            clickedCheckbox.setImageResource(R.drawable.signup_detail_checkbtn)
            selectedCheckboxId = clickedCheckbox.id
            confirmButton.isEnabled = true
            confirmButton.setBackgroundTintList(ContextCompat.getColorStateList(this,
                R.color.main_purple
            ))

            // 선택된 체크박스의 텍스트를 가져와 저장하기
            val checkboxIndex = when (clickedCheckboxId) {
                R.id.checkbox_first -> 1
                R.id.checkbox_second -> 2
                R.id.checkbox_third -> 3
                R.id.checkbox_fourth -> 4
                R.id.checkbox_fifth -> 5
                R.id.checkbox_sixth -> 6
                else -> return
            }

            // 관심사 섹션별 배열
            val movieInterests = arrayOf("로맨스", "액션", "코미디", "공포", "애니메이션", "스릴러")
            val sportsInterests = arrayOf("축구", "야구", "농구", "테니스", "골프", "수영")
            val musicInterests = arrayOf("팝", "재즈", "힙합", "클래식", "K-POP", "록")
            val foodInterests = arrayOf("한식", "양식", "중식", "일식", "베이커리", "채식")
            val bookInterests = arrayOf("소설", "자기계발", "판타지", "철학", "역사", "과학")
            val newsInterests = arrayOf("정치", "경제", "국제", "사회", "기술", "환경")
            val artInterests = arrayOf("사진", "현대미술", "회화", "조각", "일러스트", "그래픽디자인")

            fun updateInterest(clickedCheckboxId: Int, checkboxIndex: Int) {
                val interestsMap = mapOf(
                    "movie" to movieInterests,
                    "sports" to sportsInterests,
                    "music" to musicInterests,
                    "food" to foodInterests,
                    "book" to bookInterests,
                    "news" to newsInterests,
                    "art" to artInterests
                )

                // 현재 관심사 섹션 가져오기
                val sectionInterests = interestsMap[SignupData.interest] ?: return
                val index = checkboxIndex - 1
                if (index in sectionInterests.indices) {
                    SignupData.detail_interest = sectionInterests[index]
                } else {
                    // 인덱스가 범위를 벗어난 경우 기본값 설정
                    SignupData.detail_interest = sectionInterests.last()
                }
            }

            updateInterest(clickedCheckboxId, checkboxIndex)
        }
    }

    private fun resetAllCheckboxes() {
        // 모든 체크박스를 초기화
        checkboxFirst.setImageResource(R.drawable.signup_detail_uncheckbtn)
        checkboxSecond.setImageResource(R.drawable.signup_detail_uncheckbtn)
        checkboxThird.setImageResource(R.drawable.signup_detail_uncheckbtn)
        checkboxFourth.setImageResource(R.drawable.signup_detail_uncheckbtn)
        checkboxFifth.setImageResource(R.drawable.signup_detail_uncheckbtn)
        checkboxSixth.setImageResource(R.drawable.signup_detail_uncheckbtn)
    }

}