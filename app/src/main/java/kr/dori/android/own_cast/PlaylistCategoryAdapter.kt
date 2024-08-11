package kr.dori.android.own_cast

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.dori.android.own_cast.databinding.PlaylistCategoryItemBinding

class PlaylistCategoryAdapter(private val editListener: EditCategoryListener) : RecyclerView.Adapter<PlaylistCategoryAdapter.Holder>() {
    var dataList = mutableListOf<SongData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = PlaylistCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = dataList[position]
        holder.setText(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(val binding: PlaylistCategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setText(data: SongData) {
            binding.playlistCategoryTitleTv.text = data.title
            binding.playlistCategoryNumTv.text = dataList.size.toString()
            Glide.with(itemView).load(data.Img).into(binding.categoryImg)

            binding.playlistCategoryEditIv.setOnClickListener {
                val position = adapterPosition
                val dialog = EditCategoryDialog(itemView.context, editListener, position)
                dialog.show()
            }
        }
    }
}
