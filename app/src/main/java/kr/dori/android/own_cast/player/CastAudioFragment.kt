package kr.dori.android.own_cast.player

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.data.CastPlayerData
import kr.dori.android.own_cast.databinding.FragmentCastAudioBinding
import kr.dori.android.own_cast.forApiData.Cast

class CastAudioFragment(val currentCast: Cast) : Fragment() {

    lateinit var binding: FragmentCastAudioBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCastAudioBinding.inflate(inflater,container,false)

        binding.castTitle.text = currentCast.castTitle

        if(currentCast.castCreator != "헬로"){
            binding.creatorCategory.text = "${currentCast.castCreator}-${currentCast.castCategory}"
        }else{
            binding.creatorCategory.visibility = View.GONE
        }


        return binding.root

    }

    fun setCastTitle(castTitle: String){
        binding.castTitle.text = castTitle
    }

}