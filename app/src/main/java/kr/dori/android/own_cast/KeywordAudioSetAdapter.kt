package kr.dori.android.own_cast

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import kr.dori.android.own_cast.databinding.ItemAudiostyleSpinnerBinding

class KeywordAudioSetAdapter(context: Context, @LayoutRes private val resId: Int, private val audioList: List<String>)
    : ArrayAdapter<String>(context, resId, audioList) {
        //@LayoutRes는 레이아웃 리소스인데 드롭다운 메뉴의 xml 파일을 전달
        //audiol
    override fun getView(position: Int, converView: View?, parent: ViewGroup): View {
        val binding = ItemAudiostyleSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.tvSpinner.text = audioList[position]

        return binding.root
    }

    // 드롭다운된 항목들 리스트의 뷰
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemAudiostyleSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.tvSpinner.text = audioList[position]

        return binding.root
    }

    override fun getCount() = audioList.size
}
