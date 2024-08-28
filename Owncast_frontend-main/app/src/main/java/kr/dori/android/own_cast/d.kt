package kr.dori.android.own_cast

data class d(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<Result>
) {
    data class Result(
        val name: String,
        val imagePath: String,
        val playlistId: Int,
        val totalCast: Int
    )
}