package kr.dori.android.own_cast

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import java.lang.reflect.Array.getInt

class SignupData : Application() {
    companion object {

        var ok_btn : Int = 0
        var temp_interest : String = ""
        var temp_detail_interest : String = ""
        var temp_language : String = "닉네임"
        var temp_accent : String = "닉네임"

        var profile_detail_interest = ""

        private lateinit var instance: SignupData

        private const val PREF_NAME = "SignupDataPreferences"

        fun getInstance(): SignupData {
            return instance
        }

        private fun getSharedPreferences(): SharedPreferences {
            return instance.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }

        private fun getString(key: String, defaultValue: String): String {
            return getSharedPreferences().getString(key, defaultValue) ?: defaultValue
        }

        private fun setString(key: String, value: String) {
            getSharedPreferences().edit().putString(key, value).apply()
        }

        // 전역 변수들 (SharedPreferences에 저장/불러오기)
        var nickname: String
            get() = getString("nickname", "닉네임")
            set(value) = setString("nickname", value)

        var name: String
            get() = getString("name", "닉네임")
            set(value) = setString("name", value)

        var id: String
            get() = getString("id", "닉네임")
            set(value) = setString("id", value)

        var password: String
            get() = getString("password", "닉네임")
            set(value) = setString("password", value)

        var passwordconfirm: String
            get() = getString("passwordconfirm", "닉네임")
            set(value) = setString("passwordconfirm", value)

        var language: String
            get() = getString("language", "닉네임")
            set(value) = setString("language", value)

        var accent: String
            get() = getString("accent", "닉네임")
            set(value) = setString("accent", value)

        var interest: String
            get() = getString("interest", "닉네임")
            set(value) = setString("interest", value)

        var detail_interest: String
            get() = getString("detail_interest", "닉네임")
            set(value) = setString("detail_interest", value)

        var token: String
            get() = getString("toekn", "닉네임")
            set(value) = setString("toekn", value)

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}