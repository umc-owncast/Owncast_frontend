package kr.dori.android.own_cast

import android.app.Application

class SignupData : Application() {
    companion object {
        var nickname: String = "초기값"
        var id: String = "초기값"
        var password: String = "초기값"
        var name: String = "초기값"

        /*// 전역 변수 접근
        val value = SignupData.nickname
        // 전역 변수 값 수정
        SignupData.nickname = "새값"*/
    }
}