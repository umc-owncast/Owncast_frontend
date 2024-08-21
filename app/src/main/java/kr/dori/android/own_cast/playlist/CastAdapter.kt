package kr.dori.android.own_cast.playlist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import kr.dori.android.own_cast.ActivityMover
import kr.dori.android.own_cast.databinding.CastItemLayoutBinding
import kr.dori.android.own_cast.forApiData.Cast
import kr.dori.android.own_cast.player.CastWithPlaylistId

class CastAdapter(private val activityMover: ActivityMover) : RecyclerView.Adapter<CastAdapter.Holder>() {
    var dataList: MutableList<CastWithPlaylistId> = mutableListOf()

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


        init {
            binding.goPlaycastConstraint.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val cast = dataList[position]
                    Log.d("test3","${listOf(cast)}")
                    activityMover.ToPlayCast(listOf(cast))
                }
            }
        }

        fun setText(data: CastWithPlaylistId) {

            val constraintLayout = binding.root as ConstraintLayout
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)
            binding.castItemTitleTv.text = data.castTitle
            binding.timeTableTv.text = formatTime(data.audioLength)

            if(data.castCreator == "헬로") {
                binding.castItemCreator.visibility = View.GONE
                binding.castItemEditIv.visibility = View.VISIBLE
                binding.castItemEditIv.setOnClickListener {
                    activityMover.ToEditAudio(data.castId,data.playlistId)
                }
                if(data.isPublic) {
                    binding.castItemLockIv.visibility = View.GONE
                } else {
                    binding.castItemLockIv.visibility = View.VISIBLE
                    binding.castItemTitleTv.apply{

                    }
                }
            } else {
                binding.castItemEditIv.visibility = View.GONE
                binding.castItemCreator.visibility = View.VISIBLE
                binding.castItemCreator.text = "${data.castCreator}-${data.castCreator}"
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
    fun formatTime(input: String): String {
        return if (input.contains(":")) {
            // 입력이 이미 "분:초" 형식인 경우
            input
        } else {
            // 입력이 초 단위로 들어오는 경우
            val totalSeconds = input.toIntOrNull() ?: return "00:00" // 입력이 숫자가 아닌 경우 대비
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}
