package kr.dori.android.own_cast.keyword

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.FragmentKeywordAudiosetBinding
import kr.dori.android.own_cast.forApiData.AuthResponse
import kr.dori.android.own_cast.forApiData.CastInterface
import kr.dori.android.own_cast.forApiData.PostCastByKeyword
import kr.dori.android.own_cast.forApiData.PostCastByScript
import kr.dori.android.own_cast.forApiData.PostCastForResponse
import kr.dori.android.own_cast.forApiData.getRetrofit
import kotlin.coroutines.CoroutineContext

//Keyvp가 달린 3개의 프래그먼트를 관리함, frm으로 감싸고 있음.
class KeywordAudioSetFragment: Fragment(), KeywordAudioOutListener, KeywordBtnClickListener, CoroutineScope {
    lateinit var binding: FragmentKeywordAudiosetBinding
    private lateinit var sharedViewModel: KeywordViewModel

    private var alreadyVisit : Boolean = false//이미 audioScript에서 exoPlayer를 생성했는지 확인.
    //하지만.. 그냥 여기다가 할당하는 방식이 맞는거같은데 실수한거같음

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + corutineJob
    private lateinit var corutineJob: Job
    lateinit var keywordAdapter:KeywordAudiosetVPAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeywordAudiosetBinding.inflate(inflater, container, false)


        sharedViewModel = ViewModelProvider(requireActivity()).get(KeywordViewModel::class.java)
        val searchText = (arguments?.getString("searchText"))
        keywordAdapter = KeywordAudiosetVPAdapter(this,searchText)
        binding.keywordAudiosetVp.adapter = keywordAdapter
        initViewPager()

        binding.keyAudDoneIv.setOnClickListener{
            val dialog = KeywordAudioOutDialog(requireContext(), this)
            dialog.show()
        }


