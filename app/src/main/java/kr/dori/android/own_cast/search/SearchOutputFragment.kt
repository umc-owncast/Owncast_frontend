package kr.dori.android.own_cast.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.MainActivity
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.FragmentSearchOutputBinding
import kr.dori.android.own_cast.forApiData.AuthResponse
import kr.dori.android.own_cast.forApiData.CastHomeDTO
import kr.dori.android.own_cast.forApiData.CastInterface
import kr.dori.android.own_cast.forApiData.getRetrofit
import kr.dori.android.own_cast.keyword.KeywordLoadingDialog
import kr.dori.android.own_cast.player.PlayCastActivity
import kr.dori.android.own_cast.playlist.SharedViewModel
import kotlin.coroutines.CoroutineContext

class SearchOutputFragment : Fragment(), SearchMover , CoroutineScope {

    private lateinit var binding: FragmentSearchOutputBinding
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val searchAdapter = SearchAdapter(this)
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + corutineJob
    private lateinit var corutineJob: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchOutputBinding.inflate(inflater, container, false)

        val detail_interest = arguments?.getString("detail_interest","야구")
        detail_interest?.let{
            searchOtherCast(it)
        }


        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("ifsuccess", "success")
                val data: Intent? = result.data
                val isSuccess = data?.getBooleanExtra("result", false) ?: false
                (activity as? MainActivity)?.setPlaylistTableVisibility(isSuccess)
            }
        }

        //직접 이동말고 스택을 연속으로 두개 지우는게 더 깔끔하니까 수정하자.
        binding.outputBackIv.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, SearchFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.outputSearchIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.outputExitIv.setOnClickListener {
            searchViewModel.addtext("")
            requireActivity().supportFragmentManager.popBackStack()
        }

        // ViewModel 관찰하여 텍스트 뷰 업데이트
        searchViewModel.text.observe(viewLifecycleOwner, Observer { newText ->
            binding.fragmentSearchOutputTitleTv.text = newText
        })

        return binding.root
    }

    override fun goPlayCast(list: List<CastHomeDTO>, id:Long) {
        val intent = Intent(requireContext(), PlayCastActivity::class.java)
        intent.putExtra("list",ArrayList(list))
        intent.putExtra("id",id)
        activityResultLauncher.launch(intent)
    }

    override fun goAddCast(id : Long) {
        val intent = Intent(requireContext(), SearchAddCategoryActivity::class.java)
        sharedViewModel.data.value?.let{
            intent.putExtra("categoryList",ArrayList(it))
        }
        intent.putExtra("id",id)
        startActivity(intent)
    }

    override fun backSearch() {
        // 구현 필요 없음
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
                        searchAdapter.dataList =it
                    }
                    binding.fragmentSearchOutputRv.adapter = searchAdapter
                    binding.fragmentSearchOutputRv.layoutManager = GridLayoutManager(context,2)

                } ?: run {
                    // 실패 시 처리
                    Toast.makeText(requireContext(),"다시 시도해주세요", Toast.LENGTH_SHORT)
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
                Log.d("apiTest-SearchOther", response.toString())
                Log.d("apiTest-SearchOther", "연결실패 코드: ${response.code()}")

                Log.d("apiTest-SearchOther", "오류 이유: ${response.body()?.message}")

                null
            }
        } catch (e: Exception) {
            Log.d("apiTest-SearchOther", "API 호출 실패: ${e.message}")
            null
        }
    }
}
