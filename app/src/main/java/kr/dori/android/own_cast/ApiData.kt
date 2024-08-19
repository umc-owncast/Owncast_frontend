package kr.dori.android.own_cast


// 1. 닉네임 중복 체크
data class CheckNickNameResponse(
    val isSuccess: Boolean,  // 요청 성공 여부
    val code: String,        // 응답 코드
    val message: String,     // 응답 메시지
    val result: Boolean      // 닉네임 중복 여부 (true면 중복 / false면 중복 아님)
)


// 2. 아이디 중복 체크
data class CheckIDResponse (
    val isSuccess: Boolean,  // 요청 성공 여부
    val code: String,        // 응답 코드
    val message: String,     // 응답 메시지
    val result: Boolean      // 아이디 중복 여부 (true면 중복 / false면 중복 아님)
)


// 3. 회원가입

// 회원가입 요청 데이터 클래스
data class SignUpRequest(
    val nickname: String,
    val username: String,
    val loginId: String,
    val password: String,
    val language: String,
    val mainCategory: String,
    val subCategory: String
)

// 회원가입 응답 데이터 클래스
data class SignUpResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: SignUpResult? // result는 nullable로 선언
)

// 회원가입 결과 데이터 클래스
data class SignUpResult(
    val memberId: Int,
    val refreshToken: String
)


// 4. 아이디, 이름, 닉네임 업데이트

// 아이디, 이름, 닉네임 업데이트 요청 데이터 클래스
data class UpdateProfileRequest(
    val loginId: String,
    val username: String,
    val nickname: String
)

// 아이디, 이름, 닉네임 업데이트 응답 데이터 클래스
data class UpdateProfileResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: UpdateProfileResult?
)

// 업데이트 결과 데이터 클래스
data class UpdateProfileResult(
    val memberId: Int
)


// 5. 비밀번호 업데이트

// 비밀번호 업데이트 요청 데이터 클래스
data class UpdatePasswordRequest(
    val password: String
)

// 비밀번호 업데이트 응답 데이터 클래스
data class UpdatePasswordResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: UpdatePasswordResult?
)

data class UpdatePasswordResult(
    val memberId: Int
)


// 6. 로그인

// 로그인 요청 데이터 클래스
data class LoginRequest(
    val loginId: String,
    val password: String
)

// 로그인 응답 데이터 클래스
data class LoginResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: String? // 서버에서 반환된 토큰을 저장
)