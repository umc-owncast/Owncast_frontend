package kr.dori.android.own_cast.study

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kr.dori.android.own_cast.data.PlaylistInfo

import kr.dori.android.own_cast.databinding.StudyCategoryItemBinding

class StudyAdapter(private val itemClickListener: (Int) -> Unit) : RecyclerView.Adapter<StudyAdapter.Holder>() {
    var dataList: MutableList<PlaylistInfo> = mutableListOf()
    var selectedPosition : Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = StudyCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = dataList[position]
        holder.setText(data)

        Log.d("StudyAdapter", "Binding position: $position, selectedPosition: $selectedPosition, playlistName: ${data.playlistName}")

        if (position == selectedPosition) {
            holder.binding.studyCategoryTv.setTextColor(Color.parseColor("#8050F2")) // 보라색
        } else {
            holder.binding.studyCategoryTv.setTextColor(Color.BLACK) // 검은색
        }

        holder.binding.studyCategoryIv.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position

            Log.d("StudyAdapter", "Clicked position: $position, selectedPosition: $selectedPosition")

            // 전체 항목 갱신
            notifyDataSetChanged()

            // 이 코드를 사용하여 특정 항목만 갱신하려면:
            // notifyItemChanged(previousPosition)
            // notifyItemChanged(selectedPosition)

            itemClickListener(position)
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
            // 이미지 URL을 Glide로 로드
            val requestOptions = RequestOptions()
                .transform(CenterCrop(), RoundedCorners(30))

            Glide.with(itemView.context)
                .load(data.imagePath)
                .apply(requestOptions)
                .into(binding.studyCategoryIv)

            binding.studyCategoryTv.text = data.playlistName
        }
    }
}
