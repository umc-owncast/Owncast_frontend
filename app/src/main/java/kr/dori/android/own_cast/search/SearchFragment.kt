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
import androidx.recyclerview.widget.GridLayoutManager
import kr.dori.android.own_cast.MainActivity
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.data.SongData
import kr.dori.android.own_cast.databinding.FragmentSearchBinding
import kr.dori.android.own_cast.forApiData.AuthResponse

import kr.dori.android.own_cast.forApiData.CastHomeDTO
import kr.dori.android.own_cast.forApiData.CastInterface
import kr.dori.android.own_cast.forApiData.GetUserPlaylist
import kr.dori.android.own_cast.forApiData.PlayListInterface
import kr.dori.android.own_cast.forApiData.getRetrofit
import kr.dori.android.own_cast.keyword.PlaylistText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import kr.dori.android.own_cast.player.PlayCastActivity

class SearchFragment : Fragment(), SearchMover {

    lateinit var binding: FragmentSearchBinding
    private val searchAdapter = SearchAdapter(this)
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    lateinit var gridLayoutManager: GridLayoutManager
    lateinit var inflaterLayout: LayoutInflater
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

        inflaterLayout = inflater
        searchAdapter.dataList = dummyData

        //searchDataUpdate()//다른 유저 정보 받아오는 함수





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
    fun initSearchCastData(){
        val apiService = getRetrofit().create(CastInterface::class.java)
        apiService.searchHome().enqueue(object: Callback<AuthResponse<List<CastHomeDTO>>> {
            override fun onResponse(call: Call<AuthResponse<List<CastHomeDTO>>>, response: Response<AuthResponse<List<CastHomeDTO>>>) {

                val resp: AuthResponse<List<CastHomeDTO>> = response.body()!!
                when(resp.code) {
                    "COMMON200" -> {
                        Log.d("apiTest-searchHome","연결성공, resp값: ${resp.result.toString()}}")
                        resp.result?.let {
                            setItemData(it)
                        }
                    }
                    else ->{
                        Log.d("apiTest-searchHome","연결실패 코드 : ${resp.code}, ${resp.message}")

                    }
                }
            }
            override fun onFailure(call: Call<AuthResponse<List<CastHomeDTO>>>, t: Throwable) {

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
            durationTv.text = "${castHomeDTO[i].audioLength}"

            thumbButton.setOnClickListener {
                goPlayCast()
            }
            val addCtgOffBtn = itemView.findViewById<ImageView>(R.id.searchfr_item_add_category_off_iv)
            addCtgOffBtn.setOnClickListener {
                goAddCast()
            }

            binding.gridLayout.addView(itemView)
        }
    }

}
