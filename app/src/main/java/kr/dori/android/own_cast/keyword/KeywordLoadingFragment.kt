package kr.dori.android.own_cast.keyword

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.FragmentKeyvpAudiosetBinding
import kr.dori.android.own_cast.databinding.FragmentKeywordLoadingBinding

class KeywordLoadingFragment:Fragment() {
    lateinit var binding: FragmentKeywordLoadingBinding
    private lateinit var rotateAnimation: Animation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeywordLoadingBinding.inflate(inflater, container, false)

        rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_turn)

        return binding.root
    }
    fun startLoading(duration: Long) {
        binding.keywordLoadingIv.startAnimation(rotateAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            stopLoading()
        }, duration)
    }

    fun stopLoading() {
        binding.keywordLoadingIv.clearAnimation()
        parentFragmentManager.beginTransaction().remove(this).commit()
    }
}
