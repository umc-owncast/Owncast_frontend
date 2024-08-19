package kr.dori.android.own_cast.forApiData

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//기본 주소 입니당
const val BASE_URL = "http://15.164.140.239:8080"

// 사용자 토큰 입니다,,, -> 로그인 시점마다 변경되도록 로직을 구현해야 합니다.
const val TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJ1c2VySWQiOjE5LCJpYXQiOjE3MjM4MTQ0ODcsImV4cCI6MTcyNjQwNjQ4N30.K1B3wb1Q6bvKtNLyN490zcrmM3wTTHXVNqnweQoGDWk"

fun getRetrofit(): Retrofit {
    val interceptor = Interceptor { chain ->
        val request: Request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $TOKEN")
            .build()
        chain.proceed(request)
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

/*
flow
0. 데이터 클래스 생성 # 사실 .addConverterFactory가 있기 때문에 굳이 @Serializable을 사용할 필요는 없다.
1. 레트로핏 객체 생성 ( NetworkModule.kt )
2. 레트로핏 라이브러리와 안드로이드 사이의 인터페이스 설정 -> 메소드 선언 및 자동 구현 ( AuthRetrofitInterface.kt )
3. 인터페이스를 담은 레트로핏 객체 생성
4. 예외 처리
 */

