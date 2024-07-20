package kr.dori.android.own_cast

import android.os.Parcel
import android.os.Parcelable

/*
서버 배포용
data class SongData(var title: String,var Img: Int, var creator: String, var isLock: Boolean, var duration: Int, var Script: Int, var songURL: Int, var lyric: JsonReader,
 var isSave: Boolean, var category: String )
 */

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