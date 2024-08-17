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
import androidx.lifecycle.Observer
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
import kr.dori.android.own_cast.data.SongData
import kr.dori.android.own_cast.databinding.FragmentCastBinding
import kr.dori.android.own_cast.forApiData.Cast
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.getRetrofit

class CastFragment(var playlistIdList : MutableList<Long>) : Fragment(), ActivityMover {
    private lateinit var binding: FragmentCastBinding
    private lateinit var castAdapter: CastAdapter
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var playlistList: MutableList<Cast> = mutableListOf()


    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentCastBinding.inflate(inflater, container, false)

        castAdapter = CastAdapter(this)

        for (playlistId in playlistIdList){

            val getAllPlaylist = getRetrofit().create(Playlist::class.java)

            val isSave = arguments?.getBoolean("isSave") ?: false

            CoroutineScope(Dispatchers.IO).launch() {
                launch {
                    try {
                        val response =
                            getAllPlaylist.getPlaylistInfo(playlistId,0,5) //변수명이 어지럽지만 첫번째 getAll은 레트로핏 활성화 객체이고, 두번째는 인터페이스 내부 함수이다.
                        if (response.isSuccessful) {
                            var playlistInfo = response.body()?.result
                            withContext(Dispatchers.Main) {
                                playlistInfo?.let {
                                    playlistList.addAll(it.castList)
                                    Log.d("xibal", "$playlistList")

                                    castAdapter.dataList = if (isSave) {
                                        // isSave가 true일 때 필터링
                                        playlistList.filter { cast -> cast.castCreator == "헬로" }
                                    } else {
                                        // isSave가 false일 때 필터링
                                        playlistList.filter { cast -> cast.castCreator != "헬로" }
                                    }.toMutableList()

                                    // 어댑터에 알림
                                    castAdapter.notifyDataSetChanged()
                                }
                            }
                        } else {

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }

            if(isSave){
                binding.fragmentCastMaintitleTv.text = "내가 만든 캐스트"
                binding.fragmentCastTitleTv.text = "${castAdapter.itemCount},"
            }else{
                binding.fragmentCastMaintitleTv.text = "담아온 캐스트"
                binding.fragmentCastTitleTv.text = "${castAdapter.itemCount},"
            }
        }




        // CastAdapter 초기화



/*
        // ViewModel 데이터 관찰
        sharedViewModel.data.observe(viewLifecycleOwner, Observer { newData ->
            val savedData = arguments?.getParcelableArrayList<SongData>("isSave")
            val unsavedData = arguments?.getParcelableArrayList<SongData>("isNotSave")
            val data = savedData ?: unsavedData ?: arrayListOf()
            castAdapter.dataList =
            castAdapter.notifyDataSetChanged()
        })


        */

        // Initialize ActivityResultLauncher
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("ifsuccess", "success")
                val data: Intent? = result.data
                val isSuccess = data?.getBooleanExtra("result", false) ?: false
                (activity as? MainActivity)?.setPlaylistTableVisibility(isSuccess)
            }
        }

        // RecyclerView 설정
        binding.fragmentCastRv.adapter = castAdapter
        binding.fragmentCastRv.layoutManager = LinearLayoutManager(context)

        // Back 버튼 클릭 이벤트 처리
        binding.fragmentCastBackIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.fragmentCastPlayIv.setOnClickListener {
            ToPlayCast(playlistList)
        }

        binding.fragmentCastShuffleIv.setOnClickListener {
            ToPlayCast(playlistList)
        }

        return binding.root
    }

    override fun ToPlayCast(castList: List<Cast>) {
        val currentCast = CastPlayerData.currentCast

        CastPlayerData.test(castList)

        if (currentCast != null && castList.contains(currentCast)) {
            // 첫 번째 케이스: 현재 재생 중인 캐스트를 클릭한 경우
            val intent = Intent(requireContext(), PlayCastActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT // 기존 Activity 재사용
            activityResultLauncher.launch(intent)
        } else {
            // 두 번째 케이스: 다른 캐스트를 클릭한 경우
            CastPlayerData.setCastList(castList) // 새로운 캐스트 리스트 설정
            val intent = Intent(requireContext(), PlayCastActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // 새로운 Activity 생성
            activityResultLauncher.launch(intent)
        }
    }


    override fun ToEditAudio() {
        val intent = Intent(requireContext(), EditAudioActivity::class.java)
        startActivity(intent)
    }
}

