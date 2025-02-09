package kr.dori.android.own_cast.signUp


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.SignupData


class SignupFifthActivity : ComponentActivity() {

    private lateinit var cardMovie: ImageView
    private lateinit var cardSport: ImageView
    private lateinit var cardMusic: ImageView
    private lateinit var cardFood: ImageView
    private lateinit var cardBook: ImageView
    private lateinit var cardNews: ImageView
    private lateinit var cardArt: ImageView
    private lateinit var cardSelf: ImageView

    private lateinit var allCards: List<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_fifth)
        enableEdgeToEdge()

        cardMovie = findViewById(R.id.card_movie)
        cardSport = findViewById(R.id.card_sport)
        cardMusic = findViewById(R.id.card_music)
        cardFood = findViewById(R.id.card_food)
        cardBook = findViewById(R.id.card_book)
        cardNews = findViewById(R.id.card_news)
        cardArt = findViewById(R.id.card_art)
        cardSelf = findViewById(R.id.card_self)

        // allCards 리스트 초기화
        allCards = listOf(cardMovie, cardSport, cardMusic, cardFood, cardBook, cardNews, cardArt, cardSelf)

        // 각 카드에 클릭 리스너 설정
        setCardClickListener(cardMovie, R.drawable.movie_un, "movie")
        setCardClickListener(cardSport, R.drawable.sports_un, "sports")
        setCardClickListener(cardMusic, R.drawable.music_un, "music")
        setCardClickListener(cardFood, R.drawable.food_un, "food")
        setCardClickListener(cardBook, R.drawable.book_un, "book")
        setCardClickListener(cardNews, R.drawable.news_un, "news")
        setCardClickListener(cardArt, R.drawable.art_un, "art")

        // 직접 입력
        findViewById<ImageView>(R.id.card_self).setOnClickListener {
            SignupData.interest = "self"
            val intent = Intent(this, SignupSelfActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        // 메인 텍스트 설정
        findViewById<TextView>(R.id.main_text).text = "${SignupData.nickname}님이 가장 많은\n시간을 보내는 분야를 선택해주세요"
    }

    // 카드 선택시 -> 1초 뒤 화면 전환 & 나머지 카드 변환 & 모든 카드 선택 비활성화
    private fun setCardClickListener(card: ImageView, unDrawable: Int, name : String) {
        card.setOnClickListener {
            // 나머지 카드의 변환
            changeOtherCardsToWhite(card, unDrawable)

            // 모든 카드의 클릭 리스너를 비활성화
            disableAllCardClickListeners()

            // 상태 저장
            SignupData.interest = name

            // 화면 전환
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@SignupFifthActivity, SignupDetailInterestActivity::class.java)
                startActivity(intent)
            }, 1000)
        }
    }

    // 나머지 카드를 변환하는 함수
    private fun changeOtherCardsToWhite(selectedCard: ImageView, unDrawable: Int) {
        val cards = listOf(
            Pair(cardMovie, R.drawable.movie_un),
            Pair(cardSport, R.drawable.sports_un),
            Pair(cardMusic, R.drawable.music_un),
            Pair(cardFood, R.drawable.food_un),
            Pair(cardBook, R.drawable.book_un),
            Pair(cardNews, R.drawable.news_un),
            Pair(cardArt, R.drawable.art_un),
            Pair(cardSelf, R.drawable.self_un)
        )

        for ((card, drawable) in cards) {
            if (card != selectedCard) {
                card.setImageResource(drawable)
            }
        }
    }

    // 모든 카드의 클릭 리스너를 비활성화하는 함수
    private fun disableAllCardClickListeners() {
        for (card in allCards) {
            card.setOnClickListener(null)
        }
    }
}