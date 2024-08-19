package kr.dori.android.own_cast.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.FragmentSearchInputBinding

class SearchInputFragment : Fragment() {

    private lateinit var binding: FragmentSearchInputBinding
    private val searchViewModel: SearchViewModel by activityViewModels()
    private var isEmpty: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchInputBinding.inflate(inflater, container, false)

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
}
