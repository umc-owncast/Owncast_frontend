package kr.dori.android.own_cast.keyword


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.FragmentKeywordSearchBinding


class KeywordSearchFragment:Fragment() {
    lateinit var binding: FragmentKeywordSearchBinding
    private var isText = false
    private var searchText : String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeywordSearchBinding.inflate(inflater, container, false)
        binding.backMainIv.setOnClickListener{

            activity?.finish()

        }
        binding.keywordActAddIv.setOnClickListener{
            if(isText){
                Log.d("CLICK_ADD","YES")
                val fragmentTransaction = (context as KeywordActivity).supportFragmentManager.beginTransaction()
                // 백 스택에 추가하여 뒤로 가기 버튼을 통해 이전 프래그먼트로 돌아갈 수 있습니다.

                var bundle = Bundle()
                var fragment = KeywordAudioSetFragment()
                bundle.putString("searchText",searchText)
                //검색입력어를 같이 넘겨야해서 bundle넣음
                fragment.arguments = bundle
                fragmentTransaction.replace(R.id.keyword_fragment_frm, fragment)
                fragmentTransaction.addToBackStack(null)//뒤로가기 버튼으로 돌아갈 수 있음.
                fragmentTransaction.commit()
            }else{
                Toast.makeText(requireContext(), "검색어를 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        binding.keywordEt.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    searchText = binding.keywordEt.text.toString()
                }
                isText = s?.isNotEmpty() == true

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        return binding.root
    }
}