package kr.dori.android.own_cast.keyword

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.FragmentKeyvpAuioscriptBinding


class KeyvpAudioScriptFragment:Fragment() {
    lateinit var binding: FragmentKeyvpAuioscriptBinding
    private var listener: KeywordBtnClickListener? = null
    private var speedList = ArrayList<TextView>()

    private var curSpeed:Int = 2

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




        initSpeedUi()
        return binding.root




    }

    //다음 페이지 넘겨주는 listener 해제
    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    //음성 속도 지정하는 tool에 대한 listener 지정
    fun initSpeedUi(){
        binding.keyAudScrCurSpeedTv.setOnClickListener {
            binding.keyAudScrSpeedToolCl.visibility = View.VISIBLE
            binding.keyAudScrCurSpeedTv.setBackgroundResource(R.drawable.ic_keyword_audscr_speedui_abled)
        }
        //x버튼 누르면 꺼지는거
        binding.keywordSpeedBackIv.setOnClickListener {
            binding.keyAudScrSpeedToolCl.visibility = View.GONE
            binding.keyAudScrCurSpeedTv.setBackgroundResource(R.drawable.ic_keyword_audscr_speedui_disabled)

        }

        speedList.add(binding.keywordSpeed05)
        speedList.add(binding.keywordSpeed075)
        speedList.add(binding.keywordSpeed10)
        speedList.add(binding.keywordSpeed125)
        speedList.add(binding.keywordSpeed15)
        speedList.add(binding.keywordSpeed20)
        for(i:Int in 0..5){
            val speed: Float = if (i < 5) {
                0.5f + i * 0.25f
            } else {
                2.0f // 2.0만 있으므로
            }
            speedList[i].setOnClickListener {
                binding.keyAudScrCurSpeedTv.text = "${speed}X"

                //#8050F2는 MainColro

                //이전거 버튼 비활성화
                speedList[curSpeed].setTextColor(Color.parseColor("#00051F"))
                speedList[curSpeed].backgroundTintList = ColorStateList.
                valueOf(ContextCompat.getColor(this.requireContext(), R.color.button_unclick))
                //현재 버튼 활성화
                speedList[i].backgroundTintList = ColorStateList.
                valueOf(ContextCompat.getColor(this.requireContext(), R.color.main_color))
                speedList[i].setTextColor(Color.parseColor("#FFFFFF"))
                //다시 현재 버튼 위치 기억
                curSpeed = i
                binding.keyAudScrSpeedToolCl.visibility = View.GONE


            }
        }

        binding.keyAudScrSpeedToolCl.visibility = View.GONE

    }
}