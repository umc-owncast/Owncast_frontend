package kr.dori.android.own_cast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.dori.android.own_cast.databinding.FragmentOnlySearchBinding

class OnlySearchFragment : Fragment() {
    lateinit var binding: FragmentOnlySearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOnlySearchBinding.inflate(inflater, container, false)

        binding.fragmentOnlySearchBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Inflate the layout for this fragment
        return binding.root
    }


}