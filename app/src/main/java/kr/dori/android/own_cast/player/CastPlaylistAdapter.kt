package kr.dori.android.own_cast.player

import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import kr.dori.android.own_cast.SignupData
import kr.dori.android.own_cast.data.CastPlayerData


import kr.dori.android.own_cast.databinding.CastplaylistItemBinding
import java.util.Collections


import android.os.Parcel
import android.os.Parcelable

class CastPlaylistAdapter: RecyclerView.Adapter<CastPlaylistAdapter.Holder>() {

    lateinit var itemTouchHelper: ItemTouchHelper
    fun swapItems(fromPosition: Int, toPosition: Int) {
        Collections.swap(CastPlayerData.getAllCastList(), fromPosition, toPosition)
        Log.d("재생목록테스트",CastPlayerData.getAllCastList().toString())
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = CastplaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = CastPlayerData.getAllCastList()[position]
        holder.setText(data, position)
        holder.binding.castPlaylistMenuIv.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                itemTouchHelper.startDrag(holder)
            }
            false
        }
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

            CastPlayerData.getAllcastImagePath()[position].let{
                Glide.with(binding.root)
                    .load(it)
                    .centerCrop() // ImageView에 맞게 이미지 크기를 조정
                    .into(binding.castPlaylistIv)
            }
            binding.castPlaylistDuration.text = data.audioLength
            if(SignupData.nickname.equals(data.castCreator)){//유저의 이름과 제작자의 이름이 같은지
                Log.d("플레이리스트","플레이리스트 : 제목 : ${data.castTitle},  ${data.isPublic}")
                binding.castPlaylistCreator.visibility = View.GONE
                if(data.isPublic){
                    binding.castPlaylistLockIv.visibility = View.GONE
                }else{
                    binding.castPlaylistLockIv.visibility = View.VISIBLE
                }
            }else{
                binding.castPlaylistLockIv.visibility = View.GONE
                binding.castPlaylistCreator.text =" ${data.castCreator} - ${data.castCategory}"
            }
        }
        fun setDragHandle(holder:Holder){

        }
    }
    private fun formatTime(totalSeconds:Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}





/*
서버 배포용
data class SongData(var title: String,var Img: Int, var creator: String, var isLock: Boolean, var duration: Int, var Script: Int, var songURL: Int, var lyric: JsonReader,
 var isSave: Boolean, var category: String )
 */

//제목, 이미지, 생성한 유저, 공개유무, 길이, isSave, 카테고리
data class SongData(var title: String?, var Img: Int, var creator: String?, var isLock: Boolean, var duration: Int, var isSave: Boolean, var category: String?):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeInt(Img)
        parcel.writeString(creator)
        parcel.writeByte(if (isLock) 1 else 0)
        parcel.writeInt(duration)
        parcel.writeByte(if (isSave) 1 else 0)
        parcel.writeString(category)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SongData> {
        override fun createFromParcel(parcel: Parcel): SongData {
            return SongData(parcel)
        }

        override fun newArray(size: Int): Array<SongData?> {
            return arrayOfNulls(size)
        }
    }
}
