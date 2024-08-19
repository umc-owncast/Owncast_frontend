package kr.dori.android.own_cast.search

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.dori.android.own_cast.data.SongData
import kr.dori.android.own_cast.databinding.SearchItemBinding
import kr.dori.android.own_cast.forApiData.CastHomeDTO

class SearchTwoAdapter(private val mover: SearchMover) : RecyclerView.Adapter<SearchTwoAdapter.Holder>() {
    var dataList: List<CastHomeDTO> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = dataList[position]

        holder.setText(data, holder)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(val binding: SearchItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {

        }

        fun setText(data: CastHomeDTO, holder: Holder) {
            val imagePath = data.imagePath
            if (data.imagePath.startsWith("http")) {
                // URL로부터 이미지 로드 (Glide 사용)
                Glide.with(holder.itemView.context)
                    .load(data.imagePath)
                    .into(binding.searchIv)
            } else {
                // 로컬 파일에서 이미지 로드
                val bitmap = BitmapFactory.decodeFile(data.imagePath)
                binding.searchIv.setImageBitmap(bitmap)
            }
            binding.searchTitleTv.text = data.title
            binding.searchCreator.text = "${data.memberName}-${data.playlistName}"
            binding.searchDurationTv.text = formatTime(data.audioLength.toInt())
            binding.searchAddCategoryOffIv.setOnClickListener {

                mover.goAddCast(data.id)
            }
            binding.searchPlayIv.setOnClickListener {
                mover.goPlayCast(dataList, data.id)
            }
        }
    }



        private fun formatTime(totalSeconds: Int): String {
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%d:%02d", minutes, seconds)
        }

    }

}