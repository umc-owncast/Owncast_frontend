package kr.dori.android.own_cast.player

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.data.CastPlayerData
import kr.dori.android.own_cast.data.SongData
import kr.dori.android.own_cast.databinding.FragmentCastPlaylistBinding


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

        initTouchHeleper()

        return binding.root
    }

    fun initTouchHeleper(){
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition

                // 아이템의 순서를 바꿉니다.
                if(fromPosition == CastPlayerData.currentPosition){//만약 현재 재생중인걸 바꿨다면 currentPostion과
                    //currentCast를 바꾸는 작업을 해준다.
                    CastPlayerData.currentPosition = toPosition
                    CastPlayerData.currentCast = CastPlayerData.getAllCastList()[toPosition]
                }
                castPlaylistAdapter.swapItems(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // 스와이프 동작이 필요 없으므로 비워둡니다.
            }
        }

        // ItemTouchHelper를 RecyclerView에 연결
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.fragmentCastPlaylistRv)
    }
}