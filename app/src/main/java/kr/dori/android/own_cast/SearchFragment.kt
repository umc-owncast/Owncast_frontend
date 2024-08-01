package kr.dori.android.own_cast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.dori.android.own_cast.databinding.FragmentPlaylistBinding
import kr.dori.android.own_cast.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    private val saveFragment = SearchSaveCategoryFragment()
    private val onlySearchFragment = OnlySearchFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)


/*          데이터 전달 부분: 카테고리 데이터 받아와서 캐스트 정보 전달
            val bundle = Bundle().apply {

                putParcelableArrayList("isSave", ArrayList(sharedViewModel.data.value?.filter { it.isSave } ?: listOf()))
            }
            castFragment.arguments = bundle

 */
            /* 이 부분을 리사이클러뷰 어댑터에 설정해야 됨

        binding..setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, saveFragment)
                .addToBackStack(null)
                .commit()
        }
                 */
        binding.fragmentSearch.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm,onlySearchFragment)
                .addToBackStack(null)
                .commit()
        }


        return binding.root
    }
}