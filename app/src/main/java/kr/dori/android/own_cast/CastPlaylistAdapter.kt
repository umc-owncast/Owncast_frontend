package kr.dori.android.own_cast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import kr.dori.android.own_cast.databinding.CastItemLayoutBinding
import kr.dori.android.own_cast.databinding.CastplaylistItemBinding

class CastPlaylistAdapter: RecyclerView.Adapter<CastPlaylistAdapter.Holder>() {
    var dataList: MutableList<SongData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastPlaylistAdapter.Holder {
        val binding = CastplaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: CastPlaylistAdapter.Holder, position: Int) {
        val data = dataList[position]
        holder.setText(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(val binding: CastplaylistItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun setText(data: SongData) {
            binding.castPlaylistTitle.text = data.title
            binding.castPlaylistIv.setImageResource(data.Img)
            if(data.isSave){
                binding.castPlaylistCreator.visibility = View.GONE
                if(data.isLock){
                    binding.castPlaylistLockIv.visibility = View.VISIBLE
                }else{
                    binding.castPlaylistLockIv.visibility = View.GONE
                }
            }else{
                binding.castPlaylistLockIv.visibility = View.GONE
                binding.castPlaylistCreator.text = data.creator
            }
        }
    }

}