package kr.dori.android.own_cast.search

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.GridLayout
import android.widget.ImageView

import android.widget.TextView
import android.widget.Toast


import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.MainActivity
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.SignupData
import kr.dori.android.own_cast.data.CastPlayerData
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
import kr.dori.android.own_cast.player.CastWithPlaylistId

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
        var data = list.map{
            CastWithPlaylistId(
                castId= it.id,
                playlistId = -1L,
                castTitle = it.title,
                isPublic = true,
                castCreator = it.memberName,
                castCategory = detail_interest?:"로딩실패",
                audioLength = it.audioLength
            )
        }
        var imageData = list.map{
            it.imagePath
        }
        CastPlayerData.setCast(data)//데이터 초기화
        CastPlayerData.setCurrentPos(id)//
        CastPlayerData.setImagePath(imageData)
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
                if(response.isSuccessful) {
                    val resp: AuthResponse<List<CastHomeDTO>> = response.body()!!
                    resp.result?.let {
                        setItemData(it)
                    } ?: run{
                        Toast.makeText(context, "검색 결과가 없습니다",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(context, "서버 오류 코드 : ${response.code()}",Toast.LENGTH_SHORT).show()
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
                    }
                }
            }
            override fun onFailure(call: Call<AuthResponse<List<CastHomeDTO>>>, t: Throwable) {
                Toast.makeText(context, "서버 연결 실패",Toast.LENGTH_SHORT).show()
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


            if (castHomeDTO[i].imagePath.startsWith("http")) {
                // URL로부터 이미지 로드 (Glide 사용)
                Glide.with(itemView.context)
                    .load(castHomeDTO[i].imagePath)
                    .into(thumbButton)
            } else {
                // 로컬 파일에서 이미지 로드
                val bitmap = BitmapFactory.decodeFile(castHomeDTO[i].imagePath)
                thumbButton.setImageBitmap(bitmap)
            }


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
