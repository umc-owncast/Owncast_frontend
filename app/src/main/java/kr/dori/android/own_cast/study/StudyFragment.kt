package kr.dori.android.own_cast.study

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dori.android.own_cast.R
import kr.dori.android.own_cast.data.PlaylistInfo
import kr.dori.android.own_cast.databinding.FragmentStudyBinding
import kr.dori.android.own_cast.forApiData.Bookmark
import kr.dori.android.own_cast.forApiData.GetBookmark
import kr.dori.android.own_cast.forApiData.Playlist
import kr.dori.android.own_cast.forApiData.getRetrofit

class StudyFragment : Fragment() {

    private var dataCount = 0
    private val snapHelper = LinearSnapHelper()
    private lateinit var binding: FragmentStudyBinding
    private val customAdapter = StudyCustomAdapter()
    private lateinit var studyAdapter: StudyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudyBinding.inflate(inflater, container, false)
        studyAdapter = StudyAdapter { position ->
            handleItemClick(position)
        }

        // 빈 리스트로 어댑터 연결
        studyAdapter.dataList = mutableListOf()
        setupRecyclerView()
        loadPlaylistData()
        loadInitialCustomAdapterData()
        setupUI()
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.studyCategoryRv.adapter = studyAdapter
        binding.studyCategoryRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        studyAdapter.selectedPosition = 0

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.studyCustomAdapterRv.layoutManager = layoutManager
        binding.studyCustomAdapterRv.adapter = customAdapter

        snapHelper.attachToRecyclerView(binding.studyCustomAdapterRv)

        // Next, Previous 버튼 추가
        binding.fragmentStudyNextIv.setOnClickListener {
            scrollToNextItem()
        }

        binding.fragmentStudyBackIv.setOnClickListener {
            scrollToPreviousItem()
        }

        // 처음에 첫 번째 아이템을 보여줌
        if (customAdapter.itemList.isNotEmpty()) {
            binding.studyCustomAdapterRv.post {
                val firstPosition = 0
                binding.studyCustomAdapterRv.scrollToPosition(firstPosition)
                adjustSelectedItem()
            }
        }

