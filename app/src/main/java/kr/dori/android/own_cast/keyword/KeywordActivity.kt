package kr.dori.android.own_cast.keyword

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast

import androidx.activity.enableEdgeToEdge


import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

import kr.dori.android.own_cast.databinding.ActivityKeywordBinding
import kr.dori.android.own_cast.forApiData.AuthResponse

import kr.dori.android.own_cast.forApiData.CastHomeDTO
import kr.dori.android.own_cast.forApiData.CastInterface
import kr.dori.android.own_cast.forApiData.GetUserPlaylist
import kr.dori.android.own_cast.forApiData.PlayListInterface


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


//기능 : 뷰모델에 카테고리(플레이리스트) 추가, 액티비티 전반 포커스 해제, 액티비티내 프래그먼트 분배
class KeywordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKeywordBinding
    //액티비티 내부 전반에서 사용할 데이터들, 스크립트를 생성할때, 캐스트 생성할때 사용하고 서버로 보낼 예정

    private lateinit var sharedViewModel:KeywordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKeywordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val isSearch = intent.getBooleanExtra("isSearch",true)
        val searchText: String? = intent.getStringExtra("searchText")//검색할 키워드, 이미 연관 검색어를 받아올때도 똑같은 이름으로 넘긴다

        sharedViewModel = ViewModelProvider(this).get(KeywordViewModel::class.java)

        enableEdgeToEdge()
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        initCategoryInViewModel()
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



    fun initCategoryInViewModel(){
        val apiService = getRetrofit().create(PlayListInterface::class.java)
        //1. apiService후, 자신이 만들어놓은 인터페이스(함수 지정해주기)
        //2. AuthResponse에 응답으로 넘어오는 result 값의 제네릭 넣어주기 AuthResponse<List<CastHomeDTO>>
        //3. COMMON200이 성공 코드이고, resp에서 필요한 값 받기
        apiService.getPlayList().enqueue(object: Callback<AuthResponse<List<GetUserPlaylist>>> {
            override fun onResponse(call: Call<AuthResponse<List<GetUserPlaylist>>>, response: Response<AuthResponse<List<GetUserPlaylist>>>) {

                Log.d("apiTest-category", response.toString())
                Log.d("apiTest-category", response.body().toString())

                if(response.isSuccessful){
                    response.body()?.let{
                        val resp: AuthResponse<List<GetUserPlaylist>> = it
                        for(i:Int in 0..(resp.result!!.size-1)){//카테고리(플레이리스트 추가)
                            if (resp.result[i].playlistId>0){
                                sharedViewModel.addGetPlayList(PlaylistText(resp.result[i].playlistId,resp.result[i].name))
                            }

                        }
                    }

                }else{
                    Toast.makeText(this@KeywordActivity,"카테코리 정보 불러오기 실패",Toast.LENGTH_SHORT).show()
                }


            }


            override fun onFailure(call: Call<AuthResponse<List<GetUserPlaylist>>>, t: Throwable) {

                Log.d("apiTest", t.message.toString())
            }
        })
    }



}