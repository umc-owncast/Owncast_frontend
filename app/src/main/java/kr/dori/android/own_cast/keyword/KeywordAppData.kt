package kr.dori.android.own_cast.keyword

import android.app.Application

class KeywordAppData: Application() {
    companion object{
        //keyword에서 쓸 전역변수 정리
        var mainTopic = ""
        var detailTopic = listOf("프로야구","올림픽","메이저리그","국내야구","야구의 역사","야구 순위")


        var chooseIndex = -1
        var title: String = ""
        var thumbnail :Int? = null
        var user: String? = ""
        var duration: Int? = null
        var exposure:Boolean? = false
        var category:String = ""

        fun clearData(){
            chooseIndex = -1
            title = ""
            thumbnail = null
            user = ""
            duration = null
            exposure = false
            category = ""
        }

    }


}