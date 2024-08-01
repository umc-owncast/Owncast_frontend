package kr.dori.android.own_cast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.dori.android.own_cast.databinding.FragmentSearchSaveCategoryBinding


class SearchSaveCategoryFragment : Fragment() {
    lateinit var binding: FragmentSearchSaveCategoryBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchSaveCategoryBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment

        binding.fragmentSearchSaveCategoryBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return binding.root

    }


}