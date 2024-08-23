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
        return Holder(binding)
    }

    fun updateCurrentTime(currentTime: Long) {
        Log.d("ScriptAdapter", "Current Time: $currentTime")
        val newHighlightedPosition = findCurrentPosition(currentTime)
        if (newHighlightedPosition != currentHighlightedPosition) {
            val previousHighlightedPosition = currentHighlightedPosition
            currentHighlightedPosition = newHighlightedPosition
            Log.d("ScriptAdapter", "Highlight Position Changed: $previousHighlightedPosition -> $currentHighlightedPosition")
            notifyItemChanged(previousHighlightedPosition)
            notifyItemChanged(currentHighlightedPosition)
            onHighlightPositionChangeListener?.invoke(newHighlightedPosition)
        }
    }

    // 수정된 하이라이트 로직: timePoint를 시작 지점으로 간주
    private fun findCurrentPosition(currentTime: Long): Int {
        for (i in dataList.indices) {
            val sentenceStartTime = (dataList[i].timePoint * 1000).toLong()
            val nextSentenceStartTime = if (i + 1 < dataList.size) {
                (dataList[i + 1].timePoint * 1000).toLong()
            } else {
                Long.MAX_VALUE // 마지막 문장의 경우 끝 무한대로 설정
            }

            Log.d("ScriptAdapter", "Checking sentence $i: start=$sentenceStartTime, nextStart=$nextSentenceStartTime")

            if (currentTime >= sentenceStartTime && currentTime < nextSentenceStartTime) {
                Log.d("ScriptAdapter", "Current sentence index: $i")
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
            } else {
                binding.translationSentenceTv.setTextColor(Color.parseColor("#808080"))
                binding.originalSentenceTv.setTextColor(Color.parseColor("#808080"))
            }

            // 북마크 설정
            if (bookmarkList.contains(data.id)) {
                binding.onFocusOn.visibility = View.VISIBLE
                binding.notFocusOff.visibility = View.GONE
            } else {
                binding.onFocusOn.visibility = View.GONE
                binding.notFocusOff.visibility = View.VISIBLE
            }

            // 북마크 버튼 클릭 리스너
            binding.onFocusOn.setOnClickListener {
                binding.onFocusOn.visibility = View.GONE
                binding.notFocusOff.visibility = View.VISIBLE
                bookmarkList.remove(data.id)
                CastPlayerData.currentBookmarkList = bookmarkList
                Log.d("Bookmark", "Removed: ${data.id}, Current List: ${bookmarkList}")
            }

            binding.notFocusOff.setOnClickListener {
                binding.onFocusOn.visibility = View.VISIBLE
                binding.notFocusOff.visibility = View.GONE
                bookmarkList.add(data.id)
                CastPlayerData.currentBookmarkList = bookmarkList
                Log.d("Bookmark", "Added: ${data.id}, Current List: ${bookmarkList}")
            }

            // 루프 기능 설정
            if (isRepeatOn) {
                binding.loofOff.visibility = View.GONE
                binding.loofOn.visibility = View.VISIBLE
            } else {
                binding.loofOff.visibility = View.VISIBLE
                binding.loofOn.visibility = View.GONE
            }

            // 루프 버튼 클릭 리스너
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













/*
timePoint가 문장의 끝지점일 경우의 실시간 가사 기능
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
        return Holder(binding)
    }

    fun updateCurrentTime(currentTime: Long) {
        Log.d("ScriptAdapter", "Current Time: $currentTime")
        val newHighlightedPosition = findCurrentPosition(currentTime)
        if (newHighlightedPosition != currentHighlightedPosition) {
            val previousHighlightedPosition = currentHighlightedPosition
            currentHighlightedPosition = newHighlightedPosition
            Log.d("ScriptAdapter", "Highlight Position Changed: $previousHighlightedPosition -> $currentHighlightedPosition")
            // 이전 하이라이트된 위치와 새로운 하이라이트된 위치를 갱신
            notifyItemChanged(previousHighlightedPosition)
            notifyItemChanged(currentHighlightedPosition)
            //하이라이트 위치 업데이트
            onHighlightPositionChangeListener?.invoke(newHighlightedPosition)
        }
    }
    private fun findCurrentPosition(currentTime: Long): Int {
        for (i in dataList.indices) {
            val previousSentenceTime = if (i > 0) {
                (dataList[i - 1].timePoint * 1000).toLong()
            } else {
                0L  // 첫 문장의 경우, 시작을 0으로 설정
            }

            val sentenceTimePoint = (dataList[i].timePoint * 1000).toLong()

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

 */




