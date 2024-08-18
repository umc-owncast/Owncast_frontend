package kr.dori.android.own_cast.player

import android.app.Application

class PlayerApplication: Application() {
    var backgroundPlayService: BackgroundPlayService? = null
}