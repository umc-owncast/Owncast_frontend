package kr.dori.android.own_cast.playlist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.dori.android.own_cast.ActivityMover
import kr.dori.android.own_cast.FragmentMover
import kr.dori.android.own_cast.data.SongData
import kr.dori.android.own_cast.databinding.PlaylistCategoryItemBinding
import kr.dori.android.own_cast.forApiData.AuthResponse
import kr.dori.android.own_cast.forApiData.GetAllPlaylist
import retrofit2.Response

class PlaylistCategoryAdapter(private val editListener: EditCategoryListener, private val activityMover: ActivityMover, private val fragmentMover: FragmentMover) : RecyclerView.Adapter<PlaylistCategoryAdapter.Holder>() {

    var dataList: MutableList<GetAllPlaylist> = mutableListOf()
  //  var newDataList = dataList.filter{it.playlistId != 0L}.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = PlaylistCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = dataList[position]
        holder.setText(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(val binding: PlaylistCategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init{
            binding.playlistCategoryPlayIv.setOnClickListener{
                activityMover.ToPlayCast()
            }
            binding.realclick.setOnClickListener{
                fragmentMover.playlistToCategory()
            }
        }

        fun setText(data: GetAllPlaylist) {
            Log.d("xibal","${data.playlistId}")

                data.let {
                    Glide.with(binding.root.context).load(data.imagePath).into(binding.categoryImg)
                    binding.playlistCategoryTitleTv.text = it.name
                    binding.playlistCategoryNumTv.text = it.totalCast.toString()
                }

                binding.playlistCategoryEditIv.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val dialog = EditCategoryDialog(itemView.context, editListener, position.toLong())
                        dialog.show()
                    } else {
                        Log.e("PlaylistCategoryAdapter", "Invalid adapter position: $position")
                    }
                }

        }
    }
}





