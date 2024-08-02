package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import kr.dori.android.own_cast.databinding.FragmentHomeBinding
import kr.dori.android.own_cast.keyword.KeywordActivity
import kr.dori.android.own_cast.keyword.KeywordData


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private var textList = ArrayList<TextView>()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val dummyData = KeywordData("야구", arrayOf("선수", "올림픽 야구", "해외야구", "국내야구"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // ViewModel 데이터 관찰
        /*sharedViewModel.data.observe(viewLifecycleOwner, Observer { newData ->
            categoryAdapter.dataList = newData
            categoryAdapter.notifyDataSetChanged()
        })*/

        // ViewModel에 초기 데이터 설정
        if (sharedViewModel.keywordData.value == null) {
            sharedViewModel.setKeywordData(dummyData)
        }
        //밑줄 추가하는 함수
        initTextUi()


        initKeyword()
        binding.insertKeyw.setOnClickListener {
            val intent = Intent(getActivity(), KeywordActivity::class.java)
            intent.putExtra("isSearch",true)
            startActivity(intent)
        }

        binding.homefrScriptDirectInputTv.setOnClickListener {
            val intent = Intent(getActivity(), KeywordActivity::class.java)
            intent.putExtra("isSearch",false)
            startActivity(intent)
        }

        return binding.root
    }

    fun initKeyword(){

        textList.add(binding.homefrTdKeyword1Tv)
        textList.add(binding.homefrTdKeyword2Tv)
        textList.add(binding.homefrTdKeyword3Tv)
        textList.add(binding.homefrTdKeyword4Tv)
        textList.add(binding.homefrTdKeyword5Tv)
        textList.add(binding.homefrTdKeyword6Tv)
        sharedViewModel.keywordData.value?.keywordList
        for(i:Int in 0..5){
            //view모델 안에 실제 데이터가 있다면 그걸 텍스트 뷰에 그대로 반영
            if(i<sharedViewModel.keywordData.value?.keywordList!!.size){
                textList[i].text = sharedViewModel.keywordData.value?.keywordList!![i]
                textList[i].setOnClickListener {
                    val intent = Intent(getActivity(), KeywordActivity::class.java)
                    intent.putExtra("searchText",textList[i].text.toString())
                    startActivity(intent)
                }
            }else{
                textList[i].visibility = View.GONE
            }
        }
    }
    fun initTextUi(){
        var content = SpannableString(binding.homefrScriptDirectInputTv.getText().toString());
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        binding.homefrScriptDirectInputTv.text = content
        content = SpannableString(binding.homefrKeywordTopicTv.getText().toString());
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        binding.homefrKeywordTopicTv.text = content
    }
}