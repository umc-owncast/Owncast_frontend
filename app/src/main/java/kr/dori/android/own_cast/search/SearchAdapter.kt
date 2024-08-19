package kr.dori.android.own_cast.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.dori.android.own_cast.data.SongData
import kr.dori.android.own_cast.databinding.ItemSearchFrBinding
import kr.dori.android.own_cast.forApiData.CastHomeDTO

class SearchAdapter(private val mover: SearchMover) : RecyclerView.Adapter<SearchAdapter.Holder>() {
    var dataList: List<CastHomeDTO> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemSearchFrBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = dataList[position]
        holder.setData(data)


    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(val binding: ItemSearchFrBinding):RecyclerView.ViewHolder(binding.root){

        init{




        }

        fun setData(data: CastHomeDTO){
            /*val imagePath = dat
            if (data.imagePath.startsWith("http")) {
                // URL로부터 이미지 로드 (Glide 사용)
                Glide.with(holder.itemView.context)
                    .load(data.imagePath)
                    .into(binding.categoryImg)
            } else {
                // 로컬 파일에서 이미지 로드
                val bitmap = BitmapFactory.decodeFile(data.imagePath)
                binding.searchIv.setImageBitmap(bitmap)
            }*/
            binding.searchfrItemTitleTv.text = data.title
            binding.searchfrItemCategoryTv.text = "${data.memberName}-${data.playlistName}"
            binding.searchfrItemDurationTv.text = formatTime(data.audioLength.toInt())

            binding.searchfrItemAddCategoryOffIv.setOnClickListener {
                mover.goAddCast(data.id)
            }
            binding.itemThumbIv.setOnClickListener {
                mover.goPlayCast(dataList,data.id)
            }
        }
        private fun formatTime(totalSeconds:Int): String {
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%d:%02d", minutes, seconds)
        }

    }
}
