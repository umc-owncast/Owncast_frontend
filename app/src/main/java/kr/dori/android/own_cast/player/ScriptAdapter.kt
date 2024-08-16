package kr.dori.android.own_cast.player

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.databinding.ScriptItemBinding

data class Lyrics(val timeInMillis: Long, val text: String)

class ScriptAdapter : RecyclerView.Adapter<ScriptAdapter.Holder>() {
    var dataList: List<Lyrics> = emptyList()
    private var currentTime: Long = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ScriptItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = dataList[position]
        holder.bind(data, currentTime)
    }

    override fun getItemCount(): Int = dataList.size

    fun updateCurrentTime(currentTime: Long) {
        this.currentTime = currentTime
        notifyDataSetChanged()
    }

    inner class Holder(val binding: ScriptItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Lyrics, currentTime: Long) {
            binding.textView14.text = data.text

            if (data.timeInMillis <= currentTime && currentTime < (dataList.getOrNull(adapterPosition + 1)?.timeInMillis ?: Long.MAX_VALUE)) {
                binding.textView14.setTypeface(null, Typeface.BOLD)
                binding.textView14.setTextColor(ContextCompat.getColor(binding.root.context,
                    R.color.black
                ))
            } else {
                binding.textView14.setTypeface(null, Typeface.NORMAL)
                binding.textView14.setTextColor(ContextCompat.getColor(binding.root.context,
                    R.color.gray
                ))
            }
        }
    }
}




