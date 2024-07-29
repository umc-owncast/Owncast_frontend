package kr.dori.android.own_cast


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class SignupDetailInterestActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_detail_interest)

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

        val titleArray = resources.getStringArray(R.array.detail_0)
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

        // Title 설정
        val titleTextView = findViewById<TextView>(R.id.title)
        titleTextView.text = titleArray[num - 1]

        // Sections 설정
        setSectionText(R.id.section_1, detailArray[0])
        setSectionText(R.id.section_2, detailArray[1])
        setSectionText(R.id.section_3, detailArray[2])
        setSectionText(R.id.section_4, detailArray[3])
        setSectionText(R.id.section_5, detailArray[4])
        setSectionText(R.id.section_6, detailArray[5])

        // 완료 버튼
        findViewById<Button>(R.id.confirm_button).setOnClickListener {
            startActivity(Intent(this, SignupDLoadingActivity::class.java))
        }
    }

    private fun setSectionText(textViewId: Int, text: String) {
        val textView = findViewById<TextView>(textViewId)
        textView.text = text
    }
}