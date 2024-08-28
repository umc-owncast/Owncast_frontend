package kr.dori.android.own_cast.keyword

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class KeywordAudiosetVPAdapter(fragment: Fragment, val searchText: String?) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3
    //수록곡 상세정보 동영상 이렇게 세개 받을거임, 아까 배너어댑터는 새로 추가될때 받았지만
    //이미 세개가 정해져있기 때문에 createFragment에서는 정해진걸 추가해주는 방식으로 만들것임.

    val keyvpAudioScriptFragment =  KeyvpAudioScriptFragment()
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{
                val fragment = KeyvpAudioSetFragment()
                val bundle = Bundle()

                bundle.putString("searchText",searchText)
                //앞서 입력한 키워드(@+id/keyword_aud_et)를 지울지 말지 확인하기 위한 번들
                fragment.arguments = bundle
                return fragment
            }
            1 ->{
                keyvpAudioScriptFragment//안에 함수 실행시켜줘야함
            }
            else -> KeyvpAudioSaveFragment()
        }
    }

    fun getFragment(): KeyvpAudioScriptFragment{
        return keyvpAudioScriptFragment
    }
    //when은 switch 역할
}
