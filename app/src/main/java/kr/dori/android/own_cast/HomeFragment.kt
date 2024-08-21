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
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.databinding.FragmentHomeBinding
import kr.dori.android.own_cast.forApiData.CastInterface
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.forApiData.getRetrofit
import kr.dori.android.own_cast.keyword.KeywordActivity
import kr.dori.android.own_cast.keyword.KeywordAppData

import kr.dori.android.own_cast.keyword.KeywordData
import kr.dori.android.own_cast.keyword.KeywordLoadingDialog
import kr.dori.android.own_cast.keyword.KeywordViewModel
import kr.dori.android.own_cast.playlist.SharedViewModel



class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private var textList = ArrayList<TextView>()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        //데이터 설정
        if(SignupData.interest!=null){
            binding.mainInterstTv.text = SignupData.interest
            binding.homefrKeywordTopicTv.text = SignupData.interest
        }


        textViewBinding()

        if (SignupData.nickname!=null) binding.homefrFavorTv.text ="${SignupData.nickname}님,\n어떤걸 좋아하세요?"
        //밑줄 추가하는 함수
        initTextUi()
        if(KeywordAppData.detailTopic.isNullOrEmpty()){
            initData()

        }else{
            initKeyword()

        }


        binding.insertKeyw.setOnClickListener {//검색창 이동
            val intent = Intent(getActivity(), KeywordActivity::class.java)
            intent.putExtra("isSearch",true)

            startActivity(intent)
        }

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val isSuccess = data?.getBooleanExtra("result", false) ?: false
            }
        }

        binding.homefrScriptDirectInputTv.setOnClickListener {
            val intent = Intent(getActivity(), KeywordActivity::class.java)
            intent.putExtra("isSearch",false)
            activityResultLauncher.launch(intent)
        }

        return binding.root
    }

    private fun textViewBinding(){
        textList.add(binding.homefrTdKeyword1Tv)
        textList.add(binding.homefrTdKeyword2Tv)
        textList.add(binding.homefrTdKeyword3Tv)
        textList.add(binding.homefrTdKeyword4Tv)
        textList.add(binding.homefrTdKeyword5Tv)
        textList.add(binding.homefrTdKeyword6Tv)

    }
    private fun initKeyword(){
        //Log.d("initDataFinish","${KeywordAppData.detailTopic.size}")
        for(i:Int in 0..5){
            //view모델 안에 실제 데이터가 있다면 그걸 텍스트 뷰에 그대로 반영
            if(i< KeywordAppData.detailTopic.size){//detailTopic이 MainActivity에서 api받아옴 시간 좀 걸림

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

    fun initData(){

        Log.d("initDataCheck","${KeywordAppData.detailTopic.isNullOrEmpty()}, ${KeywordAppData.detailTopic}")

        val getKeyword = getRetrofit().create(CastInterface::class.java)
        val dialog = KeywordLoadingDialog(requireContext(),"데이터를 불러오고 있어요")
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        CoroutineScope(Dispatchers.IO).launch() {

            val response = getKeyword.getKeywordHome()
            launch {
                withContext(Dispatchers.Main) {
                    try {
                        dialog.dismiss()
                        if (response.isSuccessful) {
                            response.body()?.result?.let{
                                KeywordAppData.updateDetailTopic(it)
                            }
                        } else {
                            Log.d("initDataFinish","failed code : ${response.code()}")
                            Log.d("initDataFinish","${KeywordAppData.detailTopic.size}")
                            Toast.makeText(requireContext(),"데이터를 받아오는데 실패했습니다.\n 에러코드 : ${response.code()}",Toast.LENGTH_SHORT).show()
                        }
                        initKeyword()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


            }
        }
    }
}