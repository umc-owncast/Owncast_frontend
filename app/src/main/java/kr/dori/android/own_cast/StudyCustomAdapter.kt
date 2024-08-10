package kr.dori.android.own_cast

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.dori.android.own_cast.databinding.StudyItemViewBinding
import kotlin.math.abs

class StudyCustomAdapter() :
    RecyclerView.Adapter<StudyCustomAdapter.CenteredItemViewHolder>() {

    var itemList: MutableList<cardData> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CenteredItemViewHolder {
        val binding = StudyItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CenteredItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CenteredItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)

        // 초기 상태: 모든 아이템을 기본 크기로 설정
        holder.itemView.layoutParams.width = 611
        holder.itemView.layoutParams.height = 800

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

                // 거리에 따라 크기 조정, 원래는 distance와 changeLine의 대소관계로 코드를 작성했으나 좌표값이 음수가 나와서 차의 범위로 로직을 수정했다. -> 원래대로 하면 오른쪽 item의 background가 적용이 안 됐었음
                val scale = 1f - (distanceFromCenter.toFloat() / midPoint) * 0.17f
                it.scaleX = scale
                it.scaleY = scale
/*
                val distance = abs(midPoint - viewMid)
                val changeLine = abs(midPoint/1.05f)
                val absoluteValue = abs(distance - changeLine)


                if( absoluteValue <100){
                    it.setBackgroundResource(R.drawable.study_korean_side)
                }else{
                    it.setBackgroundResource(R.drawable.study_korean_center)
                }
                Log.d("test","${distanceFromCenter},${distance},${changeLine},${absoluteValue}")

 */
            }
        }
    }
}