        //뒤로가기 버튼 눌렀을때 모션 처리
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            prevPage()
        }
        binding.keyAudBackIv.setOnClickListener{
            prevPage()
        }

        return binding.root
    }

    fun initViewPager(){
        //이 코드를 넣으면 뷰페이저가 유저의 swipe를 비활성화시킴
        binding.keywordAudiosetVp.isUserInputEnabled = false

        //내부의 탭들의 간격을 벌려주는 함수
        for (i in 0 until binding.keywordAudiosetTb.tabCount) {
            val tab = (binding.keywordAudiosetTb.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(4, 0, 4, 0)
            tab.requestLayout()
        }
        //indicator를 색칠할 방법이 없어서 배경색을 칠했음
        binding.keywordAudiosetTb.getTabAt(0)?.view?.
        setBackgroundColor(resources.getColor(R.color.main_color,null))
        binding.keywordAudiosetTb.getTabAt(1)?.view?.
        setBackgroundColor(resources.getColor(R.color.gray,null))
        binding.keywordAudiosetTb.getTabAt(2)?.view?.
        setBackgroundColor(resources.getColor(R.color.gray,null))
    }

    //dialog용 listener 인터페이스 구현
    override fun getOut() {
        activity?.finish()
    }

    override fun onButtonClick() {
        binding.keywordAudiosetVp.currentItem += 1
        binding.keywordAudiosetTb.getTabAt(binding.keywordAudiosetVp.currentItem)?.view?.
        setBackgroundColor(resources.getColor(R.color.main_color,null))

        //현재 위치의 배경을 칠해줌
    }
    private fun prevPage(){

        if(binding.keywordAudiosetVp.currentItem==0){
            val isHome = (arguments?.getBoolean("isHome"))
            if(isHome==null||isHome==false){
                requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
                requireActivity().supportFragmentManager.popBackStack()
            }else{//홈프래그먼트에서 키워드 클릭하고 바로오면은, 그냥 액티비티 종료
                getOut()
            }
        }else{
            binding.keywordAudiosetTb.getTabAt(binding.keywordAudiosetVp.currentItem)?.view?.
            setBackgroundColor(resources.getColor(R.color.gray,null))

            if(binding.keywordAudiosetVp.currentItem == 1){
                alreadyVisit = true
                binding.keywordAudiosetVp.currentItem -= 1
            }else{
                binding.keywordAudiosetVp.currentItem -= 1
            }
        }




    }

    override fun createCastByScript(postCastByScript: PostCastByScript){
        corutineJob = Job()
        var dialogText : String
        if(binding.keywordAudiosetVp.currentItem==0)dialogText = "스크립트를 생성 중이에요"
        else dialogText = "스크립트를 다시 생성 중이에요"
        val dialog = KeywordLoadingDialog(requireContext(), dialogText)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        launch(Dispatchers.IO) {
            val apiResult = createCastByScriptLauncher(postCastByScript)  // API 호출
            // UI 작업은 Dispatchers.Main에서 실행
            withContext(Dispatchers.Main) {
                // 로딩창 닫기
                dialog.dismiss()
                apiResult?.let {
                    // 성공적인 API 결과를 UI에 반영

                    sharedViewModel.setSentences(it.result!!.sentences)//viewModel로 받아온 정보 넘기기
                    sharedViewModel.setCastId(it.result!!.id)
                    sharedViewModel.setUrl(it.result!!.fileUrl)

                    if(binding.keywordAudiosetVp.currentItem==0){
                        if(alreadyVisit){//이미 생성된 프래그먼트일때는 생명주기때문에 여기서 실행해줘야함
                            keywordAdapter.getFragment().initPlayer()
                            keywordAdapter.getFragment().initRecyclerView()
                        }
                        onButtonClick()//
                    }
                    else{//재생성 스크립트를 누름
                        keywordAdapter.getFragment().initPlayer()
                        keywordAdapter.getFragment().initRecyclerView()
                    }
                } ?: run {
                    // 실패 시 처리
                    Toast.makeText(requireContext(), "API 호출 실패\n 잠시후 다시 시도해주세요.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private suspend fun createCastByScriptLauncher(postCastByScript: PostCastByScript): AuthResponse<PostCastForResponse>?{
        val apiService = getRetrofit().create(CastInterface::class.java)
        val context = requireContext()
        return try {
            val response = apiService.postCastByScript(postCastByScript)

            if (response.isSuccessful) {
                Log.d("apiTest-CreateCast", "저장성공: ${response.body()?.result}")
                response.body()
            } else {

                Log.d("apiTest-CreateCast", "연결실패 에러바디: ${response.errorBody()?.string()}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "API 호출 실패\n 오류코드 : ${response.code()}", Toast.LENGTH_SHORT).show()
                }

                null
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "API 호출 실패 ${e.message}",Toast.LENGTH_SHORT).show()
            }
            Log.d("apiTest-CreateCast", "API 호출 실패: ${e.message}")
            null
        }

    }

    override fun createCastByKeyword(postCastByKeyword: PostCastByKeyword){
        corutineJob = Job()
        var dialogText : String
        if(binding.keywordAudiosetVp.currentItem==0)dialogText = "스크립트를 생성 중이에요"
        else dialogText = "스크립트를 다시 생성 중이에요"
        val dialog = KeywordLoadingDialog(requireContext(),dialogText)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        launch(Dispatchers.IO) {
            val apiResult = createCastByKeywordLauncher(postCastByKeyword)  // API 호출
            // UI 작업은 Dispatchers.Main에서 실행
            withContext(Dispatchers.Main) {
                // 로딩창 닫기
                dialog.dismiss()
                apiResult?.let {
                    // 성공적인 API 결과를 UI에 반영
                    sharedViewModel.setSentences(it.result!!.sentences)//viewModel로 받아온 정보 넘기기
                    sharedViewModel.setCastId(it.result!!.id)

                    sharedViewModel.setUrl(it.result!!.fileUrl)
                    if(binding.keywordAudiosetVp.currentItem==0){
                        if(alreadyVisit){//이미 생성된 프래그먼트일때는 생명주기때문에 여기서 실행해줘야함
                            keywordAdapter.getFragment().initPlayer()
                            keywordAdapter.getFragment().initRecyclerView()
                        }
                        onButtonClick()//
                    }
                    else{//재생성 스크립트를 누름
                        keywordAdapter.getFragment().initPlayer()
                        keywordAdapter.getFragment().initRecyclerView()
                    }

                } ?: run {
                    // 실패 시 처리

                }
            }
        }
    }
    private suspend fun createCastByKeywordLauncher(postCastByKeyword: PostCastByKeyword):AuthResponse<PostCastForResponse>?{
        val apiService = getRetrofit().create(CastInterface::class.java)

        return try {
            val response = apiService.postCastByKeyword(postCastByKeyword)
            Log.d("캐스트-키워드",response.toString())
            if (response.isSuccessful) {
                response.body()
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "API 호출 실패\n 오류코드 : ${response.code()}", Toast.LENGTH_SHORT).show()
                }
                null
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "API 호출 실패 ${e.message}",Toast.LENGTH_SHORT).show()
            }
            Log.d("apiTest-CreateCast", "API 호출 실패 exception: ${e.message}")
            null
        }
    }




    override fun onDestroy() {
        super.onDestroy()
        // 액티비티가 파괴될 때 모든 코루틴 취소
        if (::corutineJob.isInitialized) {
            corutineJob.cancel() // 또는 다른 적절한 작업
        }

    }



}