package kr.dori.android.own_cast.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.SignupData
import kr.dori.android.own_cast.databinding.FragmentSearchInputBinding
import kr.dori.android.own_cast.keyword.KeywordActivity
import kr.dori.android.own_cast.keyword.KeywordAppData

class SearchInputFragment : Fragment() {

    private lateinit var binding: FragmentSearchInputBinding
    private val searchViewModel: SearchViewModel by activityViewModels()
    private var isEmpty: Boolean = true
    private var textList = ArrayList<TextView>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchInputBinding.inflate(inflater, container, false)

        binding.searchInputTopicTv.text = SignupData.interest
        binding.fragmentSearchInputTextTv.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    searchViewModel.addtext(s.toString())
                }
                isEmpty = s?.isEmpty() ?: true
                setView()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 구현 필요 없음
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 구현 필요 없음
            }
        })

        initKeyword()

        binding.searchInputExitIv.setOnClickListener {
            searchViewModel.addtext("")
            binding.fragmentSearchInputTextTv.setText("")
        }

        binding.searchInputSearchIv.setOnClickListener {
            val output = SearchOutputFragment()

            var bundle = Bundle()

            bundle.putString("detail_interest",binding.fragmentSearchInputTextTv.text.toString())
            output.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, output)
                .addToBackStack(null) // 백 스택에 추가
                .commit()
        }

        binding.searchInputBackIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun setView() {
        if (isEmpty) {
            binding.searchInputExitIv.visibility = View.GONE
            binding.searchInputSearchIv.isClickable = false
        } else {
            binding.searchInputExitIv.visibility = View.VISIBLE
            binding.searchInputSearchIv.isClickable = true
        }
    }

    private fun textViewBinding(){
        if(!textList.isNullOrEmpty()){
            textList.clear()
        }
        textList.add(binding.keywordActSuggest1Tv)
        textList.add(binding.keywordActSuggest2Tv)
        textList.add(binding.keywordActSuggest3Tv)
        textList.add(binding.keywordActSuggest4Tv)
        textList.add(binding.keywordActSuggest5Tv)
        textList.add(binding.keywordActSuggest6Tv)
    }
    private fun initKeyword(){
        textViewBinding()
        //Log.d("initDataFinish","${KeywordAppData.detailTopic.size}")
        for(i:Int in 0..5){
            //view모델 안에 실제 데이터가 있다면 그걸 텍스트 뷰에 그대로 반영
            if(i< KeywordAppData.detailTopic.size){//detailTopic이 MainActivity에서 api받아옴 시간 좀 걸림

                textList[i].text = KeywordAppData.detailTopic[i]
                textList[i].setOnClickListener {
                    val output = SearchOutputFragment()
                    var bundle = Bundle()
                    bundle.putString("detail_interest",KeywordAppData.detailTopic[i])
                    output.arguments = bundle
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, output)
                        .addToBackStack(null) // 백 스택에 추가
                        .commit()
                }
            }else{
                textList[i].visibility = View.GONE
            }
        }
    }

}
