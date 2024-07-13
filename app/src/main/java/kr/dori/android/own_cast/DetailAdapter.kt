package kr.dori.android.own_cast

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.dori.android.own_cast.databinding.DetailItemBinding


class DetailAdapter(): RecyclerView.Adapter<DetailAdapter.Holder>() {
    var dataList = listOf<Signup>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = DetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = dataList[position]
        holder.setText(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(val binding: DetailItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val context = it.context
                val intent = Intent(context, ClearSignupActivity::class.java)
                context.startActivity(intent)
            }
        }

        fun setText(data: Signup) {
            binding.detailTv.text = data.language
        }
    }
}