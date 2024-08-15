package kr.dori.android.own_cast.keyword


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.SignupData
import kr.dori.android.own_cast.databinding.FragmentKeyvpAudiosetBinding
import kr.dori.android.own_cast.forApiData.AuthResponse
import kr.dori.android.own_cast.forApiData.CastInterface
import kr.dori.android.own_cast.forApiData.PostCastByKeyword
import kr.dori.android.own_cast.forApiData.PostCastByScript
import kr.dori.android.own_cast.forApiData.PostCastForResponse
import kr.dori.android.own_cast.forApiData.getRetrofit
import kr.dori.android.own_cast.keyworddata.VoiceList
import kr.dori.android.own_cast.keyworddata.VoiceListRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.POST
import java.util.Objects
import kotlin.coroutines.CoroutineContext

class KeyvpAudioSetFragment() : Fragment(), CoroutineScope {
    lateinit var binding: FragmentKeyvpAudiosetBinding
    //뷰페이저에서 다음 프래그먼트로  넘기는 인터페이스 변수
    private var listener: KeywordBtnClickListener? = null
    lateinit var voiceList: VoiceList//드롭 다운 메뉴에서 사용하는 음성과 데이터 클래스
    private var searchText:String? = null
    /*--------api통신용 데이터---------------*/
    private lateinit var sharedViewModel: KeywordViewModel
    private var formality:String = "OFFICIAL"
    private lateinit var voice:String
    private var audioTime:Int = 60
    /*-----------------------*/
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + corutineJob
    private lateinit var corutineJob: Job

    //뷰페이저를 갖고 있는 부모 프래그먼트를 참고해서, 다음 페이지로 넘긴다.
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? KeywordBtnClickListener
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentKeyvpAudiosetBinding.inflate(inflater, container, false)
        corutineJob = Job()//코루틴(비동기 api)를 위해초기화해준다
        sharedViewModel = ViewModelProvider(requireActivity()).get(KeywordViewModel::class.java)
        /////////////////////////////////만들고 싶어하는 텍스트 받아와서 띄우는 코드
        searchText = arguments?.getString("searchText")
        if (searchText != null) {//키워드
            binding.keywordAudEt.visibility = View.VISIBLE
            binding.keywordAudEt.text = searchText
            binding.keAudsetUnderline.visibility = View.VISIBLE
        } else {//직접 입력
            binding.keywordAudEt.visibility = View.GONE
            binding.keAudsetUnderline.visibility = View.GONE
        }
        /////////////////////////////////////
        initVoiceList()//사용자의 언어-발음에 따라 dropdownmenu변경
        initDropDownList()



