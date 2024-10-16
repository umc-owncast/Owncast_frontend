package kr.dori.android.own_cast.network

import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.forApiData.Bookmark
import kr.dori.android.own_cast.forApiData.getRetrofit

class BookmarkSyncTask(context: Context, private val addedBookmarks: List<Long>, private val removedBookmarks: List<Long>) {

    private val bookmarkService = getRetrofit().create(Bookmark::class.java)
    private val appContext = context.applicationContext

    fun execute() {
        CoroutineScope(Dispatchers.IO).launch {
            syncBookmarks()
        }
    }

    private suspend fun syncBookmarks() {
        withContext(Dispatchers.IO) {
            try {
                // 추가된 북마크 서버에 반영
                addedBookmarks.forEach { sentenceId ->
                    Log.d("BookmarkSyncTask", "Attempting to add bookmark: $sentenceId")
                    val response = bookmarkService.postBookmark(sentenceId)
                    if (response.isSuccessful) {
                        Log.d("BookmarkSyncTask", "Successfully added bookmark: $sentenceId")
                    } else {
                        Log.d("BookmarkSyncTask", "Failed to add bookmark: $sentenceId, Code: ${response.code()}")
                    }
                }

                // 삭제된 북마크 서버에 반영
                removedBookmarks.forEach { sentenceId ->
                    Log.d("BookmarkSyncTask", "Attempting to remove bookmark: $sentenceId")
                    val response = bookmarkService.deleteBookmark(sentenceId)
                    if (response.isSuccessful) {
                        Log.d("BookmarkSyncTask", "Successfully removed bookmark: $sentenceId")
                    } else {
                        Log.d("BookmarkSyncTask", "Failed to remove bookmark: $sentenceId, Code: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("BookmarkSyncTask", "Error syncing bookmarks: ${e.localizedMessage}", e)
            }
        }
    }
}