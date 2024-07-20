package kr.dori.android.own_cast

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.dori.android.own_cast.databinding.LanguageItemBinding

class PlaylistCastAdapter(): RecyclerView.Adapter<PlaylistCastAdapter.Holder>() {
    var dataList = listOf<Signup>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = LanguageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = dataList[position]
        holder.setText(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(val binding: LanguageItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            // 여기에 cast fragment로 이동하는 로직 구현하기
        }

        fun setText(data: Signup) {
            binding.languageTv.text = data.language
            Glide.with(itemView).load(data.image).into(binding.languageIv)
        }
    }
}