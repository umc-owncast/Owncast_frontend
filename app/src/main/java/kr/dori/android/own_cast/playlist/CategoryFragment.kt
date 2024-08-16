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
import kr.dori.android.own_cast.data.SongData
import kr.dori.android.own_cast.databinding.FragmentCategoryBinding
import kr.dori.android.own_cast.editAudio.EditAudioActivity
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.getRetrofit
import kr.dori.android.own_cast.player.PlayCastActivity
import retrofit2.create

class CategoryFragment(val playlistId: Long) : Fragment(), ActivityMover {

    private lateinit var binding: FragmentCategoryBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var castAdapter: CastAdapter
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding =  FragmentCategoryBinding.inflate(inflater,container,false)

        castAdapter = CastAdapter(this)



        val getAllPlaylist = getRetrofit().create(Playlist::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = getAllPlaylist.getPlaylistInfo(playlistId, 0, 5)
                if (response.isSuccessful) {
                    val playlistInfo = response.body()?.result
                    withContext(Dispatchers.Main) {
                        playlistInfo?.let {
                            // 데이터를 캐스트 리스트로 변환하여 어댑터에 설정
                            castAdapter.dataList = it.castList.toMutableList()
                            castAdapter.notifyDataSetChanged()
                        }
                    }
                } else {
                    Log.e("CategoryFragment", "Failed to fetch playlist info")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.fragmentCategoryBackIv.setOnClickListener {
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

        binding.fragmentCategoryRv.adapter = castAdapter
        binding.fragmentCategoryRv.layoutManager = LinearLayoutManager(context)

        binding.fragmentCategoryPlayIv.setOnClickListener {
            ToPlayCast()
        }

        binding.fragmentCategoryShuffleIv.setOnClickListener {
            ToPlayCast()
        }

        return binding.root
    }

    override fun ToPlayCast() {
        val intent = Intent(requireContext(), PlayCastActivity::class.java)
        activityResultLauncher.launch(intent)
    }

    override fun ToEditAudio() {
        val intent = Intent(requireContext(), EditAudioActivity::class.java)
        startActivity(intent)
    }
}

