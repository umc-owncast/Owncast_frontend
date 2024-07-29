package kr.dori.android.own_cast

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.dori.android.own_cast.databinding.FragmentHomeBinding
import kr.dori.android.own_cast.keyword.KeywordActivity


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.insertKeyw.setOnClickListener {
            val intent = Intent(getActivity(), KeywordActivity::class.java)
            intent.putExtra("isSearch",true)
            startActivity(intent)
        }
        binding.homefrScriptDirectInputTv.setOnClickListener {
            val intent = Intent(getActivity(), KeywordActivity::class.java)
            intent.putExtra("isSearch",false)
            startActivity(intent)
        }
        return binding.root



    }
}