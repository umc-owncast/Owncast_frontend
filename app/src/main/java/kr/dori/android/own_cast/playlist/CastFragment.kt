package kr.dori.android.own_cast.playlist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.ActivityMover
import kr.dori.android.own_cast.editAudio.EditAudioActivity
import kr.dori.android.own_cast.MainActivity
import kr.dori.android.own_cast.data.CastPlayerData
import kr.dori.android.own_cast.player.PlayCastActivity
import kr.dori.android.own_cast.databinding.FragmentCastBinding
import kr.dori.android.own_cast.forApiData.AuthResponse
import kr.dori.android.own_cast.forApiData.Cast
import kr.dori.android.own_cast.forApiData.GetPlayList
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.forApiData.getRetrofit
import kr.dori.android.own_cast.player.CastWithPlaylistId

class CastFragment(var playlistIdList: MutableList<Long>) : Fragment(), ActivityMover {
    private lateinit var binding: FragmentCastBinding
    private lateinit var castAdapter: CastAdapter
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var playlistList: MutableList<CastWithPlaylistId> = mutableListOf()

    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCastBinding.inflate(inflater, container, false)

        castAdapter = CastAdapter(this)

        val isSave = arguments?.getBoolean("isSave") ?: false

        val getPlaylist = getRetrofit().create(Playlist::class.java)
        // API 호출 및 데이터 설정
        // API 호출 및 데이터 설정
        CoroutineScope(Dispatchers.Main).launch {
            try {
                if (isSave) {
                    val response = getPlaylist.getSaved()
                    Log.d("CastFragment", "getSaved API 호출")
                    if (response.isSuccessful) {
                        Log.d("CastFragment", "getSaved API 성공")
                        val playlistInfo = response.body()?.result
                        withContext(Dispatchers.Main) {
                            playlistInfo?.let {
                                val castListWithPlaylistId = it.castList.map { cast ->
                                    CastWithPlaylistId(
                                        castId = cast.castId,
                                        playlistId = cast.playlistId,
                                        castTitle = cast.castTitle?:"untitled",
                                        isPublic = cast.isPublic,
                                        castCreator = cast.castCreator,
                                        castCategory = cast.castCategory,
                                        audioLength = cast.audioLength,
                                        imagePath = cast.imagePath
                                    )
                                }
                                castAdapter.dataList = castListWithPlaylistId.toMutableList()
                            }
                        }
                    } else {
                        Log.e("CastFragment", "getSaved API 실패: ${response.errorBody()?.string()}")
                    }
                } else {
                    val response = getPlaylist.getMy()
                    Log.d("CastFragment", "getMy API 호출")
                    if (response.isSuccessful) {
                        Log.d("CastFragment", "getMy API 성공")
                        val playlistInfo = response.body()?.result
                        withContext(Dispatchers.Main) {
                            playlistInfo?.let {
                                val castListWithPlaylistId = it.castList.map { cast ->
                                    CastWithPlaylistId(
                                        castId = cast.castId,
                                        playlistId = cast.playlistId,
                                        castTitle = cast.castTitle?:"untitled",
                                        isPublic = cast.isPublic,
                                        castCreator = cast.castCreator,
                                        castCategory = cast.castCategory,
                                        audioLength = cast.audioLength,
                                        imagePath = cast.imagePath
                                    )
                                }
                                castAdapter.dataList = castListWithPlaylistId.toMutableList()
                            }
                        }
                    } else {
                        Log.e("CastFragment", "getMy API 실패: ${response.errorBody()?.string()}")
                    }
                }
                binding.fragmentCastRv.adapter = castAdapter
                binding.fragmentCastRv.layoutManager = LinearLayoutManager(context)
            } catch (e: Exception) {
                Log.e("CastFragment", "API 호출 중 예외 발생", e)
                e.printStackTrace()
            }
        }




        // RecyclerView 설정
        binding.fragmentCastRv.adapter = castAdapter
        binding.fragmentCastRv.layoutManager = LinearLayoutManager(context)

        // Back 버튼 클릭 이벤트 처리
        binding.fragmentCastBackIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        // Initialize ActivityResultLauncher
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("ifsuccess", "success")
                val data: Intent? = result.data
                val isSuccess = data?.getBooleanExtra("result", false) ?: false
                (activity as? MainActivity)?.setPlaylistTableVisibility(isSuccess)
            }
        }

        return binding.root
    }


    override fun ToPlayCast(castList: List<CastWithPlaylistId>) {

        //   val currentCast = CastPlayerData.currentCast

        CastPlayerData.setCast(castList)

        val intent = Intent(requireContext(), PlayCastActivity::class.java)

        activityResultLauncher.launch(intent)

    }



    override fun ToEditAudio(id: Long,playlistId:Long) {

        val intent = Intent(requireContext(), EditAudioActivity::class.java)
        intent.putExtra("id",id)
        intent.putExtra("playlistId",playlistId)
        startActivity(intent)
    }

    private fun parseTimeToSeconds(input: String): Int {
        return if (input.contains(":")) {
            val parts = input.split(":")
            val minutes = parts[0].toIntOrNull() ?: 0
            val seconds = parts[1].toIntOrNull() ?: 0
            (minutes * 60) + seconds
        } else {
            input.toIntOrNull() ?: 0
        }
    }

    private fun getTotalAudioLengthInSeconds(castList: List<CastWithPlaylistId>): Int {
        return castList.sumOf { cast ->
            parseTimeToSeconds(cast.audioLength)
        }
    }

    private fun formatTime(input: Int): String {
        val minutes = input / 60
        val seconds = input % 60
        return String.format("%02d:%02d", minutes, seconds)
    }



}
