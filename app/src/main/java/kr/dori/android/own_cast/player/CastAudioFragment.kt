package kr.dori.android.own_cast.player

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.data.CastPlayerData
import kr.dori.android.own_cast.databinding.FragmentCastAudioBinding

class CastAudioFragment(val castName: String) : Fragment() {

    lateinit var binding: FragmentCastAudioBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCastAudioBinding.inflate(inflater,container,false)

        binding.castTitle.text = castName



        return binding.root

    }

    fun setCastTitle(castTitle: String){
        binding.castTitle.text = castTitle
    }

}