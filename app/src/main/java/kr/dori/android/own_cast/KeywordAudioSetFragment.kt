package kr.dori.android.own_cast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kr.dori.android.own_cast.databinding.FragmentHomeBinding
import kr.dori.android.own_cast.databinding.FragmentKeywordAudiosetBinding

class KeywordAudioSetFragment: Fragment() {
    lateinit var binding: FragmentKeywordAudiosetBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeywordAudiosetBinding.inflate(inflater, container, false)


        val list = listOf("남성1","남성2","여성1","여성2","여성3")

        //참고 https://develop-oj.tistory.com/27#google_vignette 드롭다운 메뉴
        binding.keywordAudStyleSp.adapter =KeywordAudioSetAdapter(requireContext(), R.layout.item_audiostyle_spinner,list)
        binding.keywordAudStyleSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val value = binding.keywordAudStyleSp.getItemAtPosition(p2).toString()
                Toast.makeText(requireContext(), value, Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                // 선택되지 않은 경우
            }
        }

        binding.keywordAudiosetMakebtnTb.setOnClickListener{
            val fragmentTransaction = (context as KeywordActivity).supportFragmentManager.beginTransaction()
            // 백 스택에 추가하여 뒤로 가기 버튼을 통해 이전 프래그먼트로 돌아갈 수 있습니다.
            fragmentTransaction.replace(R.id.keyword_fragment_frm, KeywordAudioScriptFragment())
            fragmentTransaction.addToBackStack(null)//뒤로가기 버튼으로 돌아갈 수 있음.
            fragmentTransaction.commit()
        }
        return binding.root
    }

}