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

    var onRepeatToggleListener: ((position: Int, isRepeatOn: Boolean) -> Unit)? = null

    private var currentHighlightedPosition: Int = -1
    private var repeatPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ScriptItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    fun updateCurrentTime(currentTime: Long) {
        val newHighlightedPosition = findCurrentPosition(currentTime)
        if (newHighlightedPosition != currentHighlightedPosition) {
            currentHighlightedPosition = newHighlightedPosition
            notifyDataSetChanged()
        }
    }

    private fun findCurrentPosition(currentTime: Long): Int {
        for (i in dataList.indices) {
            val sentence = dataList[i]
            val sentenceTimePoint = (sentence.timePoint * 1000).toLong()
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
                // 하이라이트된 문장에 대한 로직
                binding.translationSentenceTv.setTextColor(Color.parseColor("#000000"))
                binding.originalSentenceTv.setTextColor(Color.parseColor("#000000"))

                if (bookmarkList.contains(data.id)) {
                    binding.onFocusOn.visibility = View.VISIBLE
                    binding.notFocusOff.visibility = View.GONE
                } else {
                    binding.onFocusOn.visibility = View.GONE
                    binding.notFocusOff.visibility = View.VISIBLE
                }

                binding.onFocusOn.setOnClickListener {
                    binding.onFocusOn.visibility = View.GONE
                    binding.notFocusOff.visibility = View.VISIBLE
                    bookmarkList.remove(data.id)
                    CastPlayerData.currentBookmarkList = bookmarkList

                    Log.d("Bookmark","Removed: ${data.id}, Current List: ${bookmarkList}")
                }

                binding.notFocusOff.setOnClickListener {
                    binding.onFocusOn.visibility = View.VISIBLE
                    binding.notFocusOff.visibility = View.GONE
                    bookmarkList.add(data.id)
                    CastPlayerData.currentBookmarkList = bookmarkList

                    Log.d("Bookmark","Added: ${data.id}, Current List: ${bookmarkList}")
                }

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
            } else {
                // 하이라이트되지 않은 문장에 대한 로직
                binding.translationSentenceTv.setTextColor(Color.parseColor("#808080"))
                binding.originalSentenceTv.setTextColor(Color.parseColor("#808080"))

                if (bookmarkList.contains(data.id)) {
                    binding.notFocusOn.visibility = View.VISIBLE
                    binding.notFocusOff.visibility = View.GONE
                } else {
                    binding.notFocusOn.visibility = View.GONE
                    binding.notFocusOff.visibility = View.VISIBLE
                }

                binding.notFocusOn.setOnClickListener {
                    binding.notFocusOn.visibility = View.GONE
                    binding.notFocusOff.visibility = View.VISIBLE
                    bookmarkList.remove(data.id)
                    CastPlayerData.currentBookmarkList = bookmarkList

                    Log.d("Bookmark","Removed: ${data.id}, Current List: ${bookmarkList}")
                }

                binding.notFocusOff.setOnClickListener {
                    binding.notFocusOn.visibility = View.VISIBLE
                    binding.notFocusOff.visibility = View.GONE
                    bookmarkList.add(data.id)
                    CastPlayerData.currentBookmarkList = bookmarkList

                    Log.d("Bookmark","Added: ${data.id}, Current List: ${bookmarkList}")
                }

                binding.loofOff.visibility = View.VISIBLE
                binding.loofOn.visibility = View.GONE
            }
        }
    }
}




