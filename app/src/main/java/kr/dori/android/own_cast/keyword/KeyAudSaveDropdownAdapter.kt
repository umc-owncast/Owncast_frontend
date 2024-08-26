package kr.dori.android.own_cast.keyword

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import kr.dori.android.own_cast.R

import kr.dori.android.own_cast.databinding.ItemAudSetSpinnerBinding

class KeyAudSaveDropdownAdapter(context: Context, @LayoutRes private val resId: Int, private val categoryList:MutableList<String>)
    : ArrayAdapter<String>(context, resId, categoryList) {
    //var dataList: MutableList<PlaylistText> = mutableListOf()
    override fun getView(position: Int, converView: View?, parent: ViewGroup): View {
        val binding = ItemAudSetSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            binding.tvSpinner.text = categoryList[position]
        return binding.root
    }

    // 드롭다운된 항목들 리스트의 뷰
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemAudSetSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.tvSpinner.text = categoryList[position]
        if(position == categoryList.size-1){
            binding.icSpinnerSoundIv.setImageResource(R.drawable.playlistfr_add_iv)
            binding.icSpinnerSoundIv.visibility = View.VISIBLE
        }
        return binding.root
    }



    override fun getCount() = categoryList.size
}