        val margin = resources.getDimensionPixelSize(R.dimen.study_item_margin)
        binding.studyCustomAdapterRv.addItemDecoration(HorizontalMarginItemDecoration(margin))
    }

    private fun adjustSelectedItem() {
        val layoutManager = binding.studyCustomAdapterRv.layoutManager as LinearLayoutManager
        val centerView = snapHelper.findSnapView(layoutManager)
        centerView?.let {
            val position = layoutManager.getPosition(it)
            val actualPosition = position % dataCount
            binding.fragmentStudyStateTv.text = "${setText(actualPosition)}/$dataCount"
        }
    }

    private fun loadPlaylistData() {
        CoroutineScope(Dispatchers.IO).launch {
            val getAllPlaylist = getRetrofit().create(Playlist::class.java)
            try {
                val response = getAllPlaylist.getAllPlaylist()
                if (response.isSuccessful) {
                    val allPlaylist = response.body()?.result ?: emptyList()
                    val filteredList = mutableListOf<PlaylistInfo>()

                    allPlaylist.let { playlists ->
                        playlists.find { it.name == "내가 만든 캐스트" }?.let {
                            filteredList.add(PlaylistInfo(it.playlistId, it.name, it.imagePath))
                        }
                        playlists.find { it.name == "담아온 캐스트" }?.let {
                            filteredList.add(PlaylistInfo(it.playlistId, it.name, it.imagePath))
                        }
                        playlists.filter { it.playlistId != 0L }.forEach {
                            filteredList.add(PlaylistInfo(it.playlistId, it.name, it.imagePath))
                        }
                    }

                    withContext(Dispatchers.Main) {
                        studyAdapter.dataList = filteredList
                        studyAdapter.notifyDataSetChanged()  // 데이터 변경 후 UI 업데이트
                    }

                } else {
                    Log.e("StudyFragment", "Failed to load playlists")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("StudyFragment", "Error loading playlists", e)
            }
        }
    }

    private fun loadInitialCustomAdapterData() {
        CoroutineScope(Dispatchers.IO).launch {
            val getAllBookmark = getRetrofit().create(Bookmark::class.java)

            try {
                val response = getAllBookmark.getSaved()
                if (response.isSuccessful) {
                    val allBookmarks = response.body()?.result ?: emptyList()
                    Log.d("StudyFragment", "Bookmarks loaded successfully: ${allBookmarks.size}")

                    withContext(Dispatchers.Main) {
                        dataCount = allBookmarks.size
                        if (dataCount > 0) {
                            customAdapter.itemList = allBookmarks.toMutableList()
                            customAdapter.notifyDataSetChanged()
                            binding.fragmentStudyStateTv.text = "1/$dataCount"
                        } else {
                            // 데이터가 비어 있을 때 UI 요소 비활성화
                            handleEmptyData()
                        }
                    }
                } else {
                    Log.e("StudyFragment", "Failed to load bookmarks: ${response.errorBody()?.string()}")
                    withContext(Dispatchers.Main) {
                        handleEmptyData()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("StudyFragment", "Error loading bookmarks", e)
                withContext(Dispatchers.Main) {
                    handleEmptyData()
                }
            }
        }
    }

    private fun loadNotSaved() {
        CoroutineScope(Dispatchers.IO).launch {
            val getAllBookmark = getRetrofit().create(Bookmark::class.java)

            try {
                val response = getAllBookmark.getMy()
                if (response.isSuccessful) {
                    val allBookmarks = response.body()?.result ?: emptyList()
                    Log.d("StudyFragment", "Bookmarks loaded successfully: ${allBookmarks.size}")

                    withContext(Dispatchers.Main) {
                        dataCount = allBookmarks.size
                        if (dataCount > 0) {
                            customAdapter.itemList = allBookmarks.toMutableList()
                            customAdapter.notifyDataSetChanged()
                            binding.fragmentStudyStateTv.text = "1/$dataCount"
                        } else {
                            // 데이터가 비어 있을 때 UI 요소 비활성화
                            handleEmptyData()
                        }
                    }
                } else {
                    Log.e("StudyFragment", "Failed to load bookmarks: ${response.errorBody()?.string()}")
                    withContext(Dispatchers.Main) {
                        handleEmptyData()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("StudyFragment", "Error loading bookmarks", e)
                withContext(Dispatchers.Main) {
                    handleEmptyData()
                }
            }
        }
    }

    private fun loadCategoryBookmark(playlistId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val getAllBookmark = getRetrofit().create(Bookmark::class.java)

            try {
                val response = getAllBookmark.getBookmark(playlistId)
                if (response.isSuccessful) {
                    val allBookmarks = response.body()?.result ?: emptyList()
                    Log.d("StudyFragment", "Bookmarks loaded successfully: ${allBookmarks.size}")

                    withContext(Dispatchers.Main) {
                        dataCount = allBookmarks.size
                        if (dataCount > 0) {
                            customAdapter.itemList = allBookmarks.toMutableList()
                            customAdapter.notifyDataSetChanged()
                            binding.fragmentStudyStateTv.text = "1/$dataCount"
                        } else {
                            // 데이터가 비어 있을 때 UI 요소 비활성화
                            handleEmptyData()
                        }
                    }
                } else {
                    Log.e("StudyFragment", "Failed to load bookmarks: ${response.errorBody()?.string()}")
                    withContext(Dispatchers.Main) {
                        handleEmptyData()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("StudyFragment", "Error loading bookmarks", e)
                withContext(Dispatchers.Main) {
                    handleEmptyData()
                }
            }
        }
    }

    private fun handleEmptyData() {
        // 데이터가 비어 있을 때 UI 요소 비활성화 및 초기화
        dataCount = 0
        customAdapter.itemList.clear()
        customAdapter.notifyDataSetChanged()
        binding.fragmentStudyStateTv.text = "No bookmarks available"
        disableCustomAdapterUI()
    }

    private fun disableCustomAdapterUI() {
        binding.studyCustomAdapterRv.isEnabled = false
        binding.fragmentStudyNextIv.isEnabled = false
        binding.fragmentStudyBackIv.isEnabled = false
        binding.fragmentStudyShuffleIv.isEnabled = false
    }

    private fun scrollToNextItem() {
        val layoutManager = binding.studyCustomAdapterRv.layoutManager as LinearLayoutManager
        val currentPosition = layoutManager.findFirstVisibleItemPosition()
        if (currentPosition < customAdapter.itemList.size - 1) {
            binding.studyCustomAdapterRv.smoothScrollToPosition(currentPosition + 1)
            adjustSelectedItem()
        }
    }

    private fun scrollToPreviousItem() {
        val layoutManager = binding.studyCustomAdapterRv.layoutManager as LinearLayoutManager
        val currentPosition = layoutManager.findFirstVisibleItemPosition()
        if (currentPosition > 0) {
            binding.studyCustomAdapterRv.smoothScrollToPosition(currentPosition - 1)
            adjustSelectedItem()
        }
    }

    private fun setupUI() {
        binding.fragmentStudyLoofOffIv.setOnClickListener {
            binding.fragmentStudyLoofOnIv.visibility = View.VISIBLE
            binding.fragmentStudyLoofOffIv.visibility = View.GONE
        }

        binding.fragmentStudyLoofOnIv.setOnClickListener {
            binding.fragmentStudyLoofOffIv.visibility = View.VISIBLE
            binding.fragmentStudyLoofOnIv.visibility = View.GONE
        }

        binding.fragmentStudySoundOffIv.setOnClickListener {
            binding.fragmentStudySoundOnIv.visibility = View.VISIBLE
            binding.fragmentStudySoundOffIv.visibility = View.GONE
        }

        binding.fragmentStudySoundOnIv.setOnClickListener {
            binding.fragmentStudySoundOffIv.visibility = View.VISIBLE
            binding.fragmentStudySoundOnIv.visibility = View.GONE
        }

        binding.fragmentStudyNextIv.setOnClickListener {
            scrollToNextItem()
        }

        binding.fragmentStudyBackIv.setOnClickListener {
            scrollToPreviousItem()
        }

        binding.fragmentStudyShuffleIv.setOnClickListener {
            if (dataCount > 0) {
                customAdapter.itemList.shuffle()
                customAdapter.notifyDataSetChanged()
            }
        }

        binding.studyCustomAdapterRv.scrollToPosition(5)
        binding.studyCustomAdapterRv.post {
            val layoutManager = binding.studyCustomAdapterRv.layoutManager as LinearLayoutManager
            if (dataCount > 0) {
                layoutManager.scrollToPositionWithOffset(
                    5,
                    (binding.studyCustomAdapterRv.width / 2) - (binding.studyCustomAdapterRv.getChildAt(0).width / 2)
                )
                binding.studyCustomAdapterRv.post {
                    customAdapter.adjustItemSize(binding.studyCustomAdapterRv)
                    adjustSelectedItem()
                }
            }
        }
    }

    private fun setText(actualPosition: Int): Int {
        return if (actualPosition >= 5) {
            actualPosition - 4
        } else {
            actualPosition + dataCount - 4
        }
    }

    private fun handleItemClick(position: Int) {
        when (position) {
            0 -> loadInitialCustomAdapterData() // 첫 번째 아이템 클릭 시 초기 데이터 로드
            1 -> loadNotSaved() // 두 번째 아이템 클릭 시 저장되지 않은 북마크 로드
            else -> {
                val filteredData = studyAdapter.dataList
                if (filteredData.isNotEmpty() && position < filteredData.size) {
                    val playlistId = filteredData[position].playlistId
                    loadCategoryBookmark(playlistId) // 그 외의 경우 해당 카테고리의 북마크 로드
                }
            }
        }
    }
}
