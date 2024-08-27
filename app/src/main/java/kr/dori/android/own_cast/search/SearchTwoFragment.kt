package kr.dori.android.own_cast.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.ActivityMover
import kr.dori.android.own_cast.MainActivity
import kr.dori.android.own_cast.R

import kr.dori.android.own_cast.data.CastPlayerData

import kr.dori.android.own_cast.databinding.FragmentSearchTwoBinding
import kr.dori.android.own_cast.forApiData.AuthResponse
import kr.dori.android.own_cast.forApiData.CastHomeDTO
import kr.dori.android.own_cast.forApiData.CastInterface
import kr.dori.android.own_cast.forApiData.PostCastByScript
import kr.dori.android.own_cast.forApiData.PostCastForResponse
import kr.dori.android.own_cast.forApiData.getRetrofit
import kr.dori.android.own_cast.keyword.KeywordLoadingDialog

import kr.dori.android.own_cast.player.CastWithPlaylistId

import kr.dori.android.own_cast.player.PlayCastActivity
import kr.dori.android.own_cast.playlist.SharedViewModel
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query
import kotlin.coroutines.CoroutineContext

class SearchTwoFragment:Fragment(), SearchMover, CoroutineScope,ActivityMover {
    private lateinit var binding: FragmentSearchTwoBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private val searchTwoAdapter = SearchTwoAdapter(this)



    private val sharedViewModel: SharedViewModel by activityViewModels()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + corutineJob
    private lateinit var corutineJob: Job





    private var detail_interest : String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchTwoBinding.inflate(inflater, container, false)


        detail_interest = arguments?.getString("detail_interest","야구")
        detail_interest?.let{
            searchOtherCast(it)
        }





        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.d("ifsuccess", "success")
                    val data: Intent? = result.data
                    val isSuccess = data?.getBooleanExtra("result", false) ?: false
                    (activity as? MainActivity)?.setPlaylistTableVisibility(isSuccess)
                }
            }
        binding.searchFrTwoBackIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }


        return binding.root
    }
    fun searchOtherCast(keyword:String){
        corutineJob = Job()
        var dialogText : String = "로딩 중이에요"


        val dialog = KeywordLoadingDialog(requireContext(), dialogText)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        launch(Dispatchers.IO) {
            val apiResult = searchOtherCastLauncher(keyword)  // API 호출

            // UI 작업은 Dispatchers.Main에서 실행
            withContext(Dispatchers.Main) {
                // 로딩창 닫기
                dialog.dismiss()
                apiResult?.let {
                    // 성공적인 API 결과를 UI에 반영
                    it.result?.let {
                        searchTwoAdapter.dataList =it
                    }
                    binding.fragmentTwoRv.adapter = searchTwoAdapter
                    binding.fragmentTwoRv.layoutManager = LinearLayoutManager(context)

                } ?: run {
                    // 실패 시 처리
                    Toast.makeText(requireContext(),"다시 시도해주세요",Toast.LENGTH_SHORT)
                }
            }
        }
    }
    private suspend fun searchOtherCastLauncher(keyword:String): AuthResponse<List<CastHomeDTO>>?{
        val apiService = getRetrofit().create(CastInterface::class.java)

        return try {
            val response = apiService.postSearchAPI(keyword)
            Log.d("apiTest-SearchOther", "검색어: ${keyword}")
            if (response.code().equals("200")||response.body()?.code.equals("COMMON200")) {
                Log.d("apiTest-SearchOther", "저장성공: ${response.body()?.result}")
                response.body()
            } else {

                Log.d("apiTest-SearchOther", "연결실패 코드: ${response.code()}")
                Log.d("apiTest-SearchOther", "오류 이유: ${response.body()?.message}")
                null
            }
        } catch (e: Exception) {
            Log.d("apiTest-SearchOther", "API 호출 실패: ${e.message}")
            null
        }
    }
    override fun goPlayCast(list: List<CastHomeDTO>, id:Long) {
        val intent = Intent(requireContext(), PlayCastActivity::class.java)

        var data = list.map{
            CastWithPlaylistId(
                castId= it.id,
                playlistId = -1L,
                castTitle = it.title,
                isPublic = true,
                castCreator = it.memberName,
                castCategory = detail_interest?:"로딩실패",
                audioLength = it.audioLength,
                imagePath = it.imagePath
            )
        }

        CastPlayerData.setCast(data,1)//데이터 초기화
        CastPlayerData.setCurrentPos(id)
        ToPlayCast(data)
    }

    override fun goAddCast(castHomeDTO:CastHomeDTO) {//여기다가 카테고리 정보 담아야함
        val intent = Intent(requireContext(), SearchAddCategoryActivity::class.java)
        /*sharedViewModel.data.value?.let{

            intent.putExtra("categoryList",ArrayList(it))
        }*/
        CastPlayerData.currentCast = castHomeDTO.let{
            CastWithPlaylistId(
                castId = it.id,
                playlistId = 0L,
                castTitle = it.playlistName,
                isPublic = true,
                castCreator = it.memberName,
                castCategory = it.memberName,
                audioLength = it.audioLength,
                imagePath = it.imagePath
            )
        }
        intent.putExtra("id",id)
        startActivity(intent)
    }

    override fun backSearch() {
        TODO("Not yet implemented")
    }


    override fun ToPlayCast(castList: List<CastWithPlaylistId>) {

        // 현재 서비스가 재생 중인지 확인하고 중지
        //val currentService = getCurrentServiceInstance()
        //  service?.stopAudio()

        // 캐스트 설정 및 새 액티비티로 이동
        val intent = Intent(requireContext(), PlayCastActivity::class.java)
        activityResultLauncher.launch(intent)
    }

    override fun ToEditAudio(id: Long, playlistId: Long) {
        TODO("Not yet implemented")
    }
}
