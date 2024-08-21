package kr.dori.android.own_cast.study

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.dori.android.own_cast.data.PlaylistInfo

import kr.dori.android.own_cast.databinding.StudyCategoryItemBinding

class StudyAdapter() : RecyclerView.Adapter<StudyAdapter.Holder>() {
    var dataList: MutableList<PlaylistInfo> = mutableListOf()
    var selectedPosition : Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = StudyCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = dataList[position]
        holder.setText(data)

        if (position == selectedPosition) {
            holder.binding.studyCategoryTv.setTextColor(Color.parseColor("#8050F2")) // 보라색
        } else {
            holder.binding.studyCategoryTv.setTextColor(Color.BLACK) // 검은색
        }
        holder.binding.studyCategoryIv.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position

            // 이전에 선택된 항목 갱신
            notifyItemChanged(previousPosition)
            // 현재 선택된 항목 갱신
            notifyItemChanged(selectedPosition)
        }
    }
    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(val binding: StudyCategoryItemBinding):RecyclerView.ViewHolder(binding.root){

        init{
            binding.root.setOnClickListener {
                //데이터 세팅 부분을 구현해보도록 하자
            }
        }
        fun setText(data: PlaylistInfo){
            Glide.with(itemView.context).load(data.imagePath.toInt()).into(binding.studyCategoryIv)
            binding.studyCategoryTv.text = data.playlistName
        }
    }
}
