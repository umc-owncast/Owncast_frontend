package kr.dori.android.own_cast.player

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kr.dori.android.own_cast.data.CastPlayerData
import kr.dori.android.own_cast.databinding.FragmentCastPlaylistBinding
import java.util.Collections


class CastPlaylistFragment : Fragment() {

    private lateinit var binding: FragmentCastPlaylistBinding
    private lateinit var castPlaylistAdapter: CastPlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCastPlaylistBinding.inflate(inflater,container,false)

        castPlaylistAdapter = CastPlaylistAdapter()
        binding.fragmentCastPlaylistRv.adapter = castPlaylistAdapter
        binding.fragmentCastPlaylistRv.layoutManager = LinearLayoutManager(context)

        initPlayItem()
        initTouchHeleper()
        return binding.root
    }

    fun initTouchHeleper() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition

                // 변경된 순서를 반영하여 swipeCast 호출
                val updatedList = CastPlayerData.getAllCastList().toMutableList()
                Collections.swap(updatedList, fromPosition, toPosition)

                // swipeCast 메서드로 allCastList를 업데이트
                CastPlayerData.swipeCast(updatedList)
                Log.d("testSwipe", "변경된 리스트: ${updatedList.map{ it.castId }}, 싱글톤 리스트: ${CastPlayerData.getAllCastList().map { it.castId }}, 현재 재생중인 캐스트: ${CastPlayerData.currentCast.castId} 현재 포지션: ${CastPlayerData.currentPosition}")

                castPlaylistAdapter.notifyItemMoved(fromPosition, toPosition)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // 스와이프 동작이 필요 없으므로 비워둡니다.
            }
        }

        // ItemTouchHelper를 RecyclerView에 연결
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.fragmentCastPlaylistRv)
        castPlaylistAdapter.itemTouchHelper = itemTouchHelper
    }


    fun initPlayItem(){
        CastPlayerData.currentCast.imagePath.let{
            Glide.with(binding.root)
                .load(it)
                .centerCrop() // ImageView에 맞게 이미지 크기를 조정
                .into(binding.fragmentPlaylistCastIv)
        }
        binding.fragmentPlaylistCastIv
        binding.fragmentCastPlaylistDuration.text = CastPlayerData.currentCast.audioLength
        binding.fragmentCastPlaylistTitle.text = CastPlayerData.currentCast.castTitle
        binding.fragmentCastPlaylistCreator.text = "${CastPlayerData.currentCast.castCreator} - ${CastPlayerData.currentCast.castCategory}"
    }
}