package kr.dori.android.own_cast

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.dori.android.own_cast.databinding.CastItemLayoutBinding
class CastAdapter(private val activityMover: ActivityMover) : RecyclerView.Adapter<CastAdapter.Holder>() {
    var dataList: MutableList<SongData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = CastItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = dataList[position]
        holder.setText(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(val binding: CastItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {


        init{
            binding.goPlaycastConstraint.setOnClickListener {
                activityMover.ToPlayCast()
            }
        }
        fun setText(data: SongData) {

            val constraintLayout = binding.root as ConstraintLayout
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)

            if(data.isSave) {

                binding.castItemTitleTv.text = data.title
                binding.castItemCreator.visibility = View.GONE
                binding.castItemEditIv.visibility = View.VISIBLE
                binding.castItemEditIv.setOnClickListener {
                    activityMover.ToEditAudio()
                }
                if(data.isLock) {
                    binding.castItemLockIv.visibility = View.VISIBLE
                } else {
                    binding.castItemLockIv.visibility = View.GONE
                    binding.castItemTitleTv.apply{

                    }
                }
            } else {
                binding.castItemEditIv.visibility = View.GONE
                binding.castItemTitleTv.text = data.title
                binding.castItemCreator.visibility = View.VISIBLE
                binding.castItemCreator.text = "${data.creator}-${data.category}"
                binding.castItemLockIv.visibility=View.GONE
            }
            if (binding.castItemCreator.visibility == View.GONE && binding.castItemLockIv.visibility == View.GONE) {
                // 중앙에 배치
                constraintSet.connect(binding.castItemTitleTv.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
                constraintSet.connect(binding.castItemTitleTv.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
                constraintSet.setVerticalBias(binding.castItemTitleTv.id, 0.5f)
            } else {
                // 원래 위치
                constraintSet.connect(binding.castItemTitleTv.id, ConstraintSet.START, binding.playlistCast2Iv.id, ConstraintSet.END, 16)
                constraintSet.connect(binding.castItemTitleTv.id, ConstraintSet.END, binding.castItemEditIv.id, ConstraintSet.START, 16)
                constraintSet.setHorizontalBias(binding.castItemTitleTv.id, 0.0f)
            }
        }
    }
}
