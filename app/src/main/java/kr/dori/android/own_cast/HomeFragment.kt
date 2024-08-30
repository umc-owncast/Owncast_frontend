package kr.dori.android.own_cast

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.databinding.FragmentHomeBinding
import kr.dori.android.own_cast.forApiData.CastInterface
import kr.dori.android.own_cast.forApiData.getRetrofit
import kr.dori.android.own_cast.keyword.KeywordActivity
import kr.dori.android.own_cast.keyword.KeywordAppData
import kr.dori.android.own_cast.keyword.KeywordLoadingDialog
import kr.dori.android.own_cast.playlist.SharedViewModel
import kr.dori.android.own_cast.player.BackgroundPlayService

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private val textList = ArrayList<TextView>()
    private var keywordDataList = mutableListOf<String>()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    // 서비스 관련 변수
    private var service: BackgroundPlayService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as BackgroundPlayService.LocalBinder
            this@HomeFragment.service = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            service = null
            isBound = false
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val intent = Intent(context, BackgroundPlayService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 데이터 설정


        // Set the text of cell_center TextView to detail_interest


        // 텍스트 뷰 바인딩
        textViewBinding()
        initTextUi()

        // 사용자 닉네임이 있을 경우 인사말 설정
        if (SignupData.nickname != null) {
            binding.homefrFavorTv.text = "${SignupData.nickname}님,\n어떤걸 좋아하세요?"
        }

        // 서버와 소통하여 데이터를 가져와 리스트에 저장 및 로딩창 표시
        if(KeywordAppData.detailTopic.isNullOrEmpty()||!KeywordAppData.mainTopic.equals(SignupData.detail_interest)){//전역변수가 채워져있는지 확인(통신을 한번 했는지)
            KeywordAppData.mainTopic = SignupData.detail_interest
            fetchDataFromServer()

        }else{//이미 통신을 완료했을때는 애니메이션과 listener 처리만 해줌
            keywordDataList.clear()
            keywordDataList.addAll(KeywordAppData.detailTopic)
            keywordDataList.sortBy { it.length }
            randomizeTextViews() // 데이터가 성공적으로 받아오면 텍스트 뷰 랜덤 배치
            initListener()
        }




        // 검색창 클릭 시 키워드 액티비티로 이동
        binding.insertKeyw.setOnClickListener {
            val intent = Intent(getActivity(), KeywordActivity::class.java)
            stopAudio() // 음원 중단
            intent.putExtra("isSearch", true)
            startActivity(intent)
        }

        // 결과 처리 후 액티비티 결과를 처리하기 위한 런처 등록
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val isSuccess = data?.getBooleanExtra("result", false) ?: false
                if (isSuccess) {
                    // 데이터가 성공적으로 업데이트된 경우, 다시 텍스트 뷰를 랜덤 배치
                    randomizeTextViews()
                }
            }
        }

        // 직접 입력 클릭 시 키워드 액티비티로 이동
        binding.homefrScriptDirectInputTv.setOnClickListener {
            val intent = Intent(getActivity(), KeywordActivity::class.java)
            intent.putExtra("isSearch", false)
            stopAudio() // 음원 중단
            activityResultLauncher.launch(intent)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // 화면이 보일 때마다 텍스트 뷰의 내용 랜덤 배치 및 크기 조정
        randomizeTextViews()
        binding.cellCenter.text = SignupData.detail_interest
        binding.homefrKeywordTopicTv.text = SignupData.detail_interest
    }

    private fun fetchDataFromServer() {
        val getKeyword = getRetrofit().create(CastInterface::class.java)
        /*val dialog = KeywordLoadingDialog(requireContext(), "데이터를 불러오고 있어요")
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()*/

        CoroutineScope(Dispatchers.IO).launch {
            val response = getKeyword.getKeywordHome()
            withContext(Dispatchers.Main) {
                try {
                    //dialog.dismiss()  // 로딩창을 onCreateView에서만 표시하도록 하고, 완료 후 숨김
                    if (response.isSuccessful) {
                        response.body()?.result?.let {
                            keywordDataList.clear()
                            keywordDataList.addAll(it.keywords)
                            keywordDataList.sortBy { it.length }
                            //앱데이터 전역으로 선언(추후 viewModel정도로 해놔도 좋을듯?)
                            KeywordAppData.updateDetailTopic(it.keywords)
                            Log.d("homefr","${keywordDataList.toString()}")
                            randomizeTextViews() // 데이터가 성공적으로 받아오면 텍스트 뷰 랜덤 배치
                            initListener()
                        }
                    } else {
                        Log.d("fetchDataFromServer", "failed code : ${response.code()}")
                        Toast.makeText(requireContext(), "데이터를 받아오는데 실패했습니다.\n 에러코드 : ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }



    private fun textViewBinding() {
        textList.clear()
        // 텍스트 뷰를 리스트에 추가
        //글자수 적게 들어가야되는 3, 4부터 먼저 list에 넣음
        textList.add(binding.cell3)
        textList.add(binding.cell4)
        textList.add(binding.cell1)
        textList.add(binding.cell2)
        textList.add(binding.cell6)
        textList.add(binding.cell5)

    }
    private fun getRandomTextSize(size: Int, index:Int): Float {
        var minSize = 10f + size*1f // 최소 텍스트 크기
        var maxSize = 36f - size*2f // 최대 텍스트 크기
        if(index<2){
            minSize = 10f + size*1f // 최소 텍스트 크기
            maxSize = 36f - size*2f // 최대 텍스트 크기
        }else if(index <4){
            minSize = 14f + size*1f // 최소 텍스트 크기
            maxSize = 40f - size*2f // 최대 텍스트 크기
        }else{
            minSize = 18f + size*1f // 최소 텍스트 크기
            maxSize = 48f - size*2f // 최대 텍스트 크기
        }

        return (minSize + Math.random() * (maxSize - minSize)).toFloat()
    }

    private fun getRandomTextSizeLong(size: Int): Float {
        val minSize = 14f + size*1f // 최소 텍스트 크기
        val maxSize = 40f - size*1f // 최대 텍스트 크기
        return (minSize + Math.random() * (maxSize - minSize)).toFloat()
    }



    private fun randomizeTextViews() {
        // 데이터가 없는 경우, 빈 화면을 표시
        if (keywordDataList.isEmpty()) {
            textList.forEach { textView ->
                textView.text = ""
                textView.visibility = View.GONE
            }
            return
        }

        // 데이터를 랜덤하게 배치
        val randomizedDataShort = mutableListOf<String>()
        val randomizedDataLong = mutableListOf<String>()
        for(i in keywordDataList.size-1 downTo 0){
            if(keywordDataList[i].length <6){
                randomizedDataShort.add(keywordDataList[i])
            }
            else randomizedDataLong.add(keywordDataList[i])
        }
        randomizedDataShort.shuffle()
        randomizedDataLong.shuffle()
        for (i in textList.indices) {
            if (i < keywordDataList.size) {
                if(i<randomizedDataShort.size){
                    textList[i].text = randomizedDataShort[i]
                }else{
                    textList[i].text = randomizedDataLong[i-randomizedDataShort.size]
                }

                textList[i].visibility = View.VISIBLE
                val textSize = getRandomTextSize(keywordDataList[i].length, i)
                textList[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            } else {
                textList[i].visibility = View.GONE
            }


        }

        // 애니메이션 적용
        applyIndividualAnimationsToTextViews()
    }

    private fun applyIndividualAnimationsToTextViews() {
        // ViewTreeObserver를 사용하여 레이아웃이 완료된 후 실행
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // 기준 뷰 (cell_center) 가져오기


                textList.forEachIndexed { index, textView ->
                    // 텍스트 뷰의 랜덤 크기 설정
                    val randomSize = (14..24).random().toFloat() // 랜덤 크기
                    textView.textSize = randomSize
                    val centerView = binding.root.findViewById<View>(R.id.cell_center)

                    // 기준 뷰의 중앙 좌표 계산
                    val centerX = (centerView.x + centerView.width / 2)
                    val centerY = (centerView.y + centerView.height / 2)

                    // 텍스트 뷰의 초기 위치와 가시성 설정
                    textView.translationX = centerX - (textView.x + textView.width / 2)
                    textView.translationY = centerY - (textView.y + textView.height / 2)
                    textView.alpha = 0f // 처음에는 보이지 않도록 설정



                    // 애니메이션 설정 함수 호출
                    startAnimation(textView, index)
                }

                // 애니메이션 설정 후 리스너 제거하여 중복 호출 방지
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun startAnimation(textView: TextView, index: Int) {



        // X와 Y 방향의 애니메이션 생성
        val animatorX = ObjectAnimator.ofFloat(textView, "translationX", textView.translationX, 0f)
        val animatorY = ObjectAnimator.ofFloat(textView, "translationY", textView.translationY, 0f)
        val animatorAlpha = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f) // 애니메이션 중 보이게 설정

        // 애니메이션 설정
        animatorX.duration = 1000
        animatorY.duration = 1000
        animatorAlpha.duration = 1000
        animatorX.startDelay = (index * 100).toLong() // 각 텍스트 뷰마다 다른 시작 지연 설정
        animatorY.startDelay = (index * 100).toLong()
        animatorAlpha.startDelay = (index * 100).toLong()

        // 애니메이션 시작
        animatorX.start()
        animatorY.start()
        animatorAlpha.start()
    }

    private fun initTextUi() {
        var content = SpannableString(binding.homefrScriptDirectInputTv.text.toString())
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        binding.homefrScriptDirectInputTv.text = content

        content = SpannableString(binding.homefrKeywordTopicTv.text.toString())
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        binding.homefrKeywordTopicTv.text = content
    }

    private fun stopAudio() {
        if (isBound && service != null) {
            service?.pauseAudio()
        }
    }

    private fun initListener(){
        for(i in textList.indices){
            textList[i].setOnClickListener {
                val intent = Intent(getActivity(), KeywordActivity::class.java)
                stopAudio() // 음원 중단
                intent.putExtra("searchText", textList[i].text.toString())
                startActivity(intent)
            }
        }
    }
}