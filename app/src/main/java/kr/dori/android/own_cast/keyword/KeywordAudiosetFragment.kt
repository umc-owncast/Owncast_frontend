package kr.dori.android.own_cast.keyword

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kr.dori.android.own_cast.AddCategoryDialog
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.FragmentKeywordAudiosetBinding
//Keyvp가 달린 3개의 프래그먼트를 관리함, frm으로 감싸고 있음.
class KeywordAudioSetFragment: Fragment(), KeywordAudioOutListener, KeywordBtnClickListener {
    lateinit var binding: FragmentKeywordAudiosetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeywordAudiosetBinding.inflate(inflater, container, false)
        val keywordAdapter = KeywordAudiosetVPAdapter(this)

        binding.keywordAudiosetVp.adapter = keywordAdapter
        //이 코드를 넣으면 뷰페이저가 유저의 swipe를 비활성화시킴
        binding.keywordAudiosetVp.isUserInputEnabled = false

        //내부의 탭들의 간격을 벌려주는 함수
        for (i in 0 until binding.keywordAudiosetTb.tabCount) {
            val tab = (binding.keywordAudiosetTb.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(30, 0, 30, 0)
            tab.requestLayout()
        }

        //indicator를 색칠할 방법이 없어서 배경색을 칠했음
        binding.keywordAudiosetTb.getTabAt(0)?.view?.
        setBackgroundColor(resources.getColor(R.color.main_color,null))
        binding.keyAudDoneIv.setOnClickListener{
            val dialog = KeywordAudioOutDialog(requireContext(), this)

            dialog.show()
        }

        binding.keyAudBackIv.setOnClickListener{
            if(binding.keywordAudiosetVp.currentItem==0){
                requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
                requireActivity().supportFragmentManager.popBackStack()
            }
            else{
                prevPage()

            }
        }
        /*val makeBtn = view?.findViewById<TextView>(R.id.keyword_audioset_makebtn_tb)
        makeBtn?.setOnClickListener {
            nextPage()
            Toast.makeText(requireContext(), "yes", Toast.LENGTH_SHORT).show()
        }*/


        return binding.root
    }

    override fun getOut() {
        activity?.finish()
    }

    override fun onButtonClick() {
        binding.keywordAudiosetVp.currentItem += 1
        binding.keywordAudiosetTb.getTabAt(binding.keywordAudiosetVp.currentItem)?.view?.
        setBackgroundColor(resources.getColor(R.color.main_color,null))

        //현재 위치의 배경을 칠해줌
    }



    private fun prevPage(){
        binding.keywordAudiosetTb.getTabAt(binding.keywordAudiosetVp.currentItem)?.view?.
        setBackgroundColor(resources.getColor(R.color.white,null))
        binding.keywordAudiosetVp.currentItem -= 1
    }


}