package kr.dori.android.own_cast.player

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.dori.android.own_cast.data.CastPlayerData
import kr.dori.android.own_cast.databinding.ScriptItemBinding
import kr.dori.android.own_cast.forApiData.NewSentences

class ScriptAdapter(val currentCast: CastWithPlaylistId) : RecyclerView.Adapter<ScriptAdapter.Holder>() {
    var dataList: List<NewSentences> = emptyList()
    var bookmarkList: MutableList<Long> = mutableListOf()

    var onHighlightPositionChangeListener: ((position: Int) -> Unit)? = null

    var onRepeatToggleListener: ((position: Int, isRepeatOn: Boolean) -> Unit)? = null

    private var currentHighlightedPosition: Int = -1
    private var repeatPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ScriptItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        Log.d("bookmarkAdapter", "${dataList.map { it.id }}, ${bookmarkList}")
        Log.d("ahahahahah","${dataList}")
        return Holder(binding)
    }

    fun updateCurrentTime(currentTime: Long) {
        Log.d("ScriptAdapter", "Current Time: $currentTime")
        val newHighlightedPosition = findCurrentPosition(currentTime)
        if (newHighlightedPosition != currentHighlightedPosition) {
            val previousHighlightedPosition = currentHighlightedPosition
            currentHighlightedPosition = newHighlightedPosition
            Log.d("ScriptAdapter", "Highlight Position Changed: $previousHighlightedPosition -> $currentHighlightedPosition")
            notifyItemChanged(previousHighlightedPosition) // 이전 하이라이트된 문장 업데이트
            notifyItemChanged(currentHighlightedPosition) // 새로운 하이라이트된 문장 업데이트
            onHighlightPositionChangeListener?.invoke(newHighlightedPosition)
        }
    }

    private fun findCurrentPosition(currentTime: Long): Int {
        for (i: Int in 0 until dataList.size - 1) {
            val previousSentenceTime = if (i >= 0) {
                (dataList[i].timePoint * 1000).toLong()
            } else {
                0L
            }

            val sentenceTimePoint = (dataList[i + 1].timePoint * 1000).toLong()

            Log.d("ScriptAdapter", "Checking sentence $i: previousSentenceTime = $previousSentenceTime, sentenceTimePoint = $sentenceTimePoint")

            if (currentTime >= previousSentenceTime && currentTime < sentenceTimePoint) {
                Log.d("ScriptAdapter", "Current sentence index: $i")
                return i
            }
        }
        return -1
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = dataList[position]
        val isHighlighted = (position == currentHighlightedPosition)
        val isRepeatOn = (position == repeatPosition)

        // 북마크 상태 설정
        if (bookmarkList.contains(data.id)) {
            if (isHighlighted) {
                holder.binding.onFocusOn.visibility = View.VISIBLE
                holder.binding.notFocusOn.visibility = View.GONE
                holder.binding.notFocusOff.visibility = View.GONE
            } else {
                holder.binding.onFocusOn.visibility = View.GONE
                holder.binding.notFocusOn.visibility = View.VISIBLE
                holder.binding.notFocusOff.visibility = View.GONE
            }
        } else {
            holder.binding.onFocusOn.visibility = View.GONE
            holder.binding.notFocusOn.visibility = View.GONE
            holder.binding.notFocusOff.visibility = View.VISIBLE
        }

        holder.binding.onFocusOn.setOnClickListener {
            holder.binding.onFocusOn.visibility = View.GONE
            holder.binding.notFocusOff.visibility = View.VISIBLE
            bookmarkList.remove(data.id)
            CastPlayerData.currentBookmarkList = bookmarkList
            Log.d("Bookmark", "Removed: ${data.id}, Current List: ${bookmarkList}")
        }

        holder.binding.notFocusOn.setOnClickListener {
            holder.binding.notFocusOn.visibility = View.GONE
            holder.binding.notFocusOff.visibility = View.VISIBLE
            bookmarkList.remove(data.id)
            CastPlayerData.currentBookmarkList = bookmarkList
            Log.d("Bookmark", "Removed: ${data.id}, Current List: ${bookmarkList}")
        }

        holder.binding.notFocusOff.setOnClickListener {
            if (isHighlighted) {
                holder.binding.onFocusOn.visibility = View.VISIBLE
                holder.binding.notFocusOff.visibility = View.GONE
            } else {
                holder.binding.notFocusOn.visibility = View.VISIBLE
                holder.binding.notFocusOff.visibility = View.GONE
            }
            bookmarkList.add(data.id)
            CastPlayerData.currentBookmarkList = bookmarkList
            Log.d("Bookmark", "Added: ${data.id}, Current List: ${bookmarkList}")
        }

        // 나머지 바인딩 로직은 bind 함수에서 처리
        holder.bind(data, isHighlighted, isRepeatOn)
    }

    override fun getItemCount(): Int = dataList.size

    inner class Holder(val binding: ScriptItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: NewSentences, isHighlighted: Boolean, isRepeatOn: Boolean) {
            binding.originalSentenceTv.text = data.originalSentence
            binding.translationSentenceTv.text = data.translatedSentence

            // 하이라이트 상태 설정
            if (isHighlighted) {
                binding.translationSentenceTv.setTextColor(Color.parseColor("#000000"))
                binding.originalSentenceTv.setTextColor(Color.parseColor("#000000"))
            } else {
                binding.translationSentenceTv.setTextColor(Color.parseColor("#808080"))
                binding.originalSentenceTv.setTextColor(Color.parseColor("#808080"))
            }

            // 루프 상태 설정
            if (isRepeatOn) {
                binding.loofOff.visibility = View.GONE
                binding.loofOn.visibility = View.VISIBLE
            } else {
                binding.loofOff.visibility = View.VISIBLE
                binding.loofOn.visibility = View.GONE
            }

            binding.loofOff.setOnClickListener {
                binding.loofOff.visibility = View.GONE
                binding.loofOn.visibility = View.VISIBLE
                repeatPosition = adapterPosition
                onRepeatToggleListener?.invoke(adapterPosition, true)
                notifyDataSetChanged()
            }

            binding.loofOn.setOnClickListener {
                binding.loofOff.visibility = View.VISIBLE
                binding.loofOn.visibility = View.GONE
                repeatPosition = null
                onRepeatToggleListener?.invoke(adapterPosition, false)
                notifyDataSetChanged()
            }
        }
    }
}
