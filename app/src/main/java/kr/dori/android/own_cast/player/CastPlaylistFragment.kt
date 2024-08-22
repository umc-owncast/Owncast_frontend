package kr.dori.android.own_cast.player

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.C
import kr.dori.android.own_cast.R

import kr.dori.android.own_cast.data.CastPlayerData


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

        initPlayItem()
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
                val cast = CastPlayerData.getAllCastList().removeAt(fromPosition)
                // 아이템의 순서를 바꿉니다.
                if(fromPosition > toPosition){//만약 현재 재생중인걸 바꿨다면 currentPostion과
                    //currentCast를 바꾸는 작업을 해준다.
                    CastPlayerData.getAllCastList().add(toPosition, cast)
                    if(toPosition == CastPlayerData.currentPosition){
                        CastPlayerData.currentPosition = fromPosition
                        CastPlayerData.currentCast = CastPlayerData.getAllCastList()[fromPosition]
                    }else if(fromPosition == CastPlayerData.currentPosition){
                        CastPlayerData.currentPosition = toPosition
                        CastPlayerData.currentCast = CastPlayerData.getAllCastList()[toPosition]
                    }else if((fromPosition>CastPlayerData.currentPosition)&&(toPosition<CastPlayerData.currentPosition)){
                        CastPlayerData.currentPosition += 1
                        CastPlayerData.currentCast = CastPlayerData.getAllCastList()[CastPlayerData.currentPosition]
                    }
                }else{
                    CastPlayerData.getAllCastList().add(toPosition-1, cast)
                    if(toPosition == CastPlayerData.currentPosition){
                        CastPlayerData.currentPosition = fromPosition
                        CastPlayerData.currentCast = CastPlayerData.getAllCastList()[fromPosition]
                    }else if(fromPosition == CastPlayerData.currentPosition){
                        CastPlayerData.currentPosition = toPosition
                        CastPlayerData.currentCast = CastPlayerData.getAllCastList()[toPosition]
                    }else if((fromPosition<CastPlayerData.currentPosition)&&(toPosition>CastPlayerData.currentPosition)){
                        CastPlayerData.currentPosition -= 1
                        CastPlayerData.currentCast = CastPlayerData.getAllCastList()[CastPlayerData.currentPosition]
                    }
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