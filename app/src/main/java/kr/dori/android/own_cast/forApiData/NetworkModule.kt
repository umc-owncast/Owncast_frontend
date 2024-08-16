package kr.dori.android.own_cast.forApiData

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "http://15.164.140.239:8080"

fun getRetrofit(): Retrofit {
    val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()

    return retrofit
}

/*
flow
0. 데이터 클래스 생성 # 사실 .addConverterFactory가 있기 때문에 굳이 @Serializable을 사용할 필요는 없다.
1. 레트로핏 객체 생성 ( NetworkModule.kt )
2. 레트로핏 라이브러리와 안드로이드 사이의 인터페이스 설정 -> 메소드 선언 및 자동 구현 ( AuthRetrofitInterface.kt )
3. 인터페이스를 담은 레트로핏 객체 생성
4. 예외 처리
 */