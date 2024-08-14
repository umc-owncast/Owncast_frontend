package kr.dori.android.own_cast.keyword

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.FragmentKeywordInputBinding
import kr.dori.android.own_cast.databinding.FragmentKeywordSearchBinding

class KeywordInputFragment:Fragment() {
    lateinit var binding : FragmentKeywordInputBinding
    private var isText = false
    private var searchText : String = ""
    private lateinit var sharedViewModel: KeywordViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeywordInputBinding.inflate(inflater, container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(KeywordViewModel::class.java)


        //뒤로가기 버튼
        binding.keyInputBackIv.setOnClickListener{
            activity?.finish()
        }
        //editText변경됐을때 확인
        binding.keywordInputEt.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    //searchText = binding.keywordInputEt.text.toString()
                    val length = s.toString().length
                    binding.keyInputLengthTv.setText("(${length}자 / 10000자)")
                    //만약에 줄바꿈이나, 특수문자만 넣었으면은 false 들어감
                    isText = !(s.toString().isEmpty()||containsOnlySpecialCharacters(s.toString()))
                    searchText = binding.keywordInputEt.toString()
                }
                btnActivate()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.keyInputNextIv.setOnClickListener {
            if(isText){
                val fragmentTransaction = (context as KeywordActivity).supportFragmentManager.beginTransaction()
                //앞서 입력한 키워드(@+id/keyword_aud_et)를 지울지 말지 확인하기 위한 번들
                //이건 직접 입력했기 때문에 앞서 입력한 키워드 안뜰 것임.
                var bundle = Bundle()
                var fragment = KeywordAudioSetFragment()
                //이 뷰모델에서 키워드 직접 받아옴
                sharedViewModel.setInputKeyword(searchText)

                fragment.arguments = bundle
                fragmentTransaction.replace(R.id.keyword_fragment_frm, fragment)
                // 백 스택에 추가하여 뒤로 가기 버튼을 통해 이전 프래그먼트로 돌아갈 수 있습니다.
                fragmentTransaction.addToBackStack(null)//뒤로가기 버튼으로 돌아갈 수 있음.
                fragmentTransaction.commit()
            }else{
                Toast.makeText(requireContext(), "빈공간입니다.", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    fun btnActivate(){//텍스트 입력하면 버튼 켜지게
        if(isText){
            //textView.setTextColor(Color.parseColor("#FFFFFF"))
            //우리 메인 컬러
            binding.keyInputNextIv.backgroundTintList = ColorStateList.
            valueOf(ContextCompat.getColor(this.requireContext(), R.color.main_color))

        }
        else{
            //textView.setTextColor(Color.parseColor("#FFFFFF"))
            binding.keyInputNextIv.backgroundTintList = ColorStateList.
            valueOf(ContextCompat.getColor(this.requireContext(), R.color.button_unclick))
        }

    }
    private fun containsOnlySpecialCharacters(text: String): Boolean {
        // 특수문자만 포함된 경우를 체크하는 함수, 공백이거나 문자만 포함하면 true를 반환한다.
        return text.all { !it.isLetterOrDigit() && !it.isWhitespace() }
    }
}