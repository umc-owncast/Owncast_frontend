package kr.dori.android.own_cast.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.forApiData.Bookmark
import kr.dori.android.own_cast.forApiData.getRetrofit

class BookmarkSyncManager {

    private val bookmarkService = getRetrofit().create(Bookmark::class.java)

    // 북마크 추가
    suspend fun addBookmark(sentenceId: Long) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("Bookmark", "Attempting to add bookmark: $sentenceId")
                val response = bookmarkService.postBookmark(sentenceId)
                if (response.isSuccessful) {
                    Log.d("Bookmark", "Successfully added bookmark: $sentenceId, Bookmark ID: ${response.body()?.result?.bookmarkId}")
                } else {
                    Log.d("Bookmark", "Failed to add bookmark: $sentenceId, Code: ${response.code()}, Message: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("Bookmark", "Error adding bookmark: ${e.localizedMessage}", e)
            }
        }
    }

    // 북마크 삭제
    suspend fun removeBookmark(sentenceId: Long) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("Bookmark", "Attempting to remove bookmark: $sentenceId")
                val response = bookmarkService.deleteBookmark(sentenceId)
                if (response.isSuccessful) {
                    Log.d("Bookmark", "Successfully removed bookmark: $sentenceId")
                } else {
                    Log.d("Bookmark", "Failed to remove bookmark: $sentenceId, Code: ${response.code()}, Message: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("Bookmark", "Error removing bookmark: ${e.localizedMessage}", e)
            }
        }
    }
}
