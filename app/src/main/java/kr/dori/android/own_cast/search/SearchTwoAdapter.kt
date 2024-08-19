package kr.dori.android.own_cast.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.dori.android.own_cast.data.SongData
import kr.dori.android.own_cast.databinding.SearchItemBinding

class SearchTwoAdapter(private val mover: SearchMover) : RecyclerView.Adapter<SearchTwoAdapter.Holder>() {
    var dataList: MutableList<SongData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = dataList[position]
        holder.setText(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(val binding: SearchItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.searchPlayIv.setOnClickListener {
                mover.goPlayCast()
            }
            binding.searchAddCategoryOffIv.setOnClickListener {
                /*binding.searchAddCategoryOffIv.visibility = View.GONE
                binding.searchAddCategoryOnIv.visibility = View.VISIBLE*/
                mover.goAddCast()
            }
        }

        fun setText(data: SongData) {
            binding.searchIv.setImageResource(data.Img)
            binding.searchTitleTv.text = data.title
            binding.searchCreator.text = "${data.creator}-${data.category}"

        }
    }
}