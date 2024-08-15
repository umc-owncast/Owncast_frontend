package kr.dori.android.own_cast

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import kr.dori.android.own_cast.databinding.FragmentHomeBinding
import kr.dori.android.own_cast.keyword.KeywordActivity
import kr.dori.android.own_cast.keyword.KeywordAppData
import kr.dori.android.own_cast.keyword.KeywordData
import kr.dori.android.own_cast.keyword.KeywordViewModel


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private var textList = ArrayList<TextView>()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val dummyData = KeywordData("야구", arrayOf("선수", "올림픽 야구", "해외야구", "국내야구"))
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    val dummyData_viewmodel = mutableListOf(
        SongData("category_name1", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "animal"),
        SongData("category_name2", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "monkey"),
        SongData("category_name3", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "koala"),
        SongData("category_name4", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, true, "human"),
        SongData("category_name5", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "slug"),
        SongData("category_name6", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "animal"),
        SongData("category_name7", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "monkey"),

    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)



        // ViewModel에 초기 데이터 설정
        if (sharedViewModel.data.value.isNullOrEmpty()) {
            sharedViewModel.setData(dummyData_viewmodel)
            sharedViewModel.setKeywordData(dummyData)
        }
        //데이터 설정
        if (SignupData.nickname!=null) binding.homefrFavorTv.text ="${SignupData.nickname}님,\n어떤걸 좋아하세요?"

        //밑줄 추가하는 함수
        initTextUi()
        initKeyword()


        binding.insertKeyw.setOnClickListener {//검색창 이동
            val intent = Intent(getActivity(), KeywordActivity::class.java)
            intent.putExtra("isSearch",true)
            intent.putExtra("keywordData",sharedViewModel.keywordData.value)
            startActivity(intent)
        }



       activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
           if (result.resultCode == Activity.RESULT_OK) {
               val data: Intent? = result.data
               val isSuccess = data?.getBooleanExtra("result", false) ?: false
           }
       }

        binding.homefrScriptDirectInputTv.setOnClickListener {
            val arrayList = sharedViewModel.data.value?.let { ArrayList(it) }
            val intent = Intent(getActivity(), KeywordActivity::class.java)
            intent.putExtra("isSearch",false)
            intent.putParcelableArrayListExtra("SongData",arrayList)
            activityResultLauncher.launch(intent)
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
            /*if(i<sharedViewModel.keywordData.value?.keywordList!!.size){
                textList[i].text = sharedViewModel.keywordData.value?.keywordList!![i]
                textList[i].setOnClickListener {
                    val intent = Intent(getActivity(), KeywordActivity::class.java)
                    intent.putExtra("searchText",textList[i].text.toString())
                    startActivity(intent)
                }
            }else{
                textList[i].visibility = View.GONE
            }*/
            if(i< KeywordAppData.detailTopic.size){
                textList[i].text = KeywordAppData.detailTopic[i]
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