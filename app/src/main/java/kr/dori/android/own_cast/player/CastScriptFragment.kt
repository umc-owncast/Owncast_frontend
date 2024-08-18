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
import kr.dori.android.own_cast.forApiData.Cast
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.forApiData.getRetrofit

class CastScriptFragment(val currentCast: Cast) : Fragment() {
    lateinit var binding: FragmentCastScriptBinding
    private val playCastViewModel: PlayCastViewModel2 by activityViewModels()
    private val handler = Handler()
    val adapter = ScriptAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCastScriptBinding.inflate(inflater, container, false)

        val getScript = getRetrofit().create(Playlist::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = getScript.getCast(currentCast.castId)
                if(response.isSuccessful){
                    val castInfo = response.body()?.result
                    withContext(Dispatchers.Main){
                        Log.d("castSentence","${castInfo?.sentences}")

                        adapter.dataList = castInfo?.sentences?:listOf()
                        binding.scriptRv.adapter = adapter
                        binding.scriptRv.layoutManager = LinearLayoutManager(context)

                    }
                }else{

                }

            }catch (e: Exception){
                e.printStackTrace()
            }
        }


        // val lyricsList = parseLyrics(rawLyrics)

        val adapter = ScriptAdapter()
    //    adapter.dataList = lyricsList

/*
        playCastViewModel.currentTime.observe(viewLifecycleOwner, Observer { currentTime ->
            adapter.updateCurrentTime(currentTime)
        })

 */


        return binding.root
    }




    fun updateCurrentTime(currentTime: Long) {
        Log.d("UpdateTime", "Fragment: $currentTime")
        adapter.updateCurrentTime(currentTime)
    }



}
