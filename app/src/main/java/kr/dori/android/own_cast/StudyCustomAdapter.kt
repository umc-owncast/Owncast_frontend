package kr.dori.android.own_cast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.dori.android.own_cast.databinding.StudyItemViewBinding

class StudyCustomAdapter(private val itemList: List<cardData>) :
    RecyclerView.Adapter<StudyCustomAdapter.CenteredItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CenteredItemViewHolder {
        val binding = StudyItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CenteredItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CenteredItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)

        // 초기 상태: 모든 아이템을 기본 크기로 설정
        holder.itemView.layoutParams.width = 200
        holder.itemView.layoutParams.height = 300
    }

    override fun getItemCount(): Int = itemList.size

    class CenteredItemViewHolder(private val binding: StudyItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: cardData) {
            binding.textView.text = item.front
        }
    }

    fun adjustItemSize(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val midPoint = recyclerView.width / 2

        for (i in 0 until itemCount) {
            val view = layoutManager.findViewByPosition(i)
            view?.let {
                val viewMid = (it.left + it.right) / 2
                val distanceFromCenter = Math.abs(midPoint - viewMid)

                // 거리에 따라 크기 조정
                val scale = 1f - (distanceFromCenter.toFloat() / midPoint) * 0.3f
                it.scaleX = scale
                it.scaleY = scale
            }
        }
    }
}




