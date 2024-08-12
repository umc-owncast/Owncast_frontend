package kr.dori.android.own_cast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.dori.android.own_cast.databinding.CastItemLayoutBinding
import kr.dori.android.own_cast.databinding.PlaylistCategoryItemBinding
import kr.dori.android.own_cast.databinding.SearchItemBinding

class AddCategoryAdapter(private val mover: SearchMover) : RecyclerView.Adapter<AddCategoryAdapter.Holder>() {
    var dataList: MutableList<SongData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddCategoryAdapter.Holder {
        val binding = PlaylistCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: AddCategoryAdapter.Holder, position: Int) {
        val data = dataList[position]
        holder.setText(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(val binding: PlaylistCategoryItemBinding):RecyclerView.ViewHolder(binding.root){

        init{
            binding.root.setOnClickListener {

                mover.backSearch()
            }
        }

        fun setText(data:SongData){
            binding.playlistCategoryEditIv.visibility = View.GONE
            binding.playlistCategoryPlayIv.visibility = View.GONE
            binding.playlistCategoryTitleTv.text = data.title
            binding.playlistCategoryNumTv.text = dataList.size.toString()
            binding.categoryImg.setImageResource(data.Img)
        }
    }
}
