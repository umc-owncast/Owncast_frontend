package kr.dori.android.own_cast

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChangeSelfActivity : ComponentActivity() {

    private val MAX_LENGTH = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_self)
        enableEdgeToEdge()

        val etSelf = findViewById<EditText>(R.id.etSelf)
        val textNum = findViewById<TextView>(R.id.text_num)
        val backButton = findViewById<ImageView>(R.id.backButton)
        val btnNext = findViewById<Button>(R.id.btn_next)
        var check = 0

        findViewById<TextView>(R.id.main_text).text = "${SignupData.nickname}님이 가장 많은\n시간을 보내는 분야를 선택해주세요"

        // 초기에는 완료 버튼을 비활성화
        btnNext.isEnabled = false
        btnNext.setOnClickListener {

            // interest 값을 변환
            val mainCategory = when (SignupData.temp_interest) {
                "movie" -> "드라마/영화"
                "sports" -> "스포츠"
                "music" -> "음악"
                "food" -> "음식"
                "book" -> "책"
                "news" -> "시사/뉴스"
                "art" -> "미술"
                "self" -> "직접 입력"
                else -> "드라마/영화"
            }

            // 요청 객체 생성
            val preferenceRequest = PreferenceRequest(
                mainCategory = mainCategory,
                subCategory = SignupData.temp_detail_interest
            )

            // Retrofit 클라이언트 인스턴스를 통해 ApiService 호출
            val apiService = RetrofitClient.instance

            // 사용자 토큰 가져오기
            val userToken = "Bearer ${SignupData.token}"

            // 네트워크 호출, 토큰을 헤더에 포함
            apiService.updatePreferences(userToken, preferenceRequest)
                .enqueue(object : Callback<PreferenceResponse> {
                    override fun onResponse(
                        call: Call<PreferenceResponse>,
                        response: Response<PreferenceResponse>
                    ) {
                        // 응답이 성공적이고 요청이 성공했는지 확인
                        if (response.isSuccessful && response.body()?.isSuccess == true) {

                            // 성공적인 응답 처리
                            SignupData.profile_detail_interest = "완료"

                            SignupData.interest = SignupData.temp_interest
                            SignupData.detail_interest = SignupData.temp_detail_interest

                            Toast.makeText(this@ChangeSelfActivity, "관심사 변경 완료", Toast.LENGTH_SHORT).show()

                        } else {
                            // 응답 오류 처리
                            Toast.makeText(
                                this@ChangeSelfActivity,
                                "서버 오류: ${response.body()?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        val intent = Intent(this@ChangeSelfActivity, MainActivity ::class.java)
                        startActivity(intent)
                        finish()
                    }

                    override fun onFailure(call: Call<PreferenceResponse>, t: Throwable) {
                        // 네트워크 호출 실패 처리
                        t.printStackTrace()
                        Toast.makeText(
                            this@ChangeSelfActivity,
                            "관심사 변경 실패",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@ChangeSelfActivity, MainActivity ::class.java)
                        startActivity(intent)
                        finish()
                    }

                })


        }

        backButton.setOnClickListener {
            startActivity(Intent(this, ChangeInterestActivity::class.java))
        }

        // TextWatcher를 사용하여 EditText의 글자 수에 따라 처리
        etSelf.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 입력된 글자 수
                val inputLength = s?.length ?: 0

                // 최대 길이를 초과하지 않도록 처리
                if (inputLength > MAX_LENGTH) {
                    etSelf.setText(s?.substring(0, MAX_LENGTH))
                    etSelf.setSelection(MAX_LENGTH)
                }

                // 텍스트 뷰에 글자 수 표시
                textNum.text = "(${Math.min(inputLength, MAX_LENGTH)}자/$MAX_LENGTH 자)"
            }

            override fun afterTextChanged(s: Editable?) {
                // 상태 저장
                SignupData.temp_detail_interest = s.toString()

                // 버튼 상태 업데이트
                btnNext.isEnabled = true
                btnNext.setBackgroundTintList(ContextCompat.getColorStateList(this@ChangeSelfActivity, R.color.main_purple))
            }
        })

    }
}
