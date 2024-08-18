import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.ScriptItemBinding
import kr.dori.android.own_cast.forApiData.NewSentences

class ScriptAdapter : RecyclerView.Adapter<ScriptAdapter.Holder>() {
    var dataList: List<NewSentences> = emptyList()
    private var currentHighlightedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ScriptItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    fun updateCurrentTime(currentTime: Long) {
        // 현재 시간을 기반으로 가사를 업데이트합니다.
        val newHighlightedPosition = findCurrentPosition(currentTime)

        Log.d("UpdateTime", "Adapter: $currentTime")
        if (newHighlightedPosition != currentHighlightedPosition) {
            currentHighlightedPosition = newHighlightedPosition
            notifyDataSetChanged()
        }
    }

    private fun findCurrentPosition(currentTime: Long): Int {
        for (i in dataList.indices) {
            val sentence = dataList[i]
            val sentenceTimePoint = (sentence.timePoint * 1000).toLong() // Double을 Long으로 변환
            val nextSentenceTime = if (i + 1 < dataList.size) {
                (dataList[i + 1].timePoint * 1000).toLong()
            } else {
                Long.MAX_VALUE
            }

            if (currentTime >= sentenceTimePoint && currentTime < nextSentenceTime) {
                return i
            }
        }
        return -1
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = dataList[position]
        holder.bind(data, position == currentHighlightedPosition)
    }

    override fun getItemCount(): Int = dataList.size

    inner class Holder(val binding: ScriptItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: NewSentences, isHighlighted: Boolean) {
            binding.originalSentenceTv.text = data.originalSentence
            binding.translationSentenceTv.text = data.translatedSentence

            if (isHighlighted) {
                //binding.originalSentenceTv.setTypeface(null, Typeface.BOLD)
                //binding.translationSentenceTv.setTypeface(null, Typeface.BOLD)
                //binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.black))
                binding.translationSentenceTv.setTextColor(Color.parseColor("#000000"))
                binding.originalSentenceTv.setTextColor(Color.parseColor("#000000"))
                /*
                binding.onFocusOn.setOnClickListener {
                    binding.onFocusOn.visibility = View.GONE
                    binding.notFocusOff.visibility = View.VISIBLE
                }
                binding.notFocusOff.setOnClickListener {
                    binding.onFocusOn.visibility = View.VISIBLE
                    binding.notFocusOff.visibility = View.GONE
                }

                 */

            } else {
                //binding.originalSentenceTv.setTypeface(null, Typeface.NORMAL)
                //binding.translationSentenceTv.setTypeface(null, Typeface.NORMAL)
                //binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, android.R.color.darker_gray))
                binding.translationSentenceTv.setTextColor(Color.parseColor("#808080"))
                binding.originalSentenceTv.setTextColor(Color.parseColor("#808080"))
                /*
                binding.notFocusOn.setOnClickListener {
                    binding.notFocusOn.visibility = View.GONE
                    binding.notFocusOff.visibility = View.VISIBLE
                }
                binding.notFocusOff.setOnClickListener {
                    binding.notFocusOn.visibility = View.VISIBLE
                    binding.notFocusOff.visibility = View.GONE
                }

                 */
            }
        }
    }
}
