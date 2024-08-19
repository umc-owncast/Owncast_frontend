package kr.dori.android.own_cast.playlist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.ActivityMover
import kr.dori.android.own_cast.FragmentMover
import kr.dori.android.own_cast.data.CastPlayerData
import kr.dori.android.own_cast.data.SongData
import kr.dori.android.own_cast.databinding.PlaylistCategoryItemBinding
import kr.dori.android.own_cast.forApiData.AuthResponse
import kr.dori.android.own_cast.forApiData.GetAllPlaylist
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.forApiData.getRetrofit
import kr.dori.android.own_cast.player.CastWithPlaylistId
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

        init {
            binding.playlistCategoryPlayIv.setOnClickListener {
               val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedPlaylistId = dataList[position].playlistId
                    getCastInfo(selectedPlaylistId)
                }
            }
            binding.realclick.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedPlaylistId = dataList[position].playlistId
                    val selectedPlaylistName = dataList[position].name
                    fragmentMover.playlistToCategory(selectedPlaylistId, selectedPlaylistName)
                }
            }
        }

        fun setText(data: GetAllPlaylist) {
            Log.d("xibal", "${data.playlistId}")

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
        fun getCastInfo(playlistId: Long) {
            val getAllPlaylist = getRetrofit().create(Playlist::class.java)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = getAllPlaylist.getPlaylistInfo(playlistId, 0, 5)
                    if (response.isSuccessful) {
                        val playlistInfo = response.body()?.result
                        withContext(Dispatchers.Main) {
                            playlistInfo?.let {
                                val castList = it.castList.toMutableList()
                                val castListWithPlaylistId = castList.map{
                                    cast ->
                                    CastWithPlaylistId(
                                        castId = cast.castId,
                                        playlistId = playlistId,
                                        castTitle = cast.castTitle,
                                        isPublic = cast.isPublic,
                                        castCreator = cast.castCreator,
                                        castCategory = cast.castCategory,
                                        audioLength = cast.audioLength
                                    )
                                }
                                //CastPlayerData.setCastList(castList)  // 캐스트 리스트를 저장
                                activityMover.ToPlayCast(castListWithPlaylistId)
                            }
                        }
                    } else {
                        Log.e("PlaylistCategoryAdapter", "Failed to fetch playlist info")
                    }
                } catch (e: Exception) {
                    Log.e("PlaylistCategoryAdapter", "Exception during API call", e)
                }
            }
        }

    }
}





