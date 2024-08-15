package kr.dori.android.own_cast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.dori.android.own_cast.databinding.CastItemLayoutBinding
import kr.dori.android.own_cast.databinding.ItemSearchFrBinding
import kr.dori.android.own_cast.databinding.SearchItemBinding

class SearchAdapter(private val mover: SearchMover) : RecyclerView.Adapter<SearchAdapter.Holder>() {
    var dataList: MutableList<SongData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.Holder {
        val binding = ItemSearchFrBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: SearchAdapter.Holder, position: Int) {
        val data = dataList[position]
        holder.setData(data)


    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(val binding: ItemSearchFrBinding):RecyclerView.ViewHolder(binding.root){

        init{
            binding.itemThumbIv.setOnClickListener {
                mover.goPlayCast()
            }
            binding.searchfrItemAddCategoryOffIv.setOnClickListener {
                /*binding.searchfrItemAddCategoryOffIv.visibility = View.GONE
                binding.searchfrItemAddCategoryOnIv.visibility = View.VISIBLE*/
                mover.goAddCast()
            }


        }

        fun setData(data:SongData){
            binding.itemThumbIv.setImageResource(data.Img)
            binding.searchfrItemTitleTv.text = data.title
            binding.searchfrItemCategoryTv.text = "${data.creator}-${data.category}"
            binding.searchfrItemDurationTv.text = "${data.duration/60}:${String.format("%02d", data.duration % 60)}"
        }

    }
}
