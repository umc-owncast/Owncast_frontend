package kr.dori.android.own_cast.keyword

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class KeywordAudiosetVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3
    //수록곡 상세정보 동영상 이렇게 세개 받을거임, 아까 배너어댑터는 새로 추가될때 받았지만
    //이미 세개가 정해져있기 때문에 createFragment에서는 정해진걸 추가해주는 방식으로 만들것임.
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> KeyvpAudioSetFragment()
            1 -> KeyvpAudioScriptFragment()
            else -> KeyvpAudioSaveFragment()
        }
    }
    //when은 switch 역할
}
