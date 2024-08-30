package kr.dori.android.own_cast.playlist
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
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
import com.bumptech.glide.Glide
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
import kr.dori.android.own_cast.forApiData.getRetrofit
import kr.dori.android.own_cast.keyword.AddCategoryDialog
import kr.dori.android.own_cast.player.CastWithPlaylistId
import kr.dori.android.own_cast.player.PlayCastActivity


class PlaylistFragment : Fragment(), AddCategoryListener, EditCategoryListener, ActivityMover,
    FragmentMover,
    EditAudio {
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var categoryAdapter: PlaylistCategoryAdapter

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var playlistIdList: MutableList<Long> = mutableListOf( )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //loadPlaylist() MainActivity로 대체

        binding = FragmentPlaylistBinding.inflate(inflater, container, false)

        categoryAdapter = PlaylistCategoryAdapter(this, this, this)
        binding.category.adapter = categoryAdapter
        binding.category.layoutManager = LinearLayoutManager(context)


        sharedViewModel.data.observe(viewLifecycleOwner, Observer { newData ->
            categoryAdapter.dataList = newData.filter { it.playlistId != 0L }.toMutableList()
            categoryAdapter.notifyDataSetChanged()
        })

        loadLatestDataFromServer()

        binding.fragmentPlaylistAddIv.setOnClickListener {
            val dialog = AddCategoryDiaLogPlaylist(requireContext(), this, this)
            dialog.show()
        }

        binding.fragmentPlaylistSaveIv.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean("isSave", true)  // 세이브 버튼 클릭 시 true 전달
            }
            val castFragment = CastFragment().apply {
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
            val castFragment = CastFragment().apply {
                arguments = bundle
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, castFragment)
                .addToBackStack(null)
                .commit()
        }

        //메인 엑티비티 테이블 구현에 필요한 리졸트런처
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


    // 카테고리 추가, 수정 부분을 Optimistic UI로 바꿨습니다. 이제 데이터는 즉각적으로 반영이 되며, 이후 서버 최신 데이터를 갖고 옵니다.
    override fun onCategoryAdded(categoryName: String) {
        val tempCategory = GetAllPlaylist(categoryName, "", 0, 0)
        sharedViewModel.addData(tempCategory)
        Log.d("test9","${sharedViewModel.data.value}")


        // 비동기로 서버에 카테고리 추가 요청
        val addCategory = getRetrofit().create(Playlist::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = addCategory.postPlaylist(categoryName)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        // 서버 요청 성공: 최신 데이터로 ViewModel 업데이트
                        loadLatestDataFromServer()
                        Toast.makeText(requireContext(), "카테고리가 성공적으로 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // 서버 요청 실패 시 롤백
                    withContext(Dispatchers.Main) {
                        sharedViewModel.removeData(tempCategory)
                        Toast.makeText(requireContext(), "카테고리 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 네트워크 오류 시 롤백
                withContext(Dispatchers.Main) {
                    sharedViewModel.removeData(tempCategory)
                    Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadLatestDataFromServer()
    }
    override fun onCategoryEdit(position: Long, name: String, playlistId: Long) {
        // Optimistic UI 적용: 변경 사항을 일단 반영
        val originalData = sharedViewModel.getCategory(position)
        sharedViewModel.updateDataAt(position, name)

        // 서버에 카테고리 수정 요청
        val playlistService = getRetrofit().create(Playlist::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = playlistService.patchPlaylist(playlistId, name)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        // 서버 요청 성공: 최신 데이터로 ViewModel 업데이트
                        loadLatestDataFromServer()
                        Toast.makeText(requireContext(), "카테고리가 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // 서버 요청 실패 시 롤백
                    withContext(Dispatchers.Main) {
                        sharedViewModel.updateDataAt(position, originalData.name)
                        Toast.makeText(requireContext(),"카테고리 수정에 실패했습니다.",Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 네트워크 오류 시 롤백
                withContext(Dispatchers.Main) {
                    sharedViewModel.updateDataAt(position, originalData.name)
                    Toast.makeText(requireContext(),"네트워크 오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadLatestDataFromServer() {
        val playlistService = getRetrofit().create(Playlist::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = playlistService.getAllPlaylist()
                if (response.isSuccessful) {
                    val latestData = response.body()?.result
                    withContext(Dispatchers.Main) {
                        latestData?.let {
                            sharedViewModel.setData(it.toMutableList())
                            Log.d("test9","${sharedViewModel.data.value}")

                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun ToPlayCast() {

        // 현재 서비스가 재생 중인지 확인하고 중지
        //val currentService = getCurrentServiceInstance()
      //  service?.stopAudio()

        // 캐스트 설정 및 새 액티비티로 이동
        val intent = Intent(requireContext(), PlayCastActivity::class.java)
        activityResultLauncher.launch(intent)
    }



    override fun getCategoryData(position: Long): GetAllPlaylist {
        return sharedViewModel.data.value?.get(position.toInt())
            ?: throw IndexOutOfBoundsException("Invalid position: $position")

    }

    override fun playlistToCategory(playlistId: Long, playlistName: String) {

        val categoryFragment = CategoryFragment(playlistId, playlistName)

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

    override fun ToEditAudio(id: Long, playlistId:Long) {
        TODO("Not yet implemented")

    }
}





