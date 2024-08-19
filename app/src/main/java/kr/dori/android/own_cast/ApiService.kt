package kr.dori.android.own_cast


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiService {

    // 1. 닉네임 중복 체크
    @POST("/api/users/check/member-nickname")
    fun checkNickName(@Query("nickName") nickname : String): Call<CheckNickNameResponse>

    // 2. 아이디 중복 체크
    @POST("/api/users/check/login-id")
    fun checkID(@Query("loginId") loginId : String): Call<CheckIDResponse>

    // 3. 회원가입
    @POST("/api/users/signup")
    fun signUp(@Body signupRequest: SignUpRequest): Call<SignUpResponse>

    // 4. 아이디, 이름, 닉네임 업데이트 (토큰으로 사용자 인식)
    @POST("/api/users/setting")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Body updateProfileRequest: UpdateProfileRequest): Call<UpdateProfileResponse>

    // 5. 비밀번호 업데이트 (토큰으로 사용자 인식)
    @POST("/api/users/setting/password")
    fun updatePassword(
        @Header("Authorization") token: String,
        @Body updatePasswordRequest: UpdatePasswordRequest
    ): Call<UpdatePasswordResponse>

    // 6. 로그인
    @POST("/api/users/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

}