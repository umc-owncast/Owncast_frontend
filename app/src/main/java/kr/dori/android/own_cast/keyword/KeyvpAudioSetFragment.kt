package kr.dori.android.own_cast.keyword

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.FragmentKeyvpAudiosetBinding

class KeyvpAudioSetFragment: Fragment() {
    lateinit var binding: FragmentKeyvpAudiosetBinding
    //뷰페이저에서 다음 프래그먼트로  넘기는 인터페이스 변수
    private var listener: KeywordBtnClickListener? = null

    //뷰페이저를 갖고 있는 부모 프래그먼트를 참고해서, 다음 페이지로 넘긴다.
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? KeywordBtnClickListener

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentKeyvpAudiosetBinding.inflate(inflater, container, false)

        val searchText = arguments?.getString("searchText")

        if(searchText!=null){
            binding.keywordAudEt.visibility = View.VISIBLE
            binding.keywordAudEt.text = searchText
            binding.keywordAudEt.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        }else{
            binding.keywordAudEt.visibility = View.GONE
        }

        val list = listOf("남성1","남성2","여성1","여성2","여성3")

        //참고 https://develop-oj.tistory.com/27#google_vignette 드롭다운 메뉴
        binding.keywordAudStyleSp.adapter =
            KeyAudsetDropdownAdapter(requireContext(), R.layout.item_aud_set_spinner,list)
        binding.keywordAudStyleSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val value = binding.keywordAudStyleSp.getItemAtPosition(p2).toString()
                Toast.makeText(requireContext(), value, Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                // 선택되지 않은 경우
            }
        }

        binding.keywordAudiosetSb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress < 2) {
                    seekBar?.progress = 2
                }
                else if(progress%2==0){
                    binding.keyAudSetCurTimeTv.text = "${progress/2}분"
                }else{
                    binding.keyAudSetCurTimeTv.text = "${progress/2}분30초"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        //다음 프래그먼트로 넘어감
        binding.keywordAudiosetMakebtnTb.setOnClickListener{
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