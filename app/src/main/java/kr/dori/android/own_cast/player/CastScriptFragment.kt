package kr.dori.android.own_cast.player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
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
import kr.dori.android.own_cast.data.CastPlayerData.currentBookmarkList
import kr.dori.android.own_cast.forApiData.Bookmark
import kr.dori.android.own_cast.forApiData.GetBookmark
import kr.dori.android.own_cast.network.BookmarkSyncTask

class CastScriptFragment(val currentCast: CastWithPlaylistId) : Fragment() {
    lateinit var binding: FragmentCastScriptBinding
    private val handler = Handler(Looper.getMainLooper()) // 수정된 부분: Handler 초기화
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
        CastPlayerData.currentBookmarkList = CastPlayerData.currentBookmarkList ?: emptyList()


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
                binding.scriptFragmentTitleTv.text = castInfo.title

                adapter.dataList = castInfo.sentences
                Log.d("문장체크",castInfo.sentences.toString())
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
        adapter.updateCurrentTime(currentTime) // timePoint가 시작 지점으로 사용됨
    }

    private fun scrollToPosition(position: Int) {
        val layoutManager = binding.scriptRv.layoutManager as LinearLayoutManager
        layoutManager.scrollToPositionWithOffset(position, binding.scriptRv.height / 5)
    }

    override fun onStop() {
        super.onStop()

        val currentBookmarks = CastPlayerData.currentBookmarkList
        Log.d("Bookmark", "$currentBookmarks")

        val addedBookmarks = currentBookmarks.filter { it !in previousBookmark }
        val removedBookmarks = previousBookmark.filter { it !in currentBookmarks }

        // BookmarkSyncTask를 사용하여 북마크 동기화
        if (addedBookmarks.isNotEmpty() || removedBookmarks.isNotEmpty()) {
            val syncTask = BookmarkSyncTask(requireContext(), addedBookmarks, removedBookmarks)
            syncTask.execute()
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


    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null) // 모든 핸들러 콜백을 제거
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // 모든 핸들러 콜백 제거

        Log.d("Bookmark", "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("Bookmark", "onDetach")
    }
}
