package kr.dori.android.own_cast


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kr.dori.android.own_cast.databinding.FragmentKeywordSearchBinding


class KeywordSearchFragment:Fragment() {
    lateinit var binding: FragmentKeywordSearchBinding
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
            Log.d("CLICK_ADD","YES")
            val fragmentTransaction = (context as KeywordActivity).supportFragmentManager.beginTransaction()
             // 백 스택에 추가하여 뒤로 가기 버튼을 통해 이전 프래그먼트로 돌아갈 수 있습니다.
            fragmentTransaction.replace(R.id.keyword_fragment_frm, KeywordAudioSetFragment())
            fragmentTransaction.addToBackStack(null)//뒤로가기 버튼으로 돌아갈 수 있음.
            fragmentTransaction.commit()
        }
        return binding.root



    }
}