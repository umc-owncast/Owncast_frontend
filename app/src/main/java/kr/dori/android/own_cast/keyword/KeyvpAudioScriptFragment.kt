package kr.dori.android.own_cast.keyword

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kr.dori.android.own_cast.databinding.FragmentKeyvpAuioscriptBinding


class KeyvpAudioScriptFragment:Fragment() {
    lateinit var binding: FragmentKeyvpAuioscriptBinding
    private var listener: KeywordBtnClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? KeywordBtnClickListener
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeyvpAuioscriptBinding.inflate(inflater, container, false)



        binding.keyAudScrNextIv.setOnClickListener {
            listener?.onButtonClick()
        }


        return binding.root




    }

    //다음 페이지 넘겨주는 listener 해제
    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}