package kr.dori.android.own_cast

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.dori.android.own_cast.databinding.CastItemLayoutBinding

class CastAdapter : RecyclerView.Adapter<CastAdapter.Holder>() {
    var dataList: MutableList<SongData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = CastItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = dataList[position]
        holder.setText(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(val binding: CastItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun setText(data: SongData) {
            if(data.isSave) {
                binding.castItemTitleTv.text = "저장한 캐스트"
                binding.castItemCreator.visibility = View.GONE
                binding.castItemEditIv.visibility = View.VISIBLE
                if(data.isLock) {
                    binding.castItemLockIv.visibility = View.VISIBLE
                } else {
                    binding.castItemLockIv.visibility = View.GONE
                }
            } else {
                binding.castItemTitleTv.text = "담아온 캐스트"
                binding.castItemCreator.visibility = View.VISIBLE
                binding.castItemCreator.text = "${data.creator}-${data.category}"
                binding.castItemLockIv.visibility=View.GONE
            }
        }
    }
}
