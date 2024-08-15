package kr.dori.android.own_cast

import android.app.ActionBar.LayoutParams
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.dori.android.own_cast.databinding.FragmentPlaylistBinding
import kr.dori.android.own_cast.databinding.FragmentSearchBinding
import kr.dori.android.own_cast.forApiData.AuthResponse

import kr.dori.android.own_cast.forApiData.CastHomeDTO
import kr.dori.android.own_cast.forApiData.getRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment(),SearchMover {

    lateinit var binding: FragmentSearchBinding
    private val searchAdapter = SearchAdapter(this)
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    lateinit var gridLayoutManager: GridLayoutManager

    private val dummyData = mutableListOf(
        SongData("Cast1", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "animal"),
        SongData("Cast2", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "monkey"),
        SongData("Cast3", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "koala"),
        SongData("Cast4", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, true, "human"),
        SongData("Cast5", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "slug"),
        SongData("Cast1", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "animal"),
        SongData("Cast2", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "monkey"),
        SongData("Cast3", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "koala"),
        SongData("Cast4", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, true, "human"),
        SongData("Cast5", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "slug")
    )



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        searchAdapter.dataList = dummyData





        for (i in 0 until 4) {
            // item_layout.xml을 inflate하여 GridLayout에 추가
            val itemView = inflater.inflate(R.layout.item_search_fr, binding.gridLayout, false)
            // 필요시 itemView의 내부 요소를 수정
            val thumbButton = itemView.findViewById<ImageView>(R.id.item_thumb_iv)
            thumbButton.setOnClickListener {
                goPlayCast()
            }
            val addCtgOffBtn = itemView.findViewById<ImageView>(R.id.searchfr_item_add_category_off_iv)
            /*val addCtgOnBtn = itemView.findViewById<ImageView>(R.id.searchfr_item_add_category_on_iv)*/
            addCtgOffBtn.setOnClickListener {
                /*addCtgOffBtn.visibility = View.GONE
                addCtgOnBtn.visibility = View.VISIBLE*/
                goAddCast()
                /*addCtgOffBtn.visibility = View.VISIBLE
                addCtgOnBtn.visibility = View.GONE*/
            }

            binding.gridLayout.addView(itemView)
        }


        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("ifsuccess", "success")
                val data: Intent? = result.data
                val isSuccess = data?.getBooleanExtra("result", false) ?: false
                (activity as? MainActivity)?.setPlaylistTableVisibility(isSuccess)
            }
        }


        binding.fragmentSearchArrowIv.setOnClickListener {
            val input = SearchTwoFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, input)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
        binding.fragmentSearchPlaintextIv.setOnClickListener {
            val input = SearchInputFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, input)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }






        return binding.root
    }

    override fun goPlayCast() {
        val intent = Intent(requireContext(), PlayCastActivity::class.java)
        activityResultLauncher.launch(intent)
    }

    override fun goAddCast() {
        val intent = Intent(requireContext(), SearchAddCategoryActivity::class.java)
        startActivity(intent)
    }

    override fun backSearch() {
        TODO("Not yet implemented")
    }


    /*fun searchDataUpdate(){//서버에서 searchFragment에 나오는 4개 캐스트 데이터 받아오기
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
                        //setSongData(resp,)
                    }
                    else ->{
                        Log.d("apiTest","연결실패 코드 : ${resp.code}")

                    }
                }
            }

            override fun onFailure(call: Call<AuthResponse<List<CastHomeDTO>>>, t: Throwable) {
                Log.d("apiTest", "아예 failure 병신아")
            }
        })
    }*/
    /*fun setSongData(resp:AuthResponse<List<CastHomeDTO>>, inflater: LayoutInflater){
        for(i:Int in 0..minOf(resp.result!!.size,4)){
            // item_layout.xml을 inflate하여 GridLayout에 추가
            val itemView = inflater.inflate(R.layout.item_search_fr, binding.gridLayout, false)
            // 필요시 itemView의 내부 요소를 수정
            val thumbButton = itemView.findViewById<ImageView>(R.id.item_thumb_iv)
            //1. 제목
            itemView.findViewById<TextView>(R.id.searchfr_item_title_tv).text = resp.result[i].title
            //2. 유저-카테고리
            itemView.findViewById<TextView>(R.id.searchfr_item_category_tv).text=
                "${resp.result[i].memberName}-${resp.result[i].playlistName}"
            //3. 시간
            itemView.findViewById<TextView>(R.id.searchfr_item_duration_tv).text=
                "${resp.result[i].audioLength.toInt()/60}:${String.format("%02d", resp.result[i].audioLength.toInt()% 60)}"

            //함수
            thumbButton.setOnClickListener {
                goPlayCast()
            }

            val addCtgOffBtn = itemView.findViewById<ImageView>(R.id.searchfr_item_add_category_off_iv)
            addCtgOffBtn.setOnClickListener {
                goAddCast()
            }
            binding.gridLayout.addView(itemView)
        }
    }*/
}
