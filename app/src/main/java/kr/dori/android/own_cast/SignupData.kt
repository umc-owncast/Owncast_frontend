package kr.dori.android.own_cast

import android.app.Application


class SignupData : Application() {
    companion object {

        // signup_first
        var ok_btn_1 : Int = 0
        var ok_btn_2 : Int = 0

        // signup_second
        var nickname: String = "닉네임"

        // signup_third
        var name: String = "닉네임"
        var id: String = "닉네임"
        var password: String = "닉네임"
        var passwordconfirm: String = "닉네임"

        // signup_language
        var language : String = "닉네임"
        var accent : String = "닉네임"

        // signup_fifth
        var interest : String = "닉네임"

        // 여기부터



        /*
        전역 변수 접근
        val value = SignupData.nickname
        전역 변수 값 수정
        SignupData.nickname = "새값"
        */
    }
}