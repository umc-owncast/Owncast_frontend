package kr.dori.android.own_cast.player

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.databinding.FragmentCastScriptBinding
import kr.dori.android.own_cast.forApiData.CastInfo
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.forApiData.getRetrofit
import kotlinx.coroutines.async
import kr.dori.android.own_cast.data.CastPlayerData
import kr.dori.android.own_cast.forApiData.Bookmark
import kr.dori.android.own_cast.forApiData.GetBookmark

class CastScriptFragment(val currentCast: CastWithPlaylistId) : Fragment() {
    lateinit var binding: FragmentCastScriptBinding
    private val handler = Handler()
    val adapter = ScriptAdapter(currentCast)
    lateinit var castInfo: CastInfo
    lateinit var allBookmark: List<GetBookmark>
    lateinit var filteredBookmark: List<Long>
    lateinit var previousBookmark: List<Long>
    private val bookmarks: MutableList<Long> = mutableListOf() // 내부 변수로 북마크 관리

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCastScriptBinding.inflate(inflater, container, false)
        Log.d("Bookmark", "onCreateView")

        val getScript = getRetrofit().create(Playlist::class.java)
        val getBookmark = getRetrofit().create(Bookmark::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val castInfoDeferred = async {
                val response = getScript.getCast(currentCast.castId)
                if (response.isSuccessful) {
                    response.body()?.result
                } else {
                    null
                }
            }
            val bookmarkInfoDeferred = async {
                val response = getBookmark.getBookmark(currentCast.playlistId)
                if(response.isSuccessful){
                    response.body()?.result
                } else {
                    null
                }
            }

            castInfo = castInfoDeferred.await() ?: CastInfo(0, "", "", "", "", emptyList())
            allBookmark = bookmarkInfoDeferred.await() ?: emptyList()


            filteredBookmark = allBookmark
                .filter { it.castId == currentCast.castId }
                .map { it.sentenceId }
            val testBookmark = allBookmark.map { it.sentenceId }
            val testCastSentence = castInfo.sentences.map { it.id }
            previousBookmark = filteredBookmark

            // 내부 변수에 북마크 추가
            bookmarks.addAll(filteredBookmark)

            withContext(Dispatchers.Main) {
                Log.d("script", "${castInfo.sentences}")
                Log.d("script", "playlistId: ${currentCast.playlistId}, castId: ${currentCast.castId}")

                adapter.dataList = castInfo.sentences
                adapter.bookmarkList = bookmarks
                binding.scriptRv.adapter = adapter
                binding.scriptRv.layoutManager = LinearLayoutManager(context)

                // 하이라이트 위치 변경 시 콜백 설정
                adapter.onHighlightPositionChangeListener = { position ->
                    scrollToPosition(position)
                }



                Log.d("bookmarkTest", "현재 캐스트의 북마크 아이디들: $filteredBookmark //////// 플레이리스트의 북마크 아이디들: $testBookmark, ///////, 캐스트의 센텐스 아이디들: $testCastSentence,")
            }
        }
        return binding.root
    }

    fun updateCurrentTime(currentTime: Long) {
        Log.d("UpdateTime", "Fragment: $currentTime")
        adapter.updateCurrentTime(currentTime)
    }

    private fun scrollToPosition(position: Int) {
        val layoutManager = binding.scriptRv.layoutManager as LinearLayoutManager
        layoutManager.scrollToPositionWithOffset(position, binding.scriptRv.height / 2)
    }

    override fun onStop() {
        super.onStop()

        val currentBookmarks = CastPlayerData.currentBookmarkList
        Log.d("Bookmark","${currentBookmarks}")

        // 추가된 북마크: previousBookmark에는 없고 currentBookmarks에 있는 것들
        val addedBookmarks = currentBookmarks.filter { it !in previousBookmark }

        // 삭제된 북마크: currentBookmarks에는 없고 previousBookmark에 있는 것들
        val removedBookmarks = previousBookmark.filter { it !in currentBookmarks }
        val bookmarkService = getRetrofit().create(Bookmark::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d("Bookmark", "Starting network requests")

                // POST 요청을 통해 추가된 북마크 서버에 반영
                addedBookmarks.forEach { sentenceId ->
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

                // DELETE 요청을 통해 삭제된 북마크 서버에 반영
                removedBookmarks.forEach { sentenceId ->
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
            } catch (e: Exception) {
                Log.d("Bookmark", "Error during bookmark sync: ${e.localizedMessage}", e)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Bookmark", "onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Bookmark", "onViewCreated")
    }

    override fun onStart() {
        super.onStart()
        Log.d("Bookmark", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Bookmark", "onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Bookmark", "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("Bookmark", "onDetach")
    }
}