        binding.keywordAudiosetSb.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress < 2) {
                    seekBar?.progress = 2
                } else if (progress % 2 == 0) {
                    audioTime = progress*30
                    binding.keyAudSetCurTimeTv.text = "${progress / 2}분"
                } else {
                    audioTime = progress*30
                    binding.keyAudSetCurTimeTv.text = "${progress / 2}분30초"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        //다음 프래그먼트로 넘어감
        binding.keywordAudiosetMakebtnTb.setOnClickListener {
            if (searchText != null) {//키워드로 생성
                Log.d("apiTest-CreateCast", PostCastByKeyword(searchText!!,formality,voice,audioTime).toString())
                sharedViewModel.setPostCastKeyword(PostCastByKeyword(searchText!!,formality,voice,audioTime))
                listener?.createCastByKeyword(sharedViewModel.postCastKeyword.value!!)
            } else {//스크립트로 생성(input
                //inputFragmnet에서 따로 저장해놨음
                Log.d("apiTest-CreateCast", "inputKeywordPrint ${sharedViewModel.inputKeyword.value.toString()}")
                sharedViewModel.setPostCastScript(PostCastByScript(sharedViewModel.inputKeyword.value!!,formality,voice))
                listener?.createCastByScript(sharedViewModel.postCastScript.value!!)
            }


        }
        return binding.root
    }

    //다음 페이지 넘겨주는 listener 해제
    override fun onDetach() {
        super.onDetach()
        listener = null
    }
    private fun initDropDownList(){
        val list = voiceList.styleBusiness
        voice = voiceList.styleBusiness.dataType[0]
        //참고 https://develop-oj.tistory.com/27#google_vignette 드롭다운 메뉴
        binding.keywordAudStyleSp.adapter =
            KeyAudsetDropdownAdapter(requireContext(), R.layout.item_aud_set_spinner,list.gender)
        binding.keywordAudStyleSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val value = binding.keywordAudStyleSp.getItemAtPosition(p2).toString()

                //api 전송용 데이터를 받아옴
                if(formality.equals("CASUAL")){
                    Toast.makeText(requireContext(), voiceList.styleCasual.dataType[p2].toString(), Toast.LENGTH_SHORT).show()
                    voice = voiceList.styleCasual.dataType[p2]
                }
                else{

                    Toast.makeText(requireContext(), voiceList.styleBusiness.dataType[p2].toString(), Toast.LENGTH_SHORT).show()
                    voice = voiceList.styleBusiness.dataType[p2]
                }

                binding.keywordAudStyleSp.setBackgroundResource(R.drawable.key_audset_dropdown_off_bg)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                // 선택되지 않은 경우
                binding.keywordAudStyleSp.setBackgroundResource(R.drawable.key_audset_dropdown_off_bg)
                //다시 스피너가 색깔이 바뀌지 않은거처럼 바꿔준다
            }
        }
        //위에는 캐주얼 비즈니스 버튼에서 눌렀을때 색깔 바뀌는것과 드롭다운 메뉴 변경
        binding.keywordAudStyleBuiTv.setOnClickListener {
            if(formality.equals("CASUAL")){//캐주얼 상태일때 비즈니스 스타일로 변경
                binding.keywordAudStyleSp.adapter =
                    KeyAudsetDropdownAdapter(requireContext(), R.layout.item_aud_set_spinner,voiceList.styleBusiness.gender)
                formality = "OFFICIAL"
                changeColor(binding.keywordAudStyleBuiTv,binding.keywordAudStyleCasTv)
            }
        }
        binding.keywordAudStyleCasTv.setOnClickListener {
            if(formality.equals("OFFICIAL")){//비즈니스 상태일때 캐주얼로 변경
                binding.keywordAudStyleSp.adapter =
                    KeyAudsetDropdownAdapter(requireContext(), R.layout.item_aud_set_spinner,voiceList.styleCasual.gender)
                formality = "CASUAL"
                changeColor(binding.keywordAudStyleCasTv,binding.keywordAudStyleBuiTv)
            }
        }
    }
    private fun changeColor(selected:TextView, unselected:TextView){
        selected.backgroundTintList = ColorStateList.
        valueOf(ContextCompat.getColor(this.requireContext(), R.color.main_color))
        selected.setTextColor(Color.parseColor("#FFFFFF"))

        unselected.backgroundTintList = ColorStateList.
        valueOf(ContextCompat.getColor(this.requireContext(), R.color.button_unclick))
        unselected.setTextColor(Color.parseColor("#455A64"))
    }




    private fun initVoiceList(){
        voiceList = when(SignupData.language){
            "English"-> when(SignupData.accent){
                "usa"-> VoiceListRepository.voiceLists[0][0]
                "eng"->VoiceListRepository.voiceLists[0][1]
                "aus"->VoiceListRepository.voiceLists[0][2]
                else->VoiceListRepository.voiceLists[0][3]//ind

            }
            "Japanese"->when(SignupData.accent){
                "jp"->VoiceListRepository.voiceLists[1][0]
                else->VoiceListRepository.voiceLists[1][0]
            }
            else->when(SignupData.accent){//스페인어
                "sp"->VoiceListRepository.voiceLists[2][0]
                else->VoiceListRepository.voiceLists[2][1]
            }
        }
        Log.d("voiceLanguage",voiceList.language)
    }
}


