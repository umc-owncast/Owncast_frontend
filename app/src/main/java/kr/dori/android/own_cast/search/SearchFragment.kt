package kr.dori.android.own_cast.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.MainActivity
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.SignupData
import kr.dori.android.own_cast.data.SongData
import kr.dori.android.own_cast.databinding.FragmentSearchBinding
import kr.dori.android.own_cast.forApiData.AuthResponse

import kr.dori.android.own_cast.forApiData.CastHomeDTO

import kr.dori.android.own_cast.forApiData.CastInterface
import kr.dori.android.own_cast.forApiData.ErrorResponse
import kr.dori.android.own_cast.forApiData.GetAllPlaylist
import kr.dori.android.own_cast.forApiData.GetUserPlaylist
import kr.dori.android.own_cast.forApiData.PlayListInterface
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.forApiData.getRetrofit
import kr.dori.android.own_cast.keyword.KeywordAudioSetFragment
import kr.dori.android.own_cast.keyword.PlaylistText

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import kr.dori.android.own_cast.player.PlayCastActivity
import kr.dori.android.own_cast.playlist.SharedViewModel

class SearchFragment : Fragment(), SearchMover {

    lateinit var binding: FragmentSearchBinding
    private val searchAdapter = SearchAdapter(this)
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    lateinit var gridLayoutManager: GridLayoutManager
    lateinit var inflaterLayout: LayoutInflater
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var categoryList : List<GetAllPlaylist> = listOf()
    private var detail_interest : String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        inflaterLayout = inflater


        //searchDataUpdate()//다른 유저 정보 받아오는 함수

        initSearchCastData()
        val getAllPlaylist = kr.dori.android.own_cast.getRetrofit().create(Playlist::class.java)
        CoroutineScope(Dispatchers.IO).launch() {
            launch {
                try {
                    val response =
                        getAllPlaylist.getAllPlaylist() //변수명이 어지럽지만 첫번째 getAll은 레트로핏 활성화 객체이고, 두번째는 인터페이스 내부 함수이다.
                    if (response.isSuccessful) {
                        var playlistCategoryData = response.body()?.result
                        withContext(Dispatchers.Main) {
                            playlistCategoryData?.let {
                                sharedViewModel.setData(it.toMutableList())
                                Log.d("apitest-searchHome","${it.toString()}")
                            }
                        }
                    } else {

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }


        }


        if(SignupData.interest.equals("self")){
            binding.textView18.text = "타사용자의 owncast를 재생하거나 담아올 수 있어요"
        }else{
            detail_interest = SignupData.detail_interest
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
            var bundle = Bundle()

            bundle.putString("detail_interest",detail_interest)
            input.arguments = bundle
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

    override fun goPlayCast(list: List<CastHomeDTO>, id:Long) {//여기다간 캐스트 정보 담아야함
        val intent = Intent(requireContext(), PlayCastActivity::class.java)
        intent.putExtra("list",ArrayList(list))
        intent.putExtra("id",id)
        activityResultLauncher.launch(intent)
    }

    override fun goAddCast(id:Long) {//여기다가 카테고리 정보 담아야함
        val intent = Intent(requireContext(), SearchAddCategoryActivity::class.java)
        sharedViewModel.data.value?.let{
            intent.putExtra("categoryList",ArrayList(it))
        }
        intent.putExtra("id",id)
        startActivity(intent)
    }

    override fun backSearch() {
        TODO("Not yet implemented")
    }
    fun initSearchCastData(){
        val apiService = getRetrofit().create(CastInterface::class.java)
        apiService.searchHome().enqueue(object: Callback<AuthResponse<List<CastHomeDTO>>> {
            override fun onResponse(call: Call<AuthResponse<List<CastHomeDTO>>>, response: Response<AuthResponse<List<CastHomeDTO>>>) {
                if(response.code().equals("COMMON200")) {
                    val resp: AuthResponse<List<CastHomeDTO>> = response.body()!!
                    when (resp.code) {
                        "COMMON200" -> {
                            Log.d("apiTest-searchHome", "연결성공, resp값: ${resp.result.toString()}}")
                            resp.result?.let {
                                setItemData(it)
                            }
                        }
                        else -> {
                            Log.d("apiTest-searchHome", "연결실패 코드 : ${resp.code}, ${resp.message}")

                        }
                    }
                }else{
                    val resp= response.errorBody()?.string()
                    resp?.let {
                        try {
                            // Gson을 사용해 에러 응답을 파싱
                            val gson = Gson()
                            val errorResponse = gson.fromJson(it, ErrorResponse::class.java)
                            Log.d("apiTest-searchHome", "오류 발생: ${errorResponse.code}, ${errorResponse.message}")
                        } catch (e: Exception) {
                            Log.d("apiTest-searchHome", "에러 응답 파싱 실패: ${e.message}")
                        }
                    } ?: run {
                        Log.d("apiTest-searchHome", "에러 바디가 없음: ${response.code()}")
                    }
                }
            }
            override fun onFailure(call: Call<AuthResponse<List<CastHomeDTO>>>, t: Throwable) {
                Log.d("apiTest-searchHome","연결실패 ${t.message}")
            }
        })
    }


    fun setItemData(castHomeDTO: List<CastHomeDTO>){
        for (i in 0 until castHomeDTO.size) {
            // item_layout.xml을 inflate하여 GridLayout에 추가
            val itemView = inflaterLayout.inflate(R.layout.item_search_fr, binding.gridLayout, false)
            // 필요시 itemView의 내부 요소를 수정
            val thumbButton = itemView.findViewById<ImageView>(R.id.item_thumb_iv)
            val titleTv = itemView.findViewById<TextView>(R.id.searchfr_item_title_tv)
            val categoryTv = itemView.findViewById<TextView>(R.id.searchfr_item_category_tv)
            val durationTv = itemView.findViewById<TextView>(R.id.searchfr_item_duration_tv)
            titleTv.text = castHomeDTO[i].title
            categoryTv.text = "${castHomeDTO[i].memberName}-${castHomeDTO[i].playlistName}"
            durationTv.text = formatTime(castHomeDTO[i].audioLength.toInt())
            thumbButton.setOnClickListener {
                goPlayCast(castHomeDTO, castHomeDTO[i].id)
            }
            val addCtgOffBtn = itemView.findViewById<ImageView>(R.id.searchfr_item_add_category_off_iv)
            addCtgOffBtn.setOnClickListener {
                goAddCast(castHomeDTO[i].id)
            }

            binding.gridLayout.addView(itemView)
        }
    }
    private fun formatTime(totalSeconds:Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

}
