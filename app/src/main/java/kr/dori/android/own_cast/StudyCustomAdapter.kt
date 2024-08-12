package kr.dori.android.own_cast

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kr.dori.android.own_cast.databinding.StudyItemViewBinding
import kotlin.math.abs

class StudyCustomAdapter() :
    RecyclerView.Adapter<StudyCustomAdapter.CenteredItemViewHolder>() {

    var itemList: MutableList<cardData> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CenteredItemViewHolder {
        val binding = StudyItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CenteredItemViewHolder(binding,parent.context)
    }

    override fun onBindViewHolder(holder: CenteredItemViewHolder, position: Int) {
        val actualPosition = position % itemList.size
        val item = itemList[actualPosition]
        holder.bind(item)

        // 초기 상태: 모든 아이템을 기본 크기로 설정
        //holder.itemView.layoutParams.width = 5000
        //holder.itemView.layoutParams.height = 2000

        // 레이아웃 재계산
        holder.itemView.requestLayout()

    }

    override fun getItemCount(): Int {
        return 1000
    }

    class CenteredItemViewHolder(private val binding: StudyItemViewBinding, context: Context) : RecyclerView.ViewHolder(binding.root) {
        private var isFront = true
        private val frontAnim: AnimatorSet = AnimatorInflater.loadAnimator(context, R.animator.front_animator) as AnimatorSet
        private val backAnim: AnimatorSet = AnimatorInflater.loadAnimator(context, R.animator.back_animator) as AnimatorSet

        init {
            val scale = context.resources.displayMetrics.density
            binding.cardFront.cameraDistance = 8000 * scale
            binding.cardBack.cameraDistance = 8000 * scale

            binding.cardFront.setOnClickListener {
                binding.cardFront.isClickable = false // 클릭 잠금

                binding.cardFront.postDelayed({
                    binding.cardFront.isClickable = true // 1초 후 클릭 가능하게 만듦
                }, 1000)

                if (isFront) {
                    frontAnim.setTarget(binding.cardFront)
                    backAnim.setTarget(binding.cardBack)
                    frontAnim.start()
                    backAnim.start()
                } else {
                    frontAnim.setTarget(binding.cardBack)
                    backAnim.setTarget(binding.cardFront)
                    frontAnim.start()
                    backAnim.start()
                }
                isFront = !isFront
            }
        }

        fun bind(item: cardData) {
            binding.cardFront.text = item.front
            binding.cardBack.text = item.behind
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


                //중앙에 위치한 아이템의 포지션 값 출력하기
                val snapHelper = LinearSnapHelper()
                val centerView = snapHelper.findSnapView(layoutManager)
                val position = layoutManager.getPosition(centerView!!)

                Log.d("Center Position in adjustItemSize"," $position")
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




