package kr.dori.android.own_cast.keyword

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import kr.dori.android.own_cast.SharedViewModel
import kr.dori.android.own_cast.databinding.ActivityKeywordBinding
import kr.dori.android.own_cast.forApiData.AuthResponse
import kr.dori.android.own_cast.forApiData.AuthRetrofitInterFace
import kr.dori.android.own_cast.forApiData.CastHomeDTO
import kr.dori.android.own_cast.forApiData.getRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/*
이 뒤로 오디오 생성 및 저장 부분
 */

data class RequestApiData(
    var keyword: String,
    var formality: String,
    var voice: String,
    var audiotime:Int
)

class KeywordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKeywordBinding
    //액티비티 내부 전반에서 사용할 데이터들, 스크립트를 생성할때, 캐스트 생성할때 사용하고 서버로 보낼 예정

    private val sharedViewModel:SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKeywordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val isSearch = intent.getBooleanExtra("isSearch",true)
        val searchText: String? = intent.getStringExtra("searchText")//검색할 키워드, 이미 연관 검색어를 받아올때도 똑같은 이름으로 넘긴다
        val keywordData = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("keywordData", KeywordData::class.java)
        } else {
            intent.getParcelableExtra("keywordData") as? KeywordData
        }


        //viewModel에 쓰기 위한 데이터를 가져오는 역할
        /*val _songData:ArrayList<SongData>? = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("SongData", ArrayList::class.java) as? ArrayList<SongData>
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("SongData")
        }

        if(_songData.isNullOrEmpty()){
            Log.d("KeywordViewmodelCheck","Empty")
        }
        _songData?.let{
            sharedViewModel.setData(it.toMutableList())
            Log.d("KeywordViewmodelCheck",it.toString())
        }*/

        apiExecute()

        if(savedInstanceState == null&&searchText!=null){//연관 키워드를 클릭한 경우
            var bundle = Bundle()
            var fragment = KeywordAudioSetFragment()
            bundle.putString("searchText",searchText)
            bundle.putBoolean("isHome",true)
            fragment.arguments = bundle
            //검색입력어를 같이 넘겨야해서 bundle넣음
            supportFragmentManager.beginTransaction()
                .add(binding.keywordFragmentFrm.id, fragment)
                .commitAllowingStateLoss()
        }
        else if(savedInstanceState == null&&isSearch==true){//검색창으로 이동한 경우
            var bundle = Bundle()
            var fragment = KeywordSearchFragment()
            bundle.putParcelable("keywordData", keywordData)
           fragment.arguments = bundle
            supportFragmentManager.beginTransaction()
                .add(binding.keywordFragmentFrm.id, fragment)
                .commitAllowingStateLoss()
        }
        else if(savedInstanceState == null&&isSearch==false){//직접입력창으로 이동한 경우
            supportFragmentManager.beginTransaction()
                .add(binding.keywordFragmentFrm.id, KeywordInputFragment())
                .commitAllowingStateLoss()
        }

    }

    private fun initScreen(){

    }
    //editText에서 다른 화면을 누르면 포커스 해제되는 함수
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    fun apiExecute(){
        val apiService = getRetrofit().create(AuthRetrofitInterFace::class.java)
        //1. apiService후, 자신이 만들어놓은 인터페이스(함수 지정해주기)
        //2. AuthResponse에 응답으로 넘어오는 result 값의 제네릭 넣어주기 AuthResponse<List<CastHomeDTO>>
        //3. COMMON200이 성공 코드이고, resp에서 필요한 값 받기
        apiService.searchHome().enqueue(object: Callback<AuthResponse<List<CastHomeDTO>>> {
            override fun onResponse(call: Call<AuthResponse<List<CastHomeDTO>>>, response: Response<AuthResponse<List<CastHomeDTO>>>) {
                Log.d("SIGNUP/SUCCESS", response.toString())
                val resp: AuthResponse<List<CastHomeDTO>> = response.body()!!
                when(resp.code) {
                    "COMMON200" -> {
                        Log.d("apiTest","연결성공")
                    }
                    else ->{
                        Log.d("apiTest","연결실패 코드 : ${resp.code}")

                    }
                }
            }

            override fun onFailure(call: Call<AuthResponse<List<CastHomeDTO>>>, t: Throwable) {
                Log.d("apiTest", t.message.toString())
            }
        })
    }



}