package kr.dori.android.own_cast.study

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.StudyItemViewBinding
import kr.dori.android.own_cast.forApiData.GetBookmark

class StudyCustomAdapter :
    RecyclerView.Adapter<StudyCustomAdapter.CenteredItemViewHolder>() {

    var itemList: MutableList<GetBookmark> = mutableListOf()
    var selectedPosition: Int = -1  // 현재 선택된 포지션을 저장하는 변수 추가


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CenteredItemViewHolder {
        val binding = StudyItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CenteredItemViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: CenteredItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
        holder.itemView.requestLayout()

        // 모든 카드에 대해 마진 초기화
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.marginStart = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.default_margin)
        layoutParams.marginEnd = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.default_margin)

        // 첫 번째 카드인 경우 추가적인 마진을 설정
        if (position == 0) {
            layoutParams.marginStart = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.side_card_margin)
        }

        // 마지막 카드인 경우 추가적인 마진 설정
        if (position == itemList.size - 1) {
            layoutParams.marginEnd = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.side_card_margin)
        }

        // 초기화된 레이아웃 매개변수를 다시 설정
        holder.itemView.layoutParams = layoutParams

        // 클릭 리스너 추가
        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()  // UI 업데이트
        }
    }

    // 실제 itemList의 크기를 반환하여 무한 스크롤을 방지함
    override fun getItemCount(): Int {
        return itemList.size
    }

    class CenteredItemViewHolder(private val binding: StudyItemViewBinding, context: Context) : RecyclerView.ViewHolder(binding.root) {
        private var isFront = true
        private val frontAnim: AnimatorSet = AnimatorInflater.loadAnimator(context,
            R.animator.front_animator
        ) as AnimatorSet
        private val backAnim: AnimatorSet = AnimatorInflater.loadAnimator(context,
            R.animator.back_animator
        ) as AnimatorSet

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

        fun bind(item: GetBookmark) {
            binding.cardFront.text = item.originalSentence
            binding.cardBack.text = item.translatedSentence
        }
    }

    fun adjustItemSize(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val midPoint = recyclerView.width / 2

        for (i in 0 until itemList.size) { // itemCount가 아닌 itemList.size를 사용
            val view = layoutManager.findViewByPosition(i)
            view?.let {
                val viewMid = (it.left + it.right) / 2
                val distanceFromCenter = Math.abs(midPoint - viewMid)

                val scale = 1f - (distanceFromCenter.toFloat() / midPoint) * 0.17f
                it.scaleX = scale
                it.scaleY = scale
            }
        }

    }



}




