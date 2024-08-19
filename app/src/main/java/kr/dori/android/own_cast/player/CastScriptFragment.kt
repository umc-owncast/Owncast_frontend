package kr.dori.android.own_cast.player


import ScriptAdapter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.databinding.FragmentCastScriptBinding
import kr.dori.android.own_cast.forApiData.Bookmark
import kr.dori.android.own_cast.forApiData.Cast
import kr.dori.android.own_cast.forApiData.CastInfo
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.forApiData.getRetrofit
import kotlinx.coroutines.async
import kr.dori.android.own_cast.forApiData.GetBookmark

class CastScriptFragment(val currentCast: CastWithPlaylistId) : Fragment() {
    lateinit var binding: FragmentCastScriptBinding
    private val playCastViewModel: PlayCastViewModel2 by activityViewModels()
    private val handler = Handler()
    val adapter = ScriptAdapter(currentCast)
    lateinit var castInfo: CastInfo
    //lateinit var filteredBookmark: List<GetBookmark>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCastScriptBinding.inflate(inflater, container, false)

        val getScript = getRetrofit().create(Playlist::class.java)
       // val getBookmark = getRetrofit().create(Bookmark::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val castInfoDeferred = async {
                val response = getScript.getCast(currentCast.castId)
                if (response.isSuccessful) {
                    response.body()?.result
                } else {
                    null
                }
            }
/*
            val bookmarkDeferred = async {
                val response = getBookmark.getBookmark(currentCast.playlistId)
                if (response.isSuccessful) {
                    response.body()?.result?.filter { bookmark -> bookmark.castId == currentCast.castId }
                } else {
                    null
                }
            }

 */

            castInfo = castInfoDeferred.await() ?: CastInfo(0, "", "", "", "", emptyList())
          //  filteredBookmark = bookmarkDeferred.await() ?: emptyList()

            withContext(Dispatchers.Main) {
                Log.d("script", "${castInfo.sentences}")
                Log.d("script", "playlistId: ${currentCast.playlistId}, castId: ${currentCast.castId}")

                adapter.dataList = castInfo.sentences

                binding.scriptRv.adapter = adapter
                binding.scriptRv.layoutManager = LinearLayoutManager(context)
            }
        }
        return binding.root
    }

    fun updateCurrentTime(currentTime: Long) {
        Log.d("UpdateTime", "Fragment: $currentTime")
        adapter.updateCurrentTime(currentTime)
    }
}
