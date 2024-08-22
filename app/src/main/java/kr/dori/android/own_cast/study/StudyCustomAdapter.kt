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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CenteredItemViewHolder {
        val binding = StudyItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CenteredItemViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: CenteredItemViewHolder, position: Int) {
        val actualPosition = position % itemList.size
        val item = itemList[actualPosition]
        holder.bind(item)
        holder.itemView.requestLayout()
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




