package kr.dori.android.own_cast.keyword

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.dori.android.own_cast.SearchAdapter
import kr.dori.android.own_cast.SongData
import kr.dori.android.own_cast.databinding.ItemKeyaudscrScriptBinding
import kr.dori.android.own_cast.databinding.ItemSearchFrBinding
import kr.dori.android.own_cast.forApiData.Sentences

class KeyvpAudioScriptRVAdapter(var dataList: List<Sentences>): RecyclerView.Adapter<KeyvpAudioScriptRVAdapter.Holder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyvpAudioScriptRVAdapter.Holder {
        val binding = ItemKeyaudscrScriptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: KeyvpAudioScriptRVAdapter.Holder, position: Int) {
        val data = dataList[position]
        holder.setData(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(val binding: ItemKeyaudscrScriptBinding):RecyclerView.ViewHolder(binding.root){
        init{
        }

        fun setData(data: Sentences){
            binding.keyAudscrOriginTv.text = data.originalSentence
            binding.keyAudscrTransTv.text = data.translatedSentence
        }

    }
}