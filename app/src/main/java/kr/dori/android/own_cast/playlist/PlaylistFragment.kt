package kr.dori.android.own_cast.playlist
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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
import kr.dori.android.own_cast.FragmentMover
import kr.dori.android.own_cast.MainActivity
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.data.CastPlayerData
import kr.dori.android.own_cast.databinding.FragmentPlaylistBinding
import kr.dori.android.own_cast.editAudio.EditAudio
import kr.dori.android.own_cast.forApiData.Cast
import kr.dori.android.own_cast.forApiData.GetAllPlaylist
import kr.dori.android.own_cast.forApiData.Playlist

import kr.dori.android.own_cast.getRetrofit
import kr.dori.android.own_cast.keyword.AddCategoryDialog
import kr.dori.android.own_cast.player.PlayCastActivity


class PlaylistFragment : Fragment(), AddCategoryListener, EditCategoryListener, ActivityMover,
    FragmentMover,
    EditAudio {
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var categoryAdapter: PlaylistCategoryAdapter

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var playlistIdList: MutableList<Long> = mutableListOf( )

    /*
    val dummyData = mutableListOf(
        SongData("category_name1", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "animal"),
        SongData("category_name2", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "monkey"),
        SongData("category_name3", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "koala"),
        SongData("category_name4", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, true, "human"),
        SongData("category_name5", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "slug"),
        SongData("category_name6", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "animal"),
        SongData("category_name7", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "monkey"),
        SongData("category_name8", R.drawable.playlistfr_dummy_iv, "koyoungjun", false, 180, true, "koala"),
        SongData("category_name9", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, true, "human"),
        SongData("category_name10", R.drawable.playlistfr_dummy_iv, "koyoungjun", true, 180, false, "slug")

    )
 */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val getAllPlaylist = getRetrofit().create(Playlist::class.java)
        CoroutineScope(Dispatchers.IO).launch() {
            launch {
                try {
                    val response =
                        getAllPlaylist.getAllPlaylist() //변수명이 어지럽지만 첫번째 getAll은 레트로핏 활성화 객체이고, 두번째는 인터페이스 내부 함수이다.
                    if (response.isSuccessful) {
                        var playlistCategoryData = response.body()?.result
                        withContext(Dispatchers.Main) {
                            playlistCategoryData?.let {
                                playlistIdList = it.map { playlist -> playlist.playlistId }
                                    .filter { id -> id != 0L }
                                    .toMutableList()
                                sharedViewModel.setData(it.toMutableList())
                                Log.d("xibal","$playlistIdList")
                            }
                        }
                    } else {

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }


        }

        binding = FragmentPlaylistBinding.inflate(inflater, container, false)

        categoryAdapter = PlaylistCategoryAdapter(this, this, this)
        binding.category.adapter = categoryAdapter
        binding.category.layoutManager = LinearLayoutManager(context)

        sharedViewModel.data.observe(viewLifecycleOwner, Observer { newData ->
            categoryAdapter.dataList = newData.filter{it.playlistId != 0L}.toMutableList()

            categoryAdapter.notifyDataSetChanged()
        })
        /*
        if (sharedViewModel.data.value.isNullOrEmpty()) {
            sharedViewModel.setData(playlistCategoryData)
        }

 */

        binding.fragmentPlaylistAddIv.setOnClickListener {
            val dialog = AddCategoryDiaLogPlaylist(requireContext(), this, this)
            dialog.show()
        }


        //  val castFragment = CastFragment(playlistIdList)

        binding.fragmentPlaylistSaveIv.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean("isSave", true)  // 세이브 버튼 클릭 시 true 전달
            }
            val castFragment = CastFragment(playlistIdList).apply {
                arguments = bundle
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, castFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.fragmentPlaylistNotsaveIv.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean("isSave", false)  // 세이브 버튼 클릭 시 true 전달
            }
            val castFragment = CastFragment(playlistIdList).apply {
                arguments = bundle
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, castFragment)
                .addToBackStack(null)
                .commit()
        }



        // Initialize ActivityResultLauncher
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.d("ifsuccess", "success")
                    val data: Intent? = result.data
                    val isSuccess = data?.getBooleanExtra("result", false) ?: false
                    (activity as? MainActivity)?.setPlaylistTableVisibility(isSuccess)
                }
            }




        return binding.root
    }

    // addCategory 부분은 사용자 토큰이 필요하기에 2024-08-16시점에는 기능이 작동하지 않습니다. -> 사용자 정보와, 제목,totalCast, CastList등의 정보가 필요함 ->
    override fun onCategoryAdded(categoryName: String) {


        val addCategory = getRetrofit().create(Playlist::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = addCategory.postPlaylist(categoryName)
                if (response.isSuccessful) {
                    var newCategoryId = response.body()?.result
                    withContext(Dispatchers.Main) {
                        sharedViewModel.addData(GetAllPlaylist(categoryName, "", newCategoryId?.playlistId?: 0,0))
                        Log.d("xibal","$playlistIdList")
                    }
                }
            }catch(e: Exception){
                e.printStackTrace()
            }
        }
    }

    override fun onCategoryEdit(position: Long, newItem: GetAllPlaylist) {


        //서버 통신 전에 미리 데이터 업데이트를 시켜서 좀 더 빠릿한 느낌을 줄 수 있다.
        sharedViewModel.updateDataAt(position, newItem)

        val getAllPlaylist = getRetrofit().create(Playlist::class.java)

        CoroutineScope(Dispatchers.IO).launch() {
            launch {
                try {
                    val playlistId: Long = newItem.playlistId
                    val playlistName: String = newItem.name
                    val response = getAllPlaylist.patchPlaylist(
                        playlistId,
                        playlistName
                    )
                    if (response.isSuccessful) {
                        // var playlistCategoryData = response.body()?.result


                    } else {

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun ToPlayCast(castList: List<Cast>) {
        // 현재 서비스가 재생 중인지 확인하고 중지
        //val currentService = getCurrentServiceInstance()
      //  service?.stopAudio()

        // 캐스트 설정 및 새 액티비티로 이동
        CastPlayerData.setCast(castList)
        val intent = Intent(requireContext(), PlayCastActivity::class.java)
        activityResultLauncher.launch(intent)
    }



    override fun getCategoryData(position: Long): GetAllPlaylist {
        return sharedViewModel.data.value?.get(position.toInt())
            ?: throw IndexOutOfBoundsException("Invalid position: $position")

    }
/*
    override fun ToPlayCast(castList: List<Cast>) {
        CastPlayerData.setCast(castList)
        val intent = Intent(requireContext(), PlayCastActivity::class.java)
        activityResultLauncher.launch(intent)
    }
 */

    override fun playlistToCategory(playlistId: Long) {

        val categoryFragment = CategoryFragment(playlistId)

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, categoryFragment)
            .addToBackStack(null)
            .commit()
    }

    fun showCustomToast(message: String) {
        // Inflate the custom layout
        val inflater: LayoutInflater = layoutInflater
        val layout: View = inflater.inflate(
            R.layout.custom_toast,
            binding.root.findViewById(R.id.custom_toast_container)
        )

        // Set custom message
        val textView: TextView = layout.findViewById(R.id.toast_message_tv)
        textView.text = message

        // Create and show the Toast
        with(Toast(requireContext())) {
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }

    override fun dialogToEditAudio() {
        showCustomToast("카테고리가 추가되었어요")

    }

    override fun ToEditAudio() {
        TODO("Not yet implemented")

    }
}





