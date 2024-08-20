package kr.dori.android.own_cast.player

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.dori.android.own_cast.data.CastPlayerData
import kr.dori.android.own_cast.data.SongData
import kr.dori.android.own_cast.databinding.CastplaylistItemBinding
import java.util.Collections

class CastPlaylistAdapter: RecyclerView.Adapter<CastPlaylistAdapter.Holder>() {
    lateinit var itemTouchHelper: ItemTouchHelper
    fun swapItems(fromPosition: Int, toPosition: Int) {
        Collections.swap(CastPlayerData.getAllCastList(), fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = CastplaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = CastPlayerData.getAllCastList()[position]
        holder.setText(data, position)
        holder.setDragHandle(holder)
    }

    override fun getItemCount(): Int {
        return CastPlayerData.getAllCastList().size
    }
    //songData -> title, img, creator, isLock, duration, isSave, category
    //타 사용자의 목록을 넘기는것과(
    //자기 재생목록을 띄우는거 id도 따로 담아야것는디
    inner class Holder(val binding: CastplaylistItemBinding): RecyclerView.ViewHolder(binding.root) {
        //타 사용자 title, img, creator, isLock = false, duration, isSave = false, category이렇게 해서 받아오면 되것군
        //isSave를 통해서 자신과 타사용자를 구분한다? -> 그렇네
        //자기가 저장한걸 따로 구분해서 또 띄우나?
        fun setText(data: CastWithPlaylistId, position : Int) {
            binding.castPlaylistTitle.text = data.castTitle
            binding.castTouchzone.setOnClickListener {
                //클릭시 재생되게
            }
            val currentPos = CastPlayerData.currentPosition
            CastPlayerData.getAllcastImagePath()[currentPos].let{
                Glide.with(binding.root)
                    .load(it)
                    .centerCrop() // ImageView에 맞게 이미지 크기를 조정
                    .into(binding.castPlaylistIv)
            }
            binding.castPlaylistDuration.text = formatTime(data.audioLength.toInt())
            if(data.playlistId != -1L){//-1로 데이터 넣어서 플레이어가 저장 안한걸로 뜨게 할거임
                binding.castPlaylistCreator.visibility = View.GONE
                if(!data.isPublic){
                    binding.castPlaylistLockIv.visibility = View.VISIBLE
                }else{
                    binding.castPlaylistLockIv.visibility = View.GONE
                }
            }else{
                binding.castPlaylistLockIv.visibility = View.GONE
                binding.castPlaylistCreator.text =" ${data.castCreator} - ${data.castCategory}"
            }
        }
        fun setDragHandle(holder:Holder){
            binding.castPlaylistMenuIv.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    itemTouchHelper.startDrag(holder)
                }
                false
            }
        }
    }
    private fun formatTime(totalSeconds:Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}