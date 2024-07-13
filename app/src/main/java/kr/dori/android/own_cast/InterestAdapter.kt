package kr.dori.android.own_cast

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.dori.android.own_cast.databinding.InterestItemBinding

class InterestAdapter(): RecyclerView.Adapter<InterestAdapter.Holder>() {
    var dataList = listOf<Signup>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = InterestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = dataList[position]
        holder.setText(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(val binding: InterestItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val context = it.context
                val intent = Intent(context, DetailSetupActivity::class.java)
                context.startActivity(intent)
            }
        }

        fun setText(data: Signup) {
            binding.interestTv.text = data.language
            Glide.with(itemView).load(data.image).into(binding.interestIv)
        }
    }
}
