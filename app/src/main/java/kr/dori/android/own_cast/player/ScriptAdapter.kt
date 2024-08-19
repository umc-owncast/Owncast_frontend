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
    var bookmarkList: List<String> = emptyList()

    // 이 콜백을 통해 Activity에 반복 요청을 전달합니다.
    var onRepeatToggleListener: ((position: Int, isRepeatOn: Boolean) -> Unit)? = null

    private var currentHighlightedPosition: Int = -1

    // 반복 재생 상태를 추적하는 변수
    private var repeatPosition: Int? = null

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
        holder.bind(data, position == currentHighlightedPosition, position == repeatPosition)
    }

    override fun getItemCount(): Int = dataList.size

    inner class Holder(val binding: ScriptItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: NewSentences, isHighlighted: Boolean, isRepeatOn: Boolean) {
            binding.originalSentenceTv.text = data.originalSentence
            binding.translationSentenceTv.text = data.translatedSentence

            if (isHighlighted) {
                binding.translationSentenceTv.setTextColor(Color.parseColor("#000000"))
                binding.originalSentenceTv.setTextColor(Color.parseColor("#000000"))

                // 현재 반복 재생이 활성화된 문장인지에 따라 loopOn/loopOff를 설정
                if (isRepeatOn) {
                    binding.loofOff.visibility = View.GONE
                    binding.loofOn.visibility = View.VISIBLE
                } else {
                    binding.loofOff.visibility = View.VISIBLE
                    binding.loofOn.visibility = View.GONE
                }

                binding.loofOff.setOnClickListener {
                    // 루프 온을 표시하고, 현재 문장 반복 요청을 Activity로 전달
                    binding.loofOff.visibility = View.GONE
                    binding.loofOn.visibility = View.VISIBLE
                    repeatPosition = adapterPosition // 현재 반복 재생 문장 위치 저장
                    onRepeatToggleListener?.invoke(adapterPosition, true)
                    notifyDataSetChanged() // 상태 변경 후 전체 UI 업데이트
                }

                binding.loofOn.setOnClickListener {
                    // 루프 오프를 표시하고, 반복 재생 취소 요청을 Activity로 전달
                    binding.loofOff.visibility = View.VISIBLE
                    binding.loofOn.visibility = View.GONE
                    repeatPosition = null // 반복 재생 문장 위치 초기화
                    onRepeatToggleListener?.invoke(adapterPosition, false)
                    notifyDataSetChanged() // 상태 변경 후 전체 UI 업데이트
                }
            } else {
                binding.translationSentenceTv.setTextColor(Color.parseColor("#808080"))
                binding.originalSentenceTv.setTextColor(Color.parseColor("#808080"))

                binding.loofOff.visibility = View.GONE
                binding.loofOn.visibility = View.GONE
            }
        }
    }
}