package kr.dori.android.own_cast.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.dori.android.own_cast.databinding.CastplaylistItemBinding


import android.os.Parcel
import android.os.Parcelable

class CastPlaylistAdapter: RecyclerView.Adapter<CastPlaylistAdapter.Holder>() {
    var dataList: MutableList<SongData> = mutableListOf()
    var id:MutableList<Long> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = CastplaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = dataList[position]
        holder.setText(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    //songData -> title, img, creator, isLock, duration, isSave, category
    //타 사용자의 목록을 넘기는것과(
    //자기 재생목록을 띄우는거 id도 따로 담아야것는디
    inner class Holder(val binding: CastplaylistItemBinding): RecyclerView.ViewHolder(binding.root) {
        //타 사용자 title, img, creator, isLock = false, duration, isSave = false, category이렇게 해서 받아오면 되것군
        //isSave를 통해서 자신과 타사용자를 구분한다? -> 그렇네
        //자기가 저장한걸 따로 구분해서 또 띄우나?
        fun setText(data: SongData) {
            binding.castPlaylistTitle.text = data.title
            binding.castPlaylistIv.setImageResource(data.Img)
            binding.castPlaylistDuration.text = formatTime(data.duration)
            if(data.isSave){
                binding.castPlaylistCreator.visibility = View.GONE
                if(data.isLock){
                    binding.castPlaylistLockIv.visibility = View.VISIBLE
                }else{
                    binding.castPlaylistLockIv.visibility = View.GONE
                }
            }else{
                binding.castPlaylistLockIv.visibility = View.GONE
                binding.castPlaylistCreator.text = data.creator
            }
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
