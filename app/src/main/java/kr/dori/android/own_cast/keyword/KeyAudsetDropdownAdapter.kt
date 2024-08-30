package kr.dori.android.own_cast.keyword

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import kr.dori.android.own_cast.databinding.ItemAudSetSpinnerBinding


class KeyAudsetDropdownAdapter(context: Context, @LayoutRes private val resId: Int, private val audioList: List<String>, private val voiceInterface: VoiceInterface)
    : ArrayAdapter<String>(context, resId, audioList) {
        //@LayoutRes는 레이아웃 리소스인데 드롭다운 메뉴의 xml 파일을 전달
        //audiol
    override fun getView(position: Int, converView: View?, parent: ViewGroup): View {
        val binding = ItemAudSetSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.tvSpinner.text = audioList[position]
            binding.icSpinnerSoundIv.visibility = View.VISIBLE
            binding.icSpinnerSoundIv.setOnClickListener {
                voiceInterface.callVoice()
            }
        return binding.root
    }

    // 드롭다운된 항목들 리스트의 뷰
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemAudSetSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.tvSpinner.text = audioList[position]

        return binding.root
    }

    override fun getCount() = audioList.size
}
