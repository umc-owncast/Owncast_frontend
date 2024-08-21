package kr.dori.android.own_cast.keyword


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.SignupData
import kr.dori.android.own_cast.databinding.FragmentKeywordSearchBinding


class KeywordSearchFragment:Fragment() {
    lateinit var binding: FragmentKeywordSearchBinding
    private var isText = false
    private var searchText: String = ""//다음 프래그먼트로 화면 넘기기 위한 변수
    private var textViewList = ArrayList<TextView>()//처음에 밑에 뜨는 관련 키워드 사용 위한 변수
    private lateinit var sharedViewModel: KeywordViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeywordSearchBinding.inflate(inflater, container, false)
        binding.backMainIv.setOnClickListener {
            activity?.finish()
        }




        Log.d("KeywordSearchFragment_checkInit","onCrete")
        initTextInputListener()
        bindingView()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

    }

    fun bindingView(){
        if(!textViewList.isNullOrEmpty()) textViewList.clear()
        textViewList.add(binding.keywordActSuggest1Tv)
        textViewList.add(binding.keywordActSuggest2Tv)
        textViewList.add(binding.keywordActSuggest3Tv)
        textViewList.add(binding.keywordActSuggest4Tv)
        textViewList.add(binding.keywordActSuggest5Tv)
        textViewList.add(binding.keywordActSuggest6Tv)
        initKeyword()
    }
    fun initKeyword() {//관련 텍스트들을 초기화하는 함수
        //클래스 내 전역변수로 선언한 textList에 바인딩 목록들을 넣고 초기화한다.
        binding.keywordSearchTopicTv.text = SignupData.interest
            for (i: Int in 0..5) {
                //view모델 안에 실제 데이터가 있다면 그걸 텍스트 뷰에 그대로 반영

                if (i < KeywordAppData.detailTopic.size) {

                    Log.d("KeywordSearchFragment_checkInit", "변경 전 :${textViewList[i].text}")
                    textViewList[i].text = KeywordAppData.detailTopic[i]
                    Log.d("KeywordSearchFragment_checkInit", "변경 후 :${textViewList[i].text}")
                    textViewList[i].setOnClickListener {
                        val fragmentTransaction =
                            (context as KeywordActivity).supportFragmentManager.beginTransaction()
                        // 백 스택에 추가하여 뒤로 가기 버튼을 통해 이전 프래그먼트로 돌아갈 수 있습니다.
                        var bundle = Bundle()
                        var fragment = KeywordAudioSetFragment()
                        bundle.putString("searchText", textViewList[i].text.toString())
                        //검색입력어를 같이 넘겨야해서 bundle넣음
                        fragment.arguments = bundle
                        fragmentTransaction.replace(R.id.keyword_fragment_frm, fragment)
                        fragmentTransaction.addToBackStack(null)//뒤로가기 버튼으로 돌아갈 수 있음.
                        fragmentTransaction.commit()
                    }
                } else {
                    textViewList[i].visibility = View.GONE
                }
            }


    }

    fun searchFinish() {//엔터키나 버튼 눌렀을때 검색어와 다음 프래그먼트로 가기 위한 함수
        if (isText) {
            val fragmentTransaction =
                (context as KeywordActivity).supportFragmentManager.beginTransaction()
            // 백 스택에 추가하여 뒤로 가기 버튼을 통해 이전 프래그먼트로 돌아갈 수 있습니다.
            var bundle = Bundle()
            var fragment = KeywordAudioSetFragment()
            bundle.putString("searchText", searchText)
            //검색입력어를 같이 넘겨야해서 bundle넣음
            fragment.arguments = bundle
            fragmentTransaction.replace(R.id.keyword_fragment_frm, fragment)
            fragmentTransaction.addToBackStack(null)//뒤로가기 버튼으로 돌아갈 수 있음.
            fragmentTransaction.commit()
        } else {
            Toast.makeText(requireContext(), "검색어를 입력하세요", Toast.LENGTH_SHORT).show()
        }

    }

    fun initTextInputListener() {
        binding.keywordActAddIv.setOnClickListener {
            searchFinish()
        }
        binding.keywordSearchDeleteIv.setOnClickListener {//누르면 삭제
            if (isText) {
                binding.keywordEt.text.clear()
            }
        }
        //text가 비어있으면, isText가 변한다.
        binding.keywordEt.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    searchText = binding.keywordEt.text.toString()
                }
                isText = s?.isNotEmpty() == true


                if (isText) {
                    binding.keywordSearchDeleteIv.visibility = View.VISIBLE
                } else binding.keywordSearchDeleteIv.visibility = View.GONE

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        //엔터키를 눌렀을때 searchFinish를 실행(검색하는거)
        binding.keywordEt.setOnEditorActionListener { v, actionId, event ->
            // Check if the action is the "Enter" key
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_NEXT ||
                event?.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                searchFinish()

                true // Return true to indicate that the event was handled
            } else {
                false // Return false to allow other handlers to process the event
            }
        }
    }
}