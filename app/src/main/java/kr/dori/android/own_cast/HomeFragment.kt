package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.dori.android.own_cast.databinding.FragmentHomeBinding
import kr.dori.android.own_cast.databinding.FragmentKeywordAuioscriptBinding


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.insertKeyw.setOnClickListener {
            startActivity(Intent(getActivity(),KeywordActivity::class.java))
        }
        return binding.root



    }
}