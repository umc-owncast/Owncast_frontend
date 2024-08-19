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
import kr.dori.android.own_cast.forApiData.Cast
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.getRetrofit
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

        // CoroutineScope를 사용하여 데이터를 모두 받아온 후에 처리하도록 수정
        CoroutineScope(Dispatchers.IO).launch {
            for (playlistId in playlistIdList) {
                val getAllPlaylist = getRetrofit().create(Playlist::class.java)

                try {
                    val response = getAllPlaylist.getPlaylistInfo(playlistId, 0, 5)
                    if (response.isSuccessful) {
                        val playlistInfo = response.body()?.result
                        playlistInfo?.let {
                            // 각 Cast를 CastWithPlaylistId로 변환하고 리스트에 저장
                            val castListWithPlaylistId = it.castList.map { cast ->
                                CastWithPlaylistId(
                                    castId = cast.castId,
                                    playlistId = playlistId,
                                    castTitle = cast.castTitle,
                                    isPublic = cast.isPublic,
                                    castCreator = cast.castCreator,
                                    castCategory = cast.castCategory,
                                    audioLength = cast.audioLength
                                )
                            }
                            playlistList.addAll(castListWithPlaylistId)
                        }
                    } else {
                        // 실패 처리
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            // 데이터를 모두 받아온 후 UI 스레드에서 처리
            withContext(Dispatchers.Main) {
                Log.d("test2", "$playlistList")

                val filteringData = if (isSave) {
                    playlistList.filter { cast -> cast.castCreator == "헬로" }
                } else {
                    playlistList.filter { cast -> cast.castCreator != "헬로" }
                }.toMutableList()

                val totalAudioLengthInSeconds = getTotalAudioLengthInSeconds(filteringData)
                Log.d("TotalAudioLength", "총 오디오 길이 (초): $totalAudioLengthInSeconds")
                binding.castInfo.text = "${filteringData.size}개, ${formatTime(totalAudioLengthInSeconds)}"

                Log.d("test2", "$filteringData")

                castAdapter.dataList = filteringData

                // 어댑터에 알림
                castAdapter.notifyDataSetChanged()

                binding.fragmentCastPlayIv.setOnClickListener {
                    Log.d("test3", "$filteringData")
                    ToPlayCast(filteringData)
                }

                binding.fragmentCastShuffleIv.setOnClickListener {
                    Log.d("test3", "$filteringData")
                    ToPlayCast(filteringData)
                }

                // 제목 설정
                if (isSave) {
                    binding.fragmentCastMaintitleTv.text = "내가 만든 캐스트"
                } else {
                    binding.fragmentCastMaintitleTv.text = "담아온 캐스트"
                }

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
