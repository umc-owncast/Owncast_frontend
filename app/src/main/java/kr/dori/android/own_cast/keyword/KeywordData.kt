package kr.dori.android.own_cast.keyword

import android.os.Parcel
import android.os.Parcelable

data class KeywordData(var mainKeyword : String? = null,
    var keywordList: Array<String>? = null
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createStringArray()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mainKeyword)
        parcel.writeStringArray(keywordList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<KeywordData> {
        override fun createFromParcel(parcel: Parcel): KeywordData {
            return KeywordData(parcel)
        }

        override fun newArray(size: Int): Array<KeywordData?> {
            return arrayOfNulls(size)
        }
    }

}


