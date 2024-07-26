package kr.dori.android.own_cast.keyword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.FragmentKeyvpAudiosaveBinding


class KeyvpAudioSaveFragment : Fragment() {
    lateinit var binding: FragmentKeyvpAudiosaveBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeyvpAudiosaveBinding.inflate(inflater, container, false)

        val list = listOf("카테고리1","카테고리2","카테고리3","카테고리4","카테고리5")

        //참고 https://develop-oj.tistory.com/27#google_vignette 드롭다운 메뉴
        binding.keyAudSaveCategorySp.adapter =
            KeyAudsetDropdownAdapter(requireContext(), R.layout.item_aud_set_spinner,list)
        binding.keyAudSaveCategorySp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val value = binding.keyAudSaveCategorySp.getItemAtPosition(p2).toString()
                Toast.makeText(requireContext(), value, Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                // 선택되지 않은 경우
            }
        }

        return binding.root



    }
}