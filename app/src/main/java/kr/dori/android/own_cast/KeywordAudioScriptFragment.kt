package kr.dori.android.own_cast

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kr.dori.android.own_cast.databinding.FragmentKeywordAuioscriptBinding
import kr.dori.android.own_cast.databinding.FragmentKeywordSearchBinding

class KeywordAudioScriptFragment:Fragment() {
    lateinit var binding: FragmentKeywordAuioscriptBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeywordAuioscriptBinding.inflate(inflater, container, false)
        return binding.root



    }
}