package kr.dori.android.own_cast.playlist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.ActivityMover
import kr.dori.android.own_cast.MainActivity
import kr.dori.android.own_cast.data.CastPlayerData
import kr.dori.android.own_cast.databinding.FragmentCategoryBinding
import kr.dori.android.own_cast.editAudio.EditAudioActivity
import kr.dori.android.own_cast.forApiData.Cast
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.forApiData.getRetrofit
import kr.dori.android.own_cast.player.CastWithPlaylistId
import kr.dori.android.own_cast.player.PlayCastActivity
import retrofit2.create

class CategoryFragment() : Fragment(), ActivityMover {

    private lateinit var binding: FragmentCategoryBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var castAdapter: CastAdapter
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    lateinit var sendCastIdList: List<CastWithPlaylistId>
    private var playlistId: Long = 0 // 초기화된 변수 추가
    private var playlistName: String = "" // 초기화된 변수 추가
    constructor(playlistId: Long, playlistName: String) : this() { // 생성자 오버로딩
        this.playlistId = playlistId
        this.playlistName = playlistName
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("xibal5","${playlistId}")

        binding =  FragmentCategoryBinding.inflate(inflater,container,false)

        castAdapter = CastAdapter(this)
        binding.fragmentCategoryRv.adapter = castAdapter
        binding.fragmentCategoryRv.layoutManager = LinearLayoutManager(context)


        val getAllPlaylist = getRetrofit().create(Playlist::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = getAllPlaylist.getPlaylistInfo(playlistId, 0, 20)
                Log.d("xibal5","$response")
                if (response.isSuccessful) {
                    Log.d("xibal5","연결 성공 ㅅ")
                    val playlistInfo = response.body()?.result
                    withContext(Dispatchers.Main) {
                        playlistInfo?.let {
                            // 각 Cast를 CastWithPlaylistId로 변환하여 어댑터에 설정
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

                            // 데이터를 어댑터에 설정
                            castAdapter.dataList = castListWithPlaylistId.toMutableList()
                            Log.d("castInfo", "${castListWithPlaylistId}")
                            sendCastIdList = castListWithPlaylistId
                            castAdapter.notifyDataSetChanged()

                            // 총 오디오 길이 계산
                            val totalAudioLengthInSeconds = getTotalAudioLengthInSeconds(castListWithPlaylistId)
                            Log.d("TotalAudioLength", "총 오디오 길이 (초): $totalAudioLengthInSeconds")
                            binding.castSizeTotalLength.text = "${castListWithPlaylistId.size}개, ${formatTime(totalAudioLengthInSeconds)}"
                        }

                    }
                } else {
                    Log.d("xibal5","연결 실패요 ㅛ")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("xibal5","서버 문제요 ㅛ")
            }
        }

        binding.fragmentCategoryBackIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.playlistName.text = playlistName


        // Initialize ActivityResultLauncher
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("ifsuccess", "success")
                val data: Intent? = result.data
                val isSuccess = data?.getBooleanExtra("result", false) ?: false
                (activity as? MainActivity)?.setPlaylistTableVisibility(isSuccess)
            }
        }

        binding.fragmentCategoryRv.adapter = castAdapter
        binding.fragmentCategoryRv.layoutManager = LinearLayoutManager(context)


// 아

        binding.fragmentCategoryPlayIv.setOnClickListener {
            CastPlayerData.setCast(sendCastIdList, 0)
            ToPlayCast(sendCastIdList)
           // Log.d("Cast","$sendCastIdList")
        }

        binding.fragmentCategoryShuffleIv.setOnClickListener {
            val shuffledList = sendCastIdList.shuffled()
            CastPlayerData.setCast(shuffledList,0)
            ToPlayCast(shuffledList)
        }

        return binding.root
    }


    override fun ToPlayCast(castList: List<CastWithPlaylistId>) {

        //   val currentCast = CastPlayerData.currentCast

        val intent = Intent(requireContext(), PlayCastActivity::class.java)
        activityResultLauncher.launch(intent)


    }


    override fun ToEditAudio(id: Long,playlistId:Long) {
        val intent = Intent(requireContext(), EditAudioActivity::class.java)
        intent.putExtra("id",id)
        intent.putExtra("playlistId",playlistId)
        startActivity(intent)
    }

    fun getTotalAudioLengthInSeconds(castList: List<CastWithPlaylistId>): Int {
        return castList.sumOf { cast ->
            parseTimeToSeconds(cast.audioLength)
        }
    }

    fun parseTimeToSeconds(input: String): Int {
        return if (input.contains(":")) {
            // 입력이 "분:초" 형식인 경우
            val parts = input.split(":")
            val minutes = parts[0].toIntOrNull() ?: 0
            val seconds = parts[1].toIntOrNull() ?: 0
            (minutes * 60) + seconds
        } else {
            // 입력이 이미 초 단위인 경우
            input.toIntOrNull() ?: 0
        }
    }


    fun formatTime(input: Int): String {

            val totalSeconds = input ?: return "00:00" // 입력이 숫자가 아닌 경우 대비
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val formatTime = String.format("%02d:%02d", minutes, seconds)
            return formatTime
    }

}